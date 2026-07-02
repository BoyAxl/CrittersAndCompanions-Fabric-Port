package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.StickBugEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class StickBugModel extends DefaultedEntityGeoModel<StickBugEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("stick_bug");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/stick_bug");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/stick_bug");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_stick_bug_variant", Integer.class);
    private static final Identifier[] TEXTURES = {
            CrittersAndCompanions.createId("textures/entity/stick_bug_1.png"),
            CrittersAndCompanions.createId("textures/entity/stick_bug_2.png"),
            CrittersAndCompanions.createId("textures/entity/stick_bug_3.png")
    };

    public StickBugModel() {
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
    public Identifier getAnimationResource(StickBugEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(StickBugEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
