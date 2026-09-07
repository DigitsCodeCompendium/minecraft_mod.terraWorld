/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.dries007.tfc.world.region.WorldTopology;

public record MutableDensityFunctionContext(BlockPos.MutableBlockPos cursor, WorldTopology topology) implements DensityFunction.FunctionContext
{
    public MutableDensityFunctionContext(BlockPos.MutableBlockPos cursor)
    {
        this(cursor, WorldTopology.PLANAR);
    }

    @Override
    public int blockX()
    {
        return topology.canonicalBlockX(cursor.getX());
    }

    @Override
    public int blockY()
    {
        return cursor.getY();
    }

    @Override
    public int blockZ()
    {
        return topology.canonicalBlockZ(cursor.getZ());
    }
}
