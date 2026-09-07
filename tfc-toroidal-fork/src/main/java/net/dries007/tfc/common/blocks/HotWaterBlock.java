/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.config.TFCConfig;

public class HotWaterBlock extends LiquidBlock
{
    public HotWaterBlock(Supplier<? extends FlowingFluid> supplier, Properties properties)
    {
        super(supplier.get(), properties);
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource random)
    {
        double x = pos.getX() + random.nextFloat();
        double y = pos.getY();
        double z = pos.getZ() + random.nextFloat();
        if (level.isEmptyBlock(pos.above()))
            level.addParticle(TFCParticles.STEAM.get(), x, y + 1.0D, z, 0.0D, 0.0D, 0.0D);
        if (random.nextInt(3) == 0)
            level.addParticle(TFCParticles.BUBBLE.get(), x, y + random.nextFloat(), z, 0.0, 0.04, 0.0);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.random.nextInt(10) == 0 && entity instanceof LivingEntity living && living.getHealth() < living.getMaxHealth())
        {
            living.heal(TFCConfig.SERVER.hotWaterHealAmount.get().floatValue());
        }
    }
}
