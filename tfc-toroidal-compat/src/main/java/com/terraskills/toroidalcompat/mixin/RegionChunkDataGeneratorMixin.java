package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RegionChunkDataGenerator.class, remap = false)
public abstract class RegionChunkDataGeneratorMixin {
    @WrapOperation(
            method = "generate",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/layer/framework/ConcurrentArea;get(II)Ljava/lang/Object;"))
    private Object tfcToroidal$foldForestLayer(
            ConcurrentArea<ForestType> area,
            int chunkX,
            int chunkZ,
            Operation<Object> original) {
        return original.call(area,
                TfcCoordinateFold.chunk(chunkX, Direction.Axis.X),
                TfcCoordinateFold.chunk(chunkZ, Direction.Axis.Z));
    }

    @WrapOperation(
            method = "generateRock(IILjava/lang/Object;)V",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/layer/framework/Area;get(II)I"),
            require = 0)
    private int tfcToroidal$foldRockLayerFallback(
            Area area,
            int blockX,
            int blockZ,
            Operation<Integer> original) {
        return original.call(area,
                TfcCoordinateFold.block(blockX, Direction.Axis.X),
                TfcCoordinateFold.block(blockZ, Direction.Axis.Z));
    }

    @WrapOperation(
            method = "generateRock(IIIILnet/dries007/tfc/world/chunkdata/ChunkRockDataCache;Ljava/util/List;)Lnet/dries007/tfc/world/settings/RockSettings;",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/layer/framework/Area;get(II)I"))
    private int tfcToroidal$foldRockLayer(
            Area area,
            int blockX,
            int blockZ,
            Operation<Integer> original) {
        return original.call(area,
                TfcCoordinateFold.block(blockX, Direction.Axis.X),
                TfcCoordinateFold.block(blockZ, Direction.Axis.Z));
    }
}
