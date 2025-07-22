package com.github.eterdelta.crittersandcompanions.platform;

import com.github.eterdelta.crittersandcompanions.config.CACCommonConfig;
import com.github.eterdelta.crittersandcompanions.platform.service.IConfigs;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ForgeConfigs implements IConfigs {

    private static final Pair<CACCommonConfig, ModConfigSpec> COMMON = new ModConfigSpec.Builder().configure(CACCommonConfig::new);

    @Override
    public CACCommonConfig common() {
        return COMMON.getLeft();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, COMMON.getRight());
    }

}