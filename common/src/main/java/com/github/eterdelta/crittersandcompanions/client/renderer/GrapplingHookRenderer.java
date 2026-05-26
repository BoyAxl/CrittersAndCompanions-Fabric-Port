package com.github.eterdelta.crittersandcompanions.client.renderer;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.client.model.GrapplingHookModel;
import com.github.eterdelta.crittersandcompanions.entity.GrapplingHookEntity;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity, GrapplingHookRenderer.GrapplingHookRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(CrittersAndCompanions.createId("grappling_hook"), "main");
    private static final Identifier TEXTURE = CrittersAndCompanions.createId("textures/entity/grappling_hook.png");
    private static final int ROPE_COLOR_RED = 193;
    private static final int ROPE_COLOR_GREEN = 184;
    private static final int ROPE_COLOR_BLUE = 205;
    private static final int ROPE_COLOR_ALPHA = 255;
    private static final int ROPE_SEGMENTS = 16;
    private static final double FIRST_PERSON_VIEW_BOBBING_SCALE = 960.0D;

    private final GrapplingHookModel hookModel;

    public GrapplingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.hookModel = new GrapplingHookModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public GrapplingHookRenderState createRenderState() {
        return new GrapplingHookRenderState();
    }

    @Override
    public void extractRenderState(GrapplingHookEntity entity, GrapplingHookRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);

        if (!(entity.getOwner() instanceof Player owner) || !entity.isFocused()) {
            renderState.shouldRender = false;
            renderState.lineOriginOffset = Vec3.ZERO;
            return;
        }

        renderState.shouldRender = true;
        float attackAnim = owner.getAttackAnim(partialTick);
        float handSwing = Mth.sin(Mth.sqrt(attackAnim) * Mth.PI);
        Vec3 handPosition = getPlayerHandPosition(owner, handSwing, partialTick, this.entityRenderDispatcher);
        Vec3 hookPosition = entity.getPosition(partialTick).add(0.0D, 0.4D, 0.0D);
        renderState.lineOriginOffset = handPosition.subtract(hookPosition);
    }

    @Override
    public void submit(GrapplingHookRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!renderState.shouldRender) {
            return;
        }

        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.translate(0.0D, -1.25D, 0.0D);
        submitNodeCollector.submitModel(this.hookModel, renderState, poseStack, TEXTURE, renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
        poseStack.popPose();

        float lineX = (float) renderState.lineOriginOffset.x;
        float lineY = (float) renderState.lineOriginOffset.y;
        float lineZ = (float) renderState.lineOriginOffset.z;
        float lineWidth = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> renderRope(lineX, lineY, lineZ, lineWidth, pose, vertexConsumer));

        poseStack.popPose();
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    protected boolean affectedByCulling(GrapplingHookEntity entity) {
        return false;
    }

    private static Vec3 getPlayerHandPosition(Player player, float handSwing, float partialTick, EntityRenderDispatcher dispatcher) {
        HumanoidArm holdingArm = getHoldingArm(player);
        int side = holdingArm == HumanoidArm.RIGHT ? 1 : -1;

        if (!dispatcher.options.getCameraType().isFirstPerson() || player != Minecraft.getInstance().player) {
            float bodyRot = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * Mth.DEG_TO_RAD;
            double bodySin = Mth.sin(bodyRot);
            double bodyCos = Mth.cos(bodyRot);
            float scale = player.getScale();
            double sideOffset = side * 0.35D * scale;
            double forwardOffset = 0.8D * scale;
            float crouchOffset = player.isCrouching() ? -0.1875F : 0.0F;

            return player.getEyePosition(partialTick).add(
                    -bodyCos * sideOffset - bodySin * forwardOffset,
                    crouchOffset - 0.45D * scale,
                    -bodySin * sideOffset + bodyCos * forwardOffset);
        }

        float fov = dispatcher.options.fov().get();
        double fovScale = FIRST_PERSON_VIEW_BOBBING_SCALE / fov;
        Vec3 nearPlanePoint = dispatcher.camera.getNearPlane(fov).getPointOnPlane(side * 0.825F, -0.08F);

        return player.getEyePosition(partialTick)
                .add(nearPlanePoint.scale(fovScale)
                        .yRot(handSwing * 0.5F)
                        .xRot(-handSwing * 0.7F));
    }

    private static HumanoidArm getHoldingArm(Player player) {
        HumanoidArm mainArm = player.getMainArm();

        if (player.getMainHandItem().is(CACItems.GRAPPLING_HOOK.get())) {
            return mainArm;
        }

        return mainArm.getOpposite();
    }

    private static void renderRope(float x, float y, float z, float lineWidth, PoseStack.Pose pose, VertexConsumer vertexConsumer) {
        for (int segment = 0; segment < ROPE_SEGMENTS; segment++) {
            float start = fraction(segment);
            float end = fraction(segment + 1);
            stringVertex(x, y, z, vertexConsumer, pose, start, end, lineWidth);
            stringVertex(x, y, z, vertexConsumer, pose, end, start, lineWidth);
        }
    }

    private static float fraction(int segment) {
        return (float) segment / ROPE_SEGMENTS;
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float start, float end, float lineWidth) {
        float startX = x * start;
        float startY = y * (start * start + start) * 0.5F + 0.25F;
        float startZ = z * start;
        float normalX = x * end - startX;
        float normalY = y * (end * end + end) * 0.5F + 0.25F - startY;
        float normalZ = z * end - startZ;
        float normalLength = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);

        if (normalLength > 0.0F) {
            normalX /= normalLength;
            normalY /= normalLength;
            normalZ /= normalLength;
        }

        vertexConsumer.addVertex(pose, startX, startY, startZ)
                .setColor(ROPE_COLOR_RED, ROPE_COLOR_GREEN, ROPE_COLOR_BLUE, ROPE_COLOR_ALPHA)
                .setNormal(pose, normalX, normalY, normalZ)
                .setLineWidth(lineWidth);
    }

    public static class GrapplingHookRenderState extends EntityRenderState {
        public boolean shouldRender;
        public Vec3 lineOriginOffset = Vec3.ZERO;
    }
}
