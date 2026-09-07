/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.dries007.tfc.common.entities.AnyWaterPlacement;

@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin
{
    @ModifyReturnValue(method = "getPlacementType", at = @At("RETURN"))
    private static SpawnPlacementType inject$getPlacementType(SpawnPlacementType original)
    {
        if (original.equals(SpawnPlacementTypes.IN_WATER))
        {
            return AnyWaterPlacement.INSTANCE;
        }
        return original;
    }
}
