package com.terraskills.toroidalcompat.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.terraskills.toroidalcompat.config.ToroidalCompatConfig;
import com.toroidalworld.gen.ShapedChunkGenerator;
import com.terraskills.toroidalcompat.worldgen.ToroidalTFCChunkGenerator;
import com.toroidalworld.api.ToroidalShape;
import com.toroidalworld.api.ToroidalWorldApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;

public final class WrapDebugOverlay {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<UUID, AxisBars> enabled = new HashMap<>();
    private final Map<UUID, BlockPos> previousPositions = new HashMap<>();

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher(), "tfc-toroidal-debug");
        register(event.getDispatcher(), "toroidaldebug");
    }

    private void register(CommandDispatcher<CommandSourceStack> dispatcher, String literal) {
        dispatcher.register(Commands.literal(literal)
                .executes(context -> toggle(context.getSource().getPlayerOrException()))
                .then(Commands.literal("status")
                        .executes(context -> status(context.getSource().getPlayerOrException())))
                .then(Commands.literal("dump")
                        .executes(context -> status(context.getSource().getPlayerOrException())))
                .then(Commands.literal("seam")
                        .executes(context -> seam(context.getSource().getPlayerOrException()))));
    }

    private int seam(ServerPlayer player) {
        if (!debugEnabled(player)) return 0;
        final ServerLevel level = player.serverLevel();
        final Optional<ToroidalShape> activeShape = ToroidalWorldApi.shapeOf(level);
        if (activeShape.isEmpty()) {
            player.sendSystemMessage(Component.literal("[TFC Toroidal] No active shape."));
            return 0;
        }

        final ToroidalShape shape = activeShape.orElseThrow();
        final Object generator = level.getChunkSource().getGenerator();
        final String header = "[TFC-TOROIDAL-SEAM] generator=" + generator.getClass().getName()
                + ", climateModel=" + Climate.get(level).getClass().getName()
                + (generator instanceof ToroidalTFCChunkGenerator toroidal
                ? ", identification=" + toroidal.shape().identification()
                    + ", skewChunks=" + toroidal.shape().skewChunks()
                    + ", mirror=" + toroidal.shape().mirror()
                : "")
                + (generator instanceof ChunkGeneratorExtension extension
                ? ", configuredTempScale=" + extension.settings().temperatureScale()
                    + ", effectiveTempScale=" + extension.climateTemperatureScale()
                    + ", configuredRainScale=" + extension.settings().rainfallScale()
                : "");
        emit(player, header);

        final int y = player.blockPosition().getY();
        final int centerX = (int) Math.floor(shape.foldCoord(Direction.Axis.X, player.getX()));
        final int centerZ = (int) Math.floor(shape.foldCoord(Direction.Axis.Z, player.getZ()));
        sampleAxis(player, shape, Direction.Axis.X, y, centerX, centerZ);
        sampleAxis(player, shape, Direction.Axis.Z, y, centerX, centerZ);
        terrainProfile(player, shape, Direction.Axis.X, centerX, centerZ);
        terrainProfile(player, shape, Direction.Axis.Z, centerX, centerZ);
        emit(player, "[TFC-TOROIDAL-SEAM] END");
        player.sendSystemMessage(Component.literal("Seam report written to latest.log. Send the [TFC-TOROIDAL-SEAM] block."));
        return 1;
    }

    private static void terrainProfile(ServerPlayer player, ToroidalShape shape, Direction.Axis axis,
                                       int centerX, int centerZ) {
        if (!shape.loops(axis)) return;
        final ServerLevel level = player.serverLevel();
        final int min = shape.minBlock(axis);
        final int max = shape.maxBlock(axis);
        final StringBuilder heights = new StringBuilder();
        final StringBuilder generatedHeights = new StringBuilder();
        final StringBuilder biomes = new StringBuilder();
        for (int offset = -8; offset < 8; offset++) {
            final int coordinate = offset < 0 ? max + offset : min + offset;
            final int x = axis == Direction.Axis.X ? coordinate : centerX;
            final int z = axis == Direction.Axis.Z ? coordinate : centerZ;
            final BlockPos pos = new BlockPos(x, level.getSeaLevel(), z);
            final int height = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            final int generatedHeight = generatedHeight(level, pos);
            final String biome = level.getBiome(pos).unwrapKey()
                    .map(key -> key.location().getPath()).orElse("unregistered");
            if (!heights.isEmpty()) {
                heights.append(',');
                generatedHeights.append(',');
                biomes.append(',');
            }
            heights.append(offset).append(':').append(height);
            generatedHeights.append(offset).append(':').append(generatedHeight);
            biomes.append(offset).append(':').append(biome);
        }
        emit(player, "[TFC-TOROIDAL-PROFILE] axis=" + axis.getName().toUpperCase()
                + " heights={" + heights + "}");
        emit(player, "[TFC-TOROIDAL-PROFILE] axis=" + axis.getName().toUpperCase()
                + " generatedHeights={" + generatedHeights + "}");
        emit(player, "[TFC-TOROIDAL-PROFILE] axis=" + axis.getName().toUpperCase()
                + " biomes={" + biomes + "}");
    }

    private static void sampleAxis(ServerPlayer player, ToroidalShape shape, Direction.Axis axis,
                                   int y, int centerX, int centerZ) {
        if (!shape.loops(axis)) return;
        final int min = shape.minBlock(axis);
        final int max = shape.maxBlock(axis);
        final int width = shape.widthBlocks(axis);
        final int inset = Math.min(8, Math.max(1, width / 16));

        final BlockPos lower = axis == Direction.Axis.X
                ? new BlockPos(min + inset, y, centerZ) : new BlockPos(centerX, y, min + inset);
        final BlockPos upper = axis == Direction.Axis.X
                ? new BlockPos(max - inset, y, centerZ) : new BlockPos(centerX, y, max - inset);
        final BlockPos copy = axis == Direction.Axis.X
                ? lower.offset(width, 0, 0) : lower.offset(0, 0, width);
        final BlockPos edgeLower = axis == Direction.Axis.X
                ? new BlockPos(min, y, centerZ) : new BlockPos(centerX, y, min);
        final BlockPos edgeUpper = axis == Direction.Axis.X
                ? new BlockPos(max - 1, y, centerZ) : new BlockPos(centerX, y, max - 1);

        emit(player, diagnostic(player.serverLevel(), axis.getName().toUpperCase() + "-LOWER", lower));
        emit(player, diagnostic(player.serverLevel(), axis.getName().toUpperCase() + "-UPPER", upper));
        emit(player, diagnostic(player.serverLevel(), axis.getName().toUpperCase() + "-LOWER-COPY", copy));
        emit(player, diagnostic(player.serverLevel(), axis.getName().toUpperCase() + "-EDGE-MAX", edgeUpper));
        emit(player, diagnostic(player.serverLevel(), axis.getName().toUpperCase() + "-EDGE-MIN", edgeLower));
    }

    private static String diagnostic(ServerLevel level, String label, BlockPos pos) {
        try {
            final ChunkData data = ChunkData.get(level, pos);
            final ChunkPos chunk = new ChunkPos(pos);
            final String biome = level.getBiome(pos).unwrapKey()
                    .map(key -> key.location().toString()).orElse("unregistered");
            return "[TFC-TOROIDAL-SEAM] " + label
                    + " pos=" + pos.toShortString() + " chunk=" + chunk
                    + " biome=" + biome
                    + " avgTemp=" + format(Climate.getAverageTemperature(level, pos))
                    + " instantTemp=" + format(Climate.getInstantTemperature(level, pos))
                    + " avgRain=" + format(Climate.getAverageRainfall(level, pos))
                    + " rainVariance=" + format(Climate.getRainfallVariance(level, pos))
                    + " storedSeaTemp=" + format(data.getAverageSeaLevelTemp(pos))
                    + " storedRain=" + format(data.getAverageRainfall(pos))
                    + " storedSurfaceY=" + level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ())
                    + " generatedBaseY=" + generatedHeight(level, pos);
        } catch (RuntimeException exception) {
            return "[TFC-TOROIDAL-SEAM] " + label + " pos=" + pos.toShortString()
                    + " ERROR=" + exception;
        }
    }

    private static int generatedHeight(ServerLevel level, BlockPos pos) {
        final Object generator = level.getChunkSource().getGenerator();
        if (generator instanceof TFCChunkGenerator tfc) {
            return (int) tfc.createHeightFillerForChunk(new ChunkPos(pos)).sampleHeight(pos.getX(), pos.getZ());
        }
        return Integer.MIN_VALUE;
    }

    private static void emit(ServerPlayer player, String line) {
        LOGGER.warn(line);
        player.sendSystemMessage(Component.literal(line));
    }

    private int status(ServerPlayer player) {
        if (!debugEnabled(player)) return 0;
        final Optional<ToroidalShape> activeShape = ToroidalWorldApi.shapeOf(player.serverLevel());
        final Object generator = player.serverLevel().getChunkSource().getGenerator();
        player.sendSystemMessage(Component.literal("[TFC Toroidal] dimension="
                + player.serverLevel().dimension().location()));
        player.sendSystemMessage(Component.literal("generator=" + generator.getClass().getName()
                + ", shaped=" + (generator instanceof ShapedChunkGenerator)));
        if (activeShape.isEmpty()) {
            player.sendSystemMessage(Component.literal("shape=NONE (wrapping cannot occur)"));
            return 0;
        }

        final ToroidalShape shape = activeShape.orElseThrow();
        player.sendSystemMessage(axisStatus(shape, Direction.Axis.X, player.getX()));
        player.sendSystemMessage(axisStatus(shape, Direction.Axis.Z, player.getZ()));
        player.sendSystemMessage(Component.literal("If raw is outside [min,max) but folded differs, wait one tick. "
                + "A red fallback message means Toroidal's own entity wrap was missed."));
        return 1;
    }

    private static Component axisStatus(ToroidalShape shape, Direction.Axis axis, double raw) {
        if (!shape.loops(axis)) {
            return Component.literal(axis.getName().toUpperCase() + ": unbounded, raw=" + format(raw));
        }
        return Component.literal(axis.getName().toUpperCase() + ": raw=" + format(raw)
                + ", folded=" + format(shape.foldCoord(axis, raw))
                + ", bounds=[" + shape.minBlock(axis) + "," + shape.maxBlock(axis) + ")"
                + ", width=" + shape.widthBlocks(axis));
    }

    private int toggle(ServerPlayer player) {
        if (!debugEnabled(player)) return 0;
        final AxisBars previous = enabled.remove(player.getUUID());
        if (previous != null) {
            previous.removePlayer(player);
            player.sendSystemMessage(Component.literal("Toroidal edge overlay disabled."));
            return 0;
        }

        final Optional<ToroidalShape> shape = ToroidalWorldApi.shapeOf(player.serverLevel());
        if (shape.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    "No Toroidal World shape is active for this save. The edge overlay cannot be shown."));
            return 0;
        }

        final AxisBars bars = new AxisBars(shape.orElseThrow());
        bars.addPlayer(player);
        bars.update(player);
        enabled.put(player.getUUID(), bars);
        player.sendSystemMessage(Component.literal("Toroidal edge overlay enabled. Run the command again to hide it."));
        return 1;
    }

    @SubscribeEvent
    public void update(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            enforcePlayerWrap(player);
            if (!ToroidalCompatConfig.debugLoggingEnabled()) {
                previousPositions.remove(player.getUUID());
                final AxisBars disabledBars = enabled.remove(player.getUUID());
                if (disabledBars != null) disabledBars.removePlayer(player);
                return;
            }
            traceActualCrossing(player);
            final AxisBars bars = enabled.get(player.getUUID());
            if (bars != null) {
                bars.update(player);
            }
        }
    }

    private void traceActualCrossing(ServerPlayer player) {
        final BlockPos current = player.blockPosition();
        final BlockPos previous = previousPositions.put(player.getUUID(), current.immutable());
        final Optional<ToroidalShape> activeShape = ToroidalWorldApi.shapeOf(player.serverLevel());
        if (previous == null || activeShape.isEmpty()) return;

        final ToroidalShape shape = activeShape.orElseThrow();
        final boolean crossedX = shape.loops(Direction.Axis.X)
                && Math.abs(current.getX() - previous.getX()) > shape.widthBlocks(Direction.Axis.X) / 2;
        final boolean crossedZ = shape.loops(Direction.Axis.Z)
                && Math.abs(current.getZ() - previous.getZ()) > shape.widthBlocks(Direction.Axis.Z) / 2;
        if (!crossedX && !crossedZ) return;

        final ServerLevel level = player.serverLevel();
        final String axis = crossedX && crossedZ ? "XZ" : crossedX ? "X" : "Z";
        emit(player, "[TFC-TOROIDAL-CROSSING] axis=" + axis
                + " rawDelta=(" + (current.getX() - previous.getX()) + ","
                + (current.getZ() - previous.getZ()) + ")");
        emit(player, diagnostic(level, "BEFORE", previous)
                .replace("[TFC-TOROIDAL-SEAM]", "[TFC-TOROIDAL-CROSSING]"));
        emit(player, diagnostic(level, "AFTER", current)
                .replace("[TFC-TOROIDAL-SEAM]", "[TFC-TOROIDAL-CROSSING]"));

    }

    private static void enforcePlayerWrap(ServerPlayer player) {
        final Optional<ToroidalShape> activeShape = ToroidalWorldApi.shapeOf(player.serverLevel());
        if (activeShape.isEmpty() || player.isPassenger()) {
            return;
        }

        final ToroidalShape shape = activeShape.orElseThrow();
        final double foldedX = shape.loops(Direction.Axis.X)
                ? shape.foldCoord(Direction.Axis.X, player.getX()) : player.getX();
        final double foldedZ = shape.loops(Direction.Axis.Z)
                ? shape.foldCoord(Direction.Axis.Z, player.getZ()) : player.getZ();
        if (Math.abs(foldedX - player.getX()) > 0.001 || Math.abs(foldedZ - player.getZ()) > 0.001) {
            final String diagnostic = "Fallback wrapped " + player.getGameProfile().getName()
                    + " from (" + format(player.getX()) + ", " + format(player.getZ()) + ") to ("
                    + format(foldedX) + ", " + format(foldedZ) + ")";
            if (ToroidalCompatConfig.debugLoggingEnabled()) {
                LOGGER.warn(diagnostic);
                player.sendSystemMessage(Component.literal("[TFC Toroidal] " + diagnostic));
            }
            player.teleportTo(foldedX, player.getY(), foldedZ);
        }
    }

    private static boolean debugEnabled(ServerPlayer player) {
        if (ToroidalCompatConfig.debugLoggingEnabled()) return true;
        player.sendSystemMessage(Component.literal("Toroidal diagnostics are disabled. Set enableDebugLogging=true "
                + "in config/tfc_toroidal_compat-common.toml and reload or restart."));
        return false;
    }

    private static String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    @SubscribeEvent
    public void remove(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            previousPositions.remove(player.getUUID());
            final AxisBars bars = enabled.remove(player.getUUID());
            if (bars != null) {
                bars.removePlayer(player);
            }
        }
    }

    private static final class AxisBars {
        private final ToroidalShape shape;
        private final Map<Direction.Axis, ServerBossEvent> bars = new EnumMap<>(Direction.Axis.class);

        private AxisBars(ToroidalShape shape) {
            this.shape = shape;
            for (Direction.Axis axis : new Direction.Axis[] {Direction.Axis.X, Direction.Axis.Z}) {
                if (shape.loops(axis)) {
                    bars.put(axis, new ServerBossEvent(Component.empty(), BossEvent.BossBarColor.GREEN,
                            BossEvent.BossBarOverlay.PROGRESS));
                }
            }
        }

        private void addPlayer(ServerPlayer player) {
            bars.values().forEach(bar -> bar.addPlayer(player));
        }

        private void removePlayer(ServerPlayer player) {
            bars.values().forEach(bar -> bar.removePlayer(player));
        }

        private void update(ServerPlayer player) {
            if (ToroidalWorldApi.shapeOf(player.serverLevel()).isEmpty()) {
                return;
            }

            updateAxis(Direction.Axis.X, player.getX());
            updateAxis(Direction.Axis.Z, player.getZ());
        }

        private void updateAxis(Direction.Axis axis, double coordinate) {
            final ServerBossEvent bar = bars.get(axis);
            if (bar == null) {
                return;
            }

            final double folded = shape.foldCoord(axis, coordinate);
            final double distanceToLower = folded - shape.minBlock(axis);
            final double distanceToUpper = shape.maxBlock(axis) - folded;
            final double distance = Math.max(0.0, Math.min(distanceToLower, distanceToUpper));
            final double halfWidth = shape.widthBlocks(axis) / 2.0;
            final float proximity = (float) Math.clamp(1.0 - distance / halfWidth, 0.0, 1.0);
            final long roundedDistance = Math.round(distance);

            bar.setName(Component.literal(axis.getName().toUpperCase() + " seam: " + roundedDistance
                    + " blocks | raw " + format(coordinate) + " -> folded " + format(folded)
                    + " | [" + shape.minBlock(axis) + ", " + shape.maxBlock(axis) + ")"));
            bar.setProgress(proximity);
            bar.setColor(roundedDistance <= 16
                    ? BossEvent.BossBarColor.RED
                    : roundedDistance <= 64 ? BossEvent.BossBarColor.YELLOW : BossEvent.BossBarColor.GREEN);
        }
    }
}
