package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.client.model.geo.KoiFishModel;
import com.github.eterdelta.crittersandcompanions.entity.KoiFishEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class KoiFishRenderer extends GeoEntityRenderer<KoiFishEntity, EntityRenderState> {
    private static final double LAND_FLOP_VISUAL_RANGE = 0.65D;
    private static final double LAND_FLOP_VISUAL_MAX_OFFSET = 0.3D;

    public KoiFishRenderer(EntityRendererProvider.Context context) {
        super(context, new KoiFishModel());
    }

    @Override
    public void extractRenderState(KoiFishEntity animatable, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(animatable, renderState, partialTick);
        renderState.y += getLandFlopVisualYOffset(animatable, renderState.y);
    }

    private static float getLandFlopVisualYOffset(KoiFishEntity koiFish, double renderedY) {
        if (koiFish.isInWater()) {
            return 0.0F;
        }

        double surfaceY = findLandFlopVisualSurfaceY(koiFish, renderedY);
        if (!Double.isFinite(surfaceY)) {
            return 0.0F;
        }

        double distanceAboveSurface = renderedY - surfaceY;
        if (distanceAboveSurface <= 0.0D || distanceAboveSurface > LAND_FLOP_VISUAL_RANGE) {
            return 0.0F;
        }

        double proximity = 1.0D - distanceAboveSurface / LAND_FLOP_VISUAL_RANGE;
        double easedProximity = proximity * proximity * (3.0D - 2.0D * proximity);
        double offset = -Math.min(distanceAboveSurface, LAND_FLOP_VISUAL_MAX_OFFSET) * easedProximity;
        return (float) offset;
    }

    private static double findLandFlopVisualSurfaceY(KoiFishEntity koiFish, double renderedY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int x = Mth.floor(koiFish.getX());
        int baseY = Mth.floor(renderedY - 1.0E-4D);
        int z = Mth.floor(koiFish.getZ());

        for (int yOffset = 0; yOffset <= 2; yOffset++) {
            pos.set(x, baseY - yOffset, z);
            double floorHeight = koiFish.level().getBlockFloorHeight(pos);

            if (!Double.isFinite(floorHeight)) {
                continue;
            }

            double surfaceY = pos.getY() + floorHeight;
            double distanceAboveSurface = renderedY - surfaceY;

            if (distanceAboveSurface >= 0.0D && distanceAboveSurface <= LAND_FLOP_VISUAL_RANGE) {
                return surfaceY;
            }
        }

        return Double.NaN;
    }
}
