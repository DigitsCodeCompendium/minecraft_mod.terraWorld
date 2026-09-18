package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraskills.toroidalcompat.worldgen.PeriodicField2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.builder.ShoreAndOceanSurfaceBuilder;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShoreAndOceanSurfaceBuilder.class, remap = false)
public abstract class ShoreAndOceanSurfaceBuilderMixin {
    @Shadow @Final private NormalNoise icebergPillarNoise;
    @Shadow @Final private NormalNoise icebergPillarRoofNoise;

    @WrapOperation(
            method = "buildSurface",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/noise/Noise2D;noise(DD)D"))
    private double tfcToroidal$samplePeriodicShoreTide(
            Noise2D noise, double x, double z, Operation<Double> original) {
        return PeriodicField2D.sample(x, z, (sampleX, sampleZ) -> original.call(noise, sampleX, sampleZ));
    }

    @WrapOperation(
            method = "frozenOceanExtension",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/synth/NormalNoise;getValue(DDD)D"))
    private double tfcToroidal$samplePeriodicIcebergNoise(
            NormalNoise noise, double scaledX, double y, double scaledZ, Operation<Double> original) {
        final double scale = noise == icebergPillarNoise ? 1.28
                : noise == icebergPillarRoofNoise ? 1.17
                : 1.0;
        return PeriodicField2D.sample(scaledX / scale, scaledZ / scale,
                (x, z) -> original.call(noise, x * scale, y, z * scale));
    }

    @WrapOperation(
            method = "placeSeaIce",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/noise/Noise2D;noise(DD)D"))
    private double tfcToroidal$samplePeriodicSeaIce(
            Noise2D noise, double x, double z, Operation<Double> original) {
        return PeriodicField2D.sample(x, z, (sampleX, sampleZ) -> original.call(noise, sampleX, sampleZ));
    }
}
