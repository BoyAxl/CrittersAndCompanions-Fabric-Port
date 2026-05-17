package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.ShimaEnagaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class ShimaEnagaModel extends DefaultedEntityGeoModel<ShimaEnagaEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("shima_enaga");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/shima_enaga");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/shima_enaga");

    public ShimaEnagaModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getAnimationResource(ShimaEnagaEntity animatable) {
        return ANIMATION_RESOURCE;
    }

}
