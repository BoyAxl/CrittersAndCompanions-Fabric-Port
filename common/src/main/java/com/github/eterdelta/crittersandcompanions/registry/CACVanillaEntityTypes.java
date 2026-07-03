package com.github.eterdelta.crittersandcompanions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public final class CACVanillaEntityTypes {
    public static final EntityType<? extends Mob> BEE = mob("bee");
    public static final EntityType<? extends Mob> CAVE_SPIDER = mob("cave_spider");
    public static final EntityType<?> DROWNED = get("drowned");
    public static final EntityType<? extends Mob> ENDERMAN = mob("enderman");
    public static final EntityType<?> GUARDIAN = get("guardian");
    public static final EntityType<? extends Mob> IRON_GOLEM = mob("iron_golem");
    public static final EntityType<? extends Mob> LLAMA = mob("llama");
    public static final EntityType<? extends Mob> POLAR_BEAR = mob("polar_bear");
    public static final EntityType<? extends Mob> SPIDER = mob("spider");
    public static final EntityType<? extends Mob> VEX = mob("vex");
    public static final EntityType<? extends Mob> WOLF = mob("wolf");
    public static final EntityType<? extends Mob> ZOMBIFIED_PIGLIN = mob("zombified_piglin");

    private CACVanillaEntityTypes() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> EntityType<T> mob(String path) {
        return (EntityType<T>) get(path);
    }

    private static EntityType<?> get(String path) {
        return BuiltInRegistries.ENTITY_TYPE.getValueOrThrow(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.withDefaultNamespace(path)));
    }
}
