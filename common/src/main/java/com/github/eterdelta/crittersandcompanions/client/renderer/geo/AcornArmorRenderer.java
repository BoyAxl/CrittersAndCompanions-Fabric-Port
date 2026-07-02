package com.github.eterdelta.crittersandcompanions.client.renderer.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.item.AcornHatItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;

public class AcornArmorRenderer extends GeoArmorRenderer<AcornHatItem, HumanoidRenderState> {
    public AcornArmorRenderer() {
        super(new DefaultedItemGeoModel<>(CrittersAndCompanions.createId("armor/acorn")));
    }
}
