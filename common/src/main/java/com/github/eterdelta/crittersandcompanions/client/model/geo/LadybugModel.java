package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.LadybugEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class LadybugModel extends DefaultedEntityGeoModel<LadybugEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("ladybug");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/ladybug");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/ladybug");

    public LadybugModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getAnimationResource(LadybugEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}
