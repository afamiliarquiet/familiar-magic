package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.block.EnchantedCandleBlock;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SmokeWispBlock;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.client.gooey.SummoningTableMenu;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.item.SingedComponentRecord;
import io.github.afamiliarquiet.familiar_magic.network.SummoningRequestPayload;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.findTargetByUuid;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableBlockEntity extends BlockEntity implements IItemHandler, IItemHandlerModifiable, MenuProvider, Nameable {
    // i'm really not feeling great about implementing IItemHandlerModifiable. feels like i should be doing something else.
    // but it works for now so whatever. i'm tryin my best to be neoforgey!!
    // this is.. fine. this is great. this feels so perfect. this is a place of honor! y'know why? because it works well enough!!

    // btw if you touch my public methods in here you will explode. i've cleverly hidden many explosives. don't do it.

    // spread out for easier reference (instead of computed)
    // as for the other choices on display, no comment
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

    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    // both targetFromCandles should always be in sync
    @NotNull
    private UUID targetFromCandles = new UUID(0, 0);
    private byte[] targetFromCandlesInNybbles = new byte[32];
    // burnedTarget is only set for burning
    @Nullable
    private byte[] burnedTargetFromTrueNameInNybbles = null;
    private ItemStack trueName = ItemStack.EMPTY;
    private final ItemStackHandler offerings = new ItemStackHandler(4);
    private int burningPhase = 0; // ticks down from 8 -> 0 when burning, 0 represents not burning
    private int summoningTimer = 0;

    private final TableContainerData dataAccess = new TableContainerData(5) {
        // lady luna guide me once more orz
        @Override
        public int get(int index) {
            return switch (index) {
                case 0, 1, 2, 3, 4, 5, 6, 7 -> FamiliarTricks.nybblesToIntChomp(getCandleTargetNybbles(), index);
                case 8 -> SummoningTableBlockEntity.this.canChangeItems() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
//                case 0:
//                    BeaconBlockEntity.this.levels = value;
//                    break;
//                case 1:
//                    if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
//                        BeaconBlockEntity.playSound(BeaconBlockEntity.this.level, BeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
//                    }
//
//                    BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect(value));
//                    break;
//                case 2:
//                    BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect(value));
            }
        }

        @Override
        public int getCount() {
            return 9;
        }
    };

    public SummoningTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(FamiliarBlocks.SUMMONING_TABLE_BLOCK_ENTITY.get(), pos, blockState);
        Arrays.fill(this.targetFromCandlesInNybbles, FamiliarTricks.NO_CANDLE);
    }

    public boolean canSummon() {
        return !this.anyUnlit()
                && this.level instanceof ServerLevel serverLevel
                && findTargetByUuid(this.getCandleTarget(), serverLevel.getServer()) != null;
    }


    public BlockState startSummoning(BlockState state, boolean simulate) {
        // todo - check for unlits, then blow out candles on summoning start
        if (!this.anyUnlit() && this.level instanceof ServerLevel serverLevel) {
            LivingEntity livingTarget = findTargetByUuid(this.getCandleTarget(), serverLevel.getServer());
            if (livingTarget != null) {
                if (!simulate) {
                    this.summoningTimer = FamiliarTricks.SUMMONING_TIME_SECONDS;
                    //this.unlightAll();

                    if (livingTarget instanceof ServerPlayer player) {
                        SummoningRequestData requestData = new SummoningRequestData(
                                this.level.dimension(),
                                this.getBlockPos(),
                                Optional.of(List.of(
                                        this.offerings.getStackInSlot(0),
                                        this.offerings.getStackInSlot(1),
                                        this.offerings.getStackInSlot(2),
                                        this.offerings.getStackInSlot(3)
                                ))
                        );

                        //setRequest(player, requestData);
                        PacketDistributor.sendToPlayer(player, new SummoningRequestPayload(requestData, false));
                    } else {
                        // todo - let picky critters be picky
                        serverLevel.scheduleTick(this.getBlockPos(), state.getBlock(), level.random.nextInt(13, 62));
                    }
                }

                return state.setValue(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableState.SUMMONING);
            } else {
                // couldn't find target on server. nonexistent? unloaded? logged out?
            }
        }
        return state;
    }

    // assistant to previous method
    public void scheduledAccept() {
        if (this.level instanceof ServerLevel) {
            LivingEntity livingTarget = findTargetByUuid(this.getCandleTarget(), this.level.getServer());
            if (livingTarget != null) {
                acceptSummoning(livingTarget);
            }
        }
    }

    private static void tickSummoning(Level level, BlockPos pos, BlockState state, SummoningTableBlockEntity thys, boolean targetChanged) {
        if (thys.summoningTimer <= 0 || targetChanged) {
            thys.cancelSummoning();
            SummoningTableBlock.extinguish(null, state, level, pos);
        }

        if (thys.summoningTimer > 0) {
            thys.summoningTimer--;
        }
    }

    public void cancelSummoning() {
        if (!(level instanceof ServerLevel)) {
            // this can't happen ever. hopefully.
            return;
        }

        LivingEntity target = findTargetByUuid(this.getCandleTarget(), level.getServer());
        if (target instanceof ServerPlayer player) {
            // could i in theory use a different packet for this? yeah. should i? iunno
            // answer: YES because nulling the other one is NOT ALLOWED!!! i kinda figured :l
            SummoningRequestData requestData = new SummoningRequestData(this.level.dimension(), this.getBlockPos(), Optional.empty());
            //removeRequest(player, requestData);
            PacketDistributor.sendToPlayer(player, new SummoningRequestPayload(requestData, true));
        }

        this.summoningTimer = 0;
    }

    public void acceptSummoning(LivingEntity livingTarget) {
        if (this.level == null) {
            // this really shouldn't ever be real. also this should always be on server side?
            return;
        }
        if (livingTarget.getUUID().equals(this.getCandleTarget()) && this.getBlockState().getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableState.SUMMONING) {
            BlockPos destination = this.getBlockPos();
            livingTarget.teleportTo((ServerLevel) this.level, destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5, EnumSet.noneOf(RelativeMovement.class), livingTarget.getYRot(), livingTarget.getXRot());

            if (livingTarget instanceof PathfinderMob pathfindermob) {
                pathfindermob.getNavigation().stop();
            }

            this.giveOfferings(livingTarget);

            level.gameEvent(GameEvent.TELEPORT, livingTarget.position(), GameEvent.Context.of(livingTarget));
            level.broadcastEntityEvent(livingTarget, (byte)46);
            level.playSound(null, this.getBlockPos(), SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS, 1, 1);

            // todo - replace these with more happy variants, and also give offerings to accepting player
            cancelSummoning();
            SummoningTableBlock.extinguish(null, this.getBlockState(), this.level, this.getBlockPos());
        }
    }

    private void giveOfferings(LivingEntity target) {
        if (level == null || level.isClientSide) {
            // this isn't happening. chill out, relax, it's fine
            return;
        }
        BlockPos pos = this.getBlockPos();
        for (int i = 0; i < this.offerings.getSlots(); i++) {
            ItemStack offering = this.offerings.getStackInSlot(i);
            if (target instanceof ServerPlayer player) {
                if (!player.getInventory().add(offering)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY()+1, pos.getZ(), offering);
                }
            } else {
                // todo - feed the critters
                Containers.dropItemStack(level, pos.getX(), pos.getY()+1, pos.getZ(), offering);
            }
        }
    }

    public BlockState tryBurnName(BlockState state, boolean simulate) {
        // this fails on client because client can't see the true name. maybe that's a problem to deal with? idk

        if (!this.trueName.isEmpty()) {
            byte[] parsedTargetFromItem = FamiliarTricks.trueNameToNybbles(this.trueName.getHoverName().getString());

            if (parsedTargetFromItem != null) {
                if (!simulate) {
                    this.trueName.set(FamiliarItems.SINGED_COMPONENT, new SingedComponentRecord(true));
                    this.burnedTargetFromTrueNameInNybbles = parsedTargetFromItem;
                    this.burningPhase = 8;
                }
                return state.setValue(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableState.BURNING);
            } else {
                // failed due to bad true name
                return state;
            }
        }

        // failed due to no true name
        return state;
    }

    public void tryDesignate(BlockState state) {
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SummoningTableBlockEntity thys) {
        if (level.isClientSide) {
            // idk if client needs to do any of this tick stuff. we shall see
            return;
        }

        SummoningTableState tableState = state.getValue(SummoningTableBlock.SUMMONING_TABLE_STATE);

        // is this once a second? yea seems so - check on summoning timer n maybe cancel it
        if ((level.getGameTime() % 20L == 0L)) {

            UUID newTarget = thys.getCandleTarget();
            byte[] newTargetNybbles = thys.targetFromCandlesInNybbles;
            if (level.getGameTime() % 80L == 0L) {
                newTargetNybbles = processCandles(level, pos);
                newTarget = FamiliarTricks.nybblesToUUID(newTargetNybbles);
            }

            if (tableState == SummoningTableState.SUMMONING) {
                tickSummoning(level, pos, state, thys, !thys.getCandleTarget().equals(newTarget));
            } else if (tableState == SummoningTableState.BURNING) {
                // process burning
                tickBurning(level, pos, state, thys);
            }

            thys.targetFromCandlesInNybbles = newTargetNybbles;
            thys.targetFromCandles = newTarget;
        }

    }

    // using getters so i can remove one of these two variables later if i feel like it n convert
    private UUID getCandleTarget() {
        return this.targetFromCandles;
    }

    private byte[] getCandleTargetNybbles() {
        return this.targetFromCandlesInNybbles;
    }

    public boolean anyUnlit() {
        for (byte tasty : this.targetFromCandlesInNybbles) {
            if ((tasty & FamiliarTricks.UNLIT_CANDLE) != 0) {
                return true;
            }
        }

        return false;
    }

    private void unlightAll() {
        if (this.level == null) {
            return;
        }

        for (int i = 0; i < 32; i++) {
            // boldly unchecked because this should only be called when all the things are there... err i should check actually
            BlockPos targetPos = this.getBlockPos().offset(CANDLE_COLUMN_OFFSETS[2*i], 0, CANDLE_COLUMN_OFFSETS[2*i+1]);
            targetPos = targetPos.offset(0, ((this.targetFromCandlesInNybbles[i] >> 2) & 0b11) + 1, 0);
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.is(FamiliarBlocks.ENCHANTED_CANDLE_BLOCK)) {
                this.level.setBlock(targetPos, targetState.setValue(EnchantedCandleBlock.LIT, false), Block.UPDATE_CLIENTS);
            }
        }

        // todo - custom sound/event
        level.playSound(null, this.getBlockPos(), SoundEvents.BREEZE_LAND, SoundSource.BLOCKS, 1, 1);
    }

    // written with the aid of our lady luna :innocent:
    private static byte[] processCandles(Level level, BlockPos pos) {
        byte[] nybbles = new byte[32];

        for (int i = 0; i < 32; i++) {
            nybbles[i] = nybbleFromCandleColumn(
                    level,
                    new BlockPos(
                            pos.getX() + CANDLE_COLUMN_OFFSETS[2 * i],
                            pos.getY(),
                            pos.getZ() + CANDLE_COLUMN_OFFSETS[2 * i + 1]
                    )
            );
        }

        return nybbles;
    }

    private static byte nybbleFromCandleColumn(Level level, BlockPos bottomPos) {
        for (int yOffset = 4; yOffset > 0; yOffset--) {
            BlockState curiousBlockState = level.getBlockState(bottomPos.offset(0, yOffset, 0));
            if (curiousBlockState.is(FamiliarBlocks.ENCHANTED_CANDLE_BLOCK)) {
                byte toReturn = 0x00;
                if (!curiousBlockState.getValue(EnchantedCandleBlock.LIT)) {
                    toReturn = FamiliarTricks.UNLIT_CANDLE;
                }
                return (byte) (toReturn | (yOffset - 1) << 2 | (curiousBlockState.getValue(EnchantedCandleBlock.CANDLES) - 1));
            }
        }

        // failed to find any candle (any bits in the first nybble indicates no candle :shrug:)
        return FamiliarTricks.NO_CANDLE;
    }

    private static void tickBurning(Level level, BlockPos tablePos, BlockState state, SummoningTableBlockEntity thys) {
        if (thys.burnedTargetFromTrueNameInNybbles == null) {
            // i reserve the right to explode your base if this happens
            thys.burningPhase = 0;
        }

        if (thys.burningPhase > 0) {
            thys.burningPhase--;
            burnPhase(level, tablePos, thys.burnedTargetFromTrueNameInNybbles, thys.burningPhase, FamiliarBlocks.SMOKE_WISP_BLOCK.get());
        }

        if (thys.burningPhase <= 0) {
            if (state.getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableState.BURNING) {
                level.setBlockAndUpdate(tablePos, state.setValue(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE));
            }
        }
    }

    public static boolean superburn(Level level, BlockPos tablePos, UUID target) {
        byte[] nybbles = FamiliarTricks.uuidToNybbles(target);

        for (int i = 7; i >= 0; i--) {
            burnPhase(level, tablePos, nybbles, i, FamiliarBlocks.ENCHANTED_CANDLE_BLOCK.get());
        }

        return true;
    }

    private static void burnPhase(Level level, BlockPos tablePos, byte[] nybbles, int phase, Block blockToPlace) {
        int[] targetIndices = PHASE_INDICES[phase];
        for (int targetIndex : targetIndices) {
            burnColumn(
                    level,
                    tablePos.offset(CANDLE_COLUMN_OFFSETS[2 * targetIndex], 0, CANDLE_COLUMN_OFFSETS[2 * targetIndex + 1]),
                    nybbles[targetIndex],
                    blockToPlace
            );
        }
    }

    private static void burnColumn(Level level, BlockPos bottomPos, byte nybbleDigit, Block blockToPlace) {
        // wahaha i was right java does smear the bits (i think), this was a justified &
        int desiredHeight = ((nybbleDigit >> 2) & 0b11) + 1;
        int desiredCandles = (nybbleDigit & 0b11) + 1;

        BlockPos targetPos = bottomPos.offset(0, desiredHeight, 0);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.canBeReplaced()) {
            // maybe sound/particle?
            level.setBlockAndUpdate(targetPos, blockToPlace.defaultBlockState().setValue(SmokeWispBlock.CANDLES, desiredCandles));
        } else if (targetState.is(FamiliarBlocks.ENCHANTED_CANDLE_BLOCK) && targetState.getValue(EnchantedCandleBlock.CANDLES) == desiredCandles && !targetState.getValue(EnchantedCandleBlock.LIT)) {
            // maybe happy sound/particle?
            level.setBlockAndUpdate(targetPos, targetState.setValue(EnchantedCandleBlock.LIT, true));
        } else {
            // maybe sad sound/particle? can't make smoke here but would quite like to
        }
    }


    // ahead lies the containery part of this damnable thing. Good lird

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }

        if (!this.trueName.isEmpty()) {
            tag.put("TrueNameItem", this.trueName.save(registries));
        }
        tag.put("OfferingItems", this.offerings.serializeNBT(registries));


        this.lockKey.addToTag(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("CustomName", 8)) {
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }

        if (tag.contains("TrueNameItem", Tag.TAG_COMPOUND)) {
            // ok this isn't really quite right here but. i'll deal with that later when i get to offerings
            this.trueName = ItemStack.parse(registries, tag.getCompound("TrueNameItem")).orElse(ItemStack.EMPTY);
        }
        if (tag.contains("OfferingItems", Tag.TAG_COMPOUND)) {
            this.offerings.deserializeNBT(registries, tag.getCompound("OfferingItems"));
        }

        this.lockKey = LockCode.fromTag(tag);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
        this.lockKey = componentInput.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);

        List<ItemStack> allItems = componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().toList();
        if (!allItems.isEmpty()) {
            this.trueName = allItems.getFirst();
        }
        for (int i = 1; i < allItems.size() && i < 1 + offerings.getSlots(); i++) {
            offerings.setStackInSlot(i - 1, allItems.get(i));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            components.set(DataComponents.LOCK, this.lockKey);
        }

        ArrayList<ItemStack> allItems = new ArrayList<>(1 + this.offerings.getSlots());
        allItems.add(this.trueName);
        for (int i = 0; i < this.offerings.getSlots(); i++) {
            allItems.add(this.offerings.getStackInSlot(i));
        }
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(allItems));
    }

    @Override
    public Component getName() {
        return name != null ? name : Component.translatable("container.familiar_magic.summon");
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @SuppressWarnings("deprecation") // i mean yeah but also. it's still used in another method so idk
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
        tag.remove("Lock");
        tag.remove("TrueNameItem");
        tag.remove("OfferingItems");
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (BaseContainerBlockEntity.canUnlock(player, this.lockKey, this.getDisplayName())) {
            return new SummoningTableMenu(containerId, playerInventory, this, this.dataAccess, ContainerLevelAccess.create(player.level(), this.getBlockPos()));
        } else {
            return null;
        }
    }

    public boolean canChangeItems() {
        return this.getBlockState().getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) != SummoningTableState.SUMMONING;
    }

    @Override
    public int getSlots() {
        return 5;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            return this.trueName;
        } else if (slot > 0 && slot < 5) {
            return this.offerings.getStackInSlot(slot - 1);
        } else {
            return ItemStack.EMPTY;
        }
    }

    // can you feel her guiding voice? our lady luna speaks these methods into my ears, i am but a vessel
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack toReturn = stack;

        if (this.canChangeItems()) {
            if (slot == 0 && this.trueName.isEmpty() && stack.is(FamiliarItems.TRUE_NAME_ITEM)) {
                if (!simulate) {
                    this.trueName = stack.copyWithCount(1);
                }

                toReturn = stack.copyWithCount(stack.getCount() - 1);
            } else if (slot > 0 && slot < 5) {
                toReturn = offerings.insertItem(slot - 1, stack, simulate);
            }

            if (toReturn.getCount() != stack.getCount() && !simulate) {
                this.setChanged();
            }
        }

        return toReturn;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack toReturn = ItemStack.EMPTY;

        if (this.canChangeItems()) {
            if (slot == 0) {
                toReturn = this.trueName.copy();
                if (!simulate) {
                    this.trueName = ItemStack.EMPTY;
                }
            } else if (slot > 0 && slot < 5) {
                toReturn = offerings.extractItem(slot - 1, amount, simulate);
            }

            if (!toReturn.isEmpty() && !simulate) {
                this.setChanged();
            }
        }

        return toReturn;
    }

    @Override
    public int getSlotLimit(int slot) {
        switch (slot) {
            case 0 -> {
                return 1;
            }
            case 1, 2, 3, 4 -> {
                return this.offerings.getSlotLimit(slot - 1);
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && stack.is(FamiliarItems.TRUE_NAME_ITEM) ||
                slot > 0 && slot < 5 && offerings.isItemValid(slot - 1, stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot == 0) {
            this.trueName = stack;
            this.setChanged();
        } else if (slot > 0 && slot < 5) {
            this.offerings.setStackInSlot(slot - 1, stack);
            this.setChanged();
        }
    }
}
