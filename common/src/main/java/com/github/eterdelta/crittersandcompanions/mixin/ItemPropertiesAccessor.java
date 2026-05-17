package com.github.eterdelta.crittersandcompanions.mixin;

import java.util.Map;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemProperties.class)
public interface ItemPropertiesAccessor {

    @Accessor
    static Map<Item, Map<Identifier, ItemPropertyFunction>> getPROPERTIES() {
        throw new RuntimeException("Mixin failed");
    }

}
