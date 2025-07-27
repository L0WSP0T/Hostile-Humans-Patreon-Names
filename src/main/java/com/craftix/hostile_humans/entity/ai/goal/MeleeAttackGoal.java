package com.craftix.hostile_humans.entity.ai.goal;

import com.craftix.hostile_humans.entity.HumanEntity;
import com.craftix.hostile_humans.entity.entities.Human;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

import static com.craftix.hostile_humans.entity.HumanMobEntityData.DATA_SIT_POS;

public class MeleeAttackGoal extends HumanGoal {
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 5L;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private final boolean canPenalize = false;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;

    public MeleeAttackGoal(HumanEntity humanEntity, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(humanEntity);
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob instanceof Human human && human.isFleeing)
            return false;

        long gameTime = this.mob.level.getGameTime();
        if (gameTime - this.lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS) {
            return false;
        } else {
            this.lastCanUseCheck = gameTime;
            if (!this.mob.canAttack(mob.getTarget())) {
                return false;
            } else {
                LivingEntity livingEntity = this.mob.getTarget();
                if (canPenalize) {
                    if (--this.ticksUntilNextPathRecalculation <= 0) {
                        this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                        this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                        return this.path != null;
                    } else {
                        return true;
                    }
                }
                this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getAttackReachSqr(livingEntity) >= this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (mob instanceof Human human && human.isFleeing)
            return false;

        LivingEntity livingEntity = this.mob.getTarget();
        if (!this.mob.canAttack(mob.getTarget())) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else return this.mob.isWithinRestriction(livingEntity.blockPosition());
    }

    @Override
    public void start() {
        boolean isSit = mob.isOrderedToSit();
        if (!isSit) this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.mob.setAggressive(false);

        boolean isSit = mob.isOrderedToSit();

        if (isSit) {
            mob.setOrderedToPosition(mob.getEntityData().get(DATA_SIT_POS));
        } else {
            mob.getMoveControl().setWantedPosition(mob.position().x(), mob.position().y(), mob.position().z(), 1);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            if (livingEntity instanceof Player player) {
                ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());

                if (itemStack.is(mob.getTameItem()) || mob.isOwnedBy(player)) {
                    stop();
                    return;
                }
            }
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            double distance = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingEntity.getX();
                this.pathedTargetY = livingEntity.getY();
                this.pathedTargetZ = livingEntity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingEntity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }
                if (distance > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (distance > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingEntity, distance);
        }
    }

    protected void checkAndPerformAttack(LivingEntity livingEntity, double attackDistance) {

        double distance = this.getAttackReachSqr(livingEntity);
        if (attackDistance <= distance && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            if (mob.isBlocking()) {
                mob.stopUsingItem();
            }
            if (this.mob instanceof Human human) {
                human.lastCombatTime = human.tickCount;
            }
            this.mob.doHurtTarget(livingEntity);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(mob.getRandom().nextInt(5, 30));
    }

    protected double getAttackReachSqr(LivingEntity livingEntity) {
        return this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 3.0F + livingEntity.getBbWidth();
    }
}
