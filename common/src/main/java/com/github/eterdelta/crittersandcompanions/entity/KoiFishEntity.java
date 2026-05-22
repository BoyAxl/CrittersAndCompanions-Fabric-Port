package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class KoiFishEntity extends AbstractSchoolingFish implements GeoEntity {
    private static final double LAND_FLOP_VERTICAL_SPEED = 0.4D;
    private static final float LAND_FLOP_HORIZONTAL_SPEED = 0.05F;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(KoiFishEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public KoiFishEntity(EntityType<? extends KoiFishEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, OtterEntity.class, 8.0F, 1.7D, 1.4D));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVariant(input.getIntOr("Variant", 0));
    }

    @Override
    public void saveToBucketTag(ItemStack bucketStack) {
        super.saveToBucketTag(bucketStack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucketStack, nbt -> {
            nbt.putInt("Variant", getVariant());
        });
    }

    @Override
    public void loadFromBucketTag(CompoundTag bucketCompound) {
        super.loadFromBucketTag(bucketCompound);
        setVariant(bucketCompound.getInt("Variant").orElse(0));
    }

    @Override
    public int getMaxSchoolSize() {
        return 6;
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel serverLevel) {
        return this.random.nextInt(1, 4);
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    public void aiStep() {
        boolean shouldHandleLandFlop = !this.isInWater() && this.onGround() && this.verticalCollision;
        boolean wasVerticalCollision = this.verticalCollision;

        if (shouldHandleLandFlop) {
            this.verticalCollision = false;
        }

        super.aiStep();

        if (shouldHandleLandFlop) {
            this.verticalCollision = wasVerticalCollision;
            this.flopFromGround();
        }
    }

    private void flopFromGround() {
        this.setDeltaMovement(this.getDeltaMovement().add(
                (this.random.nextFloat() * 2.0F - 1.0F) * LAND_FLOP_HORIZONTAL_SPEED,
                LAND_FLOP_VERTICAL_SPEED,
                (this.random.nextFloat() * 2.0F - 1.0F) * LAND_FLOP_HORIZONTAL_SPEED));
        this.setOnGround(false);
        this.makeSound(this.getFlopSound());
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(CACItems.KOI_FISH_BUCKET.get());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason mobSpawnType, SpawnGroupData spawnGroupData) {
        if (mobSpawnType == EntitySpawnReason.BUCKET) return spawnGroupData;
        this.setVariant(this.random.nextInt(0, 21));
        return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }


    private PlayState predicate(AnimationTest<?> event) {
        if (this.isInWater()) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("koi_fish_swim"));
        } else {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("koi_fish_on_land"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 4, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Mth.clamp(variant, 0, 21));
    }
}
