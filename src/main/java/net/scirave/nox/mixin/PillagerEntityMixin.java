/*
 * -------------------------------------------------------------------
 * Nox
 * Copyright (c) 2024 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.nox.mixin;

import net.minecraft.entity.mob.PillagerEntity;
import net.scirave.nox.config.NoxConfig;
import net.scirave.nox.goals.Nox$MineBlockGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PillagerEntity.class)
public abstract class PillagerEntityMixin extends HostileEntityMixin {

    @Override
    public void nox$initGoals(CallbackInfo ci) {
        this.goalSelector.add(4, new Nox$MineBlockGoal((PillagerEntity) (Object) this));
    }

    @Override
    public boolean nox$isAllowedToMine() {
        return NoxConfig.pillagersBreakBlocks;
    }

}
