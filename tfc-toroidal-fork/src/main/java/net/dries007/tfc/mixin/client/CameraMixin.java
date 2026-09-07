/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.client.ClientHelpers;

@Mixin(Camera.class)
public abstract class CameraMixin
{
    @Shadow private boolean initialized;

    @Inject(method = "getFluidInCamera", at=@At("HEAD"), cancellable = true)
    private void getTFCFluid(CallbackInfoReturnable<FogType> cir)
    {
        if (this.initialized)
        {
            final FogType fog = ClientHelpers.getFluidInCamera((Camera) (Object) this);
            if (fog != null)
            {
                cir.setReturnValue(fog);
            }
        }
    }

}
