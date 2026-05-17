package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.OtterModel;
import com.github.eterdelta.crittersandcompanions.entity.OtterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;

public class OtterRenderer extends GeoEntityRenderer<OtterEntity, EntityRenderState> {
    public OtterRenderer(EntityRendererProvider.Context context) {
        super(context, new OtterModel());
    }
}
