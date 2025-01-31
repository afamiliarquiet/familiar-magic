package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.block.EnchantedCandleBlock;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.github.afamiliarquiet.familiar_magic.gooey.SummoningTableScreenHandler;
import io.github.afamiliarquiet.familiar_magic.network.SillySummoningRequestLuggage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SummoningTableBlockEntity extends LockableContainerBlockEntity {
    private static final int[] CANDLE_COLUMN_OFFSETS = {
            -5,-5,  -3,-5,  -1,-5,  1,-5,  3,-5,  5,-5,
            -5,-3,  -3,-3,  -1,-3,  1,-3,  3,-3,  5,-3,
            -5,-1,  -3,-1,                 3,-1,  5,-1,
            -5, 1,  -3, 1,                 3, 1,  5, 1,
            -5, 3,  -3, 3,  -1, 3,  1, 3,  3, 3,  5, 3,
            -5, 5,  -3, 5,  -1, 5,  1, 5,  3, 5,  5, 5
    };
    private static final int[][] PHASE_INDICES = {
            {0,  5, 26, 31}, // phase 1 (final phase)
            {1, 11, 20, 30}, // phase 2
            {4,  6, 25, 27}, // phase 3
            {3, 12, 19, 28}, // phase 4
            {2, 15, 16, 29}, // phase 5
            {7, 10, 21, 24}, // phase 6
            {8, 14, 17, 23}, // phase 7
            {9, 13, 18, 22}, // phase 8 (first phase)
    };

    private enum CandlePlacement {
        SMOKE,
        UNLIT,
        LIT;

        public BlockState asBlock() {
            return switch(this) {
                case SMOKE -> FamiliarBlocks.SMOKE_WISP.getDefaultState();
                case UNLIT -> FamiliarBlocks.ENCHANTED_CANDLE.getDefaultState();
                case LIT -> FamiliarBlocks.ENCHANTED_CANDLE.getDefaultState().with(EnchantedCandleBlock.LIT, true);
            };
        }
    }

    @NotNull
    private UUID targetFromCandles = new UUID(0, 0);
    private byte[] targetFromCandlesInNybbles = new byte[32];
    private byte @Nullable [] burnedTargetFromTrueNameInNybbles = null;
    // todo - reduce this. can't have all these states at once and it's all timers.. i can feel the debt clock ticking
    private int burningPhase = 0; // ticks down from 8 -> 0 when burning, 0 represents not burning
    private int summoningTimer = 0;
//    @Nullable
//    private PersonalPattern pendingPattern;
//    @Nullable
//    private UUID bindingTarget = null;

    private DefaultedList<ItemStack> inv = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private final SummoningTablePropertyDelegate menuData = new SummoningTablePropertyDelegate(17) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> FamiliarTricks.nybblesToIntChomp(getCandleTargetNybbles(), index);
                case 16 -> SummoningTableBlockEntity.this.canChangeItems() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // get mulched. you don't need to change dirt.
        }
    };

    public SummoningTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(FamiliarBlocks.SUMMONING_TABLE_BLOCK_ENTITY, pos, blockState);
        Arrays.fill(this.targetFromCandlesInNybbles, FamiliarTricks.NO_CANDLE);
    }

    public boolean allCandlesLit() {
        // i forgor.. maybe i should rember
        for (byte tasty : this.targetFromCandlesInNybbles) {
            if ((tasty & FamiliarTricks.CANDLE_ERROR_MASK) != 0) {
                return false;
            }
        }

        return true;
    }

    public boolean hasTarget() {
        return this.world instanceof ServerWorld serverWorld
                && allCandlesLit()
                && FamiliarTricks.findTargetByUuid(this.getCandleTarget(), serverWorld.getServer()) != null;
    }

    public boolean canChangeItems() {
        return this.getCachedState().get(SummoningTableBlock.SUMMONING_TABLE_STATE) != SummoningTableBlock.SummoningTableState.SUMMONING;
    }

    // in theory i'm using these (not true already) so i can remove one of these two variables later (need setters too..)
    private UUID getCandleTarget() {
        return this.targetFromCandles;
    }

    private byte[] getCandleTargetNybbles() {
        return this.targetFromCandlesInNybbles;
    }

    protected ItemStack trueName() {
        return this.inv.getFirst();
    }

    public static void tick(World world, BlockPos pos, BlockState state, SummoningTableBlockEntity thys) {
        if (world.isClient()) {
//            if (state.get(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableBlock.SummoningTableState.BINDING) {
//                Random random = world.getRandom();
//                for (int i = 0; i < 2; i++) {
//                    double d0 = (double) pos.getX() + random.nextDouble() * 0.625 + 0.1875;
//                    double d1 = (double) pos.getY() + random.nextDouble() * 0.75;
//                    double d2 = (double) pos.getZ() + random.nextDouble() * 0.625 + 0.1875;
//                    world.addParticle(random.nextBoolean() ? ParticleTypes.WAX_OFF : ParticleTypes.WAX_ON, d0, d1, d2, 0.0, 31 * random.nextDouble(), 0.0);
//                }
//            }
            return;
        }

        SummoningTableBlock.SummoningTableState tableState = state.get(SummoningTableBlock.SUMMONING_TABLE_STATE);

        if (world.getTime() % 20L == 0L) {
            UUID newTarget = thys.getCandleTarget();
            byte[] newTargetNybbles = thys.targetFromCandlesInNybbles;
            if (world.getTime() % 80L == 0L) {
                newTargetNybbles = processCandles(world, pos);
                newTarget = FamiliarTricks.nybblesToUUID(newTargetNybbles);
            }

//            if (tableState == SummoningTableBlock.SummoningTableState.BINDING) {
//                tickBinding(world, pos, state, thys);
//            }
            if (tableState == SummoningTableBlock.SummoningTableState.SUMMONING) {
                tickSummoning(world, pos, state, thys, !thys.getCandleTarget().equals(newTarget));
            } else if (tableState == SummoningTableBlock.SummoningTableState.BURNING) {
                tickBurning(world, pos, state, thys);
            }

            thys.targetFromCandlesInNybbles = newTargetNybbles;
            thys.targetFromCandles = newTarget;
        }
    }

    // written with the aid of our lady luna :innocent:, it says
    private static byte[] processCandles(World world, BlockPos pos) {
        byte[] nybbles = new byte[32];

        for (int i = 0; i < 32; i++) {
            nybbles[i] = nybbleFromCandleColumn(
                    world,
                    new BlockPos(
                            pos.getX() + CANDLE_COLUMN_OFFSETS[2 * i],
                            pos.getY(),
                            pos.getZ() + CANDLE_COLUMN_OFFSETS[2 * i + 1]
                    )
            );
        }

        return nybbles;
    }

    private static byte nybbleFromCandleColumn(World world, BlockPos bottomPos) {
        for (int height = 4; height > 0; height--) {
            BlockState curiousBlockState = world.getBlockState(bottomPos.add(0, height, 0));
            if (curiousBlockState.isOf(FamiliarBlocks.ENCHANTED_CANDLE)) {
                byte errors = 0b00000000;
                if (!curiousBlockState.get(EnchantedCandleBlock.LIT)) {
                    errors |= FamiliarTricks.UNLIT_CANDLE;
                }
                return (byte) (errors | FamiliarTricks.makeNybble(height, curiousBlockState.get(EnchantedCandleBlock.CANDLES)));
            }
        }

        return FamiliarTricks.NO_CANDLE;
    }

    public void cancelAll() {
        if (this.world instanceof ServerWorld serv) {

            if (this.getCachedState().get(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableBlock.SummoningTableState.SUMMONING) {
                Entity target = FamiliarTricks.findTargetByUuid(this.getCandleTarget(), serv.getServer());
                if (target instanceof ServerPlayerEntity player) {
                    SummoningRequestData requestData = new SummoningRequestData(this.world.getRegistryKey(), this.getPos(), Optional.empty());
                    ServerPlayNetworking.send(player, new SillySummoningRequestLuggage(requestData, false));
                }
            }

//            this.pendingPattern = null;
            this.burnedTargetFromTrueNameInNybbles = null;

            this.burningPhase = 0;
            this.summoningTimer = 0;
        }
    }

//    public BlockState tryBind(BlockState state) {
//        // so the vague idea is shift+rclick to start, highlight subject, highlight oopps, shift+rclick to confirm or wait 5?s to cancel
//        // means 1. finding an entity on top of summoning table - if player, they must be the one who started the process
//        // 2. scan for all oopps in 13x13 centered on table, assembling some structure of what is where as the scan proceeds
//        // 3. make entity glow
//        // 4. display oopps to player somehow???
//        // 5. store entity and oopps in blockent while waiting for confirmation
//        // 6a. tickdown and cancel, reset entity and oopps
//        // 6b. confirmation! slip the oopps into the target's soul and give them some fun effects :)
//
//        // hard parts: displaying block layout?
//        // data structure for block layout? probably copy shapedrecipe and rawshapedrecipe. maybe consider trying to use both table and 2d array depending on sparseness.. complicated. worry less about efficiency for now and just get something working?
//        // codec for block layout?
//        // i like the idea of having a list of blocks used, and then some pattern like crafting recipes
//
//        // sneak+focus+target on top+rclick empty to start designation, focus+target on top+rclick empty to finish.
//        // should this also be a new blockstate that's cancellable with shift+rclick?
//        if (this.world instanceof ServerWorld sworld) {
//            Entity targetEntity = world.getClosestEntity(LivingEntity.class, TargetPredicate.createNonAttackable(), null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), new Box(this.getPos()).expand(0, 1, 0));
//
//            if (targetEntity != null && targetEntity.getUuid().equals(this.targetFromCandles)) {
//                this.pendingPattern = PersonalPattern.fromTable(sworld, this.getPos());
//                this.summoningTimer = FamiliarTricks.SUMMONING_TIME_SECONDS;
//                this.bindingTarget = this.targetFromCandles;
//                return state.with(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableBlock.SummoningTableState.BINDING);
//            }
//        }
//
//        addFailEffects();
//        return state;
//    }
//
//    private static void tickBinding(World world, BlockPos pos, BlockState state, SummoningTableBlockEntity thys) {
//        if (thys.summoningTimer == 0) {
//            thys.cancelAll();
//            SummoningTableBlock.extinguish(null, state, world, pos);
//        }
//
//        if (thys.summoningTimer > 0) {
//            thys.summoningTimer--;
//        }
//    }
//
//    public BlockState confirmBind(BlockState state) {
//        if (this.world instanceof ServerWorld sworld) {
//            Entity targetEntity = world.getClosestEntity(LivingEntity.class, TargetPredicate.createNonAttackable(), null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), new Box(this.getPos()).expand(0, 1, 0));
//
//            if (targetEntity != null && targetEntity.getUuid().equals(this.bindingTarget)) {
//                FamiliarAttachments.setPersonalPattern(targetEntity, this.pendingPattern);
//                this.pendingPattern = null;
//                this.summoningTimer = 0;
//                this.bindingTarget = null;
//                // todo - more fanciness
//                return state.with(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableBlock.SummoningTableState.INACTIVE);
//            }
//        }
//        return state;
//    }

    public BlockState trySummon(BlockState state) {
        if (this.allCandlesLit() && this.world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, this.getPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
            Entity target = FamiliarTricks.findTargetByUuid(this.getCandleTarget(), serverWorld.getServer());
            // target not null AND either no perpat or perpat matches
            // i have slight concerns about the performance hit of running this whole trySummon method
            if ((target != null) /*&& (!(FamiliarAttachments.getPersonalPattern(target) instanceof PersonalPattern perpat) || perpat.matches(this.world, this.getPos()))*/) {

                this.summoningTimer = FamiliarTricks.SUMMONING_TIME_SECONDS;

                if (target instanceof ServerPlayerEntity player) {
                    SummoningRequestData requestData = new SummoningRequestData(
                            this.world.getRegistryKey(),
                            this.getPos(),
                            Optional.of(List.of(
                                    this.inv.get(1),
                                    this.inv.get(2),
                                    this.inv.get(3),
                                    this.inv.get(4)
                            ))
                    );

                    ServerPlayNetworking.send(player, new SillySummoningRequestLuggage(requestData, true));
                } else {
                    // todo - let picky critters be picky
                    serverWorld.scheduleBlockTick(this.getPos(), state.getBlock(), world.random.nextBetweenExclusive(13, 62));
                }

                return state.with(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableBlock.SummoningTableState.SUMMONING);
            }
        }

        addFailEffects();
        return state;
    }

    private static void tickSummoning(World world, BlockPos pos, BlockState state, SummoningTableBlockEntity thys, boolean targetChanged) {
        if (thys.summoningTimer <= 0 || targetChanged) {
            thys.cancelAll();
            SummoningTableBlock.extinguish(null, state, world, pos);
        } else {
            thys.summoningTimer--;
        }
    }

    public void scheduledAccept() {
        if (this.world instanceof ServerWorld) { // null check without null check :sleepy:
            Entity target = FamiliarTricks.findTargetByUuid(this.getCandleTarget(), this.world.getServer());
            if (target != null) {
                if (FamiliarAttachments.isWillingFamiliar(target)) {
                    acceptAndCastSummoning(target);
                } else {
                    cancelAll();
                    SummoningTableBlock.extinguish(null, this.getCachedState(), this.world, this.getPos());
                }
            }
        }
    }

    public void acceptAndCastSummoning(Entity target) {
        if (this.world == null || this.world.isClient()) {
            return;
        }

        if (target.getUuid().equals(this.getCandleTarget()) && this.getCachedState().get(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableBlock.SummoningTableState.SUMMONING) {
            BlockPos destination = this.getPos();
            target.teleport((ServerWorld) this.world, destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5, EnumSet.noneOf(PositionFlag.class), target.getYaw(), target.getPitch());

            if (target instanceof PathAwareEntity pathy) {
                pathy.getNavigation().stop();
            }

            this.giveOfferings(target);

            world.emitGameEvent(GameEvent.TELEPORT, target.getPos(), GameEvent.Emitter.of(target));
            world.sendEntityStatus(target, EntityStatuses.ADD_PORTAL_PARTICLES);
            world.playSound(null, this.getPos(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.BLOCKS, 1, 1);

            cancelAll();
            SummoningTableBlock.extinguish(null, this.getCachedState(), this.world, this.getPos());
        }
    }

    private void giveOfferings(Entity target) {
        if (world == null || world.isClient()) {
            return;
        }

        for (int i = 1; i < this.inv.size(); i++) {
            ItemStack offering = this.inv.get(i);
            if (target instanceof ServerPlayerEntity player) {
                if (!player.getInventory().insertStack(offering)) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY()+1, pos.getZ(), offering);
                }
            } else {
                // todo - feed the critters
                ItemScatterer.spawn(world, pos.getX(), pos.getY()+1, pos.getZ(), offering);
            }
        }
    }

    private void unlightAll() {
        // maybe add a config option for this but. not important. not used
    }

    public BlockState tryBurnName(BlockState state) {
        if (!this.trueName().isEmpty()) {
            byte[] itemTarget = FamiliarTricks.trueNameToNybbles(this.trueName().getName().getString());

            if (itemTarget != null) {
                this.trueName().set(FamiliarComponents.SINGED_COMPONENT, true);
                this.burnedTargetFromTrueNameInNybbles = itemTarget;
                this.burningPhase = 8;
                return state.with(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableBlock.SummoningTableState.BURNING);
            }
        }

        addFailEffects();
        return state;
    }

    private static void tickBurning(World world, BlockPos tablePos, BlockState state, SummoningTableBlockEntity thys) {
        if (thys.burnedTargetFromTrueNameInNybbles == null) {
            // i reserve the right to explode your base if this happens
            thys.burningPhase = 0;
        }

        if (thys.burningPhase > 0) {
            thys.burningPhase--;
            burnPhase(world, tablePos, thys.burnedTargetFromTrueNameInNybbles, thys.burningPhase, CandlePlacement.SMOKE);
        }

        if (thys.burningPhase <= 0) {
            if (state.get(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableBlock.SummoningTableState.BURNING) {
                world.setBlockState(tablePos, state.with(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableBlock.SummoningTableState.INACTIVE));
            }
        }
    }

    public static void superburn(World world, BlockPos tablePos, UUID target, boolean lit) {
        byte[] nybbles = FamiliarTricks.uuidToNybbles(target);

        for (int i = 7; i >= 0; i--) {
            burnPhase(world, tablePos, nybbles, i, lit ? CandlePlacement.LIT : CandlePlacement.UNLIT);
        }
    }

    private static void burnPhase(World world, BlockPos tablePos, byte[] nybbles, int phase, CandlePlacement placement) {
        int[] targetIndices = PHASE_INDICES[phase];
        for (int targetIndex : targetIndices) {
            burnColumn(
                    world,
                    tablePos.add(CANDLE_COLUMN_OFFSETS[2 * targetIndex], 0, CANDLE_COLUMN_OFFSETS[2 * targetIndex + 1]),
                    nybbles[targetIndex],
                    placement.asBlock()
            );
        }
    }

    private static void burnColumn(World world, BlockPos bottomPos, byte nybbleDigit, BlockState blockToPlace) {
        int desiredHeight = FamiliarTricks.height(nybbleDigit);
        int desiredCandles = FamiliarTricks.quantity(nybbleDigit);

        BlockPos targetPos = bottomPos.add(0, desiredHeight, 0);
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.isReplaceable()) {
            world.setBlockState(targetPos, blockToPlace.with(CandleBlock.CANDLES, desiredCandles));
        } else if (targetState.isOf(FamiliarBlocks.ENCHANTED_CANDLE) && targetState.get(EnchantedCandleBlock.CANDLES) == desiredCandles && !targetState.get(EnchantedCandleBlock.LIT)) {
            world.setBlockState(targetPos, targetState.with(EnchantedCandleBlock.LIT, true));
        } else {
            // maybe sad sound/particle? can't make smoke here but would quite like to
        }
    }

    public void addFailEffects() {
        if (this.world != null) {
            this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1);
            for (int i = 0; i < 5; i++) {
                double x = this.getPos().getX() + 0.5 + 0.5 * (0.5 - this.world.random.nextDouble());
                double y = this.getPos().getY() + 0.8375;
                double z = this.getPos().getZ() + 0.5 + 0.5 * (0.5 - this.world.random.nextDouble());
                world.addParticle(
                        ParticleTypes.SMOKE,
                        x, y, z,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.familiar_magic.summoning_table");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inv;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inv = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SummoningTableScreenHandler(syncId, playerInventory, this, this.menuData, ScreenHandlerContext.create(this.world, this.getPos()));
    }

    @Override
    public int size() {
        return this.inv.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inv, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inv, registryLookup);
    }

    // component stuff shouldn't be necessary, good ol lockey et al. do that for us i think
}
