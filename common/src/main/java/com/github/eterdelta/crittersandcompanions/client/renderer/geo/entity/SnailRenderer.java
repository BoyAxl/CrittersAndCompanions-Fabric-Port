package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.SnailModel;
import com.github.eterdelta.crittersandcompanions.entity.SnailEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

public class SnailRenderer extends GeoEntityRenderer<SnailEntity, EntityRenderState> {
    private static final DataTicket<Boolean> CLIMBING = DataTickets.create("cac_snail_climbing", Boolean.class);
    private static final DataTicket<Boolean> ON_GROUND = DataTickets.create("cac_snail_on_ground", Boolean.class);

    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context, new SnailModel());
    }

    @Override
    public void extractRenderState(SnailEntity animatable, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(animatable, renderState, partialTick);

        boolean climbing = animatable.isClimbing();
        renderState.addGeckolibData(CLIMBING, climbing);
        renderState.addGeckolibData(ON_GROUND, animatable.onGround());

        if (climbing) {
            Direction wallFace = animatable.getClimbingWallFace();
            if (wallFace != null) {
                renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, wallFace.toYRot());
            }
        }
    }

    @Override
    public void preRenderPass(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        boolean climbing = renderPassInfo.getOrDefaultGeckolibData(CLIMBING, false);
        boolean onGround = renderPassInfo.getOrDefaultGeckolibData(ON_GROUND, false);
        boolean baby = renderPassInfo.renderState() instanceof LivingEntityRenderState livingState && livingState.isBaby;
        float climbYOffset = baby ? 4.0F : 6.0F;

        renderPassInfo.addBoneUpdater((pass, bones) -> bones.ifPresent("main", bone -> {
            if (climbing && !onGround) {
                bone.setRotX(Mth.HALF_PI).setTranslateY(climbYOffset);
            } else {
                bone.setRotX(0.0F).setTranslateY(0.0F);
            }
        }));
    }
}
