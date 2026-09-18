package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraskills.toroidalcompat.worldgen.ToroidalTFCChunkGenerator;
import net.dries007.tfc.world.TFCChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TFCChunkGenerator.class, remap = false)
public abstract class TFCChunkGeneratorMixin {
    @WrapOperation(
            method = "initRandomState",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/TFCChunkGenerator;copy()Lnet/dries007/tfc/world/TFCChunkGenerator;"))
    private TFCChunkGenerator tfcToroidal$preserveToroidalCopy(
            TFCChunkGenerator instance, Operation<TFCChunkGenerator> original) {
        return instance instanceof ToroidalTFCChunkGenerator toroidal
                ? toroidal.copyToroidal()
                : original.call(instance);
    }
}
