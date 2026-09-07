package com.terraskills.toroidalcompat.client;

import com.toroidalworld.api.ToroidalShape;
import com.toroidalworld.api.ToroidalWorldApi;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;

/** Keeps TFC climate sampling and rendered chunks aligned with the active toroidal shape. */
public final class ClientClimateTopology {
    private static ClientLevel renderedLevel;
    private static boolean rebuiltForShape;

    public static void init() {
        ClimateRenderCache.setPositionTransform(ClientClimateTopology::foldPosition);
        Climate.setPositionTransform(ClientClimateTopology::foldPosition);
        NeoForge.EVENT_BUS.addListener(ClientClimateTopology::tick);
    }

    private static BlockPos foldPosition(BlockPos pos) {
        final Optional<ToroidalShape> active = shape(Minecraft.getInstance());
        if (active.isEmpty()) return pos;

        final ToroidalShape shape = active.orElseThrow();
        return new BlockPos(
                shape.loops(Direction.Axis.X)
                        ? (int) Math.floor(shape.foldCoord(Direction.Axis.X, pos.getX())) : pos.getX(),
                pos.getY(),
                shape.loops(Direction.Axis.Z)
                        ? (int) Math.floor(shape.foldCoord(Direction.Axis.Z, pos.getZ())) : pos.getZ());
    }

    private static void tick(ClientTickEvent.Post event) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            renderedLevel = null;
            rebuiltForShape = false;
            return;
        }
        if (renderedLevel != minecraft.level) {
            renderedLevel = minecraft.level;
            rebuiltForShape = false;
        }
        if (!rebuiltForShape && shape(minecraft).isPresent()) {
            rebuiltForShape = true;
            minecraft.levelRenderer.allChanged();
        }
    }

    private static Optional<ToroidalShape> shape(Minecraft minecraft) {
        if (minecraft.level != null) {
            final Optional<ToroidalShape> clientShape = ToroidalWorldApi.shapeOf(minecraft.level);
            if (clientShape.isPresent()) return clientShape;
        }
        final MinecraftServer server = minecraft.getSingleplayerServer();
        return server == null ? Optional.empty() : ToroidalWorldApi.shapeOf(server.overworld());
    }

    private ClientClimateTopology() {}
}
