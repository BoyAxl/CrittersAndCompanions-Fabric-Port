package com.github.eterdelta.crittersandcompanions.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CACCommonConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(CACCommonConfig.class);
    private static final String FILE_NAME = CrittersAndCompanions.MODID + "-common.toml";
    private static final String LEGACY_SWIM_SPEED_KEY = "swim_sped";
    private static final String SWIM_SPEED_KEY = "swim_speed";

    public final DoubleValue necklaceGuardianDebuff;
    public final DoubleValue necklaceDrownedDebuff;
    public final DoubleValue necklaceSwimSpeed;
    public final DoubleValue grapplingHookSpeed;
    public final DoubleValue grapplingHookMaxSpeed;
    public final DoubleValue grapplingHookMaxDistance;

    public double necklaceRangeDebuff(EntityType<?> type, int level) {
        if(type == EntityType.GUARDIAN && level > 1) return necklaceGuardianDebuff.getAsDouble() * level;
        if(type == EntityType.DROWNED) return necklaceDrownedDebuff.getAsDouble() * level;
        return 0;
    }

    public CACCommonConfig(ModConfigSpec.Builder builder) {
        builder.push("necklace");

        this.necklaceSwimSpeed = builder.defineInRange(SWIM_SPEED_KEY, 0.2F, 0F, 1F);
        this.necklaceDrownedDebuff = builder.defineInRange("drowned_range_debuff", 0.1F, 0F, 1F);
        this.necklaceGuardianDebuff = builder.defineInRange("guardian_range_debuff", 0.1F, 0F, 1F);

        builder.pop();
        builder.push("grappling_hook");

        this.grapplingHookSpeed = builder.defineInRange("launch_speed", 1F, 0F, 10F);
        this.grapplingHookMaxSpeed = builder.defineInRange("max_speed", 4F, 1F, 1000F);
        this.grapplingHookMaxDistance = builder.defineInRange("max_distance", 32F, 4F, 128F);

        builder.pop();
    }

    public static void migrateLegacyConfig(Path configDir) {
        Path configPath = configDir.resolve(FILE_NAME);
        if (Files.notExists(configPath)) {
            return;
        }

        try (CommentedFileConfig config = CommentedFileConfig.of(configPath.toFile())) {
            config.load();
            Object necklace = config.get("necklace");
            if (!(necklace instanceof CommentedConfig necklaceConfig)) {
                return;
            }

            Object legacySwimSpeed = necklaceConfig.get(LEGACY_SWIM_SPEED_KEY);
            if (legacySwimSpeed == null) {
                return;
            }

            if (necklaceConfig.get(SWIM_SPEED_KEY) == null) {
                necklaceConfig.set(SWIM_SPEED_KEY, legacySwimSpeed);
            }
            necklaceConfig.remove(LEGACY_SWIM_SPEED_KEY);
            config.save();
        } catch (Exception exception) {
            LOGGER.warn("Could not migrate legacy common config at {}", configPath, exception);
        }
    }

}
