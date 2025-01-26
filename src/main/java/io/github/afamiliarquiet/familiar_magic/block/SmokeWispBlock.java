package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SmokeWispBlock extends Block {
    public static final IntProperty CANDLES = Properties.CANDLES;

    public static final MapCodec<SmokeWispBlock> CODEC = AbstractBlock.createCodec(SmokeWispBlock::new);

    private static final VoxelShape SQUARETITUDE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public SmokeWispBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CANDLES, 1));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient()) {
            return;
        }

        double x = pos.getX() + 0.3125 + 0.375 * random.nextDouble();
        double y = pos.getY() + 0.3125 + 0.375 * random.nextDouble();
        double z = pos.getZ() + 0.3125 + 0.375 * random.nextDouble();
        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);

        // looking for the focus particle? see FocusBlockParticleMixin
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SQUARETITUDE;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CANDLES);
    }
}
