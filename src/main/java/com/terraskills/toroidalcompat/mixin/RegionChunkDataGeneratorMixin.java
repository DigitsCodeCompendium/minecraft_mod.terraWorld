package com.terraskills.toroidalcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraskills.toroidalcompat.worldgen.TfcCoordinateFold;
import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import com.terraskills.toroidalcompat.worldgen.RegionGeneratorBridge;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.ToDoubleFunction;

@Mixin(value = RegionChunkDataGenerator.class, remap = false)
public abstract class RegionChunkDataGeneratorMixin {
    @Shadow @Final private RegionGenerator regionGenerator;

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

    @WrapOperation(
            method = "generate",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/chunkdata/ChunkData;generatePartial(Lnet/dries007/tfc/world/chunkdata/LerpFloatLayer;Lnet/dries007/tfc/world/chunkdata/LerpFloatLayer;Lnet/dries007/tfc/world/chunkdata/LerpFloatLayer;Lnet/dries007/tfc/world/chunkdata/LerpFloatLayer;Lnet/dries007/tfc/world/chunkdata/ForestType;)V"))
    private void tfcToroidal$usePeriodicClimateLayers(
            ChunkData data,
            LerpFloatLayer rainfall,
            LerpFloatLayer rainVariance,
            LerpFloatLayer baseGroundwater,
            LerpFloatLayer temperature,
            ForestType forestType,
            Operation<Void> original) {
        if (TfcTopology.active()) {
            final ChunkPos pos = data.getPos();
            final double x = Units.blockToGridExact(pos.getMinBlockX());
            final double z = Units.blockToGridExact(pos.getMinBlockZ());
            final double size = Units.blockToGridExact(16);
            rainfall = periodicChunkLayer(x, z, size, point -> point.rainfall);
            rainVariance = periodicChunkLayer(x, z, size, point -> point.rainfallVariance);
            temperature = periodicChunkLayer(x, z, size, point -> point.temperature);
        }
        original.call(data, rainfall, rainVariance, baseGroundwater, temperature, forestType);
    }

    private LerpFloatLayer periodicChunkLayer(
            double x, double z, double size, ToDoubleFunction<Region.Point> value) {
        return new LerpFloatLayer(
                (float) samplePeriodic(x, z, value),
                (float) samplePeriodic(x, z + size, value),
                (float) samplePeriodic(x + size, z, value),
                (float) samplePeriodic(x + size, z + size, value));
    }

    private double samplePeriodic(double x, double z, ToDoubleFunction<Region.Point> value) {
        final RegionGeneratorBridge bridge = (RegionGeneratorBridge) (Object) regionGenerator;
        return TfcTopology.samplePeriodicGrid(x, z, (sampleX, sampleZ) -> {
            final int x0 = (int) Math.floor(sampleX);
            final int z0 = (int) Math.floor(sampleZ);
            final double dx = sampleX - x0;
            final double dz = sampleZ - z0;
            final double v00 = value.applyAsDouble(bridge.tfcToroidal$getOrCreateRegionPointUnwrapped(x0, z0));
            final double v01 = value.applyAsDouble(bridge.tfcToroidal$getOrCreateRegionPointUnwrapped(x0, z0 + 1));
            final double v10 = value.applyAsDouble(bridge.tfcToroidal$getOrCreateRegionPointUnwrapped(x0 + 1, z0));
            final double v11 = value.applyAsDouble(bridge.tfcToroidal$getOrCreateRegionPointUnwrapped(x0 + 1, z0 + 1));
            return net.minecraft.util.Mth.lerp(dz,
                    net.minecraft.util.Mth.lerp(dx, v00, v10),
                    net.minecraft.util.Mth.lerp(dx, v01, v11));
        });
    }
}
