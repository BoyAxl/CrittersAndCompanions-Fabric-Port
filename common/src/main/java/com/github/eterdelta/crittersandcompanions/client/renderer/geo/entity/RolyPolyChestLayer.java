package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.client.model.geo.RolyPolyChestModel;
import com.github.eterdelta.crittersandcompanions.entity.RolyPolyEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;

public class RolyPolyChestLayer extends GeoRenderLayer<RolyPolyEntity, Void, EntityRenderState> {
    private static final Identifier TEXTURE = CrittersAndCompanions.createId("textures/entity/roly_poly_chest.png");
    private final GeoModel<RolyPolyEntity> chestModel = new RolyPolyChestModel();

    public RolyPolyChestLayer(GeoRenderer<RolyPolyEntity, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        if (!renderPassInfo.getOrDefaultGeckolibData(RolyPolyRenderer.HAS_CHEST, false)) {
            return;
        }

        var chestBakedModel = this.chestModel.getBakedModel(this.chestModel.getModelResource(renderPassInfo.renderState()));
        var renderType = this.getRenderer().getRenderType(renderPassInfo.renderState(), TEXTURE);

        submitNodeCollector.order(1).submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
            renderPassInfo.poseStack().pushPose();
            renderPassInfo.poseStack().last().set(pose);
            chestBakedModel.render(renderPassInfo, vertexConsumer, renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderPassInfo.renderColor());
            renderPassInfo.poseStack().popPose();
        });
    }
}
