package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.SeaBunnyEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SeaBunnyModel extends DefaultedEntityGeoModel<SeaBunnyEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("sea_bunny");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/sea_bunny");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/sea_bunny");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_sea_bunny_variant", Integer.class);
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/sea_bunny_white.png"),
            CrittersAndCompanions.createId("textures/entity/sea_bunny_blue.png"),
            CrittersAndCompanions.createId("textures/entity/sea_bunny_yellow.png")};

    public SeaBunnyModel() {
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
    public Identifier getAnimationResource(SeaBunnyEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(SeaBunnyEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
