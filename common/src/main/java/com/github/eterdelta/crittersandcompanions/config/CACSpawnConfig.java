package com.github.eterdelta.crittersandcompanions.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.platform.RegistryEntry;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CACSpawnConfig {
    public static final String FILE_NAME = CrittersAndCompanions.MODID + "-spawns.toml";

    private static final Logger LOGGER = LoggerFactory.getLogger(CACSpawnConfig.class);
    private static final String LEGACY_COMMON_FILE_NAME = CrittersAndCompanions.MODID + "-common.toml";
    private static final Map<String, List<SpawnEntry>> DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, List<SpawnValues>> LEGACY_FLAT_DEFAULTS = new LinkedHashMap<>();

    private static CACSpawnConfig instance;

    private final Map<String, List<SpawnEntry>> entries;

    public CACSpawnConfig(Map<String, List<SpawnEntry>> entries) {
        this.entries = Collections.unmodifiableMap(copyEntries(entries));
    }

    public static CACSpawnConfig get() {
        return instance;
    }

    public static void load(Path configDir) {
        Path configPath = configDir.resolve(FILE_NAME);

        if (Files.notExists(configPath)) {
            Map<String, List<SpawnEntry>> defaults = copyEntries(DEFAULTS);
            Map<String, SpawnValues> legacyOverrides = readCustomizedLegacySpawnRates(configDir.resolve(LEGACY_COMMON_FILE_NAME));
            applyLegacyOverrides(defaults, legacyOverrides);
            writeConfig(configPath, defaults);
            instance = new CACSpawnConfig(defaults);
            return;
        }

        Map<String, List<SpawnEntry>> loaded = readConfig(configPath);
        Map<String, List<SpawnEntry>> missingDefaults = missingDefaults(loaded);
        if (!missingDefaults.isEmpty()) {
            appendConfig(configPath, missingDefaults);
            loaded.putAll(missingDefaults);
        }

        instance = new CACSpawnConfig(loaded);
    }

    public Map<String, List<SpawnEntry>> getAllEntries() {
        return this.entries;
    }

    public static EntityType<?> resolveEntityType(String entityName) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(CrittersAndCompanions.createId(entityName)).orElse(null);
    }

    private static Map<String, List<SpawnEntry>> readConfig(Path configPath) {
        Map<String, List<SpawnEntry>> loaded = new LinkedHashMap<>();

        try (CommentedFileConfig config = CommentedFileConfig.of(configPath.toFile())) {
            config.load();
            for (UnmodifiableConfig.Entry entry : config.entrySet()) {
                Object value = entry.getValue();
                if (!(value instanceof List<?> rawEntries)) {
                    continue;
                }

                List<SpawnEntry> spawnEntries = new ArrayList<>();
                for (Object rawEntry : rawEntries) {
                    SpawnEntry spawnEntry = parseSpawnEntry(entry.getKey(), rawEntry);
                    if (spawnEntry != null) {
                        spawnEntries.add(spawnEntry);
                    }
                }

                if (!spawnEntries.isEmpty()) {
                    loaded.put(entry.getKey(), List.copyOf(spawnEntries));
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to read spawn config at {}. Falling back to built-in defaults.", configPath, exception);
            return copyEntries(DEFAULTS);
        }

        return loaded;
    }

    private static SpawnEntry parseSpawnEntry(String entityName, Object rawEntry) {
        if (!(rawEntry instanceof UnmodifiableConfig config)) {
            LOGGER.warn("Skipping invalid spawn config entry for '{}': expected a TOML table", entityName);
            return null;
        }

        String biome = getString(config, "biome");
        Integer weight = getInt(config, "weight");
        Integer min = getInt(config, "min");
        Integer max = getInt(config, "max");
        if (biome == null || weight == null || min == null || max == null) {
            LOGGER.warn("Skipping incomplete spawn config entry for '{}'", entityName);
            return null;
        }
        if (min < 1 || max < min) {
            LOGGER.warn("Skipping invalid spawn group for '{}': min={}, max={}", entityName, min, max);
            return null;
        }

        SpawnEntry spawnEntry = new SpawnEntry(biome, weight, min, max);
        try {
            spawnEntry.biomeLocation();
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Skipping invalid biome spec '{}' for '{}'", biome, entityName);
            return null;
        }

        return spawnEntry;
    }

    private static String getString(UnmodifiableConfig config, String key) {
        Object value = config.get(key);
        return value instanceof String string ? string : null;
    }

    private static Integer getInt(UnmodifiableConfig config, String key) {
        Object value = config.get(key);
        return value instanceof Number number ? number.intValue() : null;
    }

    private static Map<String, SpawnValues> readCustomizedLegacySpawnRates(Path commonConfigPath) {
        Map<String, SpawnValues> overrides = new LinkedHashMap<>();
        if (Files.notExists(commonConfigPath)) {
            return overrides;
        }

        try (CommentedFileConfig config = CommentedFileConfig.of(commonConfigPath.toFile())) {
            config.load();
            Object spawnRates = config.get("spawn_rates");
            if (!(spawnRates instanceof UnmodifiableConfig spawnRatesConfig)) {
                return overrides;
            }

            for (Map.Entry<String, List<SpawnValues>> entry : LEGACY_FLAT_DEFAULTS.entrySet()) {
                SpawnValues values = readLegacySpawnValues(spawnRatesConfig, entry.getKey());
                if (values != null && isCustomizedLegacyValue(values, entry.getValue())) {
                    overrides.put(entry.getKey(), values);
                }
            }
        } catch (Exception exception) {
            LOGGER.warn("Could not read legacy spawn rates from {}. Using spawn defaults.", commonConfigPath, exception);
        }

        return overrides;
    }

    private static SpawnValues readLegacySpawnValues(UnmodifiableConfig spawnRatesConfig, String entityName) {
        Object entityConfig = spawnRatesConfig.get(entityName);
        if (!(entityConfig instanceof UnmodifiableConfig config)) {
            return null;
        }

        Integer weight = getInt(config, "weight");
        Integer min = getInt(config, "min");
        Integer max = getInt(config, "max");
        if (weight == null || min == null || max == null) {
            return null;
        }

        return new SpawnValues(weight, min, max);
    }

    private static boolean isCustomizedLegacyValue(SpawnValues values, List<SpawnValues> knownDefaults) {
        for (SpawnValues knownDefault : knownDefaults) {
            if (knownDefault.equals(values)) {
                return false;
            }
        }
        return true;
    }

    private static void applyLegacyOverrides(Map<String, List<SpawnEntry>> defaults, Map<String, SpawnValues> legacyOverrides) {
        for (Map.Entry<String, SpawnValues> override : legacyOverrides.entrySet()) {
            List<SpawnEntry> entries = defaults.get(override.getKey());
            if (entries == null) {
                continue;
            }

            List<SpawnEntry> migratedEntries = entries.stream()
                    .map(entry -> entry.withValues(override.getValue()))
                    .toList();
            defaults.put(override.getKey(), migratedEntries);
        }
    }

    private static Map<String, List<SpawnEntry>> missingDefaults(Map<String, List<SpawnEntry>> loaded) {
        Map<String, List<SpawnEntry>> missing = new LinkedHashMap<>();
        for (Map.Entry<String, List<SpawnEntry>> entry : DEFAULTS.entrySet()) {
            if (!loaded.containsKey(entry.getKey())) {
                missing.put(entry.getKey(), entry.getValue());
            }
        }
        return missing;
    }

    private static void writeConfig(Path configPath, Map<String, List<SpawnEntry>> entries) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, formatConfig(entries, true), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write spawn config at " + configPath, exception);
        }
    }

    private static void appendConfig(Path configPath, Map<String, List<SpawnEntry>> entries) {
        try {
            Files.writeString(configPath, formatConfig(entries, false), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to append spawn config at " + configPath, exception);
        }
    }

    private static String formatConfig(Map<String, List<SpawnEntry>> entries, boolean includeHeader) {
        StringBuilder builder = new StringBuilder();
        if (includeHeader) {
            builder.append("# Critters and Companions - Spawn Configuration\n");
            builder.append("# Each [[entity_name]] block defines one biome spawn entry for that entity.\n");
            builder.append("#\n");
            builder.append("# biome  : A biome ID (e.g. \"minecraft:forest\") or a biome tag prefixed with # (e.g. \"#minecraft:is_forest\")\n");
            builder.append("# weight : Spawn weight relative to other mobs in the same category. Set to 0 to disable.\n");
            builder.append("# min    : Minimum number of entities spawned per group.\n");
            builder.append("# max    : Maximum number of entities spawned per group.\n");
            builder.append("#\n");
            builder.append("# You can freely add, remove, or modify entries. Custom biomes and tags are supported.\n");
            builder.append("# Delete this file to regenerate it with the mod defaults on the next start.\n");
            builder.append('\n');
        } else {
            builder.append('\n');
        }

        for (Map.Entry<String, List<SpawnEntry>> entry : entries.entrySet()) {
            builder.append('#').append(CrittersAndCompanions.MODID).append(':').append(entry.getKey()).append('\n');
            for (SpawnEntry spawnEntry : entry.getValue()) {
                builder.append("[[").append(entry.getKey()).append("]]\n");
                builder.append("biome  = \"").append(spawnEntry.biomeSpec()).append("\"\n");
                builder.append("weight = ").append(spawnEntry.weight()).append('\n');
                builder.append("min    = ").append(spawnEntry.min()).append('\n');
                builder.append("max    = ").append(spawnEntry.max()).append('\n');
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    private static Map<String, List<SpawnEntry>> copyEntries(Map<String, List<SpawnEntry>> source) {
        Map<String, List<SpawnEntry>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<SpawnEntry>> entry : source.entrySet()) {
            copy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return copy;
    }

    private static void defaults(RegistryEntry<? extends EntityType<?>> entity, SpawnEntry... entries) {
        DEFAULTS.put(entity.getKey().identifier().getPath(), List.of(entries));
    }

    private static void legacyFlatDefaults(RegistryEntry<? extends EntityType<?>> entity, SpawnValues... defaults) {
        LEGACY_FLAT_DEFAULTS.put(entity.getKey().identifier().getPath(), List.of(defaults));
    }

    private static SpawnEntry tag(String path, int weight, int min, int max) {
        return new SpawnEntry("#c:" + path, weight, min, max);
    }

    private static SpawnEntry biome(ResourceKey<Biome> biome, int weight, int min, int max) {
        return new SpawnEntry(biome.identifier().toString(), weight, min, max);
    }

    private static Identifier parseIdentifier(String value) {
        int separator = value.indexOf(':');
        if (separator <= 0 || separator == value.length() - 1) {
            throw new IllegalArgumentException("Expected namespaced identifier, got '" + value + "'");
        }
        return Identifier.fromNamespaceAndPath(value.substring(0, separator), value.substring(separator + 1));
    }

    static {
        defaults(CACEntities.LEAF_INSECT,
                tag("is_jungle", 14, 1, 1),
                tag("is_forest", 14, 1, 1));
        defaults(CACEntities.RED_PANDA,
                tag("is_jungle", 8, 1, 2));
        defaults(CACEntities.JUMPING_SPIDER,
                tag("is_jungle", 2, 1, 1),
                tag("is_forest", 2, 1, 1),
                tag("is_lush", 2, 1, 1));
        defaults(CACEntities.FERRET,
                tag("is_forest", 3, 2, 3),
                tag("is_plains", 4, 2, 3));
        defaults(CACEntities.SEA_BUNNY,
                biome(Biomes.OCEAN, 16, 1, 2),
                biome(Biomes.DEEP_OCEAN, 16, 1, 2),
                biome(Biomes.WARM_OCEAN, 32, 1, 4),
                biome(Biomes.LUKEWARM_OCEAN, 16, 1, 4),
                biome(Biomes.DEEP_LUKEWARM_OCEAN, 16, 1, 4));
        defaults(CACEntities.DUMBO_OCTOPUS,
                biome(Biomes.OCEAN, 4, 1, 1),
                biome(Biomes.DEEP_OCEAN, 4, 1, 1),
                biome(Biomes.WARM_OCEAN, 8, 1, 1),
                biome(Biomes.LUKEWARM_OCEAN, 6, 1, 1),
                biome(Biomes.DEEP_LUKEWARM_OCEAN, 6, 1, 1));
        defaults(CACEntities.OTTER,
                biome(Biomes.RIVER, 1, 3, 5));
        defaults(CACEntities.KOI_FISH,
                biome(Biomes.RIVER, 4, 2, 5));
        defaults(CACEntities.DRAGONFLY,
                biome(Biomes.RIVER, 7, 1, 1));
        defaults(CACEntities.SHIMA_ENAGA,
                tag("is_snowy", 3, 2, 3));
        defaults(CACEntities.LADYBUG,
                tag("is_forest", 6, 1, 3),
                tag("is_lush", 6, 1, 3));
        defaults(CACEntities.STAG_BEETLE,
                tag("is_forest", 4, 1, 2),
                tag("is_lush", 4, 1, 2));
        defaults(CACEntities.ROLY_POLY,
                tag("is_forest", 5, 1, 3),
                tag("is_lush", 5, 1, 3));
        defaults(CACEntities.SNAIL,
                tag("is_forest", 5, 1, 2),
                tag("is_lush", 5, 1, 2));
        defaults(CACEntities.STICK_BUG,
                tag("is_forest", 4, 1, 2),
                tag("is_lush", 4, 1, 2));
        defaults(CACEntities.WEEVIL,
                tag("is_forest", 4, 1, 3),
                tag("is_lush", 4, 1, 3));

        legacyFlatDefaults(CACEntities.LEAF_INSECT, new SpawnValues(14, 1, 1));
        legacyFlatDefaults(CACEntities.RED_PANDA, new SpawnValues(8, 1, 2));
        legacyFlatDefaults(CACEntities.JUMPING_SPIDER, new SpawnValues(2, 1, 1), new SpawnValues(6, 1, 1));
        legacyFlatDefaults(CACEntities.FERRET, new SpawnValues(4, 2, 3));
        legacyFlatDefaults(CACEntities.SEA_BUNNY, new SpawnValues(16, 1, 4));
        legacyFlatDefaults(CACEntities.DUMBO_OCTOPUS, new SpawnValues(6, 1, 1));
        legacyFlatDefaults(CACEntities.OTTER, new SpawnValues(1, 3, 5));
        legacyFlatDefaults(CACEntities.KOI_FISH, new SpawnValues(4, 2, 5));
        legacyFlatDefaults(CACEntities.DRAGONFLY, new SpawnValues(7, 1, 1));
        legacyFlatDefaults(CACEntities.SHIMA_ENAGA, new SpawnValues(3, 2, 3));
        legacyFlatDefaults(CACEntities.LADYBUG, new SpawnValues(6, 1, 3), new SpawnValues(10, 1, 2));
        legacyFlatDefaults(CACEntities.ROLY_POLY, new SpawnValues(5, 1, 3), new SpawnValues(8, 1, 3));
        legacyFlatDefaults(CACEntities.SNAIL, new SpawnValues(5, 1, 2), new SpawnValues(8, 1, 3));
        legacyFlatDefaults(CACEntities.STAG_BEETLE, new SpawnValues(4, 1, 2), new SpawnValues(6, 1, 1));
        legacyFlatDefaults(CACEntities.STICK_BUG, new SpawnValues(4, 1, 2), new SpawnValues(8, 1, 2));
        legacyFlatDefaults(CACEntities.WEEVIL, new SpawnValues(4, 1, 3), new SpawnValues(8, 1, 2));

        instance = new CACSpawnConfig(DEFAULTS);
    }

    public record SpawnValues(int weight, int min, int max) {
    }

    public record SpawnEntry(String biomeSpec, int weight, int min, int max) {
        public boolean isTag() {
            return this.biomeSpec.startsWith("#");
        }

        public Identifier biomeLocation() {
            return parseIdentifier(this.isTag() ? this.biomeSpec.substring(1) : this.biomeSpec);
        }

        SpawnEntry withValues(SpawnValues values) {
            return new SpawnEntry(this.biomeSpec, values.weight(), values.min(), values.max());
        }
    }
}
