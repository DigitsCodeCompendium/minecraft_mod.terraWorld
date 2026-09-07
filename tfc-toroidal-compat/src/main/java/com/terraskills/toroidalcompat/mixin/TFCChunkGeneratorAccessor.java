package com.terraskills.toroidalcompat.mixin;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TFCChunkGenerator.class, remap = false)
public interface TFCChunkGeneratorAccessor {
    @Accessor("noiseSettings")
    Holder<NoiseGeneratorSettings> tfcToroidal$noiseSettings();
}
