package com.github.eterdelta.crittersandcompanions.config;

import com.github.eterdelta.crittersandcompanions.CACWorldGen;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class FabricCommonConfig extends CACCommonConfig {

    private final Map<ResourceKey<? extends EntityType<?>>, SpawnConfigValues> spawnValues = new HashMap<>();

    private void registerSpawns(ModConfigSpec.Builder builder, ResourceKey<? extends EntityType<?>> key, CACWorldGen.SpawnValues defaultValues) {
        this.registerSpawns(builder, key, defaultValues, defaultValues);
    }

    private void registerSpawns(ModConfigSpec.Builder builder, ResourceKey<? extends EntityType<?>> key, CACWorldGen.SpawnValues defaultValues, CACWorldGen.SpawnValues legacyDefaultValues) {
        builder.push(key.identifier().getPath());

        IntValue weight = builder.defineInRange("weight", defaultValues.weight(), 0, 128);
        IntValue min = builder.defineInRange("min", defaultValues.min(), 1, 128);
        IntValue max = builder.defineInRange("max", defaultValues.max(), 1, 128);

        spawnValues.put(key, new SpawnConfigValues(weight, min, max, defaultValues, legacyDefaultValues));

        builder.pop();
    }

    public CACWorldGen.SpawnValues getSpawnValues(ResourceKey<? extends EntityType<?>> key) {
        return Objects.requireNonNull(this.spawnValues.get(key), () -> "Missing spawn config for " + key.identifier()).get();
    }

    public void migrateLegacySpawnDefaults() {
        boolean changed = false;
        for (SpawnConfigValues values : this.spawnValues.values()) {
            changed |= values.migrateLegacyDefaults();
        }
        if (changed) {
            this.spawnValues.values().stream().findFirst().ifPresent(SpawnConfigValues::save);
        }
    }

    public FabricCommonConfig(ModConfigSpec.Builder builder) {
        super(builder);

        builder.push("spawn_rates");

        registerSpawns(builder, CACEntities.LEAF_INSECT.getKey(), new CACWorldGen.SpawnValues(14, 1, 1));
        registerSpawns(builder, CACEntities.RED_PANDA.getKey(), new CACWorldGen.SpawnValues(8, 1, 2));
        registerSpawns(builder, CACEntities.JUMPING_SPIDER.getKey(), new CACWorldGen.SpawnValues(2, 1, 1), new CACWorldGen.SpawnValues(6, 1, 1));
        registerSpawns(builder, CACEntities.FERRET.getKey(), new CACWorldGen.SpawnValues(4, 2, 3));
        registerSpawns(builder, CACEntities.SEA_BUNNY.getKey(), new CACWorldGen.SpawnValues(16, 1, 4));
        registerSpawns(builder, CACEntities.DUMBO_OCTOPUS.getKey(), new CACWorldGen.SpawnValues(6, 1, 1));
        registerSpawns(builder, CACEntities.OTTER.getKey(), new CACWorldGen.SpawnValues(1, 3, 5));
        registerSpawns(builder, CACEntities.KOI_FISH.getKey(), new CACWorldGen.SpawnValues(4, 2, 5));
        registerSpawns(builder, CACEntities.DRAGONFLY.getKey(), new CACWorldGen.SpawnValues(7, 1, 1));
        registerSpawns(builder, CACEntities.SHIMA_ENAGA.getKey(), new CACWorldGen.SpawnValues(3, 2, 3));
        registerSpawns(builder, CACEntities.LADYBUG.getKey(), new CACWorldGen.SpawnValues(6, 1, 3), new CACWorldGen.SpawnValues(10, 1, 2));
        registerSpawns(builder, CACEntities.ROLY_POLY.getKey(), new CACWorldGen.SpawnValues(5, 1, 3), new CACWorldGen.SpawnValues(8, 1, 3));
        registerSpawns(builder, CACEntities.SNAIL.getKey(), new CACWorldGen.SpawnValues(5, 1, 2), new CACWorldGen.SpawnValues(8, 1, 3));
        registerSpawns(builder, CACEntities.STAG_BEETLE.getKey(), new CACWorldGen.SpawnValues(4, 1, 2), new CACWorldGen.SpawnValues(6, 1, 1));
        registerSpawns(builder, CACEntities.STICK_BUG.getKey(), new CACWorldGen.SpawnValues(4, 1, 2), new CACWorldGen.SpawnValues(8, 1, 2));
        registerSpawns(builder, CACEntities.WEEVIL.getKey(), new CACWorldGen.SpawnValues(4, 1, 3), new CACWorldGen.SpawnValues(8, 1, 2));

        builder.pop();
    }

    private record SpawnConfigValues(IntValue weight, IntValue min, IntValue max, CACWorldGen.SpawnValues currentDefaults, CACWorldGen.SpawnValues legacyDefaults) implements Supplier<CACWorldGen.SpawnValues> {
        @Override
        public CACWorldGen.SpawnValues get() {
            return new CACWorldGen.SpawnValues(this.weight.getAsInt(), this.min.getAsInt(), this.max.getAsInt());
        }

        boolean migrateLegacyDefaults() {
            if (!this.get().equals(this.legacyDefaults) || this.currentDefaults.equals(this.legacyDefaults)) {
                return false;
            }
            this.weight.set(this.currentDefaults.weight());
            this.min.set(this.currentDefaults.min());
            this.max.set(this.currentDefaults.max());
            return true;
        }

        void save() {
            this.weight.save();
        }
    }

}
