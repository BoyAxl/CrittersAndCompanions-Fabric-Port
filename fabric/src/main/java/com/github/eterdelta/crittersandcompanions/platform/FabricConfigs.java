package com.github.eterdelta.crittersandcompanions.platform;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.config.CACCommonConfig;
import com.github.eterdelta.crittersandcompanions.config.FabricCommonConfig;
import com.github.eterdelta.crittersandcompanions.platform.service.IConfigs;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FabricConfigs implements IConfigs {

    private static final Pair<FabricCommonConfig, ModConfigSpec> COMMON = new ModConfigSpec.Builder().configure(FabricCommonConfig::new);

    public static void register() {
        ModConfigEvents.loading(CrittersAndCompanions.MODID).register(FabricConfigs::onConfigLoaded);
        ModConfigEvents.reloading(CrittersAndCompanions.MODID).register(FabricConfigs::onConfigLoaded);
        ConfigRegistry.INSTANCE.register(CrittersAndCompanions.MODID, ModConfig.Type.COMMON, COMMON.getRight());
    }

    private static void onConfigLoaded(ModConfig config) {
        if (config.getType() == ModConfig.Type.COMMON) {
            COMMON.getLeft().migrateLegacySpawnDefaults();
        }
    }

    @Override
    public CACCommonConfig common() {
        return COMMON.getLeft();
    }

}
