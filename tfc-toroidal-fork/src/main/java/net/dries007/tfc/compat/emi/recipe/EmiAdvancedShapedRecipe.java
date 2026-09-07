/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;

// TODO this does not handle shaped recipes that depend on input
public class EmiAdvancedShapedRecipe extends EmiCraftingRecipe
{
    public EmiAdvancedShapedRecipe(ResourceLocation id, AdvancedShapedRecipe recipe)
    {
        super(padIngredients(recipe), EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())), id, false);
    }

    // Copied from EMI's EmiShapedRecipe#padIngredients, which is not available to subclass unfortunately
    private static List<EmiIngredient> padIngredients(AdvancedShapedRecipe recipe)
    {
        List<EmiIngredient> list = Lists.newArrayList();
        int i = 0;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                if (x >= recipe.getWidth() || y >= recipe.getHeight() || i >= recipe.getIngredients().size())
                {
                    list.add(EmiStack.EMPTY);
                }
                else
                {
                    list.add(EmiIngredient.of(recipe.getIngredients().get(i++)));
                }
            }
        }
        return list;
    }
}
