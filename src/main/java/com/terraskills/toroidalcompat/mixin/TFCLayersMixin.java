package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcForestLayer;
import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TFCLayers.class, remap = false)
public abstract class TFCLayersMixin {
    @Inject(method = "createOverworldForestLayer", at = @At("HEAD"), cancellable = true)
    private static void tfcToroidal$createPeriodicForestLayer(
            long seed, IArtist<AreaFactory> artist, CallbackInfoReturnable<AreaFactory> cir) {
        if (TfcTopology.active()) cir.setReturnValue(TfcForestLayer.create(seed, artist));
    }
}
