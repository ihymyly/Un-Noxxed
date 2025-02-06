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

import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.scirave.nox.config.NoxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ProjectileEntityMixin {

    @ModifyArg(method = "applyLingeringPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadius(F)V"))
    public float nox$witchBiggerPotionRadius(float original) {
        if(NoxConfig.witchesUseLingeringPotions){
            return original * NoxConfig.witchLingeringPotionRadiusMultiplier;
        }
        return original;
    }

    @ModifyArg(method = "applyLingeringPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setWaitTime(I)V"))
    public int nox$witchFasterCloudWindup(int original) {
        return original / NoxConfig.witchPotionWindupDivisor;
    }

}
