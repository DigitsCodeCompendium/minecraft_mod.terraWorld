/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rotation.GearBoxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.LevelReader;

public class HammerItem extends ToolItem
{
    public HammerItem(Tier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_HAMMER, properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return level.getBlockState(pos).getBlock() instanceof GearBoxBlock;
    }
}
