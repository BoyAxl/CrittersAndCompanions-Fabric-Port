package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.LeafInsectEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class LeafInsectModel extends DefaultedEntityGeoModel<LeafInsectEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("leaf_insect");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/leaf_insect");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/leaf_insect");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_leaf_insect_variant", Integer.class);
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/leaf_insect_1.png"),
            CrittersAndCompanions.createId("textures/entity/leaf_insect_2.png"),
            CrittersAndCompanions.createId("textures/entity/leaf_insect_3.png")};

    public LeafInsectModel() {
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
    public Identifier getAnimationResource(LeafInsectEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(LeafInsectEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }

}
