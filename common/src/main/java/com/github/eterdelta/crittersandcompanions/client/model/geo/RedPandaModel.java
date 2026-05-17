package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.RedPandaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RedPandaModel extends GeoModel<RedPandaEntity> {
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_red_panda_baby", Boolean.class);
    private static final DataTicket<Boolean> SLEEPING = DataTickets.create("cac_red_panda_sleeping", Boolean.class);
    private static final Identifier[] MODELS = new Identifier[]{
            CrittersAndCompanions.createId("entity/red_panda"),
            CrittersAndCompanions.createId("entity/baby_red_panda")};
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/red_panda.png"),
            CrittersAndCompanions.createId("textures/entity/red_panda_sleeping.png"),
            CrittersAndCompanions.createId("textures/entity/baby_red_panda.png")};
    private static final Identifier[] ANIMATIONS = new Identifier[]{
            CrittersAndCompanions.createId("entity/red_panda"),
            CrittersAndCompanions.createId("entity/baby_red_panda")};

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODELS[renderState.getOrDefaultGeckolibData(BABY, false) ? 1 : 0];
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURES[renderState.getOrDefaultGeckolibData(BABY, false) ? 2 : renderState.getOrDefaultGeckolibData(SLEEPING, false) ? 1 : 0];
    }

    @Override
    public Identifier getAnimationResource(RedPandaEntity animatable) {
        return ANIMATIONS[animatable.isBaby() ? 1 : 0];
    }

    @Override
    public void addAdditionalStateData(RedPandaEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(BABY, animatable.isBaby());
        renderState.addGeckolibData(SLEEPING, animatable.isSleeping());
    }
}
