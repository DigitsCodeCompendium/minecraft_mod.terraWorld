package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.client.ClientClimateTopology;
import net.dries007.tfc.client.TFCColors;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = TFCColors.class, remap = false)
public abstract class TFCColorsMixin {
    @ModifyVariable(
            method = {
                    "getSeasonalFoliageColor(Lnet/minecraft/core/BlockPos;II)I",
                    "getSeasonalFoliageColor(Lnet/minecraft/core/BlockPos;I)I",
                    "getClimateColor([ILnet/minecraft/core/BlockPos;)I",
                    "getAverageClimateColor([ILnet/minecraft/core/BlockPos;F)I",
                    "getSpringSummerColor([IFFFLnet/minecraft/core/BlockPos;)I",
                    "getGreenSeasonFoliageColor(Lnet/minecraft/core/BlockPos;)I"
            },
            at = @At("HEAD"), argsOnly = true)
    private static BlockPos tfcToroidal$foldColorPosition(BlockPos pos) {
        return ClientClimateTopology.foldPosition(pos);
    }
}
