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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermiteEntity.class)
public abstract class EndermiteEntityMixin extends HostileEntityMixin {

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        if(NoxConfig.endermiteMoveSpeedMultiplier > 1) {
            EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (attr != null)
                attr.addTemporaryModifier(new EntityAttributeModifier("Nox: Endermite bonus", NoxConfig.endermiteMoveSpeedMultiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public void nox$onSuccessfulAttack(LivingEntity target) {
        if (NoxConfig.endermiteAttacksMakeTargetTeleport && target.getWorld() instanceof ServerWorld serverWorld) {
            double d = target.getX();
            double e = target.getY();
            double f = target.getZ();

            for (int i = 0; i < 16; ++i) {
                double g = target.getX() + (target.getRandom().nextDouble() - 0.5D) * 16.0D;
                double h = MathHelper.clamp(target.getY() + (double) (target.getRandom().nextInt(16) - 8), serverWorld.getBottomY(), serverWorld.getBottomY() + serverWorld.getLogicalHeight() - 1);
                double j = target.getZ() + (target.getRandom().nextDouble() - 0.5D) * 16.0D;

                if (target.hasVehicle()) {
                    target.stopRiding();
                }

                if (target.teleport(g, h, j, true)) {
                    serverWorld.playSound(null, d, e, f, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
                    target.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }

    @Override
    public void nox$shouldTakeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((source.isOf(DamageTypes.FALL))
            cir.setReturnValue(NoxConfig.endermitesImmuneToFallDamage);
        if ((source.isOf(DamageTypes.IN_WALL)))
            cir.setReturnValue(!NoxConfig.endermitesCanSuffocate);
    }
}
