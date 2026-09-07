package com.terraskills.toroidalcompat.worldgen;

import com.toroidalworld.core.WorldFold;
import com.toroidalworld.core.WrapDomain;
import net.minecraft.core.Direction;

import java.util.function.DoubleBinaryOperator;

/** Makes a complete block-coordinate field continuous at each looping seam. */
public final class PeriodicField2D {
    private static final double MAX_BLEND_BLOCKS = 512.0;

    public static double sample(double x, double z, DoubleBinaryOperator field) {
        final WorldFold fold = ActiveTfcFold.get();
        if (fold == null) return field.applyAsDouble(x, z);

        final Blend bx = blend(fold, Direction.Axis.X, x);
        final Blend bz = blend(fold, Direction.Axis.Z, z);
        final double nn = field.applyAsDouble(bx.near, bz.near);
        final double fn = bx.weight == 0 ? nn : field.applyAsDouble(bx.far, bz.near);
        final double x0 = lerp(bx.weight, nn, fn);
        if (bz.weight == 0) return x0;

        final double nf = field.applyAsDouble(bx.near, bz.far);
        final double ff = bx.weight == 0 ? nf : field.applyAsDouble(bx.far, bz.far);
        return lerp(bz.weight, x0, lerp(bx.weight, nf, ff));
    }

    private static Blend blend(WorldFold fold, Direction.Axis axis, double value) {
        if (!fold.bounds().loops(axis)) return new Blend(value, value, 0);
        final WrapDomain domain = fold.blockDomain(axis);
        final double width = domain.upperBound - domain.lowerBound;
        final double canonical = domain.wrap(value);
        final double blendWidth = Math.min(MAX_BLEND_BLOCKS, width / 8.0);
        final double start = domain.upperBound - blendWidth;
        if (canonical <= start) return new Blend(canonical, canonical - width, 0);

        final double t = (canonical - start) / blendWidth;
        return new Blend(canonical, canonical - width, t * t * (3 - 2 * t));
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private record Blend(double near, double far, double weight) { }
    private PeriodicField2D() { }
}
