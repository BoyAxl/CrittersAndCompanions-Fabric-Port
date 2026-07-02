package com.github.eterdelta.crittersandcompanions.item;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.AcornArmorRenderer;
import com.github.eterdelta.crittersandcompanions.registry.CACArmorMaterials;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;

public class AcornHatItem extends Item implements GeoItem {
    private static final int DURABILITY = 55;
    private static final double MAX_HEALTH_BONUS = 4.0D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AcornHatItem(Properties properties) {
        super(properties
                .humanoidArmor(CACArmorMaterials.ACORN, ArmorType.HELMET)
                .durability(DURABILITY)
                .attributes(createAttributes()));
        GeoItem.registerSyncedAnimatable(this);
    }

    private static ItemAttributeModifiers createAttributes() {
        return CACArmorMaterials.ACORN.createAttributes(ArmorType.HELMET)
                .withModifierAdded(
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(CrittersAndCompanions.createId("acorn_hat_health"), MAX_HEALTH_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private AcornArmorRenderer renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                if (this.renderer == null) {
                    this.renderer = new AcornArmorRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
