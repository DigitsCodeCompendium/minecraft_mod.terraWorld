/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.RiverWaterBlock;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class FloatingWaterPlantBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public static FloatingWaterPlantBlock create(RegistryPlant plant, Supplier<? extends TagKey<Fluid>> fluidTag, Properties properties)
    {
        return new FloatingWaterPlantBlock(ExtendedProperties.of(properties), fluidTag)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    private final Supplier<? extends TagKey<Fluid>> fluidTag;

    protected FloatingWaterPlantBlock(ExtendedProperties properties, Supplier<? extends TagKey<Fluid>> fluidTag)
    {
        super(properties);
        this.fluidTag = fluidTag;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        super.randomTick(state, level, pos, random);
        if (PlantRegrowth.canSpread(level, random, pos))
        {
            IntegerProperty ageProp = getPlant().getAgeProperty();
            final BlockPos newPos = PlantRegrowth.spreadSelf(state, level, pos, random, 1, 2, 1);
            if (ageProp != null && newPos != null && level.getFluidState(newPos.below(5)).isEmpty() && !(level.getBlockState(newPos.below()).getBlock() instanceof RiverWaterBlock))
            {
                level.setBlockAndUpdate(newPos, state.setValue(ageProp, 0));
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getFluidState(pos.below()).getTags().anyMatch(fluidTagKey -> (fluidTagKey == fluidTag.get()));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
