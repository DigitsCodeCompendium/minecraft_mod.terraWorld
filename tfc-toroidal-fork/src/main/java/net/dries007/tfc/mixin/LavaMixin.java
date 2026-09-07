/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

@Mixin(LavaFluid.class)
public class LavaMixin
{
    @ModifyExpressionValue(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean spreadingToWaterTypesMakesStone(boolean original, @Local(ordinal = 1) FluidState fluidState)
    {
        return original || Helpers.isFluid(fluidState, TFCTags.Fluids.ANY_INFINITE_WATER);
    }
}
