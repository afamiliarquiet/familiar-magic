package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.serialization.MapCodec;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableState;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableBlock extends BaseEntityBlock {
    public static final MapCodec<SummoningTableBlock> CODEC = simpleCodec(SummoningTableBlock::new);

    public static final EnumProperty<SummoningTableState> SUMMONING_TABLE_STATE = EnumProperty.create("summoning_table_state", SummoningTableState.class);

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SummoningTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE)
        );
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        BlockEntity blockerizer = context.getLevel().getBlockEntity(context.getClickedPos());
        if (itemAbility == ItemAbilities.FIRESTARTER_LIGHT && state.getValue(SUMMONING_TABLE_STATE) == SummoningTableState.INACTIVE && blockerizer instanceof SummoningTableBlockEntity summonizer) {
            // neoforge docs say calling state#setValue without saving it back into state does nothing, so..
            // hopefully simulate is happy :)
            // actually this shouldn't directly set lit - probably need to hand off to the blockentity here. but that's later

            if (context.getPlayer() != null && context.getPlayer().getData(FamiliarAttachments.FOCUSED)) {
                return summonizer.startSummoning(state, simulate);
            } else {
                return summonizer.tryBurnName(state, simulate);
            }
        } else {
            return super.getToolModifiedState(state, context, itemAbility, simulate);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide
                && projectile.isOnFire()
                && hit.getDirection() == Direction.UP
                && state.getValue(SUMMONING_TABLE_STATE) == SummoningTableState.INACTIVE
                && level.getBlockEntity(hit.getBlockPos()) instanceof SummoningTableBlockEntity summonizer
        ) {
            level.setBlockAndUpdate(hit.getBlockPos(), summonizer.tryBurnName(state, false));
        }
        super.onProjectileHit(level, state, hit, projectile);
    }

    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SummoningTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, FamiliarBlocks.SUMMONING_TABLE_BLOCK_ENTITY.get(), SummoningTableBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // this should really be called use regardless of item from the way it seems to work
        if (player.getMainHandItem().canPerformAction(ItemAbilities.FIRESTARTER_LIGHT)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide
                && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof SummoningTableBlockEntity zeraxos) {
//            if (serverPlayer.getData(FamiliarAttachments.FOCUSED) && level.getBlockEntity(pos) instanceof SummoningTableBlockEntity tableEntity) {
//                // and i'll make an even longer line too!! fear me
//                tableEntity.tryDesignate(state);
//            } else {
                serverPlayer.openMenu(zeraxos);
//            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        SummoningTableState tableState = state.getValue(SUMMONING_TABLE_STATE);

        // i'm choosing to allow adventure mode players to cancel summoning because that's useful for modfest
        if (stack.isEmpty() && player.isSecondaryUseActive() && tableState != SummoningTableState.INACTIVE) {
            if (tableState == SummoningTableState.SUMMONING && level.getBlockEntity(pos) instanceof SummoningTableBlockEntity tableEntity) {
                tableEntity.cancelSummoning();
            }

            extinguish(player, state, level, pos);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
    }

    public static void extinguish(@Nullable Player player, BlockState state, LevelAccessor level, BlockPos pos) {
        level.setBlock(pos, state.setValue(SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE), UPDATE_ALL_IMMEDIATE);

        level.addParticle(
                ParticleTypes.SMOKE,
                (double) pos.getX() + 0.5,
                (double) pos.getY() + 0.75,
                (double) pos.getZ() + 0.5,
                0.0,
                0.1F,
                0.0
        );

        // using glass break to copy nether portal break for now. maybe will change later
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    // this should be scheduled tick
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof SummoningTableBlockEntity tableEntity) {
            tableEntity.scheduledAccept();
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        SummoningTableState tableState = state.getValue(SUMMONING_TABLE_STATE);
        if (tableState == SummoningTableState.SUMMONING) {
            if (random.nextInt(100) == 0) {
                // truly just ripping this whole thing from respawn anchor. as usual, might change sound later. unlikely
                level.playLocalSound(pos, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            double d0 = (double)pos.getX() + 0.5 + (0.5 - random.nextDouble());
            double d1 = (double)pos.getY() + 0.75;
            double d2 = (double)pos.getZ() + 0.5 + (0.5 - random.nextDouble());
            double d3 = (double)random.nextFloat() * 0.04;
            level.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0, d3, 0.0);
        } else if (tableState == SummoningTableState.BURNING) {
            if (random.nextInt(31) == 0) {
                level.playLocalSound(pos, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1, 1, false);
            }

            if (random.nextInt(2) == 0) {
                double x = pos.getX() + 0.5 + 0.5 * (0.5 - random.nextDouble());
                double y = pos.getY() + 0.8375;
                double z = pos.getZ() + 0.5 + 0.5 * (0.5 - random.nextDouble());
                level.addParticle(
                        random.nextInt(6) == 0 ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
                        x, y, z,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SUMMONING_TABLE_STATE);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            // tell the stuff in the inventory to bounce
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SummoningTableBlockEntity summoningTableEntity) {
                summoningTableEntity.cancelSummoning();
                for (int i = 0; i < summoningTableEntity.getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), summoningTableEntity.getStackInSlot(i));
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
