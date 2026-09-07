/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface INutritionData
{
    /**
     * @return The average of the nutrition values of the player
     */
    float getAverageNutrition();

    /**
     * @return The nutrient value, in [0, 1]
     */
    float getNutrient(Nutrient nutrient);

    /**
     * @return An array of all nutrient values, each in [0, 1]
     */
    float[] getNutrients();

    /**
     * Set the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This may update the nutrition of the player.
     */
    default void setHungerAndUpdate(int hunger)
    {
        setHunger(hunger);
    }

    /**
     * Set the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This <strong>must not</strong> update the nutrition of the player.
     */
    void setHunger(int hunger);

    /**
     * Sets data from a packet, received on client side. Only contains the array of nutrient values of the player, since only those are needed for the client
     */
    void onClientUpdate(float[] nutrients);

    /**
     * Applies nutrients of the food data to the player, and incorporates the current hunger level of the player
     * @param data The {@link FoodData} of the eaten food
     * @param currentHunger The food level of the player at time of eating. Might not actually be used by an implementation
     */
    void addNutrients(FoodData data, int currentHunger);

    /**
     * @return The relevant data for computing nutrition values written to an NBT Tag
     */
    Tag writeToNbt();

    /**
     * Reads relevant data for computing nutrition values from an NBT Tag
     */
    void readFromNbt(@Nullable Tag nbt);
}
