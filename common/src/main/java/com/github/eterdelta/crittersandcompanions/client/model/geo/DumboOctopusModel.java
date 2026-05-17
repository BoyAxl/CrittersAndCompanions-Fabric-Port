package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.DumboOctopusEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class DumboOctopusModel extends DefaultedEntityGeoModel<DumboOctopusEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("dumbo_octopus");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/dumbo_octopus");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/dumbo_octopus");
    private static final DataTicket<Integer> VARIANT = DataTickets.create("cac_dumbo_octopus_variant", Integer.class);
    private static final Identifier[] TEXTURES = new Identifier[]{
            CrittersAndCompanions.createId("textures/entity/dumbo_octopus_1.png"),
            CrittersAndCompanions.createId("textures/entity/dumbo_octopus_2.png"),
            CrittersAndCompanions.createId("textures/entity/dumbo_octopus_3.png"),
            CrittersAndCompanions.createId("textures/entity/dumbo_octopus_4.png")};

    public DumboOctopusModel() {
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
    public Identifier getAnimationResource(DumboOctopusEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(DumboOctopusEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }
}
