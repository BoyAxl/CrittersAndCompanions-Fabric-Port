package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.entity.brain.AnimatedDelayedRangedAttackGoal;
import com.github.eterdelta.crittersandcompanions.entity.projectiles.MudBallProjectile;
import com.github.eterdelta.crittersandcompanions.registry.CACSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;

public class WeevilEntity extends BugEntity implements RangedAttackMob {
    private final AnimatedDelayedRangedAttackGoal<WeevilEntity> rangedAttackGoal = new AnimatedDelayedRangedAttackGoal<>(this, 1.0D, 20, 8.0F, "controller", "throw", 5);
    private final HurtByTargetGoal hurtByTargetGoal = new HurtByTargetGoal(this);
    private final NearestAttackableTargetGoal<Mob> attackTargetGoal = new NearestAttackableTargetGoal<>(this, Mob.class, 5, true, false, (entity, level) -> this.shouldAttack(entity));

    public WeevilEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level, "weevil", null, 0, 1);
        this.reassessTameGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UntamedPanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, this.temptIngredient(), false));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.4D, 10F, 2F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new DancingStrollGoal(this, 1.0D));
    }

    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        this.reassessTameGoals();
    }

    private void reassessTameGoals() {
        this.goalSelector.removeGoal(this.rangedAttackGoal);
        this.targetSelector.removeGoal(this.hurtByTargetGoal);
        this.targetSelector.removeGoal(this.attackTargetGoal);

        if (this.isTame()) {
            this.goalSelector.addGoal(3, this.rangedAttackGoal);
            this.targetSelector.addGoal(1, this.hurtByTargetGoal);
            this.targetSelector.addGoal(2, this.attackTargetGoal);
        }
    }

    public boolean shouldAttack(LivingEntity entity) {
        return entity instanceof Enemy && !(entity instanceof Creeper);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        return null;
    }

    @Override
    protected boolean canUseFoodForBreeding() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.createBugController().triggerableAnim("throw", RawAnimation.begin().thenPlay("throw")));
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return CACSounds.BUGS_HURT.get();
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float velocity) {
        Vec3 lookVec = this.getLookAngle().normalize().scale(0.5D);
        Vec3 spawnPos = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ()).add(lookVec);
        MudBallProjectile projectile = new MudBallProjectile(this.level(), this);
        projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        double x = target.getX() - projectile.getX();
        double targetCenterY = target.getY() + target.getBbHeight() / 2.0D;
        double y = targetCenterY - projectile.getY();
        double z = target.getZ() - projectile.getZ();
        double distance = Math.sqrt(x * x + z * z) * 0.2D;
        projectile.shoot(x, y + distance, z, 1.0F, 5.0F);

        this.level().addFreshEntity(projectile);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (this.getOwner() != null) {
            boolean targetHasSameOwner = target instanceof TamableAnimal tamableAnimal && tamableAnimal.isOwnedBy(this.getOwner());
            if (targetHasSameOwner) {
                return false;
            }
        }
        return super.canAttack(target);
    }
}
