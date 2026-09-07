/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public enum AnyWaterPlacement implements SpawnPlacementType
{
    INSTANCE;

    /**
     * Tracks the content of {@link net.minecraft.world.entity.SpawnPlacementTypes#IN_WATER}
     */
    @Override
    public boolean isSpawnPositionOk(LevelReader level, BlockPos pos, @Nullable EntityType<?> type)
    {
        if (type != null && level.getWorldBorder().isWithinBounds(pos))
        {
            BlockPos blockpos = pos.above();
            return Helpers.isFluid(level.getFluidState(pos), TFCTags.Fluids.ANY_INFINITE_WATER) && !level.getBlockState(blockpos).isRedstoneConductor(level, blockpos);
        }
        else
        {
            return false;
        }
    }
}
