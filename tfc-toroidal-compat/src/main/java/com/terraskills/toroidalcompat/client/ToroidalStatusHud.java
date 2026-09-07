package com.terraskills.toroidalcompat.client;

import com.terraskills.toroidalcompat.config.ToroidalCompatClientConfig;
import com.toroidalworld.api.ToroidalShape;
import com.toroidalworld.api.ToroidalWorldApi;
import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Optional;

public final class ToroidalStatusHud {
    private static final int WIDTH = 154;
    private static final int HEIGHT = 41;

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!ToroidalCompatClientConfig.SHOW_HUD.get() || minecraft.options.hideGui
                || minecraft.player == null || minecraft.level == null) return;

        final Optional<ToroidalShape> optionalShape = ToroidalWorldApi.shapeOf(minecraft.level).or(() -> {
            final var server = minecraft.getSingleplayerServer();
            return server == null ? Optional.empty() : ToroidalWorldApi.shapeOf(server.overworld());
        });
        if (optionalShape.isEmpty()) return;

        final float scale = ToroidalCompatClientConfig.HUD_SCALE.get().floatValue();
        graphics.pose().pushPose();
        graphics.pose().translate(ToroidalCompatClientConfig.LEFT_OFFSET.get(),
                graphics.guiHeight() - ToroidalCompatClientConfig.BOTTOM_OFFSET.get(), 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().translate(0, -HEIGHT, 0);

        drawPanel(graphics);
        graphics.drawString(minecraft.font,
                Component.literal("Location").withStyle(ChatFormatting.GOLD), 6, 5, 0xFFFFFFFF, true);

        final ToroidalShape shape = optionalShape.orElseThrow();
        final double foldedX = shape.loops(Direction.Axis.X)
                ? shape.foldCoord(Direction.Axis.X, minecraft.player.getX()) : minecraft.player.getX();
        final int foldedZ = (int) Math.floor(shape.loops(Direction.Axis.Z)
                ? shape.foldCoord(Direction.Axis.Z, minecraft.player.getZ()) : minecraft.player.getZ());
        final double latitude = SolarCalculator.getLatitude(foldedZ,
                Climate.get(minecraft.level).hemisphereScale()) * 180.0 / Math.PI;
        final double longitude = shape.loops(Direction.Axis.X)
                ? (foldedX - shape.minBlock(Direction.Axis.X)) / shape.widthBlocks(Direction.Axis.X) * 360.0 - 180.0
                : foldedX;
        graphics.drawString(minecraft.font, "Latitude   " + angular(latitude, "N", "S"),
                6, 17, 0xFFE7E7E7, true);
        graphics.drawString(minecraft.font, "Longitude  " + angular(longitude, "E", "W"),
                6, 29, 0xFFE7E7E7, true);
        graphics.pose().popPose();
    }

    private static String angular(double value, String positive, String negative) {
        final String direction = value < 0 ? negative : positive;
        return String.format(Locale.ROOT, "%.2f\u00B0%s", Math.abs(value), direction);
    }

    private static void drawPanel(GuiGraphics graphics) {
        graphics.fill(0, 0, WIDTH, HEIGHT, 0xFF000000);
        graphics.fill(1, 1, WIDTH - 1, HEIGHT - 1, 0xFF373737);
        graphics.fill(1, 1, WIDTH - 2, 2, 0xFFFFFFFF);
        graphics.fill(1, 1, 2, HEIGHT - 2, 0xFFFFFFFF);
        graphics.fill(2, HEIGHT - 2, WIDTH - 1, HEIGHT - 1, 0xFF555555);
        graphics.fill(WIDTH - 2, 2, WIDTH - 1, HEIGHT - 1, 0xFF555555);
    }

    private ToroidalStatusHud() {}
}
