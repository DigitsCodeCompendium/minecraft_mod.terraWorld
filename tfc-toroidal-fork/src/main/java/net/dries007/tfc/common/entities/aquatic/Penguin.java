/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;

import net.minecraft.world.entity.AnimationState;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.Helpers;

public class Penguin extends AmphibiousAnimal
{
    public final AnimationState walkingAnimation = new AnimationState();
    public final AnimationState swimmingAnimation = new AnimationState();

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.STEP_HEIGHT, 1.0);
    }

    public Penguin(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level, TFCSounds.PENGUIN);
    }

    @Override
    public boolean isPlayingDeadEffective()
    {
        return false;
    }

    @Override
    public void tick()
    {
        if (level().isClientSide)
        {
            EntityHelpers.startOrStop(walkingAnimation, EntityHelpers.isMovingOnLand(this), tickCount);
            EntityHelpers.startOrStop(swimmingAnimation, EntityHelpers.isMovingInWater(this), tickCount);
        }
        super.tick();
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.PENGUIN_FOOD);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return ambient.get();
    }

    public void playAmbientSound()
    {
        if (!this.isInWaterOrBubble())
        {
            super.playAmbientSound();
        }
    }
}
