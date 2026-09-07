/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.wood.ExtendedRotatedPillarBlock;
import net.dries007.tfc.util.Helpers;

public class BambooBlock extends ExtendedRotatedPillarBlock
{
    public BambooBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility action, boolean simulate)
    {
        if (context.getItemInHand().canPerformAction(action) && action == ItemAbilities.AXE_STRIP)
        {
            return Helpers.copyProperties(Blocks.STRIPPED_BAMBOO_BLOCK.defaultBlockState(), state);
        }
        return null;
    }

}
