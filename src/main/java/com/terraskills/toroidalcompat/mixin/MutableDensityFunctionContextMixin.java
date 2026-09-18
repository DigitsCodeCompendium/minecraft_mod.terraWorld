package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.MutableDensityFunctionContext;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MutableDensityFunctionContext.class, remap = false)
public abstract class MutableDensityFunctionContextMixin {
    @ModifyReturnValue(method = "blockX", at = @At("RETURN"))
    private int tfcToroidal$foldCaveX(int blockX) {
        return TfcCoordinateFold.block(blockX, Direction.Axis.X);
    }

    @ModifyReturnValue(method = "blockZ", at = @At("RETURN"))
    private int tfcToroidal$foldCaveZ(int blockZ) {
        return TfcCoordinateFold.block(blockZ, Direction.Axis.Z);
    }
}
