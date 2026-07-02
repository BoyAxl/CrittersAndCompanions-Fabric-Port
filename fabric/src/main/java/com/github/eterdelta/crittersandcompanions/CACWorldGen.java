package com.github.eterdelta.crittersandcompanions;

import com.github.eterdelta.crittersandcompanions.config.CACSpawnConfig;
import com.github.eterdelta.crittersandcompanions.registry.CACTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CACWorldGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(CACWorldGen.class);

    public static void register() {
        for (var entry : CACSpawnConfig.get().getAllEntries().entrySet()) {
            for (CACSpawnConfig.SpawnEntry spawnEntry : entry.getValue()) {
                addSpawnEntry(entry.getKey(), spawnEntry);
            }
        }

        addFeatureTo(CACTags.SILK_COCOON_SPAWNS, "silk_cocoon");
        addFeatureTo(CACTags.SILK_COCOON_LUSH_SPAWNS, "silk_cocoon_lush");
        addFeatureTo(CACTags.SILK_COCOON_SPAWNS, "hanging_silk_cocoon");
        addFeatureTo(CACTags.SILK_COCOON_LUSH_SPAWNS, "hanging_silk_cocoon_lush");
    }

    private static void addFeatureTo(TagKey<Biome> biome, String feature) {
        BiomeModifications.addFeature(
                it -> it.hasTag(biome),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                ResourceKey.create(Registries.PLACED_FEATURE, CrittersAndCompanions.createId(feature))
        );
    }

    private static void addSpawnEntry(String entityName, CACSpawnConfig.SpawnEntry spawnEntry) {
        if (spawnEntry.weight() <= 0) {
            return;
        }

        EntityType<?> type = CACSpawnConfig.resolveEntityType(entityName);
        if (type == null) {
            LOGGER.warn("Skipping spawn config for unknown entity '{}'", entityName);
            return;
        }

        if (spawnEntry.isTag()) {
            TagKey<Biome> tag = TagKey.create(Registries.BIOME, spawnEntry.biomeLocation());
            BiomeModifications.addSpawn(it -> it.hasTag(tag), type.getCategory(), type, spawnEntry.weight(), spawnEntry.min(), spawnEntry.max());
            return;
        }

        ResourceKey<Biome> biome = ResourceKey.create(Registries.BIOME, spawnEntry.biomeLocation());
        BiomeModifications.addSpawn(it -> it.getBiomeKey().equals(biome), type.getCategory(), type, spawnEntry.weight(), spawnEntry.min(), spawnEntry.max());
    }

}
