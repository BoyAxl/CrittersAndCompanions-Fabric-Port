package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.RolyPolyEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RolyPolyChestModel extends GeoModel<RolyPolyEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("entity/roly_poly_chest");
    private static final Identifier TEXTURE = CrittersAndCompanions.createId("textures/entity/roly_poly_chest.png");
    private static final Identifier ANIMATION = CrittersAndCompanions.createId("entity/roly_poly");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(RolyPolyEntity animatable) {
        return ANIMATION;
    }
}
