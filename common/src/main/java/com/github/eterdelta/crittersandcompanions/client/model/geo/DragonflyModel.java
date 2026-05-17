package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.DragonflyEntity;
import com.github.eterdelta.crittersandcompanions.item.DragonflyArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class DragonflyModel extends DefaultedEntityGeoModel<DragonflyEntity> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("dragonfly");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/dragonfly");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/dragonfly");
    private static final DataTicket<Identifier> ARMOR_TEXTURE = DataTickets.create("cac_dragonfly_armor_texture", Identifier.class);

    public DragonflyModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier armorTexture = renderState.getGeckolibData(ARMOR_TEXTURE);
        return armorTexture != null ? armorTexture : super.getTextureResource(renderState);
    }

    @Override
    public Identifier getAnimationResource(DragonflyEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void addAdditionalStateData(DragonflyEntity animatable, Object relatedObject, GeoRenderState renderState) {
        super.addAdditionalStateData(animatable, relatedObject, renderState);
        if (animatable.getArmor().getItem() instanceof DragonflyArmorItem armorItem) {
            renderState.addGeckolibData(ARMOR_TEXTURE, armorItem.getTexture());
        }
    }
}
