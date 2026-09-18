package com.terraskills.toroidalcompat.mixin;

import net.dries007.tfc.world.region.Region;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Region.class, remap = false)
public interface RegionAccessor {
    @Invoker("atOrThrow")
    Region.Point tfcToroidal$atOrThrow(int gridX, int gridZ);
}
