package com.github.eterdelta.crittersandcompanions.entity.brain;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Custom hybrid navigator (that works for both liquid and ground sources)
 * A chunk of the implementation is derived from the GroundNavigator from Companions!
 * https://github.com/Xylonity/Companions/blob/v1.20.1/common/src/main/java/dev/xylonity/companions/common/ai/navigator/GroundNavigator.java#L48
 */
public class OtterNavigation extends AmphibiousPathNavigation {

    private static final float EPSILON = 1.0E-8F;

    private static final double PROGRESS_MIN2 = 0.35D * 0.35D;
    private static final int PROGRESS_TICKS = 18;
    private static final float SURFACE_REQUIRED_PATH_LENGTH = 96.0F;
    private static final float SURFACE_MAX_VISITED_NODES_MULTIPLIER = 8.0F;
    private static final int SURFACE_PATH_REGION_OFFSET = 8;
    private static final int SURFACE_PATH_REACH_ACCURACY = 1;
    private static final double SURFACE_HORIZONTAL_PROGRESS_EPSILON = 1.0E-4D;
    private static final double SURFACE_CLOSE_HORIZONTAL_DISTANCE = 0.5D * 0.5D;
    private static final double SURFACE_LATERAL_Y_BIAS = 0.4D;
    private static final double SURFACE_LATERAL_MIN_UPWARD_SPEED = 0.015D;
    private static final double SURFACE_SWIM_MIN_HORIZONTAL_ACCEL = 0.02D;
    private static final double SURFACE_SWIM_MAX_HORIZONTAL_ACCEL = 0.045D;
    private static final double SURFACE_SWIM_MIN_UPWARD_SPEED = 0.025D;
    private static final double SURFACE_SWIM_MAX_UPWARD_SPEED = 0.12D;
    private static final double SURFACE_SWIM_MIN_DOWNWARD_SPEED = -0.025D;
    private static final double SURFACE_SWIM_MAX_DOWNWARD_SPEED = -0.12D;
    private static final double SURFACE_SWIM_MAX_HORIZONTAL_SPEED = 0.15D;
    private static final double SURFACE_ASCENT_CLEARANCE = 0.25D;
    private static final double WATER_EXIT_START_DISTANCE = 2.25D;
    private static final double WATER_EXIT_HORIZONTAL_ACCEL = 0.07D;
    private static final double WATER_EXIT_MAX_HORIZONTAL_SPEED = 0.22D;
    private static final double WATER_EXIT_UPWARD_SPEED = 0.11D;
    private static final double WATER_EXIT_COLLISION_UPWARD_SPEED = 0.18D;
    private static final int PATHFINDING_CACHE_SIZE = 1024;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private final Cache<BlockPos, Boolean> cache = CacheBuilder.newBuilder()
            .maximumSize(PATHFINDING_CACHE_SIZE)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    private Vec3 lastCheckPos = Vec3.ZERO;
    private int lastCheckTick = 0;
    private int stuckReplans = 0;
    private int jumpCooldown = 0;
    private int surfacePathStuckTicks = 0;
    private double lastSurfacePathDist = Double.MAX_VALUE;
    private double lastSurfacePathHorizontalDist = Double.MAX_VALUE;
    private int lastSurfacePathNodeIndex = -1;
    private double surfacePathStartY = Double.NaN;
    private boolean surfacePathMode = false;

    public OtterNavigation(Mob mob, Level level) {
        super(mob, level);
        this.setCanFloat(true);
    }

    @Override
    protected PathFinder createPathFinder(int nodes) {
        OtterNodeEvaluator evaluator = new OtterNodeEvaluator();
        evaluator.setCanPassDoors(false);
        this.nodeEvaluator = evaluator;
        return new PathFinder(this.nodeEvaluator, nodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    public void resetSurfacePathProgress() {
        this.surfacePathStuckTicks = 0;
        this.lastSurfacePathDist = Double.MAX_VALUE;
        this.lastSurfacePathHorizontalDist = Double.MAX_VALUE;
        this.lastSurfacePathNodeIndex = -1;
        this.surfacePathStartY = Double.NaN;
        this.surfacePathMode = false;
    }

    public int getSurfacePathStuckTicks() {
        return this.surfacePathStuckTicks;
    }

    public Path createSurfacePath(Set<BlockPos> targets) {
        if (targets.isEmpty()) {
            return null;
        }

        this.setMaxVisitedNodesMultiplier(SURFACE_MAX_VISITED_NODES_MULTIPLIER);
        try {
            return this.createPath(targets, SURFACE_PATH_REGION_OFFSET, false, SURFACE_PATH_REACH_ACCURACY, SURFACE_REQUIRED_PATH_LENGTH);
        } finally {
            this.resetMaxVisitedNodesMultiplier();
        }
    }

    public boolean moveToSurfacePath(Path path, double speed) {
        this.surfacePathMode = path != null;
        if (this.surfacePathMode && Double.isNaN(this.surfacePathStartY)) {
            this.surfacePathStartY = this.mob.getY();
        }
        return this.moveTo(path, speed);
    }

    @Override
    public void stop() {
        this.surfacePathMode = false;
        super.stop();
    }

    public Vec3 getSurfaceSteeringTarget(Path path, Vec3 fallbackTarget) {
        if (path != null && !path.isDone()) {
            return this.adjustSurfaceSteeringTarget(path.getNextEntityPos(this.mob));
        }

        return fallbackTarget;
    }

    public void steerSurfacePath(Path path, Vec3 steeringTarget, Vec3 targetPos, boolean followingPath, double pathSpeed, double directSpeed) {
        if (path == null) {
            return;
        }

        Vec3 moveTarget = followingPath ? steeringTarget : targetPos;
        double speed = followingPath ? pathSpeed : directSpeed;
        this.mob.getMoveControl().setWantedPosition(moveTarget.x(), moveTarget.y(), moveTarget.z(), speed);
    }

    public boolean hasClearWaterPathTo(Vec3 target) {
        return this.catchF(this.getTempMobPos(), target);
    }

    public SurfacePathProgress updateSurfacePathProgress(Path path, Vec3 steeringTarget, Vec3 finalTarget) {
        double dx = steeringTarget.x() - this.mob.getX();
        double dy = steeringTarget.y() - this.mob.getEyePosition().y();
        double dz = steeringTarget.z() - this.mob.getZ();
        double finalDx = finalTarget.x() - this.mob.getX();
        double finalDy = finalTarget.y() - this.mob.getEyePosition().y();
        double finalDz = finalTarget.z() - this.mob.getZ();

        double progressDist = dx * dx + dy * dy + dz * dz;
        double horizontalDist = dx * dx + dz * dz;
        double finalDist = finalDx * finalDx + finalDy * finalDy + finalDz * finalDz;

        if (path != null) {
            int pathNodeIndex = path.getNextNodeIndex();
            if (this.surfacePathMode && this.mob.horizontalCollision && horizontalDist > SURFACE_CLOSE_HORIZONTAL_DISTANCE) {
                this.surfacePathStuckTicks += 4;
            } else if (pathNodeIndex != this.lastSurfacePathNodeIndex) {
                this.surfacePathStuckTicks = 0;
                this.lastSurfacePathNodeIndex = pathNodeIndex;
            } else if (horizontalDist <= SURFACE_CLOSE_HORIZONTAL_DISTANCE && progressDist > this.lastSurfacePathDist - SURFACE_HORIZONTAL_PROGRESS_EPSILON) {
                this.surfacePathStuckTicks++;
            } else if (horizontalDist > SURFACE_CLOSE_HORIZONTAL_DISTANCE && horizontalDist > this.lastSurfacePathHorizontalDist - SURFACE_HORIZONTAL_PROGRESS_EPSILON) {
                this.surfacePathStuckTicks++;
            } else {
                this.surfacePathStuckTicks = 0;
            }
        } else if (progressDist > this.lastSurfacePathDist - SURFACE_HORIZONTAL_PROGRESS_EPSILON) {
            this.surfacePathStuckTicks++;
        } else {
            this.surfacePathStuckTicks = 0;
        }

        this.lastSurfacePathDist = progressDist;
        this.lastSurfacePathHorizontalDist = horizontalDist;
        return new SurfacePathProgress(progressDist, horizontalDist, finalDist, this.surfacePathStuckTicks);
    }

    public void applySurfaceMovementAssist(Vec3 wantedPosition, double travelSpeed) {
        if (!this.mob.isInWater()) {
            return;
        }

        double dx = wantedPosition.x() - this.mob.getX();
        double dy = wantedPosition.y() - this.mob.getY();
        double dz = wantedPosition.z() - this.mob.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        boolean lateralSurfacePath = this.surfacePathMode && horizontalDistance * horizontalDistance > SURFACE_CLOSE_HORIZONTAL_DISTANCE;
        boolean canAscend = !lateralSurfacePath || this.hasAscentClearance();

        Vec3 movement = this.mob.getDeltaMovement();
        if (horizontalDistance > 1.0E-4D) {
            double accel = Mth.clamp(travelSpeed * 0.14D, SURFACE_SWIM_MIN_HORIZONTAL_ACCEL, SURFACE_SWIM_MAX_HORIZONTAL_ACCEL);
            movement = movement.add(dx / horizontalDistance * accel, 0.0D, dz / horizontalDistance * accel);
        }

        if (dy < -0.03D) {
            double downwardSpeed = Mth.clamp(dy * 0.08D, SURFACE_SWIM_MAX_DOWNWARD_SPEED, SURFACE_SWIM_MIN_DOWNWARD_SPEED);
            movement = new Vec3(movement.x(), Math.min(movement.y(), downwardSpeed), movement.z());
        } else if (!canAscend) {
            movement = new Vec3(movement.x(), Math.min(movement.y(), 0.0D), movement.z());
        } else if (dy > 0.03D) {
            double upwardSpeed = Mth.clamp(dy * 0.08D, SURFACE_SWIM_MIN_UPWARD_SPEED, SURFACE_SWIM_MAX_UPWARD_SPEED);
            movement = new Vec3(movement.x(), Math.max(movement.y(), upwardSpeed), movement.z());
        } else if (dy > -0.25D) {
            movement = new Vec3(movement.x(), Math.max(movement.y(), 0.005D), movement.z());
        }

        double horizontalSpeed = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());
        if (horizontalSpeed > SURFACE_SWIM_MAX_HORIZONTAL_SPEED) {
            double scale = SURFACE_SWIM_MAX_HORIZONTAL_SPEED / horizontalSpeed;
            movement = new Vec3(movement.x() * scale, movement.y(), movement.z() * scale);
        }

        this.mob.setDeltaMovement(movement);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (!this.level.getBlockState(pos).isAir()) {
            return super.isStableDestination(pos);
        }

        BlockState below = this.level.getBlockState(pos.below());
        if (!(!below.isAir() || below.getFluidState().is(FluidTags.WATER))) {
            return false;
        }

        return this.level.getBlockState(pos).isAir() && this.level.getBlockState(pos.above()).isAir();
    }

    /**
     * Core loop for following the current path. Attempts shortcuts, moves toward the next node, and handles jumping.
     */
    @Override
    protected void followThePath() {
        // If there is no path
        if (this.path == null || this.path.isDone()) return;

        Vec3 entityPos = this.getTempMobPos();
        int nextIdx = path.getNextNodeIndex();
        double yFloor = Math.floor(entityPos.y);

        if (!this.surfacePathMode) {
            // Checks if there are any more nodes remaining on the same Y
            int lastIdx = nextIdx;
            while (lastIdx < path.getNodeCount() && path.getNode(lastIdx).y == yFloor) {
                lastIdx++;
            }

            // Computes path to the approx next node
            for (int i = lastIdx - 1; i > nextIdx; i--) {
                if (catchF(entityPos, path.getEntityPosAtNode(this.mob, i))) {
                    path.setNextNodeIndex(i);
                    break;
                }

            }
        }

        // If the entity is very close to the next node (or on an elevation change), advance the path index
        float reachThreshold = this.surfacePathMode && this.mob.isInWater() ? 1.0F : 0.8F;
        if (hasReached(path, reachThreshold) || (isAtElevationChange(path) && hasReached(path, 1.0F))) {
            path.advance();
        }

        // If we still have a node to reach, instruct the mob to move over there
        if (path.isDone()) return;

        // Move the entity
        Vec3 target = this.surfacePathMode ? this.adjustSurfaceSteeringTarget(path.getNextEntityPos(this.mob)) : path.getNextEntityPos(this.mob);
        this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.speedModifier);

        // Jump fallback (if the entity is stuck)
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }

        if (!this.mob.isInWater() && this.mob.onGround() && this.mob.horizontalCollision && jumpCooldown == 0) {
            // Direction toward the node. getDirection can disagree with the actual path heading.
            Vec3 dir = new Vec3(target.x - this.mob.getX(), 0.0, target.z - this.mob.getZ());
            if (dir.lengthSqr() > 1.0E-4) dir = dir.normalize();

            // Cell right in front of the entity
            double reach = this.mob.getBbWidth() * 0.5D + 0.6D;
            BlockPos front = BlockPos.containing(this.mob.getX() + dir.x * reach, Math.floor(this.mob.getY()), this.mob.getZ() + dir.z * reach);

            // Real height of the block (solid) in front of the entity
            VoxelShape shape =  this.level.getBlockState(front).getCollisionShape(this.level, front);
            double h = shape.isEmpty() ? 0.0D : shape.max(Direction.Axis.Y);

            // How much free space is above the entity.
            BlockPos head = front.above();
            boolean headClear = this.level.getBlockState(head).getCollisionShape(this.level, head).isEmpty();
            boolean head2Clear = this.level.getBlockState(head.above()).getCollisionShape(this.level, head.above()).isEmpty();

            // Only jump if the obstacle is "jumpable" and theres space above
            if (h > 0.01D && h <= 1.2D && headClear && head2Clear) {
                this.mob.getJumpControl().jump();
                jumpCooldown = 6;
            } else {
                // Fallback sidestep, so it can go around tight corners.
                tryPerpendicularSidestep(dir);
            }

        }

        if (this.mob.isInWater()) {
            // Fallback when the entity collides underwater
            if (this.mob.horizontalCollision && damp()) return;

            this.applyWaterExitAssist(target);

            if (this.level.getFluidState(BlockPos.containing(this.mob.getEyePosition())).is(FluidTags.WATER)) {
                double targetHorizontalDist = target.distanceToSqr(entityPos.x, target.y, entityPos.z);
                if (target.y > entityPos.y && (!this.surfacePathMode || targetHorizontalDist <= SURFACE_CLOSE_HORIZONTAL_DISTANCE) && this.hasAscentClearance()) {
                    // Forces a push to the higher Ys when the next node is in a higher Y than the current  one
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.03D, 0.0D));
                } else if (this.surfacePathMode && targetHorizontalDist > SURFACE_CLOSE_HORIZONTAL_DISTANCE && this.hasAscentClearance()) {
                    Vec3 movement = this.mob.getDeltaMovement();
                    if (movement.y() < SURFACE_LATERAL_MIN_UPWARD_SPEED) {
                        this.mob.setDeltaMovement(movement.x(), SURFACE_LATERAL_MIN_UPWARD_SPEED, movement.z());
                    }
                }
            }

            if (this.mob.onGround() && this.mob.horizontalCollision && jumpCooldown == 0) {
                BlockPos nose = this.mob.blockPosition().relative(this.mob.getDirection());
                VoxelShape shape = this.level.getBlockState(nose).getCollisionShape(this.level, nose);
                double shadeHeight = shape.isEmpty() ? 0.0D : shape.max(Direction.Axis.Y);
                if (shadeHeight > 0.01D && shadeHeight <= 1.2D
                        && this.level.getBlockState(nose.above()).getCollisionShape(this.level, nose.above()).isEmpty()
                        && this.level.getBlockState(nose.above(2)).getCollisionShape(this.level, nose.above(2)).isEmpty()) {
                    this.mob.getJumpControl().jump();
                    jumpCooldown = 6;
                }

            }

        }

        if (this.mob.tickCount - this.lastCheckTick >= PROGRESS_TICKS) {
            Vec3 curr = this.mob.position();
            double targetHorizontalDist = curr.distanceToSqr(target.x, curr.y, target.z);
            double progressCheckDist = this.surfacePathMode && targetHorizontalDist > SURFACE_CLOSE_HORIZONTAL_DISTANCE
                    ? curr.distanceToSqr(this.lastCheckPos.x, curr.y, this.lastCheckPos.z)
                    : curr.distanceToSqr(this.lastCheckPos);
            if (progressCheckDist < PROGRESS_MIN2) {
                BlockPos targetPos = this.getTargetPos();
                if (targetPos != null) {
                    if (this.surfacePathMode) {
                        Path surfacePath = this.createSurfacePath(Set.of(targetPos));
                        if (surfacePath != null && surfacePath.getNodeCount() > 0) {
                            this.moveToSurfacePath(surfacePath, Math.max(1.0D, this.speedModifier));
                        }
                    } else {
                        this.recomputePath();
                    }
                }

                if (++this.stuckReplans >= 3 && this.path != null && !this.path.isDone()) {
                    Vec3 dir = new Vec3(target.x - curr.x, 0.0, target.z - curr.z);
                    if (dir.lengthSqr() > 1.0E-4) {
                        dir = dir.normalize();
                    }

                    tryPerpendicularSidestep(dir);
                    this.stuckReplans = 0;
                }

            } else {
                this.stuckReplans = 0;
            }

            this.lastCheckPos = curr;
            this.lastCheckTick = this.mob.tickCount;
        }

    }

    private Vec3 adjustSurfaceSteeringTarget(Vec3 target) {
        double dx = target.x - this.mob.getX();
        double dy = target.y - this.mob.getY();
        double dz = target.z - this.mob.getZ();
        if (this.surfacePathMode && this.mob.isInWater() && dx * dx + dz * dz > SURFACE_CLOSE_HORIZONTAL_DISTANCE) {
            if (dy < -0.1D) {
                return target;
            }

            if (!this.hasAscentClearance()) {
                return new Vec3(target.x, Math.min(target.y, this.mob.getY()), target.z);
            }

            double y = Math.max(this.surfacePathStartY, this.mob.getY() + SURFACE_LATERAL_Y_BIAS);
            return new Vec3(target.x, y, target.z);
        }

        return target;
    }

    private boolean hasAscentClearance() {
        return this.level.noCollision(this.mob, this.mob.getBoundingBox().move(0.0D, SURFACE_ASCENT_CLEARANCE, 0.0D));
    }

    private void applyWaterExitAssist(Vec3 target) {
        if (this.surfacePathMode) {
            return;
        }

        BlockPos landTarget = this.findLandExitTarget(target);
        if (landTarget == null) {
            return;
        }

        double dx = landTarget.getX() + 0.5D - this.mob.getX();
        double dz = landTarget.getZ() + 0.5D - this.mob.getZ();
        double horizontalDistanceSqr = dx * dx + dz * dz;
        if (horizontalDistanceSqr > WATER_EXIT_START_DISTANCE || horizontalDistanceSqr < 1.0E-6D) {
            return;
        }

        double horizontalDistance = Math.sqrt(horizontalDistanceSqr);
        Vec3 movement = this.mob.getDeltaMovement().add(
                dx / horizontalDistance * WATER_EXIT_HORIZONTAL_ACCEL,
                0.0D,
                dz / horizontalDistance * WATER_EXIT_HORIZONTAL_ACCEL
        );

        double upwardSpeed = this.mob.horizontalCollision ? WATER_EXIT_COLLISION_UPWARD_SPEED : WATER_EXIT_UPWARD_SPEED;
        movement = new Vec3(movement.x(), Math.max(movement.y(), upwardSpeed), movement.z());

        double horizontalSpeed = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());
        if (horizontalSpeed > WATER_EXIT_MAX_HORIZONTAL_SPEED) {
            double scale = WATER_EXIT_MAX_HORIZONTAL_SPEED / horizontalSpeed;
            movement = new Vec3(movement.x() * scale, movement.y(), movement.z() * scale);
        }

        this.mob.setDeltaMovement(movement);
        if (this.mob.horizontalCollision && this.jumpCooldown == 0) {
            this.mob.getJumpControl().jump();
            this.jumpCooldown = 6;
        }
    }

    private BlockPos findLandExitTarget(Vec3 target) {
        BlockPos targetPos = BlockPos.containing(target);
        if (this.isLandExitTarget(targetPos)) {
            return targetPos;
        }

        BlockPos above = targetPos.above();
        if (this.isLandExitTarget(above)) {
            return above;
        }

        BlockPos below = targetPos.below();
        if (this.isLandExitTarget(below)) {
            return below;
        }

        return null;
    }

    private boolean isLandExitTarget(BlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        BlockState aboveState = this.level.getBlockState(pos.above());
        return this.level.getFluidState(pos).isEmpty()
                && this.level.getFluidState(pos.below()).isEmpty()
                && blockState.getCollisionShape(this.level, pos).isEmpty()
                && aboveState.getCollisionShape(this.level, pos.above()).isEmpty()
                && this.hasCollisionBelow(pos);
    }

    private boolean hasReached(Path path, float threshold) {
        Vec3 pos = path.getNextEntityPos(this.mob);

        if (Math.abs(this.mob.getX() - pos.x) >= threshold) return false;
        if (Math.abs(this.mob.getZ() - pos.z) >= threshold) return false;

        return Math.abs(this.mob.getY() - pos.y) <= 1.001D;
    }

    private boolean isAtElevationChange(Path path) {
        int idx = path.getNextNodeIndex();
        int end = Math.min(path.getNodeCount(), idx + Mth.ceil(this.mob.getBbWidth() * 0.5F) + 1);
        int y = path.getNode(idx).y;

        for (int i = idx + 1; i < end; i++) {
            if (path.getNode(i).y != y) {
                return true;
            }

        }

        return false;
    }

    /**
     * 3D DDA algorithm to check if a straight line to a node is clear
     */
    private boolean catchF(Vec3 from, Vec3 to) {
        var vec = to.subtract(from);

        float maxT = (float) vec.length();
        if (maxT < 1.0E-6F) return true; // too close to worry about

        // Normalized direction
        float dx = (float) (vec.x / maxT);
        float dy = (float) (vec.y / maxT);
        float dz = (float) (vec.z / maxT);

        int currentX = Mth.floor(from.x);
        int currentY = Mth.floor(from.y);
        int currentZ = Mth.floor(from.z);

        // Compute step for each axis
        int stepX;
        int stepY;
        int stepZ;
        float tNextX;
        float tNextY;
        float tNextZ;
        float tDeltaX;
        float tDeltaY;
        float tDeltaZ;

        // X axis
        if (Math.abs(dx) < EPSILON) {
            tDeltaX = Float.POSITIVE_INFINITY;
            tNextX = Float.POSITIVE_INFINITY;
            stepX = 0;
        } else {
            stepX = dx > 0 ? 1 : -1;
            float voxelBoundaryX = stepX > 0 ? Mth.floor(from.x) + 1 : Mth.floor(from.x);
            tDeltaX = 1.0F / Math.abs(dx);
            tNextX = (float) ((voxelBoundaryX - from.x) / dx);
        }

        // Y axis
        if (Math.abs(dy) < EPSILON) {
            tDeltaY = Float.POSITIVE_INFINITY;
            tNextY = Float.POSITIVE_INFINITY;
            stepY = 0;
        } else {
            stepY = dy > 0 ? 1 : -1;
            float voxelBoundaryY = stepY > 0 ? Mth.floor(from.y) + 1 : Mth.floor(from.y);
            tDeltaY = 1.0F / Math.abs(dy);
            tNextY = (float) ((voxelBoundaryY - from.y) / dy);
        }

        // Z axis
        if (Math.abs(dz) < EPSILON) {
            tDeltaZ = Float.POSITIVE_INFINITY;
            tNextZ = Float.POSITIVE_INFINITY;
            stepZ = 0;
        } else {
            stepZ = dz > 0 ? 1 : -1;
            float voxelBoundaryZ = stepZ > 0 ? Mth.floor(from.z) + 1 : Mth.floor(from.z);
            tDeltaZ = 1.0F / Math.abs(dz);
            tNextZ = (float) ((voxelBoundaryZ - from.z) / dz);
        }

        var pos = new BlockPos.MutableBlockPos();
        float t = 0.0F;
        boolean waterPath = this.mob.isInWater();

        // March along the ray until we exceed the target distance
        while (t <= maxT) {
            if (tNextX < tNextY) {
                if (tNextX < tNextZ) {
                    currentX += stepX;
                    t = tNextX;
                    tNextX += tDeltaX;
                }
                else {
                    currentZ += stepZ;
                    t = tNextZ;
                    tNextZ += tDeltaZ;
                }
            } else {
                if (tNextY < tNextZ) {
                    currentY += stepY;
                    t = tNextY;
                    tNextY += tDeltaY;
                }
                else { currentZ += stepZ;
                    t = tNextZ;
                    tNextZ += tDeltaZ;
                }

            }

            pos.set(currentX, currentY, currentZ);
            BlockState blockState = this.level.getBlockState(pos);
            boolean isPathfindable;
            if (waterPath) {
                isPathfindable = this.isClearWaterPathCell(pos, blockState);
            } else {
                var immutablePos = pos.immutable();

                // Caches nodes to avoid recomputing them again
                Boolean cachedPathfindable = cache.getIfPresent(immutablePos);
                if (cachedPathfindable == null) {
                    isPathfindable = blockState.isPathfindable(PathComputationType.LAND);
                    cache.put(immutablePos, isPathfindable);
                } else {
                    isPathfindable = cachedPathfindable;
                }
            }
            if (!isPathfindable)
                return false;

            // Also rejects dangerous or blocked path types.
            var pathType = this.nodeEvaluator.getPathType(this.mob, pos);
            float malus = this.mob.getPathfindingMalus(pathType);

            if (malus < 0.0F
                    || malus >= 8.0F
                    || pathType == PathType.FIRE
                    || pathType == PathType.FIRE_IN_NEIGHBOR)
                return false;

        }

        return true;
    }

    private boolean isClearWaterPathCell(BlockPos pos, BlockState blockState) {
        return blockState.getCollisionShape(this.level, pos).isEmpty()
                && (this.level.getFluidState(pos).is(FluidTags.WATER) || blockState.isAir());
    }

    private void tryPerpendicularSidestep(Vec3 dirNorm) {
        double px = -dirNorm.z;
        double pz = dirNorm.x;
        double side = this.mob.getRandom().nextBoolean() ? 1.0D : -1.0D;

        Vec3 curr = this.mob.position();
        if (this.tryPerpendicularSide(dirNorm, px, pz, side, curr)) {
            return;
        }

        this.tryPerpendicularSide(dirNorm, px, pz, -side, curr);
    }

    private boolean tryPerpendicularSide(Vec3 dirNorm, double px, double pz, double side, Vec3 curr) {
        BlockPos step = BlockPos.containing(curr.x + px * side * 1.5D, curr.y, curr.z + pz * side * 1.5D);
        if (this.surfacePathMode && this.mob.isInWater()) {
            return this.tryMoveToClearWater(step, Math.max(1.0D, this.speedModifier));
        }

        if (this.isStableDestination(step)) {
            this.moveTo(step.getX() + 0.5D, step.getY(), step.getZ() + 0.5D, 1.1D);
            return true;
        }

        return false;
    }

    private boolean tryMoveToClearWater(BlockPos step, double speed) {
        BlockState blockState = this.level.getBlockState(step);
        if (!this.isClearWaterPathCell(step, blockState) || !this.level.getFluidState(step).is(FluidTags.WATER)) {
            return false;
        }

        this.mob.getMoveControl().setWantedPosition(step.getX() + 0.5D, step.getY() + 0.5D, step.getZ() + 0.5D, speed);
        return true;
    }

    private boolean damp() {
        BlockPos currentPos = this.mob.blockPosition();
        if (!this.level.getFluidState(currentPos).is(FluidTags.WATER)) return false;

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            for (int i = 1; i <= 2; i++) {
                BlockPos edge = currentPos.relative(dir, i);
                if (!(this.level.getFluidState(edge).is(FluidTags.WATER) && this.level.getBlockState(edge.above()).isAir() && this.level.getBlockState(edge.above(2)).isAir())) continue;

                BlockPos land = edge.relative(dir);
                if (this.level.getBlockState(land).isAir() && this.hasCollisionBelow(land) && this.isStableDestination(land)) {
                    this.moveTo(land.getX() + 0.5D, land.getY(), land.getZ() + 0.5D, Math.max(1.0D, this.speedModifier));
                    return true;
                }
            }

        }

        return false;
    }

    private boolean hasCollisionBelow(BlockPos pos) {
        BlockPos below = pos.below();
        return !this.level.getBlockState(below).getCollisionShape(this.level, below).isEmpty();
    }

    public static final class SurfacePathProgress {
        private final double progressDist;
        private final double horizontalDist;
        private final double finalDist;
        private final int stuckTicks;

        private SurfacePathProgress(double progressDist, double horizontalDist, double finalDist, int stuckTicks) {
            this.progressDist = progressDist;
            this.horizontalDist = horizontalDist;
            this.finalDist = finalDist;
            this.stuckTicks = stuckTicks;
        }

        public double progressDist() {
            return this.progressDist;
        }

        public double horizontalDist() {
            return this.horizontalDist;
        }

        public double finalDist() {
            return this.finalDist;
        }

        public int stuckTicks() {
            return this.stuckTicks;
        }
    }

}
