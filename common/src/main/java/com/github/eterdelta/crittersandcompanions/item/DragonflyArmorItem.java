package com.github.eterdelta.crittersandcompanions.item;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class DragonflyArmorItem extends Item {
    private final Identifier texture;

    public DragonflyArmorItem(ArmorMaterial material, String tierName, Item.Properties properties) {
        this(material, CrittersAndCompanions.createId("textures/entity/dragonfly_armor_" + tierName + ".png"), properties);
    }

    public DragonflyArmorItem(ArmorMaterial material, Identifier tierName, Item.Properties properties) {
        super(properties.attributes(createAttributes(material)));
        this.texture = tierName;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    private static ItemAttributeModifiers createAttributes(ArmorMaterial material) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(CrittersAndCompanions.createId("armor"), material.defense().getOrDefault(ArmorType.CHESTPLATE, 0), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(CrittersAndCompanions.createId("toughness"), material.toughness(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY)
                .build();
    }

}
