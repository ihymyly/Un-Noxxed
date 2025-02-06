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
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.world.World;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.goals.Nox$MineBlockGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VindicatorEntity.class)
public abstract class VindicatorEntityMixin extends HostileEntityMixin {

    @Override
    public void nox$initGoals(CallbackInfo ci) {
        this.goalSelector.add(1, new Nox$MineBlockGoal((VindicatorEntity) (Object) this));
    }

    @Override
    public void nox$modifyAttributes(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (NoxConfig.vindicatorKnockbackResistanceBonus > 0) {
            EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            if (attr != null)
                attr.addTemporaryModifier(new EntityAttributeModifier("Nox: Vindicator bonus", NoxConfig.vindicatorKnockbackResistanceBonus, EntityAttributeModifier.Operation.ADDITION));
        }
        if (NoxConfig.vindicatorSpeedBonus > 1) {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(new EntityAttributeModifier("Nox: Vindicator speed bonus", NoxConfig.vindicatorSpeedBonus - 1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
    @Override
    public boolean nox$isAllowedToMine() {
        return NoxConfig.vindicatorsBreakBlocks;
    }

}
