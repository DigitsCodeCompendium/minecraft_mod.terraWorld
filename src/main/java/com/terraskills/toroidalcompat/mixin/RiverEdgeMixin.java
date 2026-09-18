package com.terraskills.toroidalcompat.mixin;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import com.terraskills.toroidalcompat.worldgen.TfcTopology;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.river.River;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

@Mixin(value = RiverEdge.class, remap = false)
public abstract class RiverEdgeMixin {
    @Shadow @Final private River.Vertex source;
    @Shadow @Final private River.Vertex drain;

    @ModifyArgs(
            method = "widthSq(DD)D",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/river/RiverHelpers;projectAlongLine(DDDDDD)D"))
    private void tfcToroidal$projectAcrossSeam(Args args) {
        final double sourceX = source.x();
        final double sourceZ = source.y();
        args.set(0, sourceX);
        args.set(1, sourceZ);
        args.set(2, sourceX + TfcTopology.shortestGridDelta(drain.x() - sourceX, Direction.Axis.X));
        args.set(3, sourceZ + TfcTopology.shortestGridDelta(drain.y() - sourceZ, Direction.Axis.Z));
        args.set(4, sourceX + TfcTopology.shortestGridDelta(args.<Double>get(4) - sourceX, Direction.Axis.X));
        args.set(5, sourceZ + TfcTopology.shortestGridDelta(args.<Double>get(5) - sourceZ, Direction.Axis.Z));
    }
}
