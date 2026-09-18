package com.terraskills.toroidalcompat.worldgen;

import net.dries007.tfc.world.region.Region;

public interface RegionGeneratorBridge {
    Region.Point tfcToroidal$getOrCreateRegionPointUnwrapped(int gridX, int gridZ);
}
