package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.world.region.AddHotspots;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = AddHotspots.class, remap = false)
public abstract class AddHotspotsMixin {
    @ModifyArgs(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/noise/Cellular2D;cell(DD)Lnet/dries007/tfc/world/noise/Cellular2D$Cell;"))
    private void tfcToroidal$foldPlateSample(Args args) {
        args.set(0, TfcTopology.grid((double) args.get(0), Direction.Axis.X));
        args.set(1, TfcTopology.grid((double) args.get(1), Direction.Axis.Z));
    }
}
