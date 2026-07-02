package com.github.eterdelta.crittersandcompanions.registry;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class CACArmorMaterials {
    public static final TagKey<Item> ACORN_REPAIR_MATERIALS = TagKey.create(Registries.ITEM, CrittersAndCompanions.createId("acorn_repair_materials"));
    private static final ResourceKey<EquipmentAsset> ACORN_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, CrittersAndCompanions.createId("acorn"));

    public static final ArmorMaterial ACORN = new ArmorMaterial(
            15,
            Map.of(
                    ArmorType.HELMET, 1,
                    ArmorType.CHESTPLATE, 0,
                    ArmorType.LEGGINGS, 0,
                    ArmorType.BOOTS, 0,
                    ArmorType.BODY, 0
            ),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            ACORN_REPAIR_MATERIALS,
            ACORN_ASSET
    );

    private CACArmorMaterials() {
    }
}
