package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.brain.SprintingFollowParentGoal;
import com.github.eterdelta.crittersandcompanions.entity.brain.TameableFollowParentGoal;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACSounds;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class FerretEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(FerretEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(FerretEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(FerretEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(FerretEntity.class, EntityDataSerializers.INT);

    private static final TagKey<Item> FOODS_TAG = TagKey.create(Registries.ITEM, CrittersAndCompanions.createId("ferret_food"));
    private static final TagKey<Item> TEMPT_TAG = TagKey.create(Registries.ITEM, CrittersAndCompanions.createId("ferret_tempt_items"));
    private static final TagKey<Block> DIG_GROUNDS_TAG = TagKey.create(Registries.BLOCK, CrittersAndCompanions.createId("ferret_dig_grounds"));

    private static final ResourceKey<LootTable> DIGGABLES = ResourceKey.create(Registries.LOOT_TABLE, CrittersAndCompanions.createId("gameplay/digging"));
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected BlockState stateToDig;
    protected int digCooldown;

    public FerretEntity(EntityType<? extends FerretEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FerretMoveControl();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.34D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEEPING, false);
        builder.define(DIGGING, false);
        builder.define(VARIANT, 0);
        builder.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new DigGoal());
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new SleepGoal(200));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 1.6D, 1.4D, (livingEntity) -> livingEntity.is(this.getLastHurtByMob()) && !livingEntity.is(this.getOwner())));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.25D));
        this.goalSelector.addGoal(7, new MeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(8, new TemptGoal(this, 1.0D, this.ingredient(TEMPT_TAG), false));
        this.goalSelector.addGoal(7, new SprintingFollowParentGoal(this, 1.4D, 10.0F, 5.0F, 2.0F));
        this.goalSelector.addGoal(10, new TameableFollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<Animal>(this, Animal.class, 10, false, false, (entity, serverLevel) -> entity instanceof Chicken || entity instanceof Rabbit));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Sleeping", isSleeping());
        output.putInt("Variant", getVariant());
        if (getCollarColor() != null) {
            output.putInt("CollarColor", getCollarColor().getId());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setSleeping(input.getBooleanOr("Sleeping", false));
        this.setVariant(input.getIntOr("Variant", 0));
        input.getInt("CollarColor").ifPresent(color -> this.setCollarColor(DyeColor.byId(color)));
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel serverLevel) {
        return this.random.nextInt(2, 5);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.digCooldown > 0) {
            this.digCooldown--;
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        FerretEntity baby = CACEntities.FERRET.get().create(level, EntitySpawnReason.BREEDING);
        if (baby == null) return null;

        var ownerReference = this.getOwnerReference();
        if (ageableMob instanceof FerretEntity ferretEntity) {
            if (this.random.nextBoolean()) {
                baby.setVariant(this.getVariant());
            } else {
                baby.setVariant(ferretEntity.getVariant());
            }

            var color = random.nextBoolean() ? getCollarColor() : ferretEntity.getCollarColor();
            if (color != null) baby.setCollarColor(color);

            if (ownerReference != null) {
                baby.setOwnerReference(ownerReference);
                baby.setTame(true, false);
            }
        }
        return baby;
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        if (super.doHurtTarget(serverLevel, entity)) {
            this.playSound(CACSounds.BITE_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (isSleeping()) return InteractionResult.PASS;

        ItemStack handStack = player.getItemInHand(interactionHand);

        if (handStack.is(TEMPT_TAG) && !isTame()) {
            handStack.consume(1, player);
            if (!level().isClientSide()) {
                if (random.nextInt(10) == 0 && Services.EVENTS.canTame(this, player)) {
                    tame(player);
                    level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    level().broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (isTame() && isOwnedBy(player)) {
            var digResult = startDigging(player, handStack);
            if (digResult != InteractionResult.PASS) return digResult;

            DyeColor dyeColor = getDyeColor(handStack);
            if (dyeColor != null && getCollarColor() != dyeColor) {
                setCollarColor(dyeColor);
                handStack.consume(1, player);
                return InteractionResult.SUCCESS;
            }

            if (isFood(handStack)) {
                if (getHealth() < getMaxHealth()) {
                    gameEvent(GameEvent.EAT, this);
                    var food = handStack.get(DataComponents.FOOD);
                    if (food != null) heal(food.nutrition());
                    handStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                }
            } else {
                setOrderedToSit(!isOrderedToSit());
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    private InteractionResult startDigging(Player player, ItemStack handStack) {
        if (handStack.is(TEMPT_TAG) && !isBaby() && !isInSittingPose()) {
            if (digCooldown <= 0) {
                stateToDig = level().getBlockState(blockPosition().below());

                if (stateToDig.is(DIG_GROUNDS_TAG)) {
                    setDigging(true);
                    digCooldown = 6000;
                    handStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                } else {
                    stateToDig = null;
                }
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canFallInLove() {
        return !this.isDigging() && super.canFallInLove();
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(FOODS_TAG);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSleeping() ? null : CACSounds.FERRET_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CACSounds.FERRET_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CACSounds.FERRET_DEATH.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason mobSpawnType, SpawnGroupData spawnGroupData) {
        spawnGroupData = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
        if (mobSpawnType.equals(EntitySpawnReason.SPAWNER) && this.random.nextFloat() <= 0.2F) {
            for (int i = 0; i < this.random.nextInt(1, 4); i++) {
                FerretEntity baby = CACEntities.FERRET.get().create(this.level(), EntitySpawnReason.SPAWNER);
                baby.setVariant(this.random.nextInt(0, 2));
                baby.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                baby.setBaby(true);
                levelAccessor.addFreshEntity(baby);
            }
        }
        this.setVariant(this.random.nextInt(0, 2));
        return spawnGroupData;
    }

    private PlayState predicate(AnimationTest<?> event) {
        if (this.isDigging()) {
            event.controller().setAnimation(RawAnimation.begin().then("dig", LoopType.PLAY_ONCE));
        } else if (this.isInSittingPose()) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("sit"));
        } else if (this.isSleeping()) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("sleep"));
        } else if (isInWater()) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("swim"));
        } else if (event.isMoving()) {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("run"));
        } else {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
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

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    public boolean isDigging() {
        return this.entityData.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        this.entityData.set(DIGGING, digging);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Mth.clamp(variant, 0, 1));
    }

    private Ingredient ingredient(TagKey<Item> tag) {
        return Ingredient.of(this.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(tag));
    }

    private DyeColor getDyeColor(ItemStack stack) {
        if (stack.is(Items.WHITE_DYE)) return DyeColor.WHITE;
        if (stack.is(Items.ORANGE_DYE)) return DyeColor.ORANGE;
        if (stack.is(Items.MAGENTA_DYE)) return DyeColor.MAGENTA;
        if (stack.is(Items.LIGHT_BLUE_DYE)) return DyeColor.LIGHT_BLUE;
        if (stack.is(Items.YELLOW_DYE)) return DyeColor.YELLOW;
        if (stack.is(Items.LIME_DYE)) return DyeColor.LIME;
        if (stack.is(Items.PINK_DYE)) return DyeColor.PINK;
        if (stack.is(Items.GRAY_DYE)) return DyeColor.GRAY;
        if (stack.is(Items.LIGHT_GRAY_DYE)) return DyeColor.LIGHT_GRAY;
        if (stack.is(Items.CYAN_DYE)) return DyeColor.CYAN;
        if (stack.is(Items.PURPLE_DYE)) return DyeColor.PURPLE;
        if (stack.is(Items.BLUE_DYE)) return DyeColor.BLUE;
        if (stack.is(Items.BROWN_DYE)) return DyeColor.BROWN;
        if (stack.is(Items.GREEN_DYE)) return DyeColor.GREEN;
        if (stack.is(Items.RED_DYE)) return DyeColor.RED;
        if (stack.is(Items.BLACK_DYE)) return DyeColor.BLACK;
        return null;
    }

    @Nullable
    public DyeColor getCollarColor() {
        if (!isTame()) return null;
        return DyeColor.byId(entityData.get(DATA_COLLAR_COLOR));
    }

    private void setCollarColor(DyeColor color) {
        entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    public class SleepGoal extends Goal {
        private final int countdownTime;
        private int countdown;

        public SleepGoal(int countdownTime) {
            this.countdownTime = countdownTime;
            this.countdown = FerretEntity.this.random.nextInt(reducedTickDelay(countdownTime));
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        public boolean canUse() {
            if (FerretEntity.this.xxa == 0.0F && FerretEntity.this.yya == 0.0F && FerretEntity.this.zza == 0.0F) {
                return this.canSleep() || FerretEntity.this.isSleeping();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return !FerretEntity.this.level().isBrightOutside();
            }
        }

        public void stop() {
            FerretEntity.this.setSleeping(false);
            this.countdown = FerretEntity.this.random.nextInt(this.countdownTime);
        }

        public void start() {
            FerretEntity.this.setInSittingPose(false);
            FerretEntity.this.setJumping(false);
            FerretEntity.this.setSleeping(true);
            FerretEntity.this.getNavigation().stop();
            FerretEntity.this.getMoveControl().setWantedPosition(FerretEntity.this.getX(), FerretEntity.this.getY(), FerretEntity.this.getZ(), 0.0D);
        }
    }

    public class DigGoal extends Goal {
        protected int digTime;

        public DigGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            return FerretEntity.this.isDigging();
        }

        @Override
        public void start() {
            this.digTime = 35;
        }

        @Override
        public void tick() {
            if (this.digTime > 0) {
                this.digTime--;

                if (this.digTime % 5 == 0 && this.digTime >= 10) {
                    FerretEntity.this.level().playSound(null, FerretEntity.this, SoundEvents.GRAVEL_HIT, SoundSource.BLOCKS, 0.2F, 1.2F);
                    for (int i = 0; i < 4; ++i) {
                        double d0 = FerretEntity.this.random.nextGaussian() * 0.01D;
                        double d1 = FerretEntity.this.random.nextGaussian() * 0.01D;
                        double d2 = FerretEntity.this.random.nextGaussian() * 0.01D;
                        ((ServerLevel) FerretEntity.this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, FerretEntity.this.stateToDig), FerretEntity.this.getX(), FerretEntity.this.getY(), FerretEntity.this.getZ(), 2, d0, d1, d2, 0.1D);
                    }
                }
                if (this.digTime == 10) {
                    var digTable = FerretEntity.this.level().getServer().reloadableRegistries().getLootTable(DIGGABLES);
                    List<ItemStack> dugItems = digTable.getRandomItems(new LootParams.Builder((ServerLevel) level()).create(LootContextParamSets.EMPTY));

                    if (!dugItems.isEmpty()) {
                        FerretEntity.this.level().playSound(null, FerretEntity.this, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.1F, 1.2F);
                    }

                    for (ItemStack stack : dugItems) {
                        ItemEntity itemEntity = new ItemEntity(FerretEntity.this.level(), FerretEntity.this.getX(), FerretEntity.this.getY(), FerretEntity.this.getZ(), stack);
                        FerretEntity.this.level().addFreshEntity(itemEntity);
                    }

                    ExperienceOrb xp = new ExperienceOrb(FerretEntity.this.level(), FerretEntity.this.getX(), FerretEntity.this.getY(), FerretEntity.this.getZ(), FerretEntity.this.random.nextInt(1, 6));
                    FerretEntity.this.level().addFreshEntity(xp);
                }
            } else {
                this.stop();
            }
        }

        @Override
        public void stop() {
            FerretEntity.this.setDigging(false);
            FerretEntity.this.stateToDig = null;
            this.digTime = 0;
        }
    }

    class FerretMoveControl extends MoveControl {
        public FerretMoveControl() {
            super(FerretEntity.this);
        }

        public void tick() {
            if (!FerretEntity.this.isSleeping()) {
                super.tick();
            }
        }
    }
}
