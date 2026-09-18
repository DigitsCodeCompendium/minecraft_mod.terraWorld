package com.terraskills.toroidalcompat.mixin;

import net.dries007.tfc.world.layer.ForestInitLayer;
import net.dries007.tfc.world.noise.Noise2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ForestInitLayer.class, remap = false)
public interface ForestInitLayerAccessor {
    @Invoker("<init>")
    static ForestInitLayer tfcToroidal$create(Noise2D noise) {
        throw new AssertionError();
    }
}
