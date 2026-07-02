package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.mixin.WallClimberNavigationAccessor;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import com.github.eterdelta.crittersandcompanions.registry.CACSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.state.AnimationTest;

public class SnailEntity extends BugEntity {
    private static final int HARVEST_COOLDOWN = 20 * 60 * 5;
    private static final int WAKE_UP_TICKS = 28;
    private static final Identifier SHELL_KNOCKBACK_RESIST = CrittersAndCompanions.createId("shell_knockback_resistance");
    private static final Identifier SHELL_ARMOR = CrittersAndCompanions.createId("shell_armor");
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);

    private int slimeHarvestCooldown;
    private int wakingUpTicks = -1;

    public SnailEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level, "snail", VARIANT, 3, 1);
        this.jumpControl = new NoJumpControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(CLIMBING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UntamedPanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, this.temptIngredient(), false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.25D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.4D, 10F, 2F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new DancingStrollGoal(this, 1.0D));
    }

    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.is(Items.GLASS_BOTTLE)) {
            if (!this.level().isClientSide()) {
                if (this.slimeHarvestCooldown <= 0) {
                    if (!player.getAbilities().instabuild) {
                        handStack.shrink(1);
                    }
                    ItemStack slimeBottle = new ItemStack(CACItems.SNAIL_SLIME_BOTTLE.get());
                    if (handStack.isEmpty()) {
                        player.setItemInHand(hand, slimeBottle);
                    } else if (!player.getInventory().add(slimeBottle)) {
                        player.drop(slimeBottle, false);
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.8F);
                    this.slimeHarvestCooldown = HARVEST_COOLDOWN;
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Climbing", this.isClimbing());
        output.putInt("SlimeHarvestCooldown", this.slimeHarvestCooldown);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setClimbing(input.getBooleanOr("Climbing", false));
        this.slimeHarvestCooldown = input.getIntOr("SlimeHarvestCooldown", 0);
        if (this.isOrderedToSit()) {
            this.updateShellDefenses(true);
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        this.setClimbing(this.horizontalCollision);
        if (this.slimeHarvestCooldown > 0) {
            this.slimeHarvestCooldown--;
        }
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        SnailEntity baby = CACEntities.SNAIL.get().create(level, EntitySpawnReason.BREEDING);
        if (baby != null && entity instanceof SnailEntity otherParent) {
            baby.setVariant(this.random.nextBoolean() ? this.getVariant() : otherParent.getVariant());
        }
        return baby;
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING);
    }

    public void setClimbing(boolean climbing) {
        this.entityData.set(CLIMBING, climbing);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        super.travel(travelVector);
        if (this.onClimbable()) {
            Vec3 movement = this.getDeltaMovement();
            double climbSpeed = this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.5D;
            if (movement.y > climbSpeed) {
                this.setDeltaMovement(movement.x, climbSpeed, movement.z);
            }
        }
    }

    @Override
    public void jumpFromGround() {
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SnailNavigation(this, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.createBugController()
                .triggerableAnim("wake_up", RawAnimation.begin().thenPlay("wake_up"))
                .triggerableAnim("hide", RawAnimation.begin().thenPlay("hide")));
    }

    @Override
    protected String getLoopAnimation(AnimationTest<?> event) {
        if (this.isInSittingPose()) {
            return "sit";
        }
        return super.getLoopAnimation(event);
    }

    private boolean isWakingUp() {
        return this.wakingUpTicks >= 0;
    }

    @Override
    public void setOrderedToSit(boolean orderedToSit) {
        if (this.isWakingUp()) {
            return;
        }

        if (this.isOrderedToSit() && !orderedToSit) {
            this.wakingUpTicks = WAKE_UP_TICKS;
            if (!this.level().isClientSide()) {
                this.triggerAnim("controller", "wake_up");
            }
            return;
        } else if (!this.isOrderedToSit() && orderedToSit && !this.level().isClientSide()) {
            this.triggerAnim("controller", "hide");
        }

        super.setOrderedToSit(orderedToSit);
        if (!this.level().isClientSide()) {
            this.updateShellDefenses(orderedToSit);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.isWakingUp() || super.isImmobile();
    }

    private void updateShellDefenses(boolean hiding) {
        var knockbackResistance = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        var armor = this.getAttribute(Attributes.ARMOR);
        if (knockbackResistance == null || armor == null) {
            return;
        }
        if (hiding) {
            if (!knockbackResistance.hasModifier(SHELL_KNOCKBACK_RESIST)) {
                knockbackResistance.addTransientModifier(new AttributeModifier(SHELL_KNOCKBACK_RESIST, 0.4D, AttributeModifier.Operation.ADD_VALUE));
            }
            if (!armor.hasModifier(SHELL_ARMOR)) {
                armor.addTransientModifier(new AttributeModifier(SHELL_ARMOR, 10.0D, AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            knockbackResistance.removeModifier(SHELL_KNOCKBACK_RESIST);
            armor.removeModifier(SHELL_ARMOR);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWakingUp()) {
            this.wakingUpTicks--;
            if (this.wakingUpTicks == 0) {
                super.setOrderedToSit(false);
                if (!this.level().isClientSide()) {
                    this.updateShellDefenses(false);
                }
            }
        }
    }

    @Override
    public @NotNull Vec3 getPassengerRidingPosition(@NotNull Entity passenger) {
        if (this.onClimbable()) {
            Direction wallFace = this.getClimbingWallFace();
            if (wallFace != null) {
                Direction away = wallFace.getOpposite();
                return this.position().add(away.getStepX() * 6.0D / 16.0D, -2.5D / 16.0D, away.getStepZ() * 6.0D / 16.0D);
            }
        }

        double yawRad = this.getYRot() * (Math.PI / 180.0D);
        return this.position().add(Math.sin(yawRad) * 2.0D / 16.0D, 8.2D / 16.0D, -Math.cos(yawRad) * 2.0D / 16.0D);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (this.onClimbable()) {
            passenger.setYRot(this.getYRot());
            passenger.yRotO = this.getYRot();
            passenger.setXRot(0.0F);
            passenger.xRotO = 0.0F;
            if (passenger instanceof Mob mob) {
                mob.yBodyRot = this.getYRot();
                mob.yHeadRot = this.getYRot();
            }
        }
    }

    public @Nullable Direction getClimbingWallFace() {
        var box = this.getBoundingBox();
        var level = this.level();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbourPos = BlockPos.containing(
                    box.getCenter().x + dir.getStepX() * (box.getXsize() / 2.0D + 0.1D),
                    box.minY,
                    box.getCenter().z + dir.getStepZ() * (box.getZsize() / 2.0D + 0.1D)
            );
            if (!level.getBlockState(neighbourPos).getCollisionShape(level, neighbourPos).isEmpty()) {
                return dir;
            }
        }
        return null;
    }

    public boolean isGaryVariant() {
        if (!this.hasCustomName()) {
            return false;
        }
        String customName = ChatFormatting.stripFormatting(this.getCustomName().getString());
        return customName.equalsIgnoreCase("gary");
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if (this.isInSittingPose()) {
            return null;
        }
        return this.isGaryVariant() ? CACSounds.SNAIL_GARY_IDLE.get() : super.getAmbientSound();
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.isGaryVariant() ? 200 : super.getAmbientSoundInterval();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return this.isGaryVariant() ? CACSounds.SNAIL_GARY_HURT.get() : SoundEvents.SLIME_HURT_SMALL;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    protected void onTamedFed() {
        if (this.isGaryVariant()) {
            this.playSound(CACSounds.SNAIL_GARY_PURR.get(), 0.5F, 1.0F);
        }
    }

    private static class NoJumpControl extends JumpControl {
        NoJumpControl(Mob mob) {
            super(mob);
        }

        @Override
        public void jump() {
        }
    }

    private static class SnailNavigation extends WallClimberNavigation {
        SnailNavigation(SnailEntity mob, Level level) {
            super(mob, level);
        }

        @Override
        public void stop() {
            super.stop();
            ((WallClimberNavigationAccessor) this).setPathToPosition(null);
        }
    }
}
