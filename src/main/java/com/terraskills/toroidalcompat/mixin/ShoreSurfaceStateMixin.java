package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraskills.toroidalcompat.worldgen.PeriodicField2D;
import net.dries007.tfc.world.noise.Noise2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Wraps the sand type, sandstone type, and sand/gravel shore material fields. */
@Mixin(targets = {
        "net.dries007.tfc.world.surface.SurfaceStates$3",
        "net.dries007.tfc.world.surface.SurfaceStates$4",
        "net.dries007.tfc.world.surface.SurfaceStates$7",
        "net.dries007.tfc.world.surface.SurfaceStates$8"
}, remap = false)
public abstract class ShoreSurfaceStateMixin {
    @WrapOperation(
            method = "getState",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/noise/Noise2D;noise(DD)D"))
    private double tfcToroidal$samplePeriodicShoreMaterial(
            Noise2D noise, double x, double z, Operation<Double> original) {
        return PeriodicField2D.sample(x, z, (sampleX, sampleZ) -> original.call(noise, sampleX, sampleZ));
    }
}
