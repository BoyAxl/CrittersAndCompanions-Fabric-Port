package com.github.eterdelta.crittersandcompanions;

import com.github.eterdelta.crittersandcompanions.config.FabricCommonConfig;
import com.github.eterdelta.crittersandcompanions.platform.RegistryEntry;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACTags;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

public class CACWorldGen {
    private static final TagKey<Biome> COMMON_IS_FOREST = commonBiomeTag("is_forest");
    private static final TagKey<Biome> COMMON_IS_JUNGLE = commonBiomeTag("is_jungle");
    private static final TagKey<Biome> COMMON_IS_LUSH = commonBiomeTag("is_lush");

    public static void register() {
        addSpawnsTo(BiomeTags.IS_JUNGLE, CACEntities.LEAF_INSECT);
        addSpawnsTo(BiomeTags.IS_JUNGLE, CACEntities.RED_PANDA);
        addSpawnsTo(COMMON_IS_JUNGLE, CACEntities.JUMPING_SPIDER);
        addSpawnsTo(BiomeTags.IS_FOREST, CACEntities.LEAF_INSECT);
        addSpawnsTo(BiomeTags.IS_FOREST, CACEntities.FERRET);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.JUMPING_SPIDER);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.LADYBUG);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.STAG_BEETLE);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.ROLY_POLY);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.SNAIL);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.STICK_BUG);
        addSpawnsTo(COMMON_IS_FOREST, CACEntities.WEEVIL);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.JUMPING_SPIDER);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.LADYBUG);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.STAG_BEETLE);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.ROLY_POLY);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.SNAIL);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.STICK_BUG);
        addSpawnsTo(COMMON_IS_LUSH, CACEntities.WEEVIL);
        addSpawnsTo(Biomes.OCEAN, CACEntities.SEA_BUNNY);
        addSpawnsTo(Biomes.OCEAN, CACEntities.DUMBO_OCTOPUS);
        addSpawnsTo(Biomes.DEEP_OCEAN, CACEntities.SEA_BUNNY);
        addSpawnsTo(Biomes.DEEP_OCEAN, CACEntities.DUMBO_OCTOPUS);
        addSpawnsTo(BiomeTags.HAS_VILLAGE_PLAINS, CACEntities.FERRET);
        addSpawnsTo(Biomes.SUNFLOWER_PLAINS, CACEntities.FERRET);
        addSpawnsTo(BiomeTags.IS_RIVER, CACEntities.OTTER);
        addSpawnsTo(BiomeTags.IS_RIVER, CACEntities.KOI_FISH);
        addSpawnsTo(BiomeTags.IS_RIVER, CACEntities.DRAGONFLY);
        addSpawnsTo(Biomes.SNOWY_PLAINS, CACEntities.SHIMA_ENAGA);
        addSpawnsTo(Biomes.WARM_OCEAN, CACEntities.SEA_BUNNY);
        addSpawnsTo(Biomes.WARM_OCEAN, CACEntities.DUMBO_OCTOPUS);
        addSpawnsTo(Biomes.LUKEWARM_OCEAN, CACEntities.SEA_BUNNY);
        addSpawnsTo(Biomes.LUKEWARM_OCEAN, CACEntities.DUMBO_OCTOPUS);
        addSpawnsTo(Biomes.DEEP_LUKEWARM_OCEAN, CACEntities.SEA_BUNNY);
        addSpawnsTo(Biomes.DEEP_LUKEWARM_OCEAN, CACEntities.DUMBO_OCTOPUS);

        addFeatureTo(CACTags.SILK_COCOON_SPAWNS, "silk_cocoon");
        addFeatureTo(CACTags.SILK_COCOON_LUSH_SPAWNS, "silk_cocoon_lush");
        addFeatureTo(CACTags.SILK_COCOON_SPAWNS, "hanging_silk_cocoon");
        addFeatureTo(CACTags.SILK_COCOON_LUSH_SPAWNS, "hanging_silk_cocoon_lush");
    }

    private static TagKey<Biome> commonBiomeTag(String path) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", path));
    }

    private static void addFeatureTo(TagKey<Biome> biome, String feature) {
        BiomeModifications.addFeature(
                it -> it.hasTag(biome),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                ResourceKey.create(Registries.PLACED_FEATURE, CrittersAndCompanions.createId(feature))
        );
    }

    private static void addSpawnsTo(ResourceKey<Biome> biome, RegistryEntry<? extends EntityType<?>> entry) {
        addSpawnsTo(it -> it.getBiomeKey().equals(biome), entry);
    }

    private static void addSpawnsTo(TagKey<Biome> biome, RegistryEntry<? extends EntityType<?>> entry) {
        addSpawnsTo(it -> it.hasTag(biome), entry);
    }

    private static void addSpawnsTo(Predicate<BiomeSelectionContext> biome, RegistryEntry<? extends EntityType<?>> entry) {
        var type = entry.get();
        var config = (FabricCommonConfig) Services.CONFIGS.common();
        var values = config.getSpawnValues(entry.getKey());
        if (values.weight() <= 0) return;
        BiomeModifications.addSpawn(biome, type.getCategory(), type, values.weight(), values.min(), values.max());
    }

    public record SpawnValues(int weight, int min, int max) {
    }

}
