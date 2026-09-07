/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.AnvilScreen;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.network.ScreenButtonPacket;

public class AnvilWeldButton extends Button
{
    private final AnvilBlockEntity anvil;

    public AnvilWeldButton(AnvilBlockEntity anvil, int guiLeft, int guiTop)
    {
        super(guiLeft + 21, guiTop + 56, 18, 18, Component.translatable("tfc.tooltip.anvil_weld"), button -> {
            PacketDistributor.sendToServer(new ScreenButtonPacket(AnvilContainer.WELD_ID));
        }, RenderHelpers.NARRATION);
        setTooltip(Tooltip.create(Component.translatable("tfc.tooltip.anvil_weld")));

        this.anvil = anvil;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        final boolean canWeld = anvil.canWeldNow();
        int x = getX();
        int y = getY();
        graphics.blit(AnvilScreen.BACKGROUND, x, y, 218, canWeld ? 0 : 18, width, height, 256, 256);
        graphics.blit(AnvilScreen.BACKGROUND, x + 1, y + 1, 236, 16, 16, 16, 256, 256);
    }

}
