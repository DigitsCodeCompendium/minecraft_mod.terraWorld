/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.util.tracker.WeatherHelpers;
import net.dries007.tfc.world.biome.BiomeBridge;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeBridge
{
    @Unique
    private final CachedMut<BiomeExtension> tfc$cachedExtension = CachedMut.unloaded();

    @Nullable
    @Override
    public BiomeExtension tfc$getExtension(@NotNull CommonLevelAccessor level)
    {
        if (!tfc$cachedExtension.isLoaded())
        {
            tfc$cachedExtension.load(TFCBiomes.findExtension(level, (Biome) (Object) this));
        }
        return tfc$cachedExtension.value();
    }


    /**
     * Replace {@link Biome#getPrecipitationAt(BlockPos)} with a method that takes our climate model into account
     * <p>
     * We only do this on the client, where we can get the client level.
     * This is safe to do even for levels that use biome based precipitation, because
     * {@link WeatherHelpers#getPrecipitationAt(Level, BlockPos, Biome.Precipitation)} checks for us what type of climate the level uses.
     * 
     * TFC doesn't currently directly use this, but it's needed for mod compatability.
     */
    @OnlyIn(Dist.CLIENT)
    @ModifyReturnValue(method = "getPrecipitationAt", at = @At("RETURN"))
    private Biome.Precipitation getPrecipitaitionFromClimate(Biome.Precipitation original, @Local BlockPos pos)
    {
        Level level = Minecraft.getInstance().level;
        if (level == null)
        {
            return original;
        }
        return WeatherHelpers.getPrecipitationAt(level, pos, original);
    }
}
