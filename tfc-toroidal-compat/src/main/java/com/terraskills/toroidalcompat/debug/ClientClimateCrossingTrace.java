package com.terraskills.toroidalcompat.debug;

import com.mojang.logging.LogUtils;
import com.terraskills.toroidalcompat.config.ToroidalCompatConfig;
import com.toroidalworld.api.ToroidalShape;
import com.toroidalworld.api.ToroidalWorldApi;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.util.Optional;

/** Logs the values consumed by TFC's client climate screen across a real wrap. */
public final class ClientClimateCrossingTrace {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Sample previous;
    private static int followupTicks;
    private static int edgeLogCooldown;
    private static boolean warnedMissingShape;
    private static ClientLevel renderedLevel;
    private static boolean rebuiltForShape;

    public static void init() {
        NeoForge.EVENT_BUS.addListener(ClientClimateCrossingTrace::tick);
        ClimateRenderCache.setPositionTransform(ClientClimateCrossingTrace::foldClimatePosition);
        Climate.setPositionTransform(ClientClimateCrossingTrace::foldClimatePosition);
    }

    private static BlockPos foldClimatePosition(BlockPos pos) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Optional<ToroidalShape> active = clientShape(minecraft);
        if (active.isEmpty()) return pos;
        final ToroidalShape shape = active.orElseThrow();
        return new BlockPos(
                shape.loops(Direction.Axis.X)
                        ? (int) Math.floor(shape.foldCoord(Direction.Axis.X, pos.getX())) : pos.getX(),
                pos.getY(),
                shape.loops(Direction.Axis.Z)
                        ? (int) Math.floor(shape.foldCoord(Direction.Axis.Z, pos.getZ())) : pos.getZ());
    }

    public static void tick(ClientTickEvent.Post event) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            previous = null;
            renderedLevel = null;
            rebuiltForShape = false;
            return;
        }

        if (renderedLevel != minecraft.level) {
            renderedLevel = minecraft.level;
            rebuiltForShape = false;
        }

        final Optional<ToroidalShape> optionalShape = clientShape(minecraft);
        if (optionalShape.isEmpty()) {
            if (ToroidalCompatConfig.debugLoggingEnabled() && !warnedMissingShape) {
                warnedMissingShape = true;
                LOGGER.warn("[TFC-TOROIDAL-CLIENT] No shape is visible to the client diagnostic");
            }
            return;
        }
        warnedMissingShape = false;
        if (!rebuiltForShape) {
            rebuiltForShape = true;
            minecraft.levelRenderer.allChanged();
            if (ToroidalCompatConfig.debugLoggingEnabled()) {
                LOGGER.info("[TFC-TOROIDAL-CLIENT] Rebuilt chunk meshes after toroidal shape became available");
            }
        }
        final ToroidalShape shape = optionalShape.orElseThrow();
        final BlockPos rawPosition = minecraft.player.blockPosition();
        ClimateRenderCache.INSTANCE.updateAt(foldClimatePosition(rawPosition));
        if (!ToroidalCompatConfig.debugLoggingEnabled()) {
            previous = null;
            followupTicks = 0;
            return;
        }
        final Sample current = sample(rawPosition);
        if (previous != null && crossed(previous.pos, current.pos, shape)) {
            LOGGER.warn("[TFC-TOROIDAL-CLIENT-CROSSING] BEFORE {}", previous);
            LOGGER.warn("[TFC-TOROIDAL-CLIENT-CROSSING] AFTER-TICK0 {}", current);
            followupTicks = 3;
        } else if (followupTicks > 0) {
            LOGGER.warn("[TFC-TOROIDAL-CLIENT-CROSSING] AFTER-TICK{} {}", 4 - followupTicks, current);
            followupTicks--;
        }
        if (edgeLogCooldown-- <= 0 && nearEdge(current.pos, shape)) {
            LOGGER.warn("[TFC-TOROIDAL-CLIENT-EDGE] {}", current);
            edgeLogCooldown = 5;
        }
        previous = current;
    }

    private static boolean nearEdge(BlockPos pos, ToroidalShape shape) {
        return shape.loops(Direction.Axis.X)
                && (pos.getX() <= shape.minBlock(Direction.Axis.X) + 2
                    || pos.getX() >= shape.maxBlock(Direction.Axis.X) - 2)
                || shape.loops(Direction.Axis.Z)
                && (pos.getZ() <= shape.minBlock(Direction.Axis.Z) + 2
                    || pos.getZ() >= shape.maxBlock(Direction.Axis.Z) - 2);
    }

    private static Optional<ToroidalShape> clientShape(Minecraft minecraft) {
        final Optional<ToroidalShape> clientShape = ToroidalWorldApi.shapeOf(minecraft.level);
        if (clientShape.isPresent()) return clientShape;

        // Toroidal World currently keeps its active shape on the logical server. In an
        // integrated client, use that immutable shape so this diagnostic can observe the
        // same boundary that performed the player teleport.
        final MinecraftServer server = minecraft.getSingleplayerServer();
        return server == null ? Optional.empty() : ToroidalWorldApi.shapeOf(server.overworld());
    }

    private static boolean crossed(BlockPos before, BlockPos after, ToroidalShape shape) {
        return shape.loops(Direction.Axis.X)
                && Math.abs(after.getX() - before.getX()) > shape.widthBlocks(Direction.Axis.X) / 2
                || shape.loops(Direction.Axis.Z)
                && Math.abs(after.getZ() - before.getZ()) > shape.widthBlocks(Direction.Axis.Z) / 2;
    }

    private static Sample sample(BlockPos pos) {
        final ClimateRenderCache cache = ClimateRenderCache.INSTANCE;
        final BlockPos folded = foldClimatePosition(pos);
        final ClimateModel model = Climate.get(Minecraft.getInstance().level);
        final ChunkData rawData = ChunkData.get(Minecraft.getInstance().level, pos);
        final ChunkData foldedData = ChunkData.get(Minecraft.getInstance().level, folded);
        return new Sample(pos.immutable(), folded.immutable(),
                rawData.status(), foldedData.status(),
                model.getInstantTemperature(Minecraft.getInstance().level, pos),
                model.getAverageRainfall(Minecraft.getInstance().level, pos),
                model.getRainfallVariance(Minecraft.getInstance().level, pos),
                model.getInstantTemperature(Minecraft.getInstance().level, folded),
                model.getAverageRainfall(Minecraft.getInstance().level, folded),
                model.getRainfallVariance(Minecraft.getInstance().level, folded),
                cache.getAverageTemperature(), cache.getInstantTemperature(),
                cache.getAverageRainfall(), cache.getInstantRainfall(), cache.getRainVariance(),
                cache.getAverageGroundwater());
    }

    private record Sample(BlockPos pos, BlockPos foldedPos,
                          ChunkData.Status rawDataStatus, ChunkData.Status foldedDataStatus,
                          float rawInstantTemp, float rawAvgRain, float rawRainVariance,
                          float foldedInstantTemp, float foldedAvgRain, float foldedRainVariance,
                          float avgTemp, float instantTemp, float avgRain,
                          float instantRain, float rainVariance, float groundwater) {}

    private ClientClimateCrossingTrace() {}
}
