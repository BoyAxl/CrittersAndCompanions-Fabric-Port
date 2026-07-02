package com.github.eterdelta.crittersandcompanions.entity.brain;

import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;

public class AnimatedDelayedRangedAttackGoal<T extends Mob & GeoEntity & RangedAttackMob> extends Goal {
    private final T owner;
    private final double speedModifier;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;
    private final String animationControllerName;
    private final String animationName;
    private final int windUpTicks;

    private @Nullable LivingEntity target;
    private int attackTime = -1;
    private int seeTime;
    private int ticksUntilAttack;
    private boolean windingUp;

    public AnimatedDelayedRangedAttackGoal(T owner, double speedModifier, int attackInterval, float attackRadius, String animationControllerName, String animationName, int windUpTicks) {
        this(owner, speedModifier, attackInterval, attackInterval, attackRadius, animationControllerName, animationName, windUpTicks);
    }

    public AnimatedDelayedRangedAttackGoal(T owner, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, String animationControllerName, String animationName, int windUpTicks) {
        this.owner = owner;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.animationControllerName = animationControllerName;
        this.animationName = animationName;
        this.windUpTicks = windUpTicks;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity bestTarget = this.owner.getTarget();
        if (bestTarget != null && bestTarget.isAlive()) {
            this.target = bestTarget;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.target != null && this.target.isAlive() && !this.owner.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
        this.windingUp = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        double targetDistSqr = this.owner.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean hasLineOfSight = this.owner.getSensing().hasLineOfSight(this.target);
        this.seeTime = hasLineOfSight ? this.seeTime + 1 : 0;
        this.owner.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.windingUp) {
            this.owner.getNavigation().stop();
            this.ticksUntilAttack--;
            if (this.ticksUntilAttack <= 0) {
                float distanceFactor = (float) Math.sqrt(targetDistSqr) / this.attackRadius;
                float power = Mth.clamp(distanceFactor, 0.1F, 1.0F);
                this.owner.performRangedAttack(this.target, power);
                this.attackTime = Mth.floor(distanceFactor * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin);
                this.windingUp = false;
            }
            return;
        }

        if (targetDistSqr <= this.attackRadiusSqr && this.seeTime >= 5) {
            this.owner.getNavigation().stop();
        } else {
            this.owner.getNavigation().moveTo(this.target, this.speedModifier);
        }

        if (--this.attackTime == 0) {
            if (!hasLineOfSight) {
                return;
            }
            this.windingUp = true;
            this.ticksUntilAttack = this.windUpTicks;
            this.owner.triggerAnim(this.animationControllerName, this.animationName);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(targetDistSqr) / this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
        }
    }
}
