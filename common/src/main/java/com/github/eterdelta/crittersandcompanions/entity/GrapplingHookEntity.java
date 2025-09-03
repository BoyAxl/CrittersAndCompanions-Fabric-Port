package com.github.eterdelta.crittersandcompanions.entity;

import com.github.eterdelta.crittersandcompanions.extension.IGrapplingState;
import com.github.eterdelta.crittersandcompanions.network.CACPacketHandler;
import com.github.eterdelta.crittersandcompanions.network.ClientboundGrapplingStatePacket;
import com.github.eterdelta.crittersandcompanions.registry.CACEntities;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import java.util.OptionalInt;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrapplingHookEntity extends ThrowableItemProjectile {
    protected boolean isStick;
    protected boolean wasStick;
    protected double stickLength;
    private boolean addedToWorld;

    public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GrapplingHookEntity(Player owner, ItemStack ownerStack, Level level) {
        this(CACEntities.GRAPPLING_HOOK.get(), level);
        this.moveTo(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYHeadRot(), owner.getXRot());
        this.setOwner(owner);
        this.setItem(ownerStack);
    }

    @Override
    protected Item getDefaultItem() {
        return CACItems.GRAPPLING_HOOK.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (!addedToWorld) {
            updateOwnerState();
            addedToWorld = true;
        }

        if (!this.level().isClientSide() && (!this.isFocused() || this.getOwner().distanceToSqr(this) > 1048)) {
            this.discard();
            return;
        }

        AABB collidableBox = this.getBoundingBox().inflate(0.1D);
        Iterable<VoxelShape> collisions = this.level().getBlockCollisions(this, collidableBox);

        isStick = false;
        for (VoxelShape shape : collisions) {
            if (!shape.isEmpty() && shape.bounds().intersects(collidableBox)) {
                isStick = true;
                break;
            }
        }

        if (isStick && !wasStick) {
            stickLength = this.position().subtract(this.getOwner().position()).lengthSqr();
            playSound(SoundEvents.SLIME_SQUISH);
        }

        wasStick = isStick;

        if (isStick && this.getOwner() != null) {
            Vec3 offset = this.position().subtract(this.getOwner().position());
            if (offset.lengthSqr() > stickLength) {
                this.getOwner().setDeltaMovement(this.getOwner().getDeltaMovement().add(offset.scale(0.02D)));
                this.getOwner().hurtMarked = true;
            }
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);
        this.updateOwnerState();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    public void pull() {
        if (this.getOwner() != null) {
            if (isStick) {
                this.getOwner().setDeltaMovement(this.position().subtract(this.getOwner().position())
                        .multiply(0.25D, 0.2D, 0.25D)
                        .add(0.0D, 0.25D, 0.0D)
                );
            }
            this.discard();
        }
    }

    public void updateOwnerState() {
        if (!this.level().isClientSide() && this.getOwner() != null
                && this.getOwner() instanceof Player player
                && this.getOwner() instanceof IGrapplingState grapplingState) {

            grapplingState.setHook(this.isAlive() ? this : null);
            CACPacketHandler.GRAPPLING_STATE.sendToTracking(player,
                    new ClientboundGrapplingStatePacket(this.isAlive() ? OptionalInt.of(this.getId()) : OptionalInt.empty(), player.getId()));
        }
    }

    public boolean isFocused() {
        if (this.getOwner() instanceof Player player) {
            return ItemStack.isSameItemSameComponents(player.getMainHandItem(), getItem())
                    || ItemStack.isSameItemSameComponents(player.getOffhandItem(), getItem());
        }
        return false;
    }

}
