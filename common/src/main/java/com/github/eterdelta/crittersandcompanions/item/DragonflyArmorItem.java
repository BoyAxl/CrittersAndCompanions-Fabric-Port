package com.github.eterdelta.crittersandcompanions.item;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class DragonflyArmorItem extends Item {
    private final ResourceLocation texture;
    private final Supplier<ItemAttributeModifiers> attributes;

    public DragonflyArmorItem(Holder<ArmorMaterial> material, String tierName, Item.Properties properties) {
        this(material, CrittersAndCompanions.createId("textures/entity/dragonfly_armor_" + tierName + ".png"), properties);
    }

    public DragonflyArmorItem(Holder<ArmorMaterial> material, ResourceLocation tierName, Item.Properties properties) {
        super(properties);
        this.texture = tierName;
        this.attributes = Suppliers.memoize(() ->
            ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(CrittersAndCompanions.createId("armor"), material.value().getDefense(ArmorItem.Type.CHESTPLATE), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)
                    .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(CrittersAndCompanions.createId("toughness"), material.value().toughness(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)
                    .build()
        );
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return attributes.get();
    }

}
