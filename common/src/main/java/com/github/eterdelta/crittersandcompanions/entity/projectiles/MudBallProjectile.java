package com.github.eterdelta.crittersandcompanions.entity.projectiles;

import com.github.eterdelta.crittersandcompanions.entity.WeevilEntity;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class MudBallProjectile extends ThrowableItemProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MudBallProjectile(EntityType<? extends MudBallProjectile> type, Level level) {
        super(type, level);
    }

    public MudBallProjectile(Level level, LivingEntity owner) {
        super(CACEntities.MUD_BALL.get(), level);
        this.setOwner(owner);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private @Nullable ParticleOptions getParticle() {
        return this.getItem().isEmpty() ? null : new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id != 3) {
            return;
        }
        ParticleOptions particle = this.getParticle();
        if (particle == null) {
            return;
        }
        for (int i = 0; i < 8; ++i) {
            this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        var owner = this.getOwner();
        var target = result.getEntity();
        if (owner instanceof WeevilEntity weevil && target instanceof LivingEntity livingTarget && !weevil.canAttack(livingTarget)) {
            return;
        }
        target.hurt(this.damageSources().thrown(this, owner), 3.0F);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.MUD_BREAK, 1.0F, 1.0F);
            this.discard();
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.MUD;
    }
}
