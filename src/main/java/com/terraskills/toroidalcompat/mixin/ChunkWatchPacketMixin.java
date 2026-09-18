package com.terraskills.toroidalcompat.mixin;

import com.terraskills.toroidalcompat.client.ClientChunkClimateCache;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.network.ChunkWatchPacket;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkWatchPacket.class, remap = false)
public abstract class ChunkWatchPacketMixin {
    @Inject(method = "handle", at = @At("HEAD"))
    private void tfcToroidal$retainCanonicalClimate(CallbackInfo ci) {
        final Level level = ClientHelpers.getLevel();
        if (level == null) return;
        final ChunkWatchPacket packet = (ChunkWatchPacket) (Object) this;
        ClientChunkClimateCache.update(level, packet.pos(), packet.rainfall(), packet.rainVariance(),
                packet.baseGroundwater(), packet.temperature(), packet.forestType());
    }
}
