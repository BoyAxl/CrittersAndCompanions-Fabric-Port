package com.github.eterdelta.crittersandcompanions;

import com.github.eterdelta.crittersandcompanions.config.CACSpawnConfig;
import com.github.eterdelta.crittersandcompanions.handler.PlayerHandler;
import com.github.eterdelta.crittersandcompanions.platform.FabricConfigs;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.InteractionResult;

public class CrittersAndCompanionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CrittersAndCompanions.init();
        CrittersAndCompanions.setup();

        CrittersAndCompanions.onAttributeCreation(FabricDefaultAttributeRegistry::register);

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide()) return InteractionResult.PASS;
            var result = PlayerHandler.onPlayerEntityInteract(entity, player, hand);
            return result != null ? result : InteractionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(PlayerHandler::onPlayerTick));
        EntityTrackingEvents.START_TRACKING.register(PlayerHandler::onPlayerStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(PlayerHandler::onPlayerStopTracking);

        CACSpawnConfig.load(Services.PLATFORM.getConfigDir());
        FabricConfigs.register();
        CACWorldGen.register();
        CACLootModifiers.register();
    }

}
