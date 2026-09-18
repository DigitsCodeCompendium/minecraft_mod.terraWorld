package com.terraskills.toroidalcompat.mixin;

import net.dries007.tfc.world.ChunkHeightFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkHeightFiller.class, remap = false)
public interface ChunkHeightFillerAccessor {
    @Accessor("blockX")
    void tfcToroidal$setBlockX(int value);

    @Accessor("blockZ")
    void tfcToroidal$setBlockZ(int value);
}
