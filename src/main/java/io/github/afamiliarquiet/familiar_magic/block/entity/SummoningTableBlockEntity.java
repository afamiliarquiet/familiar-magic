package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.block.EnchantedCandleBlock;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SmokeWispBlock;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.client.gooey.SummoningTableMenu;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.network.SomethingFamiliar;
import io.github.afamiliarquiet.familiar_magic.network.SummoningCancelledPayload;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @NotNull
    private UUID targetFromCandles = new UUID(0, 0);
    @Nullable
    private byte[] targetFromTrueNameInNybbles = null;
    private ItemStack trueName = ItemStack.EMPTY;
    private final ItemStackHandler offerings = new ItemStackHandler(4);
    private int burningPhase = 0; // ticks down from 8 -> 0 when burning, 0 represents not burning
    private int summoningTimer = 0;

    private final ContainerData dataAccess = new ContainerData() {
        // lady luna guide me once more orz
        @Override
        public int get(int index) {
            return switch (index) {
                case 0, 1, 2, 3 -> UUIDUtil.uuidToIntArray(targetFromCandles)[index];
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
            return 4;
        }
    };

    public SummoningTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(FamiliarBlocks.SUMMONING_TABLE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public BlockState startSummoning(BlockState state, boolean simulate) {
        if (this.level instanceof ServerLevel serverLevel) {
            Entity targetEntity = serverLevel.getEntity(this.targetFromCandles);
            if (targetEntity instanceof LivingEntity livingTarget) {
                this.summoningTimer = 30;

                if (livingTarget instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new SomethingFamiliar(
                            this.getBlockPos(),
                            List.of(
                                    this.offerings.getStackInSlot(0),
                                    this.offerings.getStackInSlot(1),
                                    this.offerings.getStackInSlot(2),
                                    this.offerings.getStackInSlot(3)
                            )
                    ));
                } else {
                    // todo - let picky critters be picky
                    serverLevel.scheduleTick(this.getBlockPos(), state.getBlock(), level.random.nextInt(13, 62));
                }

                return state.setValue(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableState.SUMMONING);
            }
        }
        return state;
    }

    // assistant to previous method
    public void scheduledAccept() {
        if (this.level instanceof ServerLevel serverLevel) {
            Entity targetEntity = serverLevel.getEntity(this.targetFromCandles);
            if (targetEntity instanceof LivingEntity livingTarget) {
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

        Entity target = ((ServerLevel)level).getEntity(this.targetFromCandles);
        if (target instanceof ServerPlayer player) {
            // could i in theory use a different packet for this? yeah. should i? iunno
            // answer: YES because nulling the other one is NOT ALLOWED!!! i kinda figured :l
            PacketDistributor.sendToPlayer(player, new SummoningCancelledPayload());
        }

        this.summoningTimer = 0;
    }

    public void acceptSummoning(LivingEntity livingTarget) {
        if (this.level == null) {
            // this really shouldn't ever be real
            return;
        }
        if (livingTarget.getUUID().equals(this.targetFromCandles) && this.getBlockState().getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableState.SUMMONING) {
            BlockPos destination = this.getBlockPos();
            livingTarget.teleportTo(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);

            // todo - replace these with more happy variants, and also give offerings to accepting player
            cancelSummoning();
            SummoningTableBlock.extinguish(null, this.getBlockState(), this.level, this.getBlockPos());
        }
    }

    public BlockState tryBurnName(BlockState state, boolean simulate) {
        // this fails on client because client can't see the true name. maybe that's a problem to deal with? idk

        if (!this.trueName.isEmpty()) {
            byte[] parsedTargetFromItem = FamiliarTricks.trueNameToNybbles(this.trueName.getHoverName().getString());

            if (parsedTargetFromItem != null) {
                if (!simulate) {
                    this.targetFromTrueNameInNybbles = parsedTargetFromItem;
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

        if (level.getGameTime() % 5L == 0L) {
            if (tableState == SummoningTableState.BURNING) {
                // process burning
                tickBurning(level, pos, state, thys);
            }


            // is this once a second? yea seems so - check on summoning timer n maybe cancel it
            if ((level.getGameTime() % 20L == 0L)) {
                UUID newTarget = thys.targetFromCandles;
                if (level.getGameTime() % 80L == 0L) {
                    newTarget = processCandles(level, pos);
                }

                if (tableState == SummoningTableState.SUMMONING) {
                    tickSummoning(level, pos, state, thys, !newTarget.equals(thys.targetFromCandles));
                }

                thys.targetFromCandles = newTarget;
            }
        }
    }

    // written with the aid of our lady luna :innocent:
    //@Nullable
    private static UUID processCandles(Level level, BlockPos pos) {
        long uuidMost = 0, uuidLeast = 0;
        int nybblesTaken = 0;

        // columns, when facing northward
        for (int z = pos.getZ() - 5, zLimit = pos.getZ() + 5; z <= zLimit; z+=2) {
            // rows, when facing northward
            for (int x = pos.getX() - 5, xLimit = pos.getX() + 5; x <= xLimit; x+=2) {
                // skip center voids
                if (Math.abs(x - pos.getX()) <= 1 && Math.abs(z - pos.getZ()) <= 1) {
                    continue;
                }

                byte nybble = nybbleFromCandleColumn(level, new BlockPos(x, pos.getY(), z));

                // store nybble
                if (nybblesTaken < 16) {
                    uuidMost <<= 4;
                    uuidMost |= nybble;
                } else {
                    uuidLeast <<= 4;
                    uuidLeast |= nybble;
                }
                nybblesTaken++;
            }
        }

        return new UUID(uuidMost, uuidLeast);
    }

    private static byte nybbleFromCandleColumn(Level level, BlockPos bottomPos) {
        for (int yOffset = 4; yOffset > 0; yOffset--) {
            BlockState curiousBlockState = level.getBlockState(bottomPos.offset(0, yOffset, 0));
            if (curiousBlockState.is(FamiliarBlocks.ENCHANTED_CANDLE_BLOCK)) {
                return (byte) ((yOffset - 1) << 2 | (curiousBlockState.getValue(EnchantedCandleBlock.CANDLES) - 1));
            }
        }

        // failed to find any candle
        return (byte) 0x00;
    }

    private static void tickBurning(Level level, BlockPos tablePos, BlockState state, SummoningTableBlockEntity thys) {
        if (thys.targetFromTrueNameInNybbles == null) {
            // i reserve the right to explode your base if this happens
            thys.burningPhase = 0;
        }

        if (thys.burningPhase > 0) {
            thys.burningPhase--;
            int[] targetIndices = PHASE_INDICES[thys.burningPhase];
            for (int targetIndex : targetIndices) {
                burnColumn(
                        level,
                        tablePos.offset(CANDLE_COLUMN_OFFSETS[2 * targetIndex], 0, CANDLE_COLUMN_OFFSETS[2 * targetIndex + 1]),
                        thys.targetFromTrueNameInNybbles[targetIndex]
                );
            }
        }

        if (thys.burningPhase <= 0) {
            if (state.getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableState.BURNING) {
                level.setBlockAndUpdate(tablePos, state.setValue(SummoningTableBlock.SUMMONING_TABLE_STATE, SummoningTableState.INACTIVE));
            }
        }
    }

    private static void burnColumn(Level level, BlockPos bottomPos, byte nybbleDigit) {
        // wahaha i was right java does smear the bits (i think), this was a justified &
        int desiredHeight = ((nybbleDigit >> 2) & 0b11) + 1;
        int desiredCandles = (nybbleDigit & 0b11) + 1;

        BlockPos targetPos = bottomPos.offset(0, desiredHeight, 0);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.canBeReplaced()) {
            // maybe sound/particle?
            level.setBlockAndUpdate(targetPos, FamiliarBlocks.SMOKE_WISP_BLOCK.get().defaultBlockState().setValue(SmokeWispBlock.CANDLES, desiredCandles));
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
        for (int i = 1; i < allItems.size() && i < 1+offerings.getSlots(); i++) {
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
            return new SummoningTableMenu(containerId, playerInventory, this, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()));
        } else {
            return null;
        }
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

        return toReturn;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack toReturn = ItemStack.EMPTY;

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

        return toReturn;
    }

    @Override
    public int getSlotLimit(int slot) {
        switch(slot) {
            case 0 -> {return 1;}
            case 1,2,3,4 -> {return this.offerings.getSlotLimit(slot - 1);}
            default -> {return 0;}
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
