package com.github.eterdelta.crittersandcompanions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class CrittersAndCompanionsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CrittersAndCompanionsClient.init();

        CrittersAndCompanionsClient.registerEntityLayers((id, factory) -> ModelLayerRegistry.registerModelLayer(id, factory::get));
        CrittersAndCompanionsClient.registerEntityRenderers(EntityRendererRegistry::register);

        var resourcePack = CrittersAndCompanions.createId("friendlyart");
        FabricLoader.getInstance().getModContainer(resourcePack.getNamespace()).ifPresent(mod -> {
            ResourceManagerHelper.registerBuiltinResourcePack(resourcePack, mod, ResourcePackActivationType.NORMAL);
        });
    }

}
