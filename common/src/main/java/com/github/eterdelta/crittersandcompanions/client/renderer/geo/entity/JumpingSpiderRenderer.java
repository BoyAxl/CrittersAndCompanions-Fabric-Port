package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.JumpingSpiderModel;
import com.github.eterdelta.crittersandcompanions.entity.JumpingSpiderEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

public class JumpingSpiderRenderer extends GeoEntityRenderer<JumpingSpiderEntity, EntityRenderState> {
    public JumpingSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new JumpingSpiderModel());
    }

    @Override
    public void preRenderPass(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        if (!(renderPassInfo.renderState() instanceof LivingEntityRenderState livingState)) {
            return;
        }

        renderPassInfo.addBoneUpdater((pass, bones) -> bones.ifPresent("head_rotation", bone -> bone
                .setRotX(livingState.xRot * Mth.DEG_TO_RAD)
                .setRotY(livingState.yRot * Mth.DEG_TO_RAD)));
    }
}
