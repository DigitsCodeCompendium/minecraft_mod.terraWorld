/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.compat.sodium;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.dries007.tfc.common.TFCTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Sodium uses its own fluid renderer, which colors water differently from other fluids. This makes
 * TFC's water types have an unwanted visual difference, so make Sodium count them as water too.
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer")
public class DefaultFluidRendererMixin
{
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"), require = 0)
    private TagKey<Fluid> colorWaterTypesAsWater(TagKey<Fluid> tag)
    {
        return TFCTags.Fluids.ANY_INFINITE_WATER;
    }
}
