package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.OtterModel;
import com.github.eterdelta.crittersandcompanions.entity.OtterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.GeoEntityRenderer;

public class OtterRenderer extends GeoEntityRenderer<OtterEntity, EntityRenderState> {
    public OtterRenderer(EntityRendererProvider.Context context) {
        super(context, new OtterModel());
        this.withRenderLayer(new OtterHeldItemLayer(context, this));
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<EntityRenderState> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);

        if (renderPassInfo.renderState() instanceof LivingEntityRenderState livingState && livingState.isBaby) {
            float ageScale = livingState.ageScale;
            renderPassInfo.poseStack().scale(ageScale, ageScale, ageScale);
        }
    }
}
