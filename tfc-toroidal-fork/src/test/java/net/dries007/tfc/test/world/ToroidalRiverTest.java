/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.world.region.WorldTopology;
import net.dries007.tfc.world.river.MidpointFractal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToroidalRiverTest
{
    @Test
    public void riverDistanceAndFlowRepeatAcrossXSeam()
    {
        final WorldTopology topology = WorldTopology.toroidal(0, 0, 512, 512);
        final MidpointFractal river = new MidpointFractal(RandomSource.create(42), 4, 3.25, 1.0, 3.9, 1.5, topology);

        assertEquals(river.intersectDistance(3.95, 1.4), river.intersectDistance(-0.05, 1.4), 1e-12);
        assertEquals(river.calculateFlow(3.95, 1.4), river.calculateFlow(-0.05, 1.4));
    }

    @Test
    public void regionCoordinatesRepeatAcrossBothSeams()
    {
        final WorldTopology topology = WorldTopology.toroidal(-256, -256, 512, 512);

        assertEquals(topology.canonicalGridX(-2), topology.canonicalGridX(2));
        assertEquals(topology.canonicalGridZ(-2), topology.canonicalGridZ(2));
        assertEquals(topology.canonicalBlockX(-257), topology.canonicalBlockX(255));
        assertEquals(topology.canonicalBlockZ(256), topology.canonicalBlockZ(-256));
    }

    @Test
    public void continuousGridFieldsRepeatForNonGridAlignedWorlds()
    {
        final WorldTopology topology = WorldTopology.toroidal(-504, -504, 1008, 1008);
        final double widthInGrid = 1008d / 128d;
        final double sample = topology.samplePeriodicGrid(3.91, -1.27, (x, z) -> x * x + 2d * z);

        assertEquals(sample, topology.samplePeriodicGrid(3.91 + widthInGrid, -1.27, (x, z) -> x * x + 2d * z), 1e-12);
        assertEquals(sample, topology.samplePeriodicGrid(3.91, -1.27 - widthInGrid, (x, z) -> x * x + 2d * z), 1e-12);
    }

    @Test
    public void climateScaleFitsWholeCyclesAroundTorus()
    {
        final WorldTopology topology = WorldTopology.toroidal(-504, -256, 1008, 512);

        assertEquals(504, topology.climateScaleBlocks(true, 20_000));
        assertEquals(256, topology.climateScaleBlocks(false, 20_000));
        assertEquals(0, topology.climateScaleBlocks(false, 0));
    }

    @Test
    public void climateRunsFromJoinedNorthPoleToCentralEquator()
    {
        final WorldTopology topology = WorldTopology.toroidal(-256, -256, 512, 512);
        final int scale = topology.climateScaleBlocks(false, 20_000);
        final int offset = topology.climateZOffsetBlocks(20_000);

        assertEquals(256, scale);
        assertEquals(128, offset);
        assertEquals(-scale / 2, -256 + offset); // North pole at joined top/bottom seam
        assertEquals(scale / 2, offset); // Equator halfway around the torus
        assertEquals(Mth.HALF_PI, SolarCalculator.getLatitude(-256 + offset, scale, true));
        assertEquals(Mth.PI / 4f, SolarCalculator.getLatitude(-128 + offset, scale, true));
        assertEquals(0f, SolarCalculator.getLatitude(offset, scale, true));
        assertEquals(Mth.PI / 4f, SolarCalculator.getLatitude(128 + offset, scale, true));
        assertEquals(Mth.HALF_PI, SolarCalculator.getLatitude(256 + offset, scale, true));
        assertEquals(true, topology.mirrorsSouthernHemisphere());
    }

    @Test
    public void finalBiomeQuartIsWeldedToFirstAcrossSeam()
    {
        final WorldTopology topology = WorldTopology.toroidal(-256, -256, 512, 512);

        assertEquals(topology.biomeQuartX(-64), topology.biomeQuartX(63));
        assertEquals(topology.biomeQuartZ(-64), topology.biomeQuartZ(63));
        assertEquals(topology.biomeQuartX(-63), topology.biomeQuartX(65));
    }
}
