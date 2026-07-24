package com.github.eterdelta.crittersandcompanions.mixin;

import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {
    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void cac$injectJockeyVehicles(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Zombie self = (Zombie) (Object) this;
        if (!self.isBaby() || self.isPassenger() || level.getRandom().nextFloat() >= 0.1F) {
            return;
        }

        if (level.getRandom().nextBoolean()) {
            var mount = CACEntities.SNAIL.get().create(self.level(), EntitySpawnReason.JOCKEY);
            if (mount != null) {
                mount.snapTo(self.getX(), self.getY(), self.getZ(), self.getYRot(), 0.0F);
                mount.finalizeSpawn(level, difficulty, EntitySpawnReason.JOCKEY, null);
                mount.setJockeyMount(true);
                self.startRiding(mount, false, false);
                level.addFreshEntity(mount);
            }
        } else {
            var mount = CACEntities.ROLY_POLY.get().create(self.level(), EntitySpawnReason.JOCKEY);
            if (mount != null) {
                mount.snapTo(self.getX(), self.getY(), self.getZ(), self.getYRot(), 0.0F);
                mount.finalizeSpawn(level, difficulty, EntitySpawnReason.JOCKEY, null);
                mount.setJockeyMount(true);
                self.startRiding(mount, false, false);
                level.addFreshEntity(mount);
            }
        }
    }
}
