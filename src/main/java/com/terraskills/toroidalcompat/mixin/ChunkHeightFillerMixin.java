package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.ChunkHeightFiller;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChunkHeightFiller.class, remap = false)
public abstract class ChunkHeightFillerMixin {
    @ModifyVariable(method = "setupColumn", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$foldColumnX(int blockX) {
        return TfcCoordinateFold.block(blockX, Direction.Axis.X);
    }

    @ModifyVariable(method = "setupColumn", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int tfcToroidal$foldColumnZ(int blockZ) {
        return TfcCoordinateFold.block(blockZ, Direction.Axis.Z);
    }
}
