package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.biome.RegionBiomeSource;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RegionBiomeSource.class, remap = false)
public abstract class RegionBiomeSourceMixin {
    @ModifyVariable(method = "getBiomeExtensionNoRiver", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$foldBiomeQuartX(int quartX) {
        return TfcCoordinateFold.quart(quartX, Direction.Axis.X);
    }

    @ModifyVariable(method = "getBiomeExtensionNoRiver", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int tfcToroidal$foldBiomeQuartZ(int quartZ) {
        return TfcCoordinateFold.quart(quartZ, Direction.Axis.Z);
    }

    @ModifyVariable(method = "getPartition", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$foldPartitionBlockX(int blockX) {
        return TfcCoordinateFold.block(blockX, Direction.Axis.X);
    }

    @ModifyVariable(method = "getPartition", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int tfcToroidal$foldPartitionBlockZ(int blockZ) {
        return TfcCoordinateFold.block(blockZ, Direction.Axis.Z);
    }
}
