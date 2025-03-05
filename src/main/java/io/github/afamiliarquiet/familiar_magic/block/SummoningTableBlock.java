package io.github.afamiliarquiet.familiar_magic.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
            // todo - i gotta clean this up. what a mess
            if (player.isSneaking()) {
                if (state.get(SUMMONING_TABLE_STATE) != SummoningTableState.INACTIVE) {
                    zeraxos.cancelAll();
                    extinguish(player, state, world, pos);
                } else if (FamiliarAttachments.isFocused(player)) {
                    world.setBlockState(pos, zeraxos.tryBind(state));
                } else {
                    serverPlayer.openHandledScreen(zeraxos);
                }
            } else if (state.get(SUMMONING_TABLE_STATE) == SummoningTableState.BINDING && FamiliarAttachments.isFocused(player)) {
                world.setBlockState(pos, zeraxos.confirmBind(state));
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
                return summonizer.trySummon(state);
            } else {
                return summonizer.tryBurnName(state);
            }
        } else {
            return state;
        }
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && projectile.isOnFire() && hit.getSide() == Direction.UP) {
            world.setBlockState(hit.getBlockPos(), onIgnition(state, new ItemUsageContext(world, projectile.getOwner() instanceof PlayerEntity player ? player : null, Hand.MAIN_HAND, ItemStack.EMPTY, hit)));
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
        if (tableState == SummoningTableState.BINDING) {
            // meow meow time for particle spam?
            // random border particle, i think
            int side = random.nextInt(4);
            float floaty = random.nextFloat();
            world.addParticle(
                    ParticleTypes.WITCH,
                    pos.getX() + ((side % 2 == 0) ? floaty : (side == 1) ? 0 : 1), pos.getY() + 0.75, pos.getZ() + ((side % 2 != 0) ? floaty : (side == 2) ? 0 : 1),
                    0.0, random.nextFloat() * 0.1, 0
            );

            // spewy orange and white sparkles :D
            // yea im doing d0-d2 in different ways for each state dont worry about it imts so fine
            // actually this part is going in blockentity clientside tick because i want it CONSTANTLY streamin particles.
//            for (int i = 0; i < 6; i++) {
//                double d0 = (double)pos.getX() + random.nextDouble() * 0.625 + 0.1875;
//                double d1 = (double)pos.getY() + random.nextDouble() * 0.75;
//                double d2 = (double)pos.getZ() + random.nextDouble() * 0.625 + 0.1875;
//                world.addParticle(random.nextBoolean() ? ParticleTypes.WAX_OFF : ParticleTypes.WAX_ON, d0, d1, d2, 0.0, 31 * random.nextDouble(), 0.0);
//            }
        } else if (tableState == SummoningTableState.SUMMONING) {
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
                stbe.cancelAll();
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
        SUMMONING("summoning", 13),
        BINDING("binding", 15); // your soul always shines the brightest

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
