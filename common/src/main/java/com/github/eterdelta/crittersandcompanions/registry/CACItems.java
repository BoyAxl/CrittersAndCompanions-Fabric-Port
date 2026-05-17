package com.github.eterdelta.crittersandcompanions.registry;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.item.DragonflyArmorItem;
import com.github.eterdelta.crittersandcompanions.item.GrapplingHookItem;
import com.github.eterdelta.crittersandcompanions.item.PearlNecklaceItem;
import com.github.eterdelta.crittersandcompanions.item.SilkLeashItem;
import com.github.eterdelta.crittersandcompanions.platform.RegistryEntry;
import com.github.eterdelta.crittersandcompanions.platform.RegistryHelper;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluids;

public class CACItems {
    private static final RegistryHelper<Item> ITEMS = Services.PLATFORM.createRegistryHelper(Registries.ITEM, CrittersAndCompanions.MODID);

    public static final RegistryEntry<Item> CLAM = ITEMS.register("clam", () -> new Item(properties("clam")));
    public static final RegistryEntry<Item> DRAGONFLY_WING = ITEMS.register("dragonfly_wing", () -> new Item(properties("dragonfly_wing")));
    public static final RegistryEntry<Item> KOI_FISH = ITEMS.register("koi_fish", () -> new Item(properties("koi_fish").food(Foods.TROPICAL_FISH)));
    public static final RegistryEntry<Item> PEARL = ITEMS.register("pearl", () -> new Item(properties("pearl")));
    public static final RegistryEntry<Item> SILK = ITEMS.register("silk", () -> new Item(properties("silk")));

    public static final RegistryEntry<Item> SEA_BUNNY_SLIME_BOTTLE = ITEMS.register("sea_bunny_slime_bottle", () -> new Item(properties("sea_bunny_slime_bottle").craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryEntry<Item> SEA_BUNNY_SLIME_BLOCK = ITEMS.register("sea_bunny_slime_block", () -> new BlockItem(CACBlocks.SEA_BUNNY_SLIME_BLOCK.get(), blockProperties("sea_bunny_slime_block")));

    public static final RegistryEntry<Item> SILK_LEAD = ITEMS.register("silk_lead", () -> new SilkLeashItem(properties("silk_lead")));
    public static final RegistryEntry<Item> GRAPPLING_HOOK = ITEMS.register("grappling_hook", () -> new GrapplingHookItem(properties("grappling_hook").stacksTo(1)));

    public static final RegistryEntry<Item> PEARL_NECKLACE_1 = ITEMS.register("pearl_necklace_1", () -> new PearlNecklaceItem(properties("pearl_necklace_1").stacksTo(1), 1));
    public static final RegistryEntry<Item> PEARL_NECKLACE_2 = ITEMS.register("pearl_necklace_2", () -> new PearlNecklaceItem(properties("pearl_necklace_2").stacksTo(1), 2));
    public static final RegistryEntry<Item> PEARL_NECKLACE_3 = ITEMS.register("pearl_necklace_3", () -> new PearlNecklaceItem(properties("pearl_necklace_3").stacksTo(1), 3));

    public static final RegistryEntry<Item> DUMBO_OCTOPUS_BUCKET = ITEMS.register("dumbo_octopus_bucket", () -> new MobBucketItem(CACEntities.DUMBO_OCTOPUS.get(), Fluids.WATER,  SoundEvents.BUCKET_EMPTY_FISH, properties("dumbo_octopus_bucket").stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final RegistryEntry<Item> KOI_FISH_BUCKET = ITEMS.register("koi_fish_bucket", () -> new MobBucketItem(CACEntities.KOI_FISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties("koi_fish_bucket").stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final RegistryEntry<Item> SEA_BUNNY_BUCKET = ITEMS.register("sea_bunny_bucket", () -> new MobBucketItem(CACEntities.SEA_BUNNY.get(), Fluids.WATER,SoundEvents.BUCKET_EMPTY_FISH, properties("sea_bunny_bucket").stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));

    public static final RegistryEntry<Item> DIAMOND_DRAGONFLY_ARMOR = ITEMS.register("diamond_dragonfly_armor", () -> new DragonflyArmorItem(ArmorMaterials.DIAMOND, "diamond", properties("diamond_dragonfly_armor").stacksTo(1)));
    public static final RegistryEntry<Item> GOLD_DRAGONFLY_ARMOR = ITEMS.register("gold_dragonfly_armor", () -> new DragonflyArmorItem(ArmorMaterials.GOLD, "gold", properties("gold_dragonfly_armor").stacksTo(1)));
    public static final RegistryEntry<Item> IRON_DRAGONFLY_ARMOR = ITEMS.register("iron_dragonfly_armor", () -> new DragonflyArmorItem(ArmorMaterials.IRON, "iron", properties("iron_dragonfly_armor").stacksTo(1)));

    public static final RegistryEntry<Item> DRAGONFLY_SPAWN_EGG = ITEMS.register("dragonfly_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.DRAGONFLY, 0x08EECF, 0xD3FF96, properties("dragonfly_spawn_egg")));
    public static final RegistryEntry<Item> FERRET_SPAWN_EGG = ITEMS.register("ferret_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.FERRET, 0xC5AC88, 0x37212D, properties("ferret_spawn_egg")));
    public static final RegistryEntry<Item> DUMBO_OCTOPUS_SPAWN_EGG = ITEMS.register("dumbo_octopus_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.DUMBO_OCTOPUS, 0xFCDC4C, 0x162630, properties("dumbo_octopus_spawn_egg")));
    public static final RegistryEntry<Item> JUMPING_SPIDER_SPAWN_EGG = ITEMS.register("jumping_spider_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.JUMPING_SPIDER, 0x34191E, 0x865F33, properties("jumping_spider_spawn_egg")));
    public static final RegistryEntry<Item> KOI_FISH_SPAWN_EGG = ITEMS.register("koi_fish_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.KOI_FISH, 0xF3ECED, 0xFB5321, properties("koi_fish_spawn_egg")));
    public static final RegistryEntry<Item> LEAF_INSECT_SPAWN_EGG = ITEMS.register("leaf_insect_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.LEAF_INSECT, 0xDAD475, 0x3C6C34, properties("leaf_insect_spawn_egg")));
    public static final RegistryEntry<Item> OTTER_SPAWN_EGG = ITEMS.register("otter_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.OTTER, 0x352C34, 0xB49494, properties("otter_spawn_egg")));
    public static final RegistryEntry<Item> RED_PANDA_SPAWN_EGG = ITEMS.register("red_panda_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.RED_PANDA, 0xF4943C, 0x13131B, properties("red_panda_spawn_egg")));
    public static final RegistryEntry<Item> SEA_BUNNY_SPAWN_EGG = ITEMS.register("sea_bunny_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.SEA_BUNNY, 0xF4ECE4, 0x453337, properties("sea_bunny_spawn_egg")));
    public static final RegistryEntry<Item> SHIMA_ENAGA_SPAWN_EGG = ITEMS.register("shima_enaga_spawn_egg", () -> Services.PLATFORM.createSpawnEgg(CACEntities.SHIMA_ENAGA, 0xFCFCEC, 0x5C3C34, properties("shima_enaga_spawn_egg")));

    public static final RegistryEntry<Item> SILK_COCOON = ITEMS.register("silk_cocoon", () -> new BlockItem(CACBlocks.SILK_COCOON.get(), blockProperties("silk_cocoon")));

    private static Item.Properties properties(String id) {
        return new Item.Properties().setId(key(id));
    }

    private static Item.Properties blockProperties(String id) {
        return properties(id).useBlockDescriptionPrefix();
    }

    private static ResourceKey<Item> key(String id) {
        return ResourceKey.create(Registries.ITEM, CrittersAndCompanions.createId(id));
    }

    public static void init() {
        // Load the class
    }

}
