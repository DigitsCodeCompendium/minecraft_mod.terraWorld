package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.client.ClientChunkClimateCache;
import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkData.class, remap = false)
public abstract class ChunkDataMixin {
    @Inject(
            method = "get(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Lnet/dries007/tfc/world/chunkdata/ChunkData;",
            at = @At("HEAD"), cancellable = true)
    private static void tfcToroidal$getTranslatedClimate(
            LevelReader reader, BlockPos pos, CallbackInfoReturnable<ChunkData> cir) {
        if (!(reader instanceof Level level) || !level.isClientSide()) return;
        setCached(level, new ChunkPos(
                TfcCoordinateFold.block(pos.getX(), Direction.Axis.X) >> 4,
                TfcCoordinateFold.block(pos.getZ(), Direction.Axis.Z) >> 4), cir);
    }

    @Inject(
            method = "get(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/ChunkPos;)Lnet/dries007/tfc/world/chunkdata/ChunkData;",
            at = @At("HEAD"), cancellable = true)
    private static void tfcToroidal$getTranslatedClimate(
            LevelReader reader, ChunkPos pos, CallbackInfoReturnable<ChunkData> cir) {
        if (!(reader instanceof Level level) || !level.isClientSide()) return;
        setCached(level, new ChunkPos(
                TfcCoordinateFold.chunk(pos.x, Direction.Axis.X),
                TfcCoordinateFold.chunk(pos.z, Direction.Axis.Z)), cir);
    }

    private static void setCached(
            Level level, ChunkPos pos, CallbackInfoReturnable<ChunkData> cir) {
        final ChunkData cached = ClientChunkClimateCache.get(level, pos);
        if (cached != null) cir.setReturnValue(cached);
    }
}
