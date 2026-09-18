package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.world.river.MidpointFractal;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = MidpointFractal.class, remap = false)
public abstract class MidpointFractalMixin {
    @Shadow @Final public double[] segments;

    @ModifyVariable(
            method = {"maybeIntersect", "intersectDistance", "calculateFlow", "intersectIndex"},
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double tfcToroidal$nearestRiverX(double gridX) {
        final double center = 0.5d * (segments[0] + segments[segments.length - 2]);
        return center + TfcTopology.shortestGridDelta(gridX - center, Direction.Axis.X);
    }

    @ModifyVariable(
            method = {"maybeIntersect", "intersectDistance", "calculateFlow", "intersectIndex"},
            at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double tfcToroidal$nearestRiverZ(double gridZ) {
        final double center = 0.5d * (segments[1] + segments[segments.length - 1]);
        return center + TfcTopology.shortestGridDelta(gridZ - center, Direction.Axis.Z);
    }
}
