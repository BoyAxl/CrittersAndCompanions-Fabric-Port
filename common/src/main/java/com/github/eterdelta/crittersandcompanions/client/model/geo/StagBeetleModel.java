package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.StagBeetleEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class StagBeetleModel extends DefaultedEntityGeoModel<StagBeetleEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("stag_beetle");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/stag_beetle");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/stag_beetle");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_stag_beetle_variant", Integer.class);
    private static final Identifier[] TEXTURES = {
            CrittersAndCompanions.createId("textures/entity/stag_beetle_1.png"),
            CrittersAndCompanions.createId("textures/entity/stag_beetle_2.png"),
            CrittersAndCompanions.createId("textures/entity/stag_beetle_3.png"),
            CrittersAndCompanions.createId("textures/entity/stag_beetle_4.png"),
            CrittersAndCompanions.createId("textures/entity/stag_beetle_5.png"),
            CrittersAndCompanions.createId("textures/entity/stag_beetle_6.png")
    };

    public StagBeetleModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURES[Mth.clamp(renderState.getOrDefaultGeckolibData(VARIANT, 0), 0, TEXTURES.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(StagBeetleEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(StagBeetleEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
