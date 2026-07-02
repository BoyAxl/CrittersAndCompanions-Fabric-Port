package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import com.github.eterdelta.crittersandcompanions.registry.CACTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;

public abstract class BugEntity extends TamableAnimal implements GeoEntity {
    private static final int HEALTH_REGEN_INTERVAL = 20 * 60;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final TagKey<Item> foodTag;
    private final TagKey<Item> temptTag;
    private final EntityDataAccessor<Integer> variantData;
    private final int variantCount;
    private final int danceVariantCount;

    private BlockPos activeJukebox;
    private boolean dancing;
    private int danceIndex = 1;

    protected BugEntity(EntityType<? extends TamableAnimal> type, Level level, String id, EntityDataAccessor<Integer> variantData, int variantCount, int danceVariantCount) {
        super(type, level);
        this.foodTag = itemTag(id + "_food");
        this.temptTag = itemTag(id + "_tempt_items");
        this.variantData = variantData;
        this.variantCount = variantCount;
        this.danceVariantCount = danceVariantCount;
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, CrittersAndCompanions.createId(id));
    }

    public static boolean checkBugSpawnRules(EntityType<? extends BugEntity> entityType, LevelAccessor levelAccessor, EntitySpawnReason spawnType, BlockPos blockPos, RandomSource random) {
        BlockState ground = levelAccessor.getBlockState(blockPos.below());
        return blockPos.getY() > levelAccessor.getSeaLevel() - 16 && ground.is(CACTags.BUG_SPAWN_GROUND);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.variantData != null) {
            output.putInt("Variant", this.getVariant());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        if (this.variantData != null) {
            this.setVariant(input.getIntOr("Variant", 0));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason mobSpawnType, SpawnGroupData spawnGroupData) {
        if (this.variantData != null && this.variantCount > 1 && mobSpawnType != EntitySpawnReason.BUCKET) {
            this.setVariant(this.random.nextInt(this.variantCount));
        }
        return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        if (this.activeJukebox == null || !this.activeJukebox.closerToCenterThan(this.position(), 5.0D) || !this.level().getBlockState(this.activeJukebox).is(Blocks.JUKEBOX)) {
            this.activeJukebox = null;
            this.dancing = false;
        }
        super.aiStep();
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.isTame() && this.tickCount % HEALTH_REGEN_INTERVAL == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.activeJukebox = pos;
        this.dancing = playing;
        if (playing && this.danceVariantCount > 1) {
            this.danceIndex = this.random.nextInt(this.danceVariantCount) + 1;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame()) {
            if (!this.isOwnedBy(player)) {
                return super.mobInteract(player, hand);
            }

            if (stack.is(this.foodTag()) && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide()) {
                    this.gameEvent(GameEvent.EAT, this);
                    this.heal(2.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.onTamedFed();
                }
                return InteractionResult.SUCCESS;
            }

            if (stack.is(this.foodTag()) || stack.is(this.temptTag())) {
                if (!this.canUseFoodForBreeding()) {
                    return InteractionResult.SUCCESS;
                }
                return super.mobInteract(player, hand);
            }

            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(this.temptTag())) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!this.level().isClientSide()) {
                if (this.random.nextInt(10) == 0 && Services.EVENTS.canTame(this, player)) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    this.onTamedFed();
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    protected void onTamedFed() {
    }

    protected boolean canUseFoodForBreeding() {
        return true;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(this.foodTag());
    }

    protected Ingredient temptIngredient() {
        return Ingredient.of(this.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(this.temptTag()));
    }

    // Mob calls registerGoals from its constructor before BugEntity's tag fields are assigned.
    private TagKey<Item> foodTag() {
        return this.foodTag != null ? this.foodTag : itemTag(this.entityPath() + "_food");
    }

    private TagKey<Item> temptTag() {
        return this.temptTag != null ? this.temptTag : itemTag(this.entityPath() + "_tempt_items");
    }

    private String entityPath() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getPath();
    }

    protected AnimationController<?> createBugController() {
        return new AnimationController<>("controller", 4, this::bugPredicate);
    }

    protected PlayState bugPredicate(AnimationTest<?> event) {
        String customAnimation = this.getCustomAnimation(event);
        if (customAnimation != null) {
            event.controller().setAnimation(RawAnimation.begin().thenPlay(customAnimation));
            return PlayState.CONTINUE;
        }

        event.controller().setAnimation(RawAnimation.begin().thenLoop(this.getLoopAnimation(event)));
        return PlayState.CONTINUE;
    }

    protected String getCustomAnimation(AnimationTest<?> event) {
        return null;
    }

    protected String getLoopAnimation(AnimationTest<?> event) {
        if (this.isDancing()) {
            return this.getDanceAnimationName();
        }
        if (this.isInSittingPose()) {
            return "sit";
        }
        return event.isMoving() ? "walk" : "idle";
    }

    public boolean isDancing() {
        return this.dancing;
    }

    public String getDanceAnimationName() {
        return this.danceIndex == 1 ? "dance" : "dance_" + this.danceIndex;
    }

    public int getVariant() {
        return this.variantData == null ? 0 : this.entityData.get(this.variantData);
    }

    public void setVariant(int variant) {
        if (this.variantData != null) {
            this.entityData.set(this.variantData, Mth.clamp(variant, 0, this.variantCount - 1));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected static class UntamedPanicGoal extends PanicGoal {
        private final TamableAnimal animal;

        public UntamedPanicGoal(TamableAnimal animal, double speedModifier) {
            super(animal, speedModifier);
            this.animal = animal;
        }

        @Override
        public boolean canUse() {
            return !this.animal.isTame() && super.canUse();
        }
    }

    protected static class DancingStrollGoal extends WaterAvoidingRandomStrollGoal {
        private final BugEntity bug;

        public DancingStrollGoal(BugEntity bug, double speedModifier) {
            super(bug, speedModifier);
            this.bug = bug;
        }

        @Override
        public boolean canUse() {
            return !this.bug.isDancing() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bug.isDancing() && super.canContinueToUse();
        }
    }
}
