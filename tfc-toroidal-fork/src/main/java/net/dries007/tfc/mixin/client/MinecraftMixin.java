/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.dries007.tfc.util.SelfTests;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(
        method = "lambda$new$7(Lnet/minecraft/client/Minecraft$GameLoadCookie;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ResourceLoadStateTracker;finishReload()V")
    )
    private void runSelfTests(CallbackInfo ci)
    {
        SelfTests.runClientSelfTests();
    }

    @Shadow @Nullable
    public MultiPlayerGameMode gameMode;

    /**
     * Prevents the client from trying to instantly break the same block twice in a tick. This would cause
     * issues with blocks that turn into another block when broken, such as snow piles and charcoal piles.
     */
    @ModifyExpressionValue(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z", ordinal = 1))
    private boolean startAttackPreventDoubleBreaking(boolean original)
    {
        return original || !this.gameMode.isDestroying();
    }
}
