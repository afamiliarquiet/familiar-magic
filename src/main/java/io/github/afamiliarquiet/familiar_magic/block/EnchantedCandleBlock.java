package io.github.afamiliarquiet.familiar_magic.block;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.List;

public class EnchantedCandleBlock extends CandleBlock {
    public static final MapCodec<CandleBlock> CODEC = AbstractBlock.createCodec(EnchantedCandleBlock::new);

    private static final List<List<Vec3d>> FLAME_OFFSETS = ImmutableList.of(
            ImmutableList.of(new Vec3d(0.46875, 0.875, 0.46875)),
            ImmutableList.of(new Vec3d(0.40625, 0.9375, 0.34375), new Vec3d(0.59375, 0.75, 0.65625)),
            ImmutableList.of(new Vec3d(0.46875, 0.875, 0.40625), new Vec3d(0.65625, 0.9375, 0.71875), new Vec3d(0.34375, 0.75, 0.65625)),
            ImmutableList.of(new Vec3d(0.28125, 0.875, 0.34375), new Vec3d(0.59375, 1, 0.40625), new Vec3d(0.65625, 0.6875, 0.65625), new Vec3d(0.34375, 0.8125, 0.59375))
    );

    private static final List<List<Vec3d>> ENCHANT_OFFSETS = ImmutableList.of(
            ImmutableList.of(new Vec3d(0.46875, 0.375, 0.46875)),
            ImmutableList.of(new Vec3d(0.40625, 0.4375, 0.34375), new Vec3d(0.59375, 0.3125, 0.65625)),
            ImmutableList.of(new Vec3d(0.46875, 0.375, 0.40625), new Vec3d(0.65625, 0.5, 0.71875), new Vec3d(0.34375, 0.5, 0.65625)),
            ImmutableList.of(new Vec3d(0.28125, 0.375, 0.34375), new Vec3d(0.59375, 0.5625, 0.40625), new Vec3d(0.65625, 0.4375, 0.65625), new Vec3d(0.34375, 0.4375, 0.59375))
    );

    private static final List<VoxelShape> SHAPES = ImmutableList.of(
            Block.createCuboidShape(6, 4, 6, 9, 12, 9),
            Block.createCuboidShape(5, 4, 4, 11, 13, 12),
            Block.createCuboidShape(4, 4, 5, 12, 13, 13),
            Block.createCuboidShape(3, 3, 4, 12, 14, 12)
    );

    @Override
    public MapCodec<CandleBlock> getCodec() {
        return CODEC; // idk about this still. But It's Fine?
    }

    public EnchantedCandleBlock(Settings settings) {
        super(settings);
    }

    // ignition for this is provided by being nearly a candle and being in the candles tag

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState clickedState = context.getWorld().getBlockState(context.getBlockPos());
        BlockState placementState = super.getPlacementState(context);
        if (placementState != null && context.getPlayer() != null && !context.getPlayer().getAbilities().allowModifyWorld) {
            if (clickedState.isOf(FamiliarBlocks.SMOKE_WISP)) {
                return placementState.with(CANDLES, clickedState.get(SmokeWispBlock.CANDLES));
            }
        }
        return placementState;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // will kersplode if there's ever more than 4 candles in the blockstate.
        // but who would dare do that to me? nobody. it's not possible. they could never.
        return SHAPES.get(state.get(CANDLES) - 1);
    }

    @Override
    protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
        // unlike getShape, this is perfectly safe even with 5 candles!
        // haha got you!! you totally tried it and kersploded. yeah, this also kersplodes.
        return FLAME_OFFSETS.get(state.get(CANDLES) - 1);
    }

    protected List<Vec3d> getEnchantOffsets(BlockState state) {
        return ENCHANT_OFFSETS.get(state.get(CANDLES) - 1);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (random.nextFloat() < (0.031f * state.get(CANDLES))) {
            List<Vec3d> offsets = getEnchantOffsets(state);
            Vec3d chosenOffset = offsets.get(random.nextInt(offsets.size()));
            world.addParticle(
                    ParticleTypes.ENCHANT,
                    pos.getX() + chosenOffset.x, pos.getY() + chosenOffset.y, pos.getZ() + chosenOffset.z,
                    0, 0, 0);
        }
    }
}
