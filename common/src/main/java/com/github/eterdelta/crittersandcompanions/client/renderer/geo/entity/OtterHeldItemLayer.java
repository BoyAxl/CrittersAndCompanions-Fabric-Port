package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.entity.OtterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;

public class OtterHeldItemLayer extends BlockAndItemGeoLayer<OtterEntity, Void, EntityRenderState> {
    private static final String HELD_ITEM_BONE = "held_item";
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_otter_baby", Boolean.class);

    public OtterHeldItemLayer(EntityRendererProvider.Context context, GeoRenderer<OtterEntity, Void, EntityRenderState> renderer) {
        super(context, renderer);
    }

    @Override
    protected List<RenderData> getRelevantBones(OtterEntity animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
        var held = animatable.getMainHandItem();
        if (held.isEmpty()) return List.of();

        var displayContext = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        var itemState = RenderUtil.createRenderStateForItem(held, this.itemModelResolver, displayContext, animatable);
        return List.of(RenderData.item(HELD_ITEM_BONE, displayContext, itemState));
    }

    @Override
    public void addRenderData(OtterEntity animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
        renderState.addGeckolibData(BABY, animatable.isBaby());

        var relevantBones = getRelevantBones(animatable, relatedObject, renderState, partialTick);
        if (!relevantBones.isEmpty()) {
            renderState.addGeckolibData(CONTENTS, relevantBones);
        }
    }

    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState itemState, ItemDisplayContext displayContext, EntityRenderState renderState, SubmitNodeCollector submitNodeCollector, int packedLight) {
        poseStack.pushPose();
        bone.translateAwayFromPivotPoint(poseStack);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.05D, 0.2D, -0.9D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        if (renderState.getOrDefaultGeckolibData(BABY, false)) {
            poseStack.translate(0.0D, -0.6D, 0.0D);
        } else {
            poseStack.translate(0.0D, -0.125D, 0.0D);
        }

        super.submitItemStackRender(poseStack, bone, itemState, displayContext, renderState, submitNodeCollector, packedLight);
        poseStack.popPose();
    }
}
