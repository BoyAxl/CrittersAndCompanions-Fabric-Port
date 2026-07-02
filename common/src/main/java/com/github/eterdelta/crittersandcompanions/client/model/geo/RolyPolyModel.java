package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.RolyPolyEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RolyPolyModel extends GeoModel<RolyPolyEntity> {
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_roly_poly_baby", Boolean.class);
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_roly_poly_variant", Integer.class);
    private static final Identifier ADULT_MODEL = CrittersAndCompanions.createId("entity/roly_poly");
    private static final Identifier BABY_MODEL = CrittersAndCompanions.createId("entity/baby_roly_poly");
    private static final Identifier[] TEXTURES = {
            CrittersAndCompanions.createId("textures/entity/roly_poly_1.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_2.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_3.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_4.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_5.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_6.png"),
            CrittersAndCompanions.createId("textures/entity/roly_poly_7.png")
    };
    private static final Identifier BABY_TEXTURE = CrittersAndCompanions.createId("textures/entity/baby_roly_poly.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(BABY, false) ? BABY_MODEL : ADULT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        if (renderState.getOrDefaultGeckolibData(BABY, false)) {
            return BABY_TEXTURE;
        }
        return TEXTURES[Mth.clamp(renderState.getOrDefaultGeckolibData(VARIANT, 0), 0, TEXTURES.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(RolyPolyEntity animatable) {
        return animatable.isBaby() ? BABY_MODEL : ADULT_MODEL;
    }

    @Override
    public void addAdditionalStateData(RolyPolyEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(BABY, animatable.isBaby());
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
