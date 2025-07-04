/*
 * -------------------------------------------------------------------
 * Nox
 * Copyright (c) 2025 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.nox.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.util.NoxUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntityMixin {

    @Shadow
    private int blockBreakingCooldown;

    private int nox$reinforcementsCooldown = NoxConfig.witherCallReinforcementsCooldown;

    private void nox$witherBreakBlocks() {
        if (!this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || !NoxConfig.destructiveWither) return;
        Box box = this.getBoundingBox().expand(1, 0, 1);

        int i = MathHelper.floor(box.minX);
        int j = MathHelper.floor(box.minY);
        int k = MathHelper.floor(box.minZ);
        int l = MathHelper.floor(box.maxX);
        int m = MathHelper.floor(box.maxY);
        int n = MathHelper.floor(box.maxZ);
        boolean bl = false;

        for (int o = i; o <= l; ++o) {
            for (int p = j; p <= m; ++p) {
                for (int q = k; q <= n; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.getWorld().getBlockState(blockPos);
                    if (!blockState.isAir() && !blockState.isIn(BlockTags.WITHER_IMMUNE)) {
                        if (NoxUtil.isAtWoodLevel(blockState)) {
                            bl = this.getWorld().removeBlock(blockPos, false) || bl;
                        } else {
                            bl = this.getWorld().breakBlock(blockPos, true, (WitherEntity) (Object) this) || bl;
                        }
                    }
                }
            }
        }

        if (bl) {
            this.getWorld().syncWorldEvent(null, 1022, this.getBlockPos(), 0);
        }

    }

    @Inject(method = "onSummoned", at = @At("TAIL"))
    private void nox$onSummoned(CallbackInfo ci) {
        this.setHealth(this.getMaxHealth());
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    public void nox$witherNoVanillaBreak(CallbackInfo ci) {
        if (NoxConfig.witherRapidlyBreaksSurroundingBlocks)
            this.blockBreakingCooldown = NoxConfig.witherBlockBreakingCooldown;
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    public void nox$witherBetterBreak(CallbackInfo ci) {
        if (NoxConfig.witherRapidlyBreaksSurroundingBlocks)
            nox$witherBreakBlocks();
    }

    @Override
    public void nox$onTick(CallbackInfo ci) {
        LivingEntity target = this.getTarget();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (nox$reinforcementsCooldown > 0) {
                nox$reinforcementsCooldown--;
            } else if (target != null && target.squaredDistanceTo((WitherEntity) (Object) this) <= MathHelper.square(NoxConfig.witherReinforcementsTriggerRadius)) {
                nox$reinforcementsCooldown = NoxConfig.witherCallReinforcementsCooldown;
                for (int i = 0; i < NoxConfig.witherReinforcementsGroupSize; i++) {
                    WitherSkeletonEntity skeleton = EntityType.WITHER_SKELETON.create(serverWorld);
                    if (skeleton != null) {
                        skeleton.setPos(this.getX() + this.getRandom().nextBetween(-2, 2), this.getY(), this.getZ() + this.getRandom().nextBetween(-2, 2));
                        skeleton.initialize(serverWorld, this.getWorld().getLocalDifficulty(skeleton.getBlockPos()), SpawnReason.REINFORCEMENT, null, null);
                        serverWorld.spawnEntityAndPassengers(skeleton);
                        skeleton.setTarget(target);
                        skeleton.playSpawnEffects();
                    }
                }
            }
        }
    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        //Non-applicable
    }

    @Override
    public void nox$hostileAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addTemporaryModifier(new EntityAttributeModifier("Nox: Wither bonus", NoxConfig.witherBaseHealthMultiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.setHealth(this.getMaxHealth());
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addTemporaryModifier(new EntityAttributeModifier("Nox: Wither bonus", NoxConfig.witherFollowRangeMultiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
    }

    @Override
    public void nox$shouldTakeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        super.nox$shouldTakeDamage(source, amount, cir);
        if ((source.isOf(DamageTypes.IN_WALL) && !NoxConfig.withersSuffocate)) {
            cir.setReturnValue(false);
        }
    }

}
