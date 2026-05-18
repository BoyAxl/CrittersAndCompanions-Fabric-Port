package com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.api.CACColors;
import com.github.eterdelta.crittersandcompanions.entity.FerretEntity;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;

public class FerretOverlayerLayer extends GeoRenderLayer<FerretEntity, Void, EntityRenderState> {

    private static final DataTicket<Boolean> BABY = DataTickets.create("cac_ferret_overlay_baby", Boolean.class);
    private static final DataTicket<DyeColor> COLLAR_COLOR = DataTickets.create("cac_ferret_overlay_collar_color", DyeColor.class);
    private static final Map<DyeColor, Identifier> TEXTURES = createTextures(false);
    private static final Map<DyeColor, Identifier> BABY_TEXTURES = createTextures(true);

    private static Map<DyeColor, Identifier> createTextures(boolean baby) {
        var base = baby ? "baby_ferret" : "ferret";

        var map = new ImmutableMap.Builder<DyeColor, Identifier>();
        CACColors.supported().forEach(dye -> {
            var id = CrittersAndCompanions.createId("textures/entity/%s_tamed_overlay_%s.png".formatted(base, dye.getSerializedName()));
            map.put(dye, id);
        });
        return map.build();
    }

    public FerretOverlayerLayer(GeoRenderer<FerretEntity, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    @Override
    public void addRenderData(FerretEntity animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
        renderState.addGeckolibData(BABY, animatable.isBaby());
        renderState.addGeckolibData(COLLAR_COLOR, animatable.getCollarColor());
    }

    @Nullable
    protected Identifier getTextureResource(EntityRenderState renderState) {
        var color = renderState.getGeckolibData(COLLAR_COLOR);
        if (color == null) return null;
        var map = renderState.getOrDefaultGeckolibData(BABY, false) ? BABY_TEXTURES : TEXTURES;
        return map.get(color);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        var texture = getTextureResource(renderPassInfo.renderState());
        if (texture == null) return;
        getRenderer().submitRenderTasks(renderPassInfo, submitNodeCollector, RenderTypes.entityCutout(texture));
    }
}
