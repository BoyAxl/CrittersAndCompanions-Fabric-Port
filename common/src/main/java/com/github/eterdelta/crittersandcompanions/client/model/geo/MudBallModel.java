package com.github.eterdelta.crittersandcompanions.client.model.geo;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.projectiles.MudBallProjectile;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class MudBallModel extends DefaultedEntityGeoModel<MudBallProjectile> {
    private static final Identifier MODEL = CrittersAndCompanions.createId("mud_ball");
    private static final Identifier MODEL_RESOURCE = CrittersAndCompanions.createId("entity/mud_ball");
    private static final Identifier ANIMATION_RESOURCE = CrittersAndCompanions.createId("entity/mud_ball");

    public MudBallModel() {
        super(MODEL);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL_RESOURCE;
    }

    @Override
    public Identifier getAnimationResource(MudBallProjectile animatable) {
        return ANIMATION_RESOURCE;
    }
}
