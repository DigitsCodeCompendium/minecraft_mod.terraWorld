/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/**
 * Implementation for Bubble Column Down particles, overriding default ones so they can work in any liquid not just water
 */
public class BubbleColumnDownParticle extends TextureSheetParticle
{
    private float angle;


    public BubbleColumnDownParticle(ClientLevel worldIn, double x, double y, double z)
    {
        // Sets Bubble column particle paramters
        super(worldIn, x, y, z);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= random.nextFloat() * 0.6F + 0.2F;
        this.lifetime = (int)(Math.random() * 60.0) + 30;
        this.hasPhysics = false;
        this.xd = 0.0;
        this.yd = -0.05;
        this.zd = 0.0;
        this.gravity = 0.002F;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime)
            this.remove();
        this.xd += (double)(0.6F * Mth.cos(this.angle));
        this.zd += (double)(0.6F * Mth.sin(this.angle));
        this.xd *= 0.07;
        this.zd *= 0.07;
        this.move(this.xd, this.yd, this.zd);
        if (this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).isEmpty() || this.onGround)
            this.remove();
        this.angle += 0.08F;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            BubbleColumnDownParticle particle = new BubbleColumnDownParticle(level, x, y, z);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
