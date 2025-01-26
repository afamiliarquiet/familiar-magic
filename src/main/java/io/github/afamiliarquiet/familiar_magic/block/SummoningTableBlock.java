package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.serialization.MapCodec;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SummoningTableBlock extends BlockWithEntity implements Burnable {
    public static final MapCodec<SummoningTableBlock> CODEC = AbstractBlock.createCodec(SummoningTableBlock::new);

    public static final EnumProperty<SummoningTableState> SUMMONING_TABLE_STATE = EnumProperty.of("summoning_table_state", SummoningTableState.class);

    protected static final VoxelShape SHAPE = SummoningTableBlock.createCuboidShape(0, 0, 0, 16, 12, 16);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public SummoningTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE));
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SummoningTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, FamiliarBlocks.SUMMONING_TABLE_BLOCK_ENTITY, SummoningTableBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // deferred to be handled by their mixins
        if (FamiliarTricks.canIgnite(player.getMainHandStack())) {
            return ActionResult.PASS;
        }

        if (!world.isClient()
                && player instanceof ServerPlayerEntity serverPlayer
                && world.getBlockEntity(pos) instanceof SummoningTableBlockEntity zeraxos) {
            if (player.isSneaking() && state.get(SUMMONING_TABLE_STATE) != SummoningTableState.INACTIVE) {
                if (state.get(SUMMONING_TABLE_STATE) == SummoningTableState.SUMMONING) {
                    zeraxos.cancelSummoning();
                }

                extinguish(player, state, world, pos);
            } else {
                serverPlayer.openHandledScreen(zeraxos);
            }
        }

        return ActionResult.success(world.isClient());
    }

    public static void extinguish(@Nullable PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos) {
        world.setBlockState(pos, state.with(SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE), SummoningTableBlock.NOTIFY_ALL_AND_REDRAW);

        world.addParticle(
                ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5,
                0, 0.1f, 0
        );

        world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    public BlockState onIgnition(BlockState state, ItemUsageContext context) {
        if (state.get(SUMMONING_TABLE_STATE) == SummoningTableState.INACTIVE && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof SummoningTableBlockEntity summonizer) {
            if (context.getPlayer() != null && FamiliarAttachments.isFocused(context.getPlayer())) {
                FamiliarMagic.LOGGER.info("client: " + context.getWorld().isClient() + ", focused and summoning");
                return summonizer.trySummon(state);
            } else {
                FamiliarMagic.LOGGER.info("client: " + context.getWorld().isClient() + ", unfocused and burning");
                return summonizer.tryBurnName(state);
            }
        } else {
            return state;
        }
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && projectile.isOnFire() && hit.getSide() == Direction.UP) {
            onIgnition(state, new ItemUsageContext(world, projectile.getOwner() instanceof PlayerEntity player ? player : null, Hand.MAIN_HAND, ItemStack.EMPTY, hit));
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof SummoningTableBlockEntity screeber) {
            screeber.scheduledAccept();
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        SummoningTableState tableState = state.get(SUMMONING_TABLE_STATE);
        if (tableState == SummoningTableState.SUMMONING) {
            if (random.nextInt(100) == 0) {
                // truly just ripping this whole thing from respawn anchor. as usual, might change sound later. unlikely
                world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            double d0 = (double)pos.getX() + 0.5 + (0.5 - random.nextDouble());
            double d1 = (double)pos.getY() + 0.75;
            double d2 = (double)pos.getZ() + 0.5 + (0.5 - random.nextDouble());
            double d3 = (double)random.nextFloat() * 0.04;
            world.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0, d3, 0.0);
        } else if (tableState == SummoningTableState.BURNING) {
            if (random.nextInt(31) == 0) {
                world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1, 1, false);
            }

            if (random.nextInt(2) == 0) {
                double x = pos.getX() + 0.5 + 0.5 * (0.5 - random.nextDouble());
                double y = pos.getY() + 0.8375;
                double z = pos.getZ() + 0.5 + 0.5 * (0.5 - random.nextDouble());
                world.addParticle(
                        random.nextInt(6) == 0 ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
                        x, y, z,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SUMMONING_TABLE_STATE);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof SummoningTableBlockEntity stbe) {
                stbe.cancelSummoning();
                ItemScatterer.spawn(world, pos, stbe);
                //world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // one day i might decide to add more redstone friendliness.
    // not today, nerds. but when that day comes make sure to update onStateReplaced
//    @Override
//    protected boolean hasComparatorOutput(BlockState state) {
//        return super.hasComparatorOutput(state);
//    }
//
//    @Override
//    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
//        return super.getComparatorOutput(state, world, pos);
//    }

    public enum SummoningTableState implements StringIdentifiable {
        INACTIVE("inactive", 7),
        BURNING("burning", 10),
        SUMMONING("summoning", 13);

        private final String name;
        private final int lightLevel;

        SummoningTableState(String name, int lightLevel) {
            this.name = name;
            this.lightLevel = lightLevel;
        }

        public String asString() {
            return this.name;
        }

        public int lightLevel() {
            return this.lightLevel;
        }
    }
}
