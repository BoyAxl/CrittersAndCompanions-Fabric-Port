package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.RolyPolyModel;
import com.github.eterdelta.crittersandcompanions.entity.RolyPolyEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

public class RolyPolyRenderer extends GeoEntityRenderer<RolyPolyEntity, EntityRenderState> {
    public static final DataTicket<Boolean> HAS_CHEST = DataTickets.create("cac_roly_poly_has_chest", Boolean.class);
    private static final DataTicket<Boolean> DANCE_WALKING_LEGS = DataTickets.create("cac_roly_poly_dance_walking_legs", Boolean.class);
    private static final ControllerState[] EMPTY_CONTROLLER_STATES = new ControllerState[0];

    public RolyPolyRenderer(EntityRendererProvider.Context context) {
        super(context, new RolyPolyModel());
        this.withRenderLayer(new RolyPolyChestLayer(this));
    }

    @Override
    public void extractRenderState(RolyPolyEntity animatable, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(animatable, renderState, partialTick);
        renderState.addGeckolibData(HAS_CHEST, animatable.hasChest());
        renderState.addGeckolibData(DANCE_WALKING_LEGS, animatable.isDancing() && "dance_2".equals(animatable.getDanceAnimationName()));
    }

    @Override
    public void preRenderPass(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        boolean walkingLegs = this.usesWalkingLegs(renderPassInfo);
        renderPassInfo.addBoneUpdater((pass, bones) -> {
            bones.ifPresent("left_legs", bone -> bone.skipRender(walkingLegs).skipChildrenRender(walkingLegs));
            bones.ifPresent("right_legs", bone -> bone.skipRender(walkingLegs).skipChildrenRender(walkingLegs));
            bones.ifPresent("left_legs_walk", bone -> bone.skipRender(!walkingLegs).skipChildrenRender(!walkingLegs));
            bones.ifPresent("right_legs_walk", bone -> bone.skipRender(!walkingLegs).skipChildrenRender(!walkingLegs));
        });
    }

    private boolean usesWalkingLegs(RenderPassInfo<EntityRenderState> renderPassInfo) {
        if (renderPassInfo.getOrDefaultGeckolibData(DANCE_WALKING_LEGS, false)) {
            return true;
        }

        ControllerState[] controllerStates = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ANIMATION_CONTROLLER_STATES, EMPTY_CONTROLLER_STATES);
        for (ControllerState controllerState : controllerStates) {
            if (controllerState == null || controllerState.animationPoint() == null || controllerState.animationPoint().animation() == null) {
                continue;
            }

            String animationName = controllerState.animationPoint().animation().name();
            if ("walk".equals(animationName) || "dance_2".equals(animationName) || "flipped".equals(animationName)) {
                return true;
            }
        }

        return false;
    }
}
