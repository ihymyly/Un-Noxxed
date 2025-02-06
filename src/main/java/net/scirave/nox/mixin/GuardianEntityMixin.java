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
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuardianEntity.class)
public abstract class GuardianEntityMixin extends HostileEntityMixin {
    private static final BlockState nox$WATER = Blocks.WATER.getDefaultState();
    private static final BlockState nox$FLOWING_WATER = nox$WATER.with(Properties.LEVEL_15, 8);
    private static final BlockState nox$SMALL_WATER = nox$WATER.with(Properties.LEVEL_15, 7);

    @Override
    public void nox$onDamaged(DamageSource source, float amount, CallbackInfo ci) {
    if (NoxConfig.guardiansPlaceWaterOnDeath && !this.getWorld().isClient) {
            BlockPos pos = this.getBlockPos();
            BlockState state = this.getWorld().getBlockState(pos);
            if (state != nox$WATER && state.isReplaceable()) {
                if (NoxConfig.guardianDeathLeavesWaterSource)
                    this.getWorld().setBlockState(pos, nox$WATER);
                else {
                    // order matters
                    state = this.getWorld().getBlockState(pos.up());
                    this.getWorld().setBlockState(pos, nox$FLOWING_WATER);
                    if (state != nox$WATER && state.isReplaceable())
                        this.getWorld().setBlockState(pos.up(), nox$SMALL_WATER);
                }
            }
        }
    }
}
