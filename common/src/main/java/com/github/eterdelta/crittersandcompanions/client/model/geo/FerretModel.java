package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.FerretEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class FerretModel extends GeoModel<FerretEntity> {
    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_ferret_baby", Boolean.class);
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_ferret_variant", Integer.class);
    private static final Identifier[] MODELS = new Identifier[]{
            CrittersAndCompanions.createId("entity/ferret"),
            CrittersAndCompanions.createId("entity/baby_ferret")};
    private static final Identifier[] ADULT_TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/ferret_1.png"),
            CrittersAndCompanions.createId("textures/entity/ferret_2.png")};
    private static final Identifier[] BABY_TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/baby_ferret_1.png"),
            CrittersAndCompanions.createId("textures/entity/baby_ferret_2.png")};
    private static final Identifier[] ANIMATIONS = new Identifier[]{
            CrittersAndCompanions.createId("entity/ferret"),
            CrittersAndCompanions.createId("entity/baby_ferret")};

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODELS[renderState.getOrDefaultGeckolibData(BABY, false) ? 1 : 0];
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        boolean baby = renderState.getOrDefaultGeckolibData(BABY, false);
        int variant = Mth.clamp(renderState.getOrDefaultGeckolibData(VARIANT, 0), 0, ADULT_TEXTURES.length - 1);
        return baby ? BABY_TEXTURES[variant] : ADULT_TEXTURES[variant];
    }

    @Override
    public Identifier getAnimationResource(FerretEntity animatable) {
        return ANIMATIONS[animatable.isBaby() ? 1 : 0];
    }

    @Override
    public void addAdditionalStateData(FerretEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(BABY, animatable.isBaby());
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }

}
