/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.minecraft.world.item.ItemStack;

public enum RoundCreationDateModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        return FoodCapability.roundCreationDate(stack);
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.ROUND_CREATION_DATE.get();
    }
}