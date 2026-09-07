/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.dries007.tfc.common.TFCTags;

@Mixin(SwimNodeEvaluator.class)
public class SwimNodeEvaluatorMixin
{
    @ModifyArg(method = "getPathTypeOfMob", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> checkForTFCFluids(TagKey<Fluid> tag)
    {
        if (tag == FluidTags.WATER)
        {
            return TFCTags.Fluids.ANY_INFINITE_WATER;
        }
        return tag;
    }
}
