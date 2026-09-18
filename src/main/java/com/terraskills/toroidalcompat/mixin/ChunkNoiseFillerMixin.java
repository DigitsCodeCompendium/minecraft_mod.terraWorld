package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.ChunkNoiseFiller;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkNoiseFiller.class, remap = false)
public abstract class ChunkNoiseFillerMixin {
    @Redirect(
            method = "fillFromNoise",
            at = @At(value = "FIELD", target = "Lnet/dries007/tfc/world/ChunkNoiseFiller;blockX:I", opcode = 181))
    private void tfcToroidal$foldFilledBlockX(ChunkNoiseFiller instance, int value) {
        ((ChunkHeightFillerAccessor) instance).tfcToroidal$setBlockX(
                TfcCoordinateFold.block(value, Direction.Axis.X));
    }

    @Redirect(
            method = "fillFromNoise",
            at = @At(value = "FIELD", target = "Lnet/dries007/tfc/world/ChunkNoiseFiller;blockZ:I", opcode = 181))
    private void tfcToroidal$foldFilledBlockZ(ChunkNoiseFiller instance, int value) {
        ((ChunkHeightFillerAccessor) instance).tfcToroidal$setBlockZ(
                TfcCoordinateFold.block(value, Direction.Axis.Z));
    }
}
