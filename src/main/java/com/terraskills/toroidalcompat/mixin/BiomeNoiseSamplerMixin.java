package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.PeriodicField2D;
import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.noise.Noise2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BiomeNoiseSampler.class, remap = false)
public interface BiomeNoiseSamplerMixin {
    @ModifyVariable(method = "fromHeightNoise", at = @At("HEAD"), argsOnly = true)
    private static Noise2D tfcToroidal$makeCompleteHeightFieldPeriodic(Noise2D original) {
        return (x, z) -> PeriodicField2D.sample(x, z, original::noise);
    }
}
