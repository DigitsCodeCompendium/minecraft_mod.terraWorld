package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.client.overworld.SolarCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = SolarCalculator.class, remap = false)
public abstract class SolarCalculatorMixin {
    @ModifyArgs(
            method = "getLatitude(IF)F",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/Helpers;triangle(FFFF)F"))
    private static void tfcToroidal$offsetLatitude(Args args) {
        final float triangleScale = args.get(2);
        if (triangleScale != 0f) {
            final int hemisphereScale = Math.round(1f / (4f * triangleScale));
            args.set(3, args.<Float>get(3) + TfcTopology.climateZOffsetBlocks(hemisphereScale));
        }
    }

    @ModifyReturnValue(method = "getLatitude", at = @At("RETURN"))
    private static float tfcToroidal$mirrorLatitude(float latitude) {
        return TfcTopology.mirrorsSouthernHemisphere() ? Math.abs(latitude) : latitude;
    }

    @Inject(method = "getInNorthernHemisphere(IF)Z", at = @At("HEAD"), cancellable = true)
    private static void tfcToroidal$mirrorHemisphere(
            int z, float hemisphereScale, CallbackInfoReturnable<Boolean> cir) {
        if (TfcTopology.mirrorsSouthernHemisphere()) cir.setReturnValue(true);
    }
}
