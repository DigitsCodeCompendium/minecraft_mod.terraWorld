/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

/**
 * Spreading bushes have two parts: a bush block, which is a full block which can grow up to three blocks tall, and a cane block, which is a horizontal protrusion that can output from the sides of a bush block.
 * The cane can then turn into more bush blocks, spreading the plant and allowing it to climb up hills.
 * Both the cane and the bush block use the "stage" property from {@link SeasonalPlantBlock} to determine and limit their growth.
 * <p>
 * Spreading:
 * <ul>
 *   <li>Cane blocks always convert to bush blocks, if they can.</li>
 *   <li>Bush blocks can grow up to three blocks upwards, but can only spread canes to adjacent blocks, meaning a single berry bush has four directions to spread in.</li>
 *   <li>Stage 0 is for newly planted bushes. Stage 1 is for all bush blocks that are bushes and grown naturally. Advancing to stage 2 means the bush is mature, and won't spread anymore.</li>
 *   <li>This means an individual horizontal position can spread up to three blocks adjacent, *but* unless the bush is climbing a hill, most of those canes won't be able to grow into bushes, because they're on solid ground. Meaning natural bush spreading will eventually stop, as the bush will reach all stage 2, where it is unable to spread.</li>
 * </ul>
 * The player can harvest bush blocks, stage 2 for a guaranteed drop, all other stages for 1/2 chance.
 */
public class SpreadingBushBlock extends StationaryBerryBushBlock implements IForgeBlockExtension, HoeOverlayBlock
{
    protected final Supplier<? extends Block> companion;
    protected final int maxHeight;

    public SpreadingBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, climateRange);
        this.companion = companion;
        this.maxHeight = maxHeight;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    public Block getCane()
    {
        return companion.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.block() : PLANT_SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        final BlockPos climatePos;
        if (level.getBlockEntity(pos) instanceof BerryBushBlockEntity plant)
        {
            climatePos = plant.getStemPos();
        }
        else
        {
            climatePos = pos;
        }
        final int hydration = getFruitBushHydrationFromRootPos(level, climatePos);
        final float temp = Climate.getAverageTemperature(level, climatePos);

        if (climateRange.get().checkBoth(hydration, temp, false))
        {
            this.tick(state, level, pos, random);
        }
        else
        {
            level.setBlockAndUpdate(pos, getDeadState(state));
        }
    }

    @Override
    protected void growAndPropagate(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, int cycles, int growthsRemaining)
    {
        // Increment stage by one
        final int originalStage = state.getValue(STAGE);

        if (originalStage == 0)
        {
            // Stage 0 -> grow into stage 1
            final BlockState newState = state.setValue(STAGE, 1);
            placeBlockAndResetCounter(level, pos, newState, cycles, growthsRemaining);
            return;
        }
        if (originalStage == 1)
        {
            // Stage 1: Check a random horizontal direction, and try to grow that way
            final Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            final BlockPos adjacentPos = pos.relative(dir);
            if (level.getBlockState(adjacentPos).isEmpty())
            {
                placeNewBushAndResetCounters(level, adjacentPos, pos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, dir).setValue(LIFECYCLE, state.getValue(LIFECYCLE)), cycles, growthsRemaining);
                maybeStopGrowing(level, pos, state, random);
            }
            else
            {
                // If it can't grow in that direction, attempt to grow the bush upwards
                final BlockPos abovePos = pos.above();
                if (level.isEmptyBlock(abovePos) && distanceToGround(level, pos, maxHeight) < maxHeight)
                {
                    // Growing upwards grows at stage = 1, because stage = 0 is just newly planted bushes.
                    state.setValue(STAGE, 1).setValue(LIFECYCLE, state.getValue(LIFECYCLE));
                    placeNewBushAndResetCounters(level, abovePos, pos, state, cycles, growthsRemaining);

                    // Increase age to stop this block from growing again in future
                    level.setBlock(pos, state.setValue(STAGE, 2), Block.UPDATE_ALL);
                }
            }
        }
    }

    private void maybeStopGrowing(ServerLevel level, BlockPos pos, BlockState state, RandomSource random)
    {
        if (random.nextBoolean())
        {
            // Before allowing this to propagate multiple times, check that there aren't too many bush blocks nearby
            int count = 0;
            for (BlockPos target : BlockPos.betweenClosed(pos.offset(-2, 0, -2), pos.offset(2, 1, 2)))
            {
                if (level.getBlockState(target).getBlock() == this)
                {
                    count++;
                    if (count > 10)
                    {
                        return;
                    }
                }
            }
        }
        // Otherwise, stop growing
        level.setBlock(pos, state.setValue(STAGE, 2), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        final BlockState belowState = level.getBlockState(belowPos);
        return mayPlaceOn(belowState, level, belowPos) || (belowState.getBlock() == this && belowState.getValue(STAGE) != 0);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.SPREADING_FRUIT_GROWS_ON);
    }

    private void placeNewBushAndResetCounters(ServerLevel level, BlockPos newPos, BlockPos oldPos, BlockState state, int cycles, int growths)
    {
        level.setBlockAndUpdate(newPos, state);
        // If block grows, set the new block's stem position to match the original
        if (level.getBlockEntity(oldPos) instanceof BerryBushBlockEntity sourceBush && level.getBlockEntity(newPos) instanceof BerryBushBlockEntity newBush)
        {
            sourceBush.resetCounter();
            sourceBush.increaseCounter((long) TFCConfig.SERVER.berryBushGrowthTicks.get() * cycles);
            // It is assumed that the number of growths remaining has already been reduced prior to calling this method
            sourceBush.setGrowthsRemaining(growths);

            newBush.resetCounter();
            newBush.increaseCounter((long) TFCConfig.SERVER.berryBushGrowthTicks.get() * cycles);
            newBush.setGrowthsRemaining(growths);

            newBush.setStemPos(sourceBush.getStemPos());
        }
        else
        {
            TerraFirmaCraft.LOGGER.error("Failed to update growing berry bush block entity at: {}", oldPos);
        }
        level.getBlockState(oldPos).tick(level, oldPos, level.random);
        level.getBlockState(newPos).tick(level, newPos, level.random);
    }
}
