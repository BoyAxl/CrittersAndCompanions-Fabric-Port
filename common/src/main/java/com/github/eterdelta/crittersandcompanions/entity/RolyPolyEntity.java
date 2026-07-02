package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.menu.RolyPolyMenu;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACSounds;
import com.github.eterdelta.crittersandcompanions.registry.CACTags;
import java.util.EnumSet;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.manager.AnimatableManager;

public class RolyPolyEntity extends BugEntity {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(RolyPolyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_CHEST = SynchedEntityData.defineId(RolyPolyEntity.class, EntityDataSerializers.BOOLEAN);

    private SimpleContainer inventory = new SimpleContainer(RolyPolyMenu.SLOTS);
    private @Nullable Player viewer;

    public RolyPolyEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level, "roly_poly", VARIANT, 7, 2);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(HAS_CHEST, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FreezeWhileChestAccessedGoal());
        this.goalSelector.addGoal(2, new UntamedPanicGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, this.temptIngredient(), false));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.4D, 10F, 2F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new DancingStrollGoal(this, 1.0D));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isTame() && this.isOwnedBy(player)) {
            if (player.isSecondaryUseActive() && this.hasChest()) {
                if (!this.level().isClientSide()) {
                    this.viewer = player;
                    player.openMenu(new ChestMenuProvider());
                }
                return InteractionResult.SUCCESS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!this.hasChest() && stack.is(CACTags.WOODEN_CHESTS) && !this.isBaby()) {
                if (!this.level().isClientSide()) {
                    this.setHasChest(true);
                    this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.viewer != null && this.viewer.containerMenu == this.viewer.inventoryMenu) {
            this.viewer = null;
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("HasChest", this.hasChest());
        if (this.hasChest()) {
            ContainerHelper.saveAllItems(output.child("ChestInventory"), this.inventory.getItems());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setHasChest(input.getBooleanOr("HasChest", false));
        this.inventory = new SimpleContainer(RolyPolyMenu.SLOTS);
        if (this.hasChest()) {
            ContainerHelper.loadAllItems(input.childOrEmpty("ChestInventory"), this.inventory.getItems());
        }
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        this.dropChest(level);
    }

    public boolean hasChest() {
        return this.entityData.get(HAS_CHEST);
    }

    public void setHasChest(boolean value) {
        this.entityData.set(HAS_CHEST, value);
        this.refreshDimensions();
    }

    public void dropChest(ServerLevel level) {
        if (!this.hasChest()) {
            return;
        }
        this.spawnAtLocation(level, Items.CHEST);
        for (ItemStack stack : this.inventory.removeAllItems()) {
            if (!stack.isEmpty()) {
                this.spawnAtLocation(level, stack);
            }
        }
        this.setHasChest(false);
        this.inventory = new SimpleContainer(RolyPolyMenu.SLOTS);
        this.viewer = null;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        RolyPolyEntity baby = CACEntities.ROLY_POLY.get().create(level, EntitySpawnReason.BREEDING);
        if (baby != null && entity instanceof RolyPolyEntity otherParent) {
            baby.setVariant(this.random.nextBoolean() ? this.getVariant() : otherParent.getVariant());
        }
        return baby;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(this.createBugController());
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return CACSounds.BUGS_HURT.get();
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (HAS_CHEST.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        EntityDimensions baseDimensions = super.getDefaultDimensions(pose);
        if (this.hasChest()) {
            return EntityDimensions.fixed(baseDimensions.width(), baseDimensions.height() + 0.5F).withEyeHeight(baseDimensions.eyeHeight());
        }
        return baseDimensions;
    }

    private boolean isBeingAccessed() {
        return this.viewer != null;
    }

    private class FreezeWhileChestAccessedGoal extends Goal {
        FreezeWhileChestAccessedGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return RolyPolyEntity.this.isBeingAccessed();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }
    }

    private class ChestMenuProvider implements ExtendedMenuProvider<RolyPolyMenu.OpeningData> {
        @Override
        public RolyPolyMenu.OpeningData getScreenOpeningData(ServerPlayer player) {
            return RolyPolyMenu.OpeningData.INSTANCE;
        }

        @Override
        public Component getDisplayName() {
            return RolyPolyEntity.this.getDisplayName();
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new RolyPolyMenu(containerId, playerInventory, RolyPolyEntity.this.inventory, () -> {
                if (RolyPolyEntity.this.level() instanceof ServerLevel serverLevel) {
                    RolyPolyEntity.this.dropChest(serverLevel);
                }
            });
        }
    }
}
