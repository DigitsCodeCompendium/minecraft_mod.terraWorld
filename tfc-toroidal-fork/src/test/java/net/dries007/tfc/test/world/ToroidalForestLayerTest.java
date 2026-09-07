/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.region.WorldTopology;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToroidalForestLayerTest
{
    @Test
    public void forestLayerRepeatsAcrossBothStitches()
    {
        final int minChunk = -64;
        final int sizeChunks = 128;
        final WorldTopology topology = WorldTopology.toroidal(
            minChunk * 16, minChunk * 16, sizeChunks * 16, sizeChunks * 16);
        final Area forest = TFCLayers.createOverworldForestLayer(0x5EEDL, IArtist.nope(), topology).get();

        for (int x = minChunk - 4; x < minChunk + sizeChunks + 4; x++)
        {
            for (int z = minChunk - 4; z < minChunk + sizeChunks + 4; z++)
            {
                final int expected = forest.get(x, z);
                assertEquals(expected, forest.get(x + sizeChunks, z));
                assertEquals(expected, forest.get(x, z + sizeChunks));
            }
        }
    }
}
