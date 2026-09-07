/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.LemmingModel;
import net.dries007.tfc.common.entities.prey.Pest;

public class LemmingRenderer extends SimpleMobRenderer<Pest, LemmingModel>
{
    public LemmingRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new LemmingModel(RenderHelpers.bakeSimple(ctx, "lemming")), "lemming", 0.2f, false, 1f, false, true, null);
    }

    @Override
    protected void setupRotations(Pest entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale)
    {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        if (entity.isClimbing())
        {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
            poseStack.popPose();
        }
        if (entity.draggingAnimation.isStarted())
        {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            poseStack.popPose();
        }
    }
}
