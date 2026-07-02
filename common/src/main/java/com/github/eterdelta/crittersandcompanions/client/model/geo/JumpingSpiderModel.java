package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.JumpingSpiderEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class JumpingSpiderModel extends GeoModel<JumpingSpiderEntity> {
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_jumping_spider_variant", Integer.class);
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/jumping_spider");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/jumping_spider");
    private static final Identifier[] TEXTURES = {
            CrittersAndCompanions.createId("textures/entity/jumping_spider_1.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_2.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_3.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_4.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_5.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_6.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_7.png"),
            CrittersAndCompanions.createId("textures/entity/jumping_spider_8.png")
    };

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURES[Mth.clamp(renderState.getOrDefaultGeckolibData(VARIANT, 0), 0, TEXTURES.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(JumpingSpiderEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(JumpingSpiderEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
