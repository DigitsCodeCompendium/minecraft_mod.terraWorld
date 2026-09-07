/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BerryBushBlockEntity extends TickingPlantBlockEntity
{
    public static void reset(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(TickCounterBlockEntity::resetCounter);
    }

    public static void resetPickedTick(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(BerryBushBlockEntity::resetLastPickedCounter);
    }

    // Allows for large bushes without runaway spreading
    private int growthsRemaining = 24;

    protected BerryBushBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BERRY_BUSH.get(), pos, state);
    }

    protected BerryBushBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        growthsRemaining = nbt.getInt("growthsRemaining");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putInt("growthsRemaining", growthsRemaining);
        super.saveAdditional(nbt, provider);
    }

    /**
     * Adds to the amount of time counted by setting the lastUpdateTick farther in the past
     */
    public void decreaseGrowthsRemaining(int amount)
    {
        growthsRemaining -= amount;
        setChanged();
    }

    public void setGrowthsRemaining(int growths)
    {
        growthsRemaining = growths;
        setChanged();
    }

    public int getGrowthsRemaining()
    {
        return growthsRemaining;
    }

}
