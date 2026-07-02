package com.github.eterdelta.crittersandcompanions;

import com.github.eterdelta.crittersandcompanions.client.model.GrapplingHookModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.DragonflyModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.DumboOctopusModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.LadybugModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.LeafInsectModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.MudBallModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.RedPandaModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.StagBeetleModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.StickBugModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.WeevilModel;
import com.github.eterdelta.crittersandcompanions.client.gui.RolyPolyScreen;
import com.github.eterdelta.crittersandcompanions.client.model.geo.SeaBunnyModel;
import com.github.eterdelta.crittersandcompanions.client.model.geo.ShimaEnagaModel;
import com.github.eterdelta.crittersandcompanions.client.renderer.GrapplingHookRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.FerretRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.JumpingSpiderRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.KoiFishRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.OtterRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.RolyPolyRenderer;
import com.github.eterdelta.crittersandcompanions.client.renderer.geo.entity.SnailRenderer;
import com.github.eterdelta.crittersandcompanions.platform.event.RegisterEntityRenderers;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACMenuTypes;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.gui.screens.MenuScreens;
import com.geckolib.renderer.GeoEntityRenderer;

public class CrittersAndCompanionsClient {

    public static void init() {
        MenuScreens.register(CACMenuTypes.ROLY_POLY_CHEST.get(), RolyPolyScreen::new);
    }

    public static void registerEntityRenderers(RegisterEntityRenderers event) {
        event.accept(CACEntities.OTTER.get(), OtterRenderer::new);
        event.accept(CACEntities.JUMPING_SPIDER.get(), JumpingSpiderRenderer::new);
        event.accept(CACEntities.KOI_FISH.get(), KoiFishRenderer::new);
        event.accept(CACEntities.DRAGONFLY.get(), context -> new GeoEntityRenderer<>(context, new DragonflyModel()));
        event.accept(CACEntities.SEA_BUNNY.get(), context -> new GeoEntityRenderer<>(context, new SeaBunnyModel()));
        event.accept(CACEntities.SHIMA_ENAGA.get(), context -> new GeoEntityRenderer<>(context, new ShimaEnagaModel()));
        event.accept(CACEntities.FERRET.get(), FerretRenderer::new);
        event.accept(CACEntities.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        event.accept(CACEntities.DUMBO_OCTOPUS.get(), context -> new GeoEntityRenderer<>(context, new DumboOctopusModel()));
        event.accept(CACEntities.LEAF_INSECT.get(), context -> new GeoEntityRenderer<>(context, new LeafInsectModel()));
        event.accept(CACEntities.RED_PANDA.get(), context -> new GeoEntityRenderer<>(context, new RedPandaModel()));
        event.accept(CACEntities.LADYBUG.get(), context -> new GeoEntityRenderer<>(context, new LadybugModel()));
        event.accept(CACEntities.ROLY_POLY.get(), RolyPolyRenderer::new);
        event.accept(CACEntities.SNAIL.get(), SnailRenderer::new);
        event.accept(CACEntities.STAG_BEETLE.get(), context -> new GeoEntityRenderer<>(context, new StagBeetleModel()));
        event.accept(CACEntities.STICK_BUG.get(), context -> new GeoEntityRenderer<>(context, new StickBugModel()));
        event.accept(CACEntities.WEEVIL.get(), context -> new GeoEntityRenderer<>(context, new WeevilModel()));
        event.accept(CACEntities.MUD_BALL.get(), context -> new GeoEntityRenderer<>(context, new MudBallModel()));
    }

    public static void registerEntityLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> event) {
        event.accept(GrapplingHookRenderer.LAYER_LOCATION, GrapplingHookModel::createLayer);
    }
}
