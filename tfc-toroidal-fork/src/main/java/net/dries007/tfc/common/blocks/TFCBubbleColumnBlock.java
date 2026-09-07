/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

public class TFCBubbleColumnBlock extends BubbleColumnBlock
{
    private static final Lazy<Map<Fluid, Block>> BUBBLE_FLUIDS = Lazy.of(() -> Map.of(
        Fluids.WATER.getSource(), TFCBlocks.FRESHWATER_BUBBLE_COLUMN.get(),
        TFCFluids.SALT_WATER.getSource(), TFCBlocks.SALTWATER_BUBBLE_COLUMN.get(),
        TFCFluids.SPRING_WATER.getSource(), TFCBlocks.SPRING_WATER_BUBBLE_COLUMN.get()
    ));

    @Nullable
    public static Block columnOf(Fluid fluid)
    {
        return BUBBLE_FLUIDS.get().get(fluid);
    }

    // Determine if bubbles go up or down
    public static boolean isDownBubbles(LevelAccessor level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof TFCBubbleColumnBlock)
            return state.getValue(DRAG_DOWN);

        return state.getBlock() instanceof TFCMagmaBlock;
    }

    public static void updateColumnForFluid(LevelAccessor level, BlockPos pos)
    {
        final BlockState startState = level.getBlockState(pos);
        if (!(startState.getBlock() instanceof TFCMagmaBlock))
            return;

        final BlockPos.MutableBlockPos cursor = pos.above().mutable();
        final Fluid startingFluid = level.getFluidState(cursor).getType();
        final Block columnBlock = columnOf(startingFluid);
        if (columnBlock == null)
            return;
        final BlockState newColumn = columnBlock.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, true);

        while (cursor.getY() >= level.getMinBuildHeight() && cursor.getY() < level.getMaxBuildHeight())
        {
            BlockState state = level.getBlockState(cursor);

            if (!canExistIn(state) || state.getBlock() instanceof TFCMagmaBlock)
            {
                // Replace existing bubble column with fluid if necessary
                if (state.getBlock() instanceof TFCBubbleColumnBlock)
                {
                    level.setBlock(cursor, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
                }
                break;
            }

            // Only set the block if it's not already correct
            if (!Helpers.isBlock(state, newColumn.getBlock()))
            {
                level.setBlock(cursor, newColumn, Block.UPDATE_ALL);
            }

            cursor.move(Direction.UP);
        }
    }

    // Determines if bubble column is allowed to exist in current block state
    public static boolean canExistIn(BlockState state)
    {
        // Bubble column already exists here
        if (state.getBlock() instanceof TFCBubbleColumnBlock) return true;
        // If block state is air or empty, check if fluid supports bubble columns
        if (FluidHelpers.isAirOrEmptyFluid(state) && !state.isAir())
        {
            return columnOf(state.getFluidState().getType()) != null;
        }
        return false;
    }
    private final Supplier<? extends Fluid> fluid;

    public TFCBubbleColumnBlock(Properties properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        // Override to add the entity immune check for all our aquatic animals, who want to ignore these
        if (!Helpers.isEntity(entity, TFCTags.Entities.BUBBLE_COLUMN_IMMUNE))
        {
            super.entityInside(state, level, pos, entity);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        updateColumnForFluid(level, pos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return fluid.get().defaultFluidState();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();
        if (level.isEmptyBlock(pos.above()) && this == columnOf(TFCFluids.SPRING_WATER.getSource()))
        {
            level.addParticle(TFCParticles.STEAM.get(), x, y + 1.0D, z, 0.0D, 0.0D, 0.0D);
        }
        if (isDownBubbles(level, pos))
        {
            level.addParticle(TFCParticles.BUBBLE_COLUMN_DOWN.get(), x + 0.5, y + 0.8, z, 0.0, 0.0, 0.0);
        }
        else
        {
            level.addAlwaysVisibleParticle(TFCParticles.BUBBLE_COLUMN_UP.get(), x + 0.5, y, z + 0.5, 0.0, 0.04, 0.0);
            level.addAlwaysVisibleParticle(TFCParticles.BUBBLE_COLUMN_UP.get(), x + (double) random.nextFloat(), y + (double) random.nextFloat(), z + (double) random.nextFloat(), 0.0, 0.04, 0.0);
        }
    }

    // Modified from the vanilla one in order to support TFC fluids
    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final Fluid fluid = getFluid();

        level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        if (!state.canSurvive(level, pos))
        {
            return fluid.defaultFluidState().createLegacyBlock();
        }

        if (facing == Direction.DOWN || (facing == Direction.UP && !(facingState.getBlock() instanceof TFCBubbleColumnBlock) && canExistIn(facingState)))
        {
            level.scheduleTick(pos, this, 5);
        }

        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState below = level.getBlockState(pos.below());
        return below.getBlock() instanceof TFCBubbleColumnBlock
            || below.getBlock() instanceof TFCMagmaBlock;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state)
    {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        return new ItemStack(getFluid().getBucket());
    }

    public Fluid getFluid()
    {
        return fluid.get();
    }
}
