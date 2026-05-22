package com.github.eterdelta.crittersandcompanions;

import com.github.eterdelta.crittersandcompanions.client.model.geo.DragonflyModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.DumboOctopusModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.JumpingSpiderModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.LeafInsectModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.RedPandaModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.SeaBunnyModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.ShimaEnagaModel;
import com.github.eterdelta.crittersandcompanions.client.renderer.GrapplingHookRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.FerretRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.KoiFishRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.OtterRenderer;
import com.github.eterdelta.crittersandcompanions.platform.event.RegisterEntityRenderers;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import com.geckolib.renderer.GeoEntityRenderer;

public class CrittersAndCompanionsClient {

    public static void init() {
    }

    public static void registerEntityRenderers(RegisterEntityRenderers event) {
        event.accept(CACEntities.OTTER.get(), OtterRenderer::new);
        event.accept(CACEntities.JUMPING_SPIDER.get(), context -> new GeoEntityRenderer<>(context, new JumpingSpiderModel()));
        event.accept(CACEntities.KOI_FISH.get(), KoiFishRenderer::new);
        event.accept(CACEntities.DRAGONFLY.get(), context -> new GeoEntityRenderer<>(context, new DragonflyModel()));
        event.accept(CACEntities.SEA_BUNNY.get(), context -> new GeoEntityRenderer<>(context, new SeaBunnyModel()));
        event.accept(CACEntities.SHIMA_ENAGA.get(), context -> new GeoEntityRenderer<>(context, new ShimaEnagaModel()));
        event.accept(CACEntities.FERRET.get(), FerretRenderer::new);
        event.accept(CACEntities.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        event.accept(CACEntities.DUMBO_OCTOPUS.get(), context -> new GeoEntityRenderer<>(context, new DumboOctopusModel()));
        event.accept(CACEntities.LEAF_INSECT.get(), context -> new GeoEntityRenderer<>(context, new LeafInsectModel()));
        event.accept(CACEntities.RED_PANDA.get(), context -> new GeoEntityRenderer<>(context, new RedPandaModel()));
    }

    public static void registerEntityLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> event) {
    }
}
