package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = OverworldClimateModel.class, remap = false)
public abstract class OverworldClimateModelMixin {
    @Shadow @Final protected float temperatureScale;

    @ModifyReturnValue(method = "hemisphereScale", at = @At("RETURN"))
    private float tfcToroidal$effectiveHemisphereScale(float original) {
        return TfcTopology.climateScaleBlocks(Direction.Axis.Z, Math.round(original));
    }

    @ModifyVariable(
            method = {
                    "getAverageTemperature", "getInstantTemperature", "getAverageRainfall",
                    "getRainfallVariance", "getInstantRainfall", "getBaseGroundwater",
                    "getAverageGroundwater", "getInstantGroundwater", "getFog", "getWind"
            },
            at = @At("HEAD"), argsOnly = true)
    private BlockPos tfcToroidal$foldClimatePosition(BlockPos pos) {
        return new BlockPos(
                TfcCoordinateFold.block(pos.getX(), Direction.Axis.X),
                pos.getY(),
                TfcCoordinateFold.block(pos.getZ(), Direction.Axis.Z));
    }

    @ModifyVariable(method = "calculateMonthlyTemperature", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$offsetMonthlyLatitude(int z) {
        return z + TfcTopology.climateZOffsetBlocks(Math.round(temperatureScale));
    }

    @Redirect(
            method = "calculateMonthlyTemperature",
            at = @At(value = "FIELD", target = "Lnet/dries007/tfc/util/climate/OverworldClimateModel;temperatureScale:F"))
    private float tfcToroidal$effectiveMonthlyScale(OverworldClimateModel instance) {
        return instance.hemisphereScale();
    }

    @ModifyExpressionValue(
            method = "calculateMonthlyTemperature",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/Helpers;triangle(FFFF)F"))
    private float tfcToroidal$mirrorMonthlyLatitude(float factor) {
        return TfcTopology.mirrorsSouthernHemisphere() ? Math.abs(factor) : factor;
    }
}
