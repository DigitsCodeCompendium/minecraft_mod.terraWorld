/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.recipes.SewingRecipe;

public interface SewingRecipes extends Recipes
{
    default void sewingRecipes()
    {
        sewingRecipe(List.of(
            "   # #   ",
            "  ## ##  ",
            "         ",
            "  ## ##  ",
            "   # #   "
        ), List.of(
            "BBBWWBBB",
            "BBWWWWBB",
            "BBWWWWBB",
            "BBBWWBBB"
        ), Items.FLOWER_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  ## ##  ",
            "  #   #  ",
            "  # # #  ",
            "  # # #  ",
            "  ## ##  "
        ), List.of(
            "BBWBBWBB",
            "BBBBBBBB",
            "BBWWWWBB",
            "BBWBBWBB"
        ), Items.CREEPER_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  #   #  ",
            "   ###   ",
            "  ## ##  ",
            "         ",
            "  #   #  "
        ), List.of(
            "BBWWWWBB",
            "BBWBBWBB",
            "BBWWWWBB",
            "BBWWWWBB"
        ), Items.SKULL_BANNER_PATTERN);
        sewingRecipe(List.of(
            "  ## ##  ",
            "  ## ##  ",
            "         ",
            "  ## ##  ",
            "  ## ##  "
        ), List.of(
            "BBWWWWBB",
            "BBWBBWBB",
            "BBWBBWBB",
            "BBWWWWBB"
        ), Items.GLOBE_BANNER_PATTERN);
        sewingRecipe(List.of(
            "         ",
            " # # # # ",
            "         ",
            " # # # # ",
            "         "
        ), List.of(
            "WWWWWWWW",
            "WBWBBWBW",
            "WBWBBWBW",
            "WWWWWWWW"
        ), Items.PIGLIN_BANNER_PATTERN);
        sewingRecipe(List.of(
            "         ",
            " # # #  #",
            "       # ",
            " # # # # ",
            "         "
        ), List.of(
            "WWWWBBBB",
            "WBBBBBBW",
            "WBBBBBBW",
            "WWWWWWWW"
        ), Items.MOJANG_BANNER_PATTERN);
        sewingRecipe(List.of(
            "         ",
            "   # #   ",
            "  #   #  ",
            "    #    ",
            "    #    "
        ), List.of(
            "BBBWWBBB",
            "BWWWWWWB",
            "BBWWWWBB",
            "BBBWWBBB"
        ), Items.GUSTER_BANNER_PATTERN);
        sewingRecipe(List.of(
            "     # # ",
            "    #    ",
            "         ",
            "    #    ",
            "     # # "
        ), List.of(
            "BBBBBWWB",
            "BBBBWWBB",
            "BBBBWWBB",
            "BBBBBWWB"
        ), Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "    #    ",
            "   # #   ",
            "  #   #  ",
            " #     # "
        ), List.of(
            "BBBBBBBB",
            "BBBWWBBB",
            "BBWWWWBB",
            "BWWWWWWB"
        ), Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "   # #   ",
            "  #   #  ",
            "         ",
            "  #   #  ",
            "   # #   "
        ), List.of(
            "BBBWWBBB",
            "BBWBBWBB",
            "BBWBBWBB",
            "BBBWWBBB"
        ), Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "# # # # #",
            "         ",
            "         ",
            "     ##  "
        ), List.of(
            "BBBBBBBB",
            "WWWWWWWW",
            "BBBBBWBB",
            "BBBBBWBB"
        ), Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "   ##### ",
            "         ",
            "#####    ",
            "         "
        ), List.of(
            "BBBBBWWW",
            "BBBWWWBB",
            "BWWWBBBB",
            "WWBBBBBB"
        ), Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "  # # #  ",
            "         ",
            " # # #   ",
            "         ",
            " # # #   "
        ), List.of(
            "BBWBWBWB",
            "BBWBWBWB",
            "BWBWBWBB",
            "BWBWBWBB"
        ), Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "   # #   ",
            "         ",
            "# ## ## #",
            "         ",
            "   # #   "
        ), List.of(
            "BBBWWBBB",
            "WWWBBWWW",
            "BBWBBWBB",
            "BBBWWBBB"
        ), Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "      #  ",
            "   #   # ",
            "    #    ",
            "  #      ",
            "   #     "
        ), List.of(
            "BBBBBBWB",
            "BBBWWWBB",
            "BBBWBBBB",
            "BBWBBBBB"
        ), Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "  #      ",
            "         ",
            "  # ##   ",
            "         ",
            "     ##  "
        ), List.of(
            "BBWBBBBB",
            "BBWWWBBB",
            "BBBBWBBB",
            "BBBBWWBB"
        ), Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " #     # ",
            "# #   # #",
            "         ",
            "# #   # #",
            " #     # "
        ), List.of(
            "BBBBBBBB",
            "BWBBBBWB",
            "BWBBBBWB",
            "BBBBBBBB"
        ), Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "  #   #  ",
            "         ",
            "  ## ##  ",
            "         "
        ), List.of(
            "BBWBBWBB",
            "BBWBBWBB",
            "BBWBBWBB",
            "BBWWWWBB"
        ), Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " # #     ",
            "    # #  ",
            "         ",
            "    # #  ",
            " # #     "
        ), List.of(
            "BWWWBBBB",
            "BBBBWWWB",
            "BBBBWWWB",
            "BWWWBBBB"
        ), Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "#### ##  ",
            "      #  ",
            "####     ",
            "         "
        ), List.of(
            "BBBBBBBB",
            "WWWBBWBB",
            "WWWBWBBB",
            "WWWWWBBB"
        ), Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "     ##  ",
            " #       ",
            " #   ##  ",
            " #       ",
            "     ##  "
        ), List.of(
            "BBBBBWBB",
            "BWWBBWBB",
            "BWWBBWBB",
            "BBBBBWBB"
        ), Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            " #       ",
            " #    ## ",
            "      ## ",
            "  # #    ",
            "       # "
        ), List.of(
            "BWBBBWWB",
            "BWWBBWBW",
            "BBWBWBWB",
            "BBWWBBBW"
        ), Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "##     ##",
            "   ###   ",
            "   # #   ",
            "   # #   "
        ), List.of(
            "WWBBBBWW",
            "BWWBBWWB",
            "BBBWWBBB",
            "BBBWWBBB"
        ), Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "  #      ",
            "    ##   ",
            "     #   ",
            "   #  #  ",
            "         "
        ), List.of(
            "BBWWWWBB",
            "BBWBWWBB",
            "BBWBBBBB",
            "BBWWWWBB"
        ), Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
        sewingRecipe(List.of(
            "         ",
            "   #   # ",
            "    #    ",
            "  #      ",
            "      #  "
        ), List.of(
            "BWBWBBBB",
            "BWBWBBWB",
            "BWBBBBWB",
            "BWWWWBWB"
        ), Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
    }

    private void sewingRecipe(List<String> stitches, List<String> squares, ItemLike output)
    {
        add(SewingRecipe.from(String.join("", stitches), String.join("", squares), new ItemStack(output)));
    }
}
