package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
//ClassesAreTaggedWithThisNonsenseByDefault
//IntelliJUnderstandsThatIAmNotBotheredByNonnullStuffByDefault
//BeepBeepByDefault
public class SmokeWispBlock extends Block {
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;

    public static final MapCodec<SmokeWispBlock> CODEC = simpleCodec(SmokeWispBlock::new);

    private static final VoxelShape SQUARETITUDE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public SmokeWispBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(CANDLES, 1)
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        //for (int i = 0; i < 3; i++) {
            double d0 = (double)pos.getX() + 0.3125 + 0.375 * random.nextDouble();
            double d1 = (double)pos.getY() + 0.3125 + 0.375 * random.nextDouble();
            double d2 = (double)pos.getZ() + 0.3125 + 0.375 * random.nextDouble();
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
        //}
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SQUARETITUDE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CANDLES);
    }
}
