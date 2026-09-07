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
/*
 * Implementation for generic bubble particles, overriding default bubble particles to work in any liquid not just water.
 */
public class BubbleParticle extends TextureSheetParticle
{
    public BubbleParticle(ClientLevel worldIn, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        // Sets Bubble particle paramters
        super(worldIn, x, y, z);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= random.nextFloat() * 0.6F + 0.2F;
        this.xd = motionX * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.yd = motionY * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.zd = motionZ * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime)
            this.remove();
        yd += 0.002D;
        move(xd, yd, zd);
        xd *= 0.85F;
        yd *= 0.85F;
        zd *= 0.85F;
        if (this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).isEmpty())
            this.remove();
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
            BubbleParticle particle = new BubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
