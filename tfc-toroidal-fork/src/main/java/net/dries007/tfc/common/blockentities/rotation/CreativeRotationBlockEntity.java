/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blocks.rotation.CreativeRotationBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;

public class CreativeRotationBlockEntity extends TickableBlockEntity implements RotatingBlockEntity
{
    // 30 RPM
    public static final float MAX_SPEED = Mth.TWO_PI / (2 * 20);
    public static final int MAX_STEPS = 8;
    public static final float LERP_SPEED = MAX_SPEED / MAX_STEPS;

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeRotationBlockEntity motor)
    {
        motor.checkForLastTickSync();

        clientTick(level, pos, state, motor);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CreativeRotationBlockEntity motor)
    {
        final Rotation.Tickable rotation = motor.node.rotation();
        rotation.tick();
        float target = motor.step * LERP_SPEED;
        if (rotation.speed() != target)
        {
            rotation.setSpeed(target);
        }
    }

    private final CreativeSourceNode node;
    private int step;
    private boolean invalid;

    //TODO add a function to change rotation axis
    public CreativeRotationBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CREATIVE_MOTOR.get(), pos, state);
    }

    protected CreativeRotationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        Direction.Axis axis = state.getValue(CreativeRotationBlock.AXIS);
        this.node = new CreativeSourceNode(pos, Node.ofAxis(axis), Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE), 0f);
    }

    @Override
    public void markAsInvalidInNetwork()
    {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork()
    {
        return invalid;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        node.rotation().saveToTag(tag);
        tag.putInt("step", step);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        node.rotation().loadFromTag(tag);
        step = tag.getInt("step");
        invalid = tag.getBoolean("invalid");
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    public void incrementSpeed()
    {
        step = Mth.clamp(step + 1, -MAX_STEPS, MAX_STEPS);
    }

    public void decrementSpeed()
    {
        step = Mth.clamp(step - 1, -MAX_STEPS, MAX_STEPS);
    }

    private static class CreativeSourceNode extends SourceNode
    {
        protected CreativeSourceNode(BlockPos pos, EnumSet<Direction> connections, Direction rotationDirection, float speed)
        {
            super(pos, connections, rotationDirection, speed);
        }

        public void setDirection(Direction.Axis axis, Direction.AxisDirection direction)
        {
            this.rotation = Rotation.of(Direction.fromAxisAndDirection(axis, direction), 0);
        }

        @Override
        public String toString()
        {
            // Using connections() over including a field to track the axis
            return "CreativeRotator[pos=%s, connections=%s]".formatted(pos(), connections());
        }
    }
}
