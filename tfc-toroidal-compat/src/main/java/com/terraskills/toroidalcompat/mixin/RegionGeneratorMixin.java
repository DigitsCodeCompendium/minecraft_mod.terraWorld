package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.region.RegionGenerator;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RegionGenerator.class, remap = false)
public abstract class RegionGeneratorMixin {
    @ModifyVariable(method = "getOrCreateRegionPoint", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$foldRegionGridX(int gridX) {
        return TfcCoordinateFold.grid(gridX, Direction.Axis.X);
    }

    @ModifyVariable(method = "getOrCreateRegionPoint", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int tfcToroidal$foldRegionGridZ(int gridZ) {
        return TfcCoordinateFold.grid(gridZ, Direction.Axis.Z);
    }

    @ModifyVariable(method = "getOrCreatePartitionPoint", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int tfcToroidal$foldRiverGridX(int gridX) {
        return TfcCoordinateFold.grid(gridX, Direction.Axis.X);
    }

    @ModifyVariable(method = "getOrCreatePartitionPoint", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int tfcToroidal$foldRiverGridZ(int gridZ) {
        return TfcCoordinateFold.grid(gridZ, Direction.Axis.Z);
    }
}
