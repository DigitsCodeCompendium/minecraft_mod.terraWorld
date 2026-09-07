package com.terraskills.toroidalcompat.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.terraskills.toroidalcompat.mixin.TFCChunkGeneratorAccessor;
import com.toroidalworld.core.WorldFold;
import com.toroidalworld.core.WorldFolds;
import com.toroidalworld.gen.ShapedChunkGenerator;
import com.toroidalworld.shape.FlatShape;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.WorldTopology;
import net.dries007.tfc.world.settings.Settings;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class ToroidalTFCChunkGenerator extends TFCChunkGenerator implements ShapedChunkGenerator {
    public static final MapCodec<ToroidalTFCChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TFCChunkGenerator.CODEC.forGetter(ToroidalTFCChunkGenerator::unshaped),
            ShapedChunkGenerator.SHAPE_CODEC.fieldOf("toroidal_shape").forGetter(ToroidalTFCChunkGenerator::shape)
    ).apply(instance, ToroidalTFCChunkGenerator::new));

    private final FlatShape shape;
    private final WorldFold transformer;

    public ToroidalTFCChunkGenerator(TFCChunkGenerator source, FlatShape shape) {
        this((BiomeSourceExtension) source.getBiomeSource(), noiseSettings(source), source.settings(), shape);
    }

    private ToroidalTFCChunkGenerator(
            BiomeSourceExtension biomeSource,
            Holder<NoiseGeneratorSettings> noiseSettings,
            net.dries007.tfc.world.settings.Settings settings,
            FlatShape shape) {
        super(biomeSource, noiseSettings, settings);
        this.shape = shape;
        this.transformer = WorldFolds.of(shape);
        ActiveTfcFold.set(this.transformer);
    }

    @Override
    public FlatShape shape() {
        return shape;
    }

    @Override
    public WorldFold transformer() {
        return transformer;
    }

    @Override
    protected RegionGenerator createRegionGenerator(Settings settings, Seed seed) {
        final var x = transformer.blockDomain(Direction.Axis.X);
        final var z = transformer.blockDomain(Direction.Axis.Z);
        return new RegionGenerator(settings, seed, WorldTopology.toroidal(
                x.lowerBound,
                z.lowerBound,
                x.domainLength,
                z.domainLength
        ));
    }

    @Override
    protected TFCChunkGenerator copy() {
        return new ToroidalTFCChunkGenerator(
                ((BiomeSourceExtension) getBiomeSource()).copy(),
                noiseSettings(this),
                settings(),
                shape
        );
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected MapCodec<TFCChunkGenerator> codec() {
        // TFC narrows ChunkGenerator's codec return type to MapCodec<TFCChunkGenerator>.
        // The registry still needs the concrete subtype codec so saved worlds decode
        // back into this shaped generator; MapCodec is invariant, hence this bridge.
        return (MapCodec) CODEC;
    }

    private TFCChunkGenerator unshaped() {
        return new TFCChunkGenerator((BiomeSourceExtension) getBiomeSource(), noiseSettings(this), settings());
    }

    private static Holder<NoiseGeneratorSettings> noiseSettings(TFCChunkGenerator generator) {
        return ((TFCChunkGeneratorAccessor) generator).tfcToroidal$noiseSettings();
    }

}
