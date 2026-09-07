/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.function.DoubleBinaryOperator;

/**
 * Describes how TFC's regional planner connects world coordinates.
 * The default topology is an infinite plane. Compatibility generators may
 * supply a finite torus without making TFC depend on a particular shape mod.
 */
public interface WorldTopology
{
    WorldTopology PLANAR = new WorldTopology() {};

    default boolean wraps()
    {
        return false;
    }

    default int canonicalBlockX(int x) { return x; }
    default int canonicalBlockZ(int z) { return z; }
    default int canonicalGridX(int x) { return x; }
    default int canonicalGridZ(int z) { return z; }
    default int biomeQuartX(int x) { return x; }
    default int biomeQuartZ(int z) { return z; }
    default double canonicalGridX(double x) { return x; }
    default double canonicalGridZ(double z) { return z; }
    default double shortestGridDeltaX(double delta) { return delta; }
    default double shortestGridDeltaZ(double delta) { return delta; }
    default double samplePeriodicGrid(double x, double z, DoubleBinaryOperator field) { return field.applyAsDouble(x, z); }
    default int climateScaleBlocks(boolean axisIsX, int requestedScale) { return requestedScale; }
    default int climateZOffsetBlocks(int requestedScale) { return 0; }
    default boolean mirrorsSouthernHemisphere() { return false; }
    default int minChunkX() { return 0; }
    default int minChunkZ() { return 0; }
    default int widthChunks() { return 0; }
    default int heightChunks() { return 0; }

    static WorldTopology toroidal(int minBlockX, int minBlockZ, int widthBlocks, int heightBlocks)
    {
        if (widthBlocks <= 0 || heightBlocks <= 0)
        {
            throw new IllegalArgumentException("Toroidal dimensions must be positive");
        }
        return new Toroidal(minBlockX, minBlockZ, widthBlocks, heightBlocks);
    }

    record Toroidal(int minBlockX, int minBlockZ, int widthBlocks, int heightBlocks) implements WorldTopology
    {
        @Override public boolean wraps() { return true; }
        @Override public int minChunkX() { return Math.floorDiv(minBlockX, 16); }
        @Override public int minChunkZ() { return Math.floorDiv(minBlockZ, 16); }
        @Override public int widthChunks() { return widthBlocks / 16; }
        @Override public int heightChunks() { return heightBlocks / 16; }
        @Override public int canonicalBlockX(int x) { return minBlockX + Math.floorMod(x - minBlockX, widthBlocks); }
        @Override public int canonicalBlockZ(int z) { return minBlockZ + Math.floorMod(z - minBlockZ, heightBlocks); }
        @Override public int canonicalGridX(int x) { return (int) Math.floor(canonicalGridX((double) x)); }
        @Override public int canonicalGridZ(int z) { return (int) Math.floor(canonicalGridZ((double) z)); }
        @Override public int biomeQuartX(int x) { return weldQuart(x, minBlockX, widthBlocks); }
        @Override public int biomeQuartZ(int z) { return weldQuart(z, minBlockZ, heightBlocks); }
        @Override public double canonicalGridX(double x) { return wrap(x, minBlockX / (double) Units.GRID_WIDTH_IN_BLOCK, widthBlocks / (double) Units.GRID_WIDTH_IN_BLOCK); }
        @Override public double canonicalGridZ(double z) { return wrap(z, minBlockZ / (double) Units.GRID_WIDTH_IN_BLOCK, heightBlocks / (double) Units.GRID_WIDTH_IN_BLOCK); }
        @Override public double shortestGridDeltaX(double delta) { return shortest(delta, widthBlocks / (double) Units.GRID_WIDTH_IN_BLOCK); }
        @Override public double shortestGridDeltaZ(double delta) { return shortest(delta, heightBlocks / (double) Units.GRID_WIDTH_IN_BLOCK); }
        @Override public double samplePeriodicGrid(double x, double z, DoubleBinaryOperator field)
        {
            final Blend bx = blend(x, minBlockX / (double) Units.GRID_WIDTH_IN_BLOCK, widthBlocks / (double) Units.GRID_WIDTH_IN_BLOCK);
            final Blend bz = blend(z, minBlockZ / (double) Units.GRID_WIDTH_IN_BLOCK, heightBlocks / (double) Units.GRID_WIDTH_IN_BLOCK);
            final double nn = field.applyAsDouble(bx.near, bz.near);
            final double fn = bx.weight == 0 ? nn : field.applyAsDouble(bx.far, bz.near);
            final double x0 = lerp(bx.weight, nn, fn);
            if (bz.weight == 0) return x0;
            final double nf = field.applyAsDouble(bx.near, bz.far);
            final double ff = bx.weight == 0 ? nf : field.applyAsDouble(bx.far, bz.far);
            return lerp(bz.weight, x0, lerp(bx.weight, nf, ff));
        }

        @Override public int climateScaleBlocks(boolean axisIsX, int requestedScale)
        {
            if (requestedScale == 0) return 0;
            final int circumference = axisIsX ? widthBlocks : heightBlocks;
            // Both longitudinal rainfall and the mirrored pole-to-equator
            // temperature field have a complete period of 2*scale.
            final int periodMultiplier = 2;
            final int cycles = Math.max(1, Math.round(circumference / (periodMultiplier * (float) requestedScale)));
            return Math.max(1, Math.round(circumference / (periodMultiplier * (float) cycles)));
        }

        @Override public int climateZOffsetBlocks(int requestedScale)
        {
            final int scale = climateScaleBlocks(false, requestedScale);
            // TFC's unshifted northern pole is at -scale/2. Move it to the
            // joined top/bottom map seam. The normally-southern half is
            // mirrored, placing the equator halfway around the torus.
            return scale == 0 ? 0 : -scale / 2 - minBlockZ;
        }

        @Override public boolean mirrorsSouthernHemisphere() { return true; }

        private static double wrap(double value, double min, double size)
        {
            return min + (value - min - Math.floor((value - min) / size) * size);
        }

        private static int weldQuart(int value, int minBlock, int sizeBlocks)
        {
            final int minQuart = Math.floorDiv(minBlock, 4);
            final int sizeQuart = Math.floorDiv(sizeBlocks, 4);
            final int canonical = minQuart + Math.floorMod(value - minQuart, sizeQuart);
            // Biomes are categorical and cannot be numerically interpolated. Weld
            // the final quart cell to the first so biome-selected terrain samplers
            // have the same boundary condition on both sides of the torus.
            return canonical == minQuart + sizeQuart - 1 ? minQuart : canonical;
        }

        private static double shortest(double delta, double size)
        {
            return delta - Math.floor(delta / size + 0.5d) * size;
        }

        private static Blend blend(double value, double min, double size)
        {
            final double canonical = wrap(value, min, size);
            // Climate fields need a very broad transition. A short edge blend can
            // technically be continuous while still changing by tens of mm of
            // rainfall in the last few blocks, which is visibly a seam.
            final double blendWidth = size / 2d;
            final double start = min + size - blendWidth;
            if (canonical <= start) return new Blend(canonical, canonical - size, 0);
            final double t = (canonical - start) / blendWidth;
            return new Blend(canonical, canonical - size, t * t * (3d - 2d * t));
        }

        private static double lerp(double t, double a, double b) { return a + t * (b - a); }
        private record Blend(double near, double far, double weight) {}
    }
}
