package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.extension.IGrapplingState;
import com.github.eterdelta.crittersandcompanions.network.CACPacketHandler;
import com.github.eterdelta.crittersandcompanions.network.ClientboundGrapplingStatePacket;
import com.github.eterdelta.crittersandcompanions.platform.Services;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrapplingHookEntity extends ThrowableItemProjectile {
    private static final double MAX_RENDER_DISTANCE_SQR = 4096.0D;
    private static final double STICK_COLLISION_INFLATE = 0.25D;
    private static final double AIR_DRAG = 0.98D;
    private static final double GRAVITY = -0.03D;
    private static final double TETHER_ACCELERATION_SCALE = 0.01D;
    private static final double PULL_SPEED_DIVISOR = 4.0D;

    protected boolean isStick;
    protected double stickLength;
    private boolean addedToWorld;
    private Vec3 stuckPosition;
    private BlockPos stuckBlockPos;

    public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GrapplingHookEntity(Player owner, ItemStack ownerStack, Level level) {
        this(CACEntities.GRAPPLING_HOOK.get(), level);
        this.snapTo(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYHeadRot(), owner.getXRot());
        this.setOwner(owner);
        this.setItem(ownerStack);
    }

    @Override
    protected Item getDefaultItem() {
        return CACItems.GRAPPLING_HOOK.get();
    }

    @Override
    public void tick() {
        if (this.isStick && this.stuckPosition != null) {
            this.freezeAtStuckPosition();
        }

        super.tick();

        if (!addedToWorld) {
            updateOwnerState();
            addedToWorld = true;
        }

        Entity owner = getOwner();
        if (discardOnServerIfOwnerInvalid(owner)) {
            return;
        }

        double offsetLengthSqr = distanceToSqr(owner);

        var maxDistance = Services.CONFIGS.common().grapplingHookMaxDistance.get();
        var maxDistanceSqr = maxDistance * maxDistance;
        boolean focused = isFocused();
        if (!level().isClientSide() && (!focused || offsetLengthSqr > maxDistanceSqr)) {
            discard();
            return;
        }

        boolean wasStick = this.isStick;
        boolean willStick = hasValidStuckPosition() || isTouchingCollidableBlock();

        if (willStick && !wasStick) {
            this.stickTo(this.position(), null, offsetLengthSqr);
        } else if (!willStick && wasStick) {
            this.clearStickState();
        }

        this.isStick = willStick;
        if (this.isStick) {
            this.freezeAtStuckPosition();
            applyTetherForce(owner, offsetLengthSqr);
        } else {
            applyFreeMovement();
        }

        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);

        if (!this.isStick) {
            var owner = getOwner();
            this.stickTo(hitResult.getLocation(), hitResult.getBlockPos(), owner != null ? distanceToSqr(owner) : 0.0D);
        }
    }

    @Override
    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);
        updateOwnerState();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < MAX_RENDER_DISTANCE_SQR;
    }

    public void pull() {
        Entity owner = getOwner();
        if (discardOnServerIfOwnerInvalid(owner)) {
            return;
        }

        if (isStick) {
            var pullSpeed = Services.CONFIGS.common().grapplingHookSpeed.get() / PULL_SPEED_DIVISOR;
            var maxSpeed = Services.CONFIGS.common().grapplingHookMaxSpeed.get();
            var direction = position().subtract(owner.position()).normalize();
            var distance = distanceTo(owner);
            owner.setDeltaMovement(direction.scale(Math.min(maxSpeed, pullSpeed * distance)));
        }

        discard();
    }

    public void updateOwnerState() {
        Entity owner = getOwner();
        if (!level().isClientSide() && owner instanceof Player player && owner instanceof IGrapplingState grapplingState) {

            grapplingState.setHook(isAlive() ? this : null);
            CACPacketHandler.GRAPPLING_STATE.sendToTracking(player,
                    new ClientboundGrapplingStatePacket(isAlive() ? OptionalInt.of(getId()) : OptionalInt.empty(), player.getId()));
        }
    }

    public boolean isFocused() {
        Entity owner = getOwner();
        if (hasValidOwner(owner) && owner instanceof Player player) {
            return ItemStack.isSameItemSameComponents(player.getMainHandItem(), getItem())
                    || ItemStack.isSameItemSameComponents(player.getOffhandItem(), getItem());
        }
        return false;
    }

    private boolean hasValidStuckPosition() {
        return this.isStick && this.stuckPosition != null && this.isStuckBlockStillValid();
    }

    private boolean isTouchingCollidableBlock() {
        var collidableBox = getBoundingBox().inflate(STICK_COLLISION_INFLATE);
        var collisions = level().getBlockCollisions(this, collidableBox);

        for (VoxelShape shape : collisions) {
            if (!shape.isEmpty() && shape.bounds().intersects(collidableBox)) {
                return true;
            }
        }

        return false;
    }

    private void applyTetherForce(Entity owner, double offsetLengthSqr) {
        if (offsetLengthSqr > stickLength) {
            var direction = position().subtract(owner.position()).normalize();
            var maxSpeed = Services.CONFIGS.common().grapplingHookMaxSpeed.get();
            var scale = Math.min(maxSpeed, TETHER_ACCELERATION_SCALE * Math.sqrt(offsetLengthSqr));
            if (scale >= 0) {
                owner.setDeltaMovement(owner.getDeltaMovement().add(direction.scale(scale)));
                owner.hurtMarked = true;
            }
        }

        setDeltaMovement(Vec3.ZERO);
    }

    private void applyFreeMovement() {
        setDeltaMovement(getDeltaMovement().scale(AIR_DRAG));
        setDeltaMovement(getDeltaMovement().add(0.0D, GRAVITY, 0.0D));
    }

    private void stickTo(Vec3 position, BlockPos blockPos, double stickLength) {
        this.isStick = true;
        this.stickLength = stickLength;
        this.stuckPosition = position;
        this.stuckBlockPos = blockPos;
        this.playSound(SoundEvents.SLIME_SQUISH);
        this.freezeAtStuckPosition();
    }

    private void freezeAtStuckPosition() {
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);

        if (this.stuckPosition != null) {
            this.setPos(this.stuckPosition);
        }
    }

    private boolean isStuckBlockStillValid() {
        if (this.stuckBlockPos == null) {
            return true;
        }

        return !this.level().getBlockState(this.stuckBlockPos).getCollisionShape(this.level(), this.stuckBlockPos).isEmpty();
    }

    private void clearStickState() {
        this.isStick = false;
        this.stickLength = 0.0D;
        this.stuckPosition = null;
        this.stuckBlockPos = null;
        this.setNoGravity(false);
    }

    private static boolean hasValidOwner(Entity owner) {
        return owner instanceof Player && owner.isAlive() && !owner.isRemoved();
    }

    private boolean discardOnServerIfOwnerInvalid(Entity owner) {
        if (hasValidOwner(owner)) {
            return false;
        }

        if (!level().isClientSide()) {
            discard();
        }

        return true;
    }

}
