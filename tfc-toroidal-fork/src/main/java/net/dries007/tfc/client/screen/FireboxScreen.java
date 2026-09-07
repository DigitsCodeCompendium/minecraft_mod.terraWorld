/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.FireboxBlockEntity;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.container.FireboxContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class FireboxScreen extends BlockEntityScreen<FireboxBlockEntity, FireboxContainer>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/firebox.png");
    private static final ResourceLocation THERMOMETER = Helpers.identifier("container/thermometer");
    private static final ResourceLocation THERMOMETER_INDICATOR = Helpers.identifier("container/thermometer_indicator");

    public FireboxScreen(FireboxContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, TEXTURE);

        inventoryLabelY += 36;
        imageHeight += 36;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        graphics.blitSprite(THERMOMETER, leftPos + 7, topPos + 16, 17, 74);
        final int temperature = Heat.scaleTemperatureForGui(blockEntity.getTemperature());
        if (temperature > 0)
        {
            graphics.blitSprite(THERMOMETER_INDICATOR, leftPos + 8, topPos + 76 - temperature, 15, 5);
            final long left = blockEntity.getTimeLeft();
            if (left == -1 || blockEntity.getHeatingCount() == 0)
            {
                graphics.drawString(font, Component.translatable("tfc.tooltip.firebox.no_heat"), leftPos + 20, topPos + 95, 0x404040, false);
                return;
            }
            if (left > 0)
            {
                graphics.drawString(font, Component.translatable("tfc.tooltip.firebox.time_to_heat", blockEntity.getHeatingCount(), Calendars.CLIENT.getTimeDelta(left)), leftPos + 20, topPos + 95, 0x404040, false);
            }
            else
            {
                graphics.drawString(font, Component.translatable("tfc.tooltip.firebox.heated", blockEntity.getHeatingCount()), leftPos + 20, topPos + 95, 0x404040, false);
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
        if (text != null)
        {
            if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 8, topPos + 76 - 51, 15, 51))
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }

    }
}
