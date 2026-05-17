package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.OtterEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class OtterModel extends GeoModel<OtterEntity> {
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_otter_baby", Boolean.class);
    private static final Identifier[] MODELS = new Identifier[]{
            CrittersAndCompanions.createId("entity/otter"),
            CrittersAndCompanions.createId("entity/baby_otter")};
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/otter.png"),
            CrittersAndCompanions.createId("textures/entity/baby_otter.png")};
    private static final Identifier[] ANIMATIONS = new Identifier[]{
            CrittersAndCompanions.createId("entity/otter"),
            CrittersAndCompanions.createId("entity/baby_otter")};

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODELS[renderState.getOrDefaultGeckolibData(BABY, false) ? 1 : 0];
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURES[renderState.getOrDefaultGeckolibData(BABY, false) ? 1 : 0];
    }

    @Override
    public Identifier getAnimationResource(OtterEntity animatable) {
        return ANIMATIONS[animatable.isBaby() ? 1 : 0];
    }

    @Override
    public void addAdditionalStateData(OtterEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(BABY, animatable.isBaby());
    }

}
