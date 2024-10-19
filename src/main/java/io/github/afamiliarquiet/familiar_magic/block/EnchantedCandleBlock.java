package io.github.afamiliarquiet.familiar_magic.block;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EnchantedCandleBlock extends CandleBlock {
    public static final MapCodec<CandleBlock> CODEC = simpleCodec(EnchantedCandleBlock::new);

    private static final List<List<Vec3>> FLAME_OFFSETS = ImmutableList.of(
            ImmutableList.of(new Vec3(0.46875, 0.875, 0.46875)),
            ImmutableList.of(new Vec3(0.40625, 0.9375, 0.34375), new Vec3(0.59375, 0.75, 0.65625)),
            ImmutableList.of(new Vec3(0.46875, 0.875, 0.40625), new Vec3(0.65625, 0.9375, 0.71875), new Vec3(0.34375, 0.75, 0.65625)),
            ImmutableList.of(new Vec3(0.28125, 0.875, 0.34375), new Vec3(0.59375, 1, 0.40625), new Vec3(0.65625, 0.6875, 0.65625), new Vec3(0.34375, 0.8125, 0.59375))
    );

    private static final List<List<Vec3>> ENCHANT_OFFSETS = ImmutableList.of(
            ImmutableList.of(new Vec3(0.46875, 0.375, 0.46875)),
            ImmutableList.of(new Vec3(0.40625, 0.4375, 0.34375), new Vec3(0.59375, 0.3125, 0.65625)),
            ImmutableList.of(new Vec3(0.46875, 0.375, 0.40625), new Vec3(0.65625, 0.5, 0.71875), new Vec3(0.34375, 0.5, 0.65625)),
            ImmutableList.of(new Vec3(0.28125, 0.375, 0.34375), new Vec3(0.59375, 0.5625, 0.40625), new Vec3(0.65625, 0.4375, 0.65625), new Vec3(0.34375, 0.4375, 0.59375))
    );

    private static final List<VoxelShape> SHAPES = ImmutableList.of(
            Block.box(6, 4, 6, 9, 12, 9),
            Block.box(5, 4, 4, 11, 13, 12),
            Block.box(4, 4, 5, 12, 13, 13),
            Block.box(3, 3, 4, 12, 14, 12)
    );

    @Override
    public MapCodec<CandleBlock> codec() {
        return CODEC; // idk about this. But It's Fine
    }

    public EnchantedCandleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.FIRESTARTER_LIGHT && !state.getValue(LIT) &&  !state.getValue(WATERLOGGED)) {
            return state.setValue(LIT, Boolean.TRUE);
        } else {
            return super.getToolModifiedState(state, context, itemAbility, simulate);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // will kersplode if there's ever more than 4 candles in the blockstate.
        // but who would dare do that to me? nobody. it's not possible. they could never.
        return SHAPES.get(state.getValue(CANDLES) - 1);
    }

    @Override
    protected List<Vec3> getParticleOffsets(BlockState state) {
        // unlike getShape, this is perfectly safe even with 5 candles!
        // haha got you!! you totally tried it and kersploded. yeah, this also kersplodes.
        return FLAME_OFFSETS.get(state.getValue(CANDLES) - 1);
    }

    protected List<Vec3> getEnchantOffsets(BlockState state) {
        return ENCHANT_OFFSETS.get(state.getValue(CANDLES) - 1);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        // this is kinda funky because i don't want to make another offsets list for the bottoms of the candles
        List<Vec3> offsets = getEnchantOffsets(state);
        Vec3 chosenOffset = offsets.get(random.nextInt(offsets.size()));
        if (random.nextFloat() < (0.031f * state.getValue(CANDLES))) {
            level.addParticle(
                    ParticleTypes.ENCHANT,
                    pos.getX() + chosenOffset.x, pos.getY() + chosenOffset.y, pos.getZ() + chosenOffset.z,
                    0, 0, 0);
        }
    }
}
