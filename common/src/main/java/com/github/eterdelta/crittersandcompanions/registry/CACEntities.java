package com.github.eterdelta.crittersandcompanions.registry;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.DragonflyEntity;
import com.github.eterdelta.crittersandcompanions.entity.DumboOctopusEntity;
import com.github.eterdelta.crittersandcompanions.entity.FerretEntity;
import com.github.eterdelta.crittersandcompanions.entity.GrapplingHookEntity;
import com.github.eterdelta.crittersandcompanions.entity.JumpingSpiderEntity;
import com.github.eterdelta.crittersandcompanions.entity.KoiFishEntity;
import com.github.eterdelta.crittersandcompanions.entity.LadybugEntity;
import com.github.eterdelta.crittersandcompanions.entity.LeafInsectEntity;
import com.github.eterdelta.crittersandcompanions.entity.OtterEntity;
import com.github.eterdelta.crittersandcompanions.entity.RedPandaEntity;
import com.github.eterdelta.crittersandcompanions.entity.RolyPolyEntity;
import com.github.eterdelta.crittersandcompanions.entity.SeaBunnyEntity;
import com.github.eterdelta.crittersandcompanions.entity.ShimaEnagaEntity;
import com.github.eterdelta.crittersandcompanions.entity.SnailEntity;
import com.github.eterdelta.crittersandcompanions.entity.StagBeetleEntity;
import com.github.eterdelta.crittersandcompanions.entity.StickBugEntity;
import com.github.eterdelta.crittersandcompanions.entity.WeevilEntity;
import com.github.eterdelta.crittersandcompanions.entity.projectiles.MudBallProjectile;
import com.github.eterdelta.crittersandcompanions.platform.RegistryEntry;
import com.github.eterdelta.crittersandcompanions.platform.RegistryHelper;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class CACEntities {
    private static final RegistryHelper<EntityType<?>> ENTITIES = Services.PLATFORM.createRegistryHelper(Registries.ENTITY_TYPE, CrittersAndCompanions.MODID);

    public static final RegistryEntry<EntityType<DragonflyEntity>> DRAGONFLY = ENTITIES.register("dragonfly", () -> EntityType.Builder.of(DragonflyEntity::new, MobCategory.AMBIENT).sized(0.9F, 0.4F).build(key("dragonfly")));
    public static final RegistryEntry<EntityType<DumboOctopusEntity>> DUMBO_OCTOPUS = ENTITIES.register("dumbo_octopus", () -> EntityType.Builder.of(DumboOctopusEntity::new, MobCategory.WATER_AMBIENT).sized(0.4F, 0.4F).build(key("dumbo_octopus")));
    public static final RegistryEntry<EntityType<FerretEntity>> FERRET = ENTITIES.register("ferret", () -> EntityType.Builder.of(FerretEntity::new, MobCategory.CREATURE).sized(0.8F, 0.7F).build(key("ferret")));
    public static final RegistryEntry<EntityType<GrapplingHookEntity>> GRAPPLING_HOOK = ENTITIES.register("grappling_hook", () -> EntityType.Builder.<GrapplingHookEntity>of(GrapplingHookEntity::new, MobCategory.MISC).sized(0.2F, 0.2F).noSave().noSummon().build(key("grappling_hook")));
    public static final RegistryEntry<EntityType<JumpingSpiderEntity>> JUMPING_SPIDER = ENTITIES.register("jumping_spider", () -> EntityType.Builder.of(JumpingSpiderEntity::new, MobCategory.CREATURE).sized(0.5F, 0.4F).build(key("jumping_spider")));
    public static final RegistryEntry<EntityType<KoiFishEntity>> KOI_FISH = ENTITIES.register("koi_fish", () -> EntityType.Builder.of(KoiFishEntity::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.3F).build(key("koi_fish")));
    public static final RegistryEntry<EntityType<LeafInsectEntity>> LEAF_INSECT = ENTITIES.register("leaf_insect", () -> EntityType.Builder.of(LeafInsectEntity::new, MobCategory.AMBIENT).sized(0.4F, 0.3F).build(key("leaf_insect")));
    public static final RegistryEntry<EntityType<LadybugEntity>> LADYBUG = ENTITIES.register("ladybug", () -> EntityType.Builder.of(LadybugEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.375F).build(key("ladybug")));
    public static final RegistryEntry<EntityType<OtterEntity>> OTTER = ENTITIES.register("otter", () -> EntityType.Builder.of(OtterEntity::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.6F).build(key("otter")));
    public static final RegistryEntry<EntityType<RedPandaEntity>> RED_PANDA = ENTITIES.register("red_panda", () -> EntityType.Builder.of(RedPandaEntity::new, MobCategory.CREATURE).sized(0.75F, 0.65F).build(key("red_panda")));
    public static final RegistryEntry<EntityType<RolyPolyEntity>> ROLY_POLY = ENTITIES.register("roly_poly", () -> EntityType.Builder.of(RolyPolyEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.4F).build(key("roly_poly")));
    public static final RegistryEntry<EntityType<SeaBunnyEntity>> SEA_BUNNY = ENTITIES.register("sea_bunny", () -> EntityType.Builder.of(SeaBunnyEntity::new, MobCategory.WATER_AMBIENT).sized(0.45F, 0.3F).build(key("sea_bunny")));
    public static final RegistryEntry<EntityType<ShimaEnagaEntity>> SHIMA_ENAGA = ENTITIES.register("shima_enaga", () -> EntityType.Builder.of(ShimaEnagaEntity::new, MobCategory.CREATURE).sized(0.5F, 0.6F).build(key("shima_enaga")));
    public static final RegistryEntry<EntityType<SnailEntity>> SNAIL = ENTITIES.register("snail", () -> EntityType.Builder.of(SnailEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.575F).build(key("snail")));
    public static final RegistryEntry<EntityType<StagBeetleEntity>> STAG_BEETLE = ENTITIES.register("stag_beetle", () -> EntityType.Builder.of(StagBeetleEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.45F).build(key("stag_beetle")));
    public static final RegistryEntry<EntityType<StickBugEntity>> STICK_BUG = ENTITIES.register("stick_bug", () -> EntityType.Builder.of(StickBugEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.385F).build(key("stick_bug")));
    public static final RegistryEntry<EntityType<WeevilEntity>> WEEVIL = ENTITIES.register("weevil", () -> EntityType.Builder.of(WeevilEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.5F).build(key("weevil")));
    public static final RegistryEntry<EntityType<MudBallProjectile>> MUD_BALL = ENTITIES.register("mud_ball", () -> EntityType.Builder.<MudBallProjectile>of(MudBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("mud_ball")));

    private static ResourceKey<EntityType<?>> key(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, CrittersAndCompanions.createId(id));
    }

    public static void init() {
        // Load the class
    }
}
