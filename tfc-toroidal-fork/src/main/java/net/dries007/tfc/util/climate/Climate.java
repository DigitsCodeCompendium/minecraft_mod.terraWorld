/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.tracker.WorldTracker;

/**
 * Provides access on a per-world basis to the underlying {@link ClimateModel}, along with additional utility methods for interacting
 * with the climate. Most of the methods here are simply bouncers to the methods on climate model, through the model for the given level.
 *
 * @see ClimateModel
 */
public final class Climate
{
    private static UnaryOperator<BlockPos> positionTransform = UnaryOperator.identity();

    /** Compatibility hook for finite worlds whose client may render translated copies. */
    public static void setPositionTransform(UnaryOperator<BlockPos> transform)
    {
        positionTransform = transform == null ? UnaryOperator.identity() : transform;
    }

    public static BlockPos transformPosition(BlockPos pos)
    {
        return positionTransform.apply(pos);
    }

    /**
     * @return The climate model for the provided {@code level}.
     */
    public static ClimateModel get(Level level)
    {
        return WorldTracker.get(level).getClimateModel();
    }

    /**
     * Selects and initializes the climate model for the world.
     */
    public static void chooseModelForWorld(ServerLevel level)
    {
        final SelectClimateModelEvent event = new SelectClimateModelEvent(level);
        NeoForge.EVENT_BUS.post(event);
        WorldTracker.get(level).setClimateModel(event.getModel());
    }

    public static float getInstantTemperature(Level level, BlockPos pos, long calendarTick, int daysInMonth)
    {
        return get(level).getInstantTemperature(level, transformPosition(pos), calendarTick, daysInMonth);
    }

    public static float getInstantTemperature(Level level, BlockPos pos, ICalendar calendar, long calendarTick)
    {
        return get(level).getInstantTemperature(level, pos, calendarTick, calendar.getCalendarDaysInMonth());
    }

    public static float getInstantTemperature(Level level, BlockPos pos, ICalendar calendar)
    {
        return get(level).getInstantTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float getInstantTemperature(Level level, BlockPos pos)
    {
        return getInstantTemperature(level, pos, Calendars.get(level));
    }

    public static float getAverageTemperature(Level level, BlockPos pos)
    {
        return get(level).getAverageTemperature(level, transformPosition(pos));
    }

    public static float getAverageRainfall(Level level, BlockPos pos)
    {
        return get(level).getAverageRainfall(level, transformPosition(pos));
    }

    public static float getRainfallVariance(Level level, BlockPos pos)
    {
        return get(level).getRainfallVariance(level, transformPosition(pos));
    }

    public static float getAverageGroundwater(Level level, BlockPos pos)
    {
        return get(level).getAverageGroundwater(level, transformPosition(pos));
    }

    /**
     * Converts a vanilla biome temperature into a TFC temperature in degrees Celsius.
     * <p>
     * Vanilla temperature assigns 0.15 as the freezing point of water (0C), and typically ranges from -0.5 to +1 in the overworld. We infer
     * this to mean O C -> 0.15, -30 C -> -0.51, and +30C -> 0.801
     */
    public static float fromVanilla(float vanillaTemperature)
    {
        return (vanillaTemperature - 0.15f) / 0.0217f;
    }
}
