package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.block.EnchantedCandleBlock;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.gooey.SummoningTableMenu;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableBlockEntity extends BlockEntity implements IItemHandlerModifiable, MenuProvider, Nameable {
    // i'm really not feeling great about implementing IItemHandlerModifiable. feels like i should be doing something else.
    // but it works for now so whatever. i'm tryin my best to be neoforgey!!

    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private UUID target = null;
    private ItemStack item = ItemStack.EMPTY;

    private final ContainerData dataAccess = new ContainerData() {
        // lady luna guide me once more orz
        @Override
        public int get(int index) {
            return switch (index) {
                case 0, 1, 2, 3 -> target == null ? 0 : UUIDUtil.uuidToIntArray(target)[index];
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

    public BlockState tryActivate(BlockState state, boolean simulate) {
        if (this.target != null && this.level instanceof ServerLevel serverLevel) {
            Entity targetEntity = serverLevel.getEntity(this.target);
            if (targetEntity instanceof LivingEntity livingTarget) {
                BlockPos destination = this.getBlockPos();
                livingTarget.teleportTo(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
                return state.setValue(SummoningTableBlock.LIT, true);
            }
        }
        return state;
    }

    public BlockState tryBurnName(BlockState state, boolean simulate) {
        return state;
    }

    public boolean tryDesignate(BlockState state) {
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SummoningTableBlockEntity thisish) {
        UUID oldTarget = thisish.target;
        if (level.getGameTime() % 80L == 0L) {
            thisish.target = processCandles(level, pos);
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

    private static byte nybbleFromCandleColumn(Level level, BlockPos pos) {
        for (int yOffset = 4; yOffset > 0; yOffset--) {
            BlockState curiousBlockState = level.getBlockState(pos.offset(0, yOffset, 0));
            if (curiousBlockState.is(FamiliarBlocks.ENCHANTED_CANDLE_BLOCK)) {
                return (byte) ((yOffset - 1) << 2 | (curiousBlockState.getValue(EnchantedCandleBlock.CANDLES) - 1));
            }
        }

        // failed to find any candle
        return (byte) 0x00;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }

        this.lockKey.addToTag(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CustomName", 8)) {
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }

        this.lockKey = LockCode.fromTag(tag);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
        this.lockKey = componentInput.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);

        this.item = componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            components.set(DataComponents.LOCK, this.lockKey);
        }

        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
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
        tag.remove("Items");
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
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            return this.item;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot == 0 && this.item.isEmpty() && stack.is(FamiliarItems.TRUE_NAME_ITEM)) {
            if (!simulate) {
                this.item = stack.copyWithCount(1);
            }

            return stack.copyWithCount(stack.getCount() - 1);
        } else {
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0) {
            ItemStack toReturn = this.item.copy();
            if (!simulate) {
                this.item = ItemStack.EMPTY;
            }
            return toReturn;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? 1 : 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && stack.is(FamiliarItems.TRUE_NAME_ITEM);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot == 0) {
            this.item = stack;
        }
    }
}
