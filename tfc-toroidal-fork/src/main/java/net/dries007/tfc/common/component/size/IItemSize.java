/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.size;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

/**
 * Represents a {@link Size} and {@link Weight} that can be attached to items. Item sizes can be sourced from several places, in order:
 * <ol>
 *     <li>An {@link Item} that implements {@link IItemSize} directly</li>
 *     <li>An {@link BlockItem} where the corresponding {@link Block} implements {@link IItemSize} directly</li>
 *     <li>Any matching {@code item_size} that is loaded from JSON, that matches the given item</li>
 *     <li>A default size/weight combination based on a very simple heuristic using different classes of items</li>
 * </ol>
 *
 * Right now due to how components work, IItemSize does not dynamically update stack sizes.
 * It will call getWeight once, on reload, using the default {@link ItemStack} to set the default size.
 * For new created stacks, you have to find a way to call {@link IItemSize#modifyWeight(ItemStack)} to update the component.
 * See {@link net.dries007.tfc.util.loot.ApplyStackSizeFunction} for how this applies to block loot.
 * See {@link net.dries007.tfc.common.blocks.devices.SealableDeviceBlock#getCloneItemStack(BlockState, HitResult, LevelReader, BlockPos, Player)} for the other use case.
 */
public interface IItemSize
{
    /**
     * @return the size of this {@code stack}, determining what size containers this item can be placed within.
     */
    Size getSize(ItemStack stack);

    /**
     * @return the weight of this {@code stack}, determining the stack size of this item.
     */
    Weight getWeight(ItemStack stack);

    /**
     * Call this in {@link Block#getCloneItemStack(BlockState, HitResult, LevelReader, BlockPos, Player)}
     * or in an equivalent location. This is now required to 'actually' set the weight on new stacks.
     *
     * This is intended as a sort of post-processing on stacks where we always control their creation.
     */
    default void modifyWeight(ItemStack stack)
    {
        stack.set(DataComponents.MAX_STACK_SIZE, getWeight(stack).stackSize);
    }
}
