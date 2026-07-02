package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.WeevilEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class WeevilModel extends DefaultedEntityGeoModel<WeevilEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("weevil");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/weevil");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/weevil");

    public WeevilModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getAnimationResource(WeevilEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}
