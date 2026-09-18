package com.terraskills.toroidalcompat.client;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.LerpFloatLayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientChunkClimateCache {
    private static final Map<Long, ChunkData> CACHE = new ConcurrentHashMap<>();
    private static Level level;

    public static void update(
            Level currentLevel, ChunkPos pos, LerpFloatLayer rainfall, LerpFloatLayer rainVariance,
            LerpFloatLayer baseGroundwater, LerpFloatLayer temperature, ForestType forestType) {
        if (level != currentLevel) {
            CACHE.clear();
            level = currentLevel;
        }
        final ChunkData data = new ChunkData(pos);
        data.onUpdatePacket(rainfall, rainVariance, baseGroundwater, temperature, forestType);
        CACHE.put(pos.toLong(), data);
    }

    public static ChunkData get(Level currentLevel, ChunkPos pos) {
        if (level != currentLevel) return null;
        return CACHE.get(pos.toLong());
    }

    private ClientChunkClimateCache() {}
}
