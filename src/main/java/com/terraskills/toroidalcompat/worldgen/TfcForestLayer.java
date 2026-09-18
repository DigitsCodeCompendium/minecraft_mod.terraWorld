package com.terraskills.toroidalcompat.worldgen;

import com.terraskills.toroidalcompat.mixin.ForestInitLayerAccessor;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.ForestEdgeLayer;
import net.dries007.tfc.world.layer.ForestInitLayer;
import net.dries007.tfc.world.layer.ForestRandomizeLayer;
import net.dries007.tfc.world.layer.ForestRandomizeSmallLayer;
import net.dries007.tfc.world.layer.ZoomLayer;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.minecraft.core.Direction;

import java.util.Random;

/** Builds TFC's forest layer while welding every zoom stage at looping seams. */
public final class TfcForestLayer {
    public static AreaFactory create(long seed, IArtist<AreaFactory> artist) {
        final Random random = new Random(seed);

        AreaFactory layer = periodic(
                ForestInitLayerAccessor.tfcToroidal$create(
                        new OpenSimplex2D(random.nextInt()).spread(0.25f)).apply(random.nextLong()), 5);
        artist.draw("forest", 1, layer);
        layer = periodic(ForestRandomizeLayer.INSTANCE.apply(random.nextLong(), layer), 5);
        artist.draw("forest", 2, layer);
        layer = periodic(ZoomLayer.FUZZY.apply(random.nextLong(), layer), 4);
        artist.draw("forest", 3, layer);
        layer = periodic(ForestRandomizeLayer.INSTANCE.apply(random.nextLong(), layer), 4);
        artist.draw("forest", 4, layer);
        layer = periodic(ZoomLayer.FUZZY.apply(random.nextLong(), layer), 3);
        artist.draw("forest", 5, layer);
        layer = periodic(ZoomLayer.NORMAL.apply(random.nextLong(), layer), 2);
        artist.draw("forest", 6, layer);
        layer = periodic(ForestEdgeLayer.INSTANCE.apply(random.nextLong(), layer), 2);
        artist.draw("forest", 7, layer);
        layer = periodic(ForestRandomizeSmallLayer.INSTANCE.apply(random.nextLong(), layer), 2);
        artist.draw("forest", 8, layer);

        for (int i = 0; i < 2; i++) {
            layer = periodic(ZoomLayer.NORMAL.apply(random.nextLong(), layer), 1 - i);
            artist.draw("forest", 9 + i, layer);
        }
        return layer;
    }

    private static AreaFactory periodic(AreaFactory factory, int remainingZooms) {
        if (!TfcTopology.loops(Direction.Axis.X) && !TfcTopology.loops(Direction.Axis.Z)) return factory;

        final int scale = 1 << remainingZooms;
        final int minX = Math.floorDiv(TfcTopology.minChunk(Direction.Axis.X), scale);
        final int minZ = Math.floorDiv(TfcTopology.minChunk(Direction.Axis.Z), scale);
        final int width = Math.max(1, TfcTopology.sizeChunks(Direction.Axis.X) / scale);
        final int height = Math.max(1, TfcTopology.sizeChunks(Direction.Axis.Z) / scale);
        return () -> {
            final Area source = factory.get();
            return new Area((x, z) -> source.get(
                    wrap(x, minX, width, Direction.Axis.X),
                    wrap(z, minZ, height, Direction.Axis.Z)), 1024);
        };
    }

    private static int wrap(int value, int min, int size, Direction.Axis axis) {
        return TfcTopology.loops(axis) ? min + Math.floorMod(value - min, size) : value;
    }

    private TfcForestLayer() {}
}
