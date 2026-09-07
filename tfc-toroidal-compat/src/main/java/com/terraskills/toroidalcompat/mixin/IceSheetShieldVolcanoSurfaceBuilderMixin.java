package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.PeriodicField2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.builder.IceSheetShieldVolcanoSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = IceSheetShieldVolcanoSurfaceBuilder.class, remap = false)
public abstract class IceSheetShieldVolcanoSurfaceBuilderMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Noise2D tfcToroidal$makeGlacierBasePeriodic(Noise2D original) {
        return (x, z) -> PeriodicField2D.sample(x, z, original::noise);
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static Noise2D tfcToroidal$makeGlacierSurfacePeriodic(Noise2D original) {
        return (x, z) -> PeriodicField2D.sample(x, z, original::noise);
    }
}
