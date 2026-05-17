package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.JumpingSpiderEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class JumpingSpiderModel extends DefaultedEntityGeoModel<JumpingSpiderEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("jumping_spider");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/jumping_spider");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/jumping_spider");

    public JumpingSpiderModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getAnimationResource(JumpingSpiderEntity animatable) {
        return ANIMATION_RESOURCE;
    }

}
