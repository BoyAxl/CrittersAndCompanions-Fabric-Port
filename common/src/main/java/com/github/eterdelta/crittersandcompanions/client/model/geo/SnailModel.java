package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.SnailEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SnailModel extends GeoModel<SnailEntity> {
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_snail_baby", Boolean.class);
    private static final DataTicket<Boolean> GARY = DataTickets.create("cac_snail_gary", Boolean.class);
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_snail_variant", Integer.class);
    private static final Identifier ADULT_MODEL = CrittersAndCompanions.createId("entity/snail");
    private static final Identifier BABY_MODEL = CrittersAndCompanions.createId("entity/baby_snail");
    private static final Identifier[] TEXTURES = {
            CrittersAndCompanions.createId("textures/entity/snail_1.png"),
            CrittersAndCompanions.createId("textures/entity/snail_2.png"),
            CrittersAndCompanions.createId("textures/entity/snail_3.png")
    };
    private static final Identifier GARY_TEXTURE = CrittersAndCompanions.createId("textures/entity/snail_gary.png");
    private static final Identifier BABY_TEXTURE = CrittersAndCompanions.createId("textures/entity/baby_snail.png");
    private static final Identifier BABY_GARY_TEXTURE = CrittersAndCompanions.createId("textures/entity/baby_snail_gary.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(BABY, false) ? BABY_MODEL : ADULT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        boolean baby = renderState.getOrDefaultGeckolibData(BABY, false);
        boolean gary = renderState.getOrDefaultGeckolibData(GARY, false);
        if (baby) {
            return gary ? BABY_GARY_TEXTURE : BABY_TEXTURE;
        }
        return gary ? GARY_TEXTURE : TEXTURES[Mth.clamp(renderState.getOrDefaultGeckolibData(VARIANT, 0), 0, TEXTURES.length - 1)];
    }

    @Override
    public Identifier getAnimationResource(SnailEntity animatable) {
        return animatable.isBaby() ? BABY_MODEL : ADULT_MODEL;
    }

    @Override
    public void addAdditionalStateData(SnailEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(BABY, animatable.isBaby());
        renderState.addGeckolibData(GARY, animatable.isGaryVariant());
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
