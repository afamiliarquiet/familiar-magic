package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.LichenGrower;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.function.ToIntFunction;

public class SprinkledGlowstoneDustBlock extends MultifaceGrowthBlock {
    public static final MapCodec<SprinkledGlowstoneDustBlock> CODEC = createCodec(SprinkledGlowstoneDustBlock::new);
    private final LichenGrower grower = new LichenGrower(new NoGrowChecker(this));

    @Override
    protected MapCodec<? extends MultifaceGrowthBlock> getCodec() {
        return CODEC;
    }

    public SprinkledGlowstoneDustBlock(Settings settings) {
        super(settings);
    }

    public static ToIntFunction<BlockState> lumiumi(int luminance) {
        return state -> MultifaceGrowthBlock.hasAnyDirection(state) ? luminance : 0;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Items.GLOWSTONE_DUST.getDefaultStack();
    }

    @Override
    public LichenGrower getGrower() {
        return null;
    }

    class NoGrowChecker extends LichenGrower.LichenGrowChecker {

        public NoGrowChecker(MultifaceGrowthBlock lichen) {
            super(SprinkledGlowstoneDustBlock.this);
        }

        @Override
        public boolean canGrow(BlockView world, BlockPos pos, LichenGrower.GrowPos growPos) {
            return false;
        }

        @Override
        protected boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
            return false;
        }

        @Override
        public boolean canGrow(BlockState state, Direction direction) {
            return false;
        }
    }
}
