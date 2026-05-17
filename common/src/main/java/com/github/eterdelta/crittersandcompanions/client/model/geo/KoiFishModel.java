package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.KoiFishEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class KoiFishModel extends DefaultedEntityGeoModel<KoiFishEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("koi_fish");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/koi_fish");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/koi_fish");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_koi_fish_variant", Integer.class);
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/koi_fish_1.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_2.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_3.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_4.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_5.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_6.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_7.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_8.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_9.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_10.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_11.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_12.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_13.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_14.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_15.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_16.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_17.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_18.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_19.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_20.png"),
            CrittersAndCompanions.createId("textures/entity/koi_fish_21.png")};

    public KoiFishModel() {
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
    public Identifier getAnimationResource(KoiFishEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(KoiFishEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
