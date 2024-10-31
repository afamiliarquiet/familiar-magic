package io.github.afamiliarquiet.familiar_magic.client.gooey;

import com.mojang.datafixers.util.Pair;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_TRUE_NAME = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_slot_true_name");

    private final ContainerLevelAccess levelAccess;
    protected final ContainerData tableData;
    private final SlotItemHandler trueNameSlot;

    public SummoningTableMenu(int containerId, Inventory playerInv) {
        this(containerId,  playerInv, new ItemStackHandler(5), new SimpleContainerData(5), ContainerLevelAccess.NULL);
    }
    public SummoningTableMenu(int containerId, Inventory playerInventory, IItemHandler tableInventory, ContainerData tableData, ContainerLevelAccess levelAccess) {
        super(FamiliarGUIStuffs.SUMMONING_TABLE_MENU.get(), containerId);
        checkContainerDataCount(tableData, 5);
        this.levelAccess = levelAccess;
        this.tableData = tableData;

        // table inv
        this.trueNameSlot = new MoodySlotItemHandler(tableInventory, 0, 44, 31) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(FamiliarItems.TRUE_NAME_ITEM);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_TRUE_NAME);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };

        this.addSlot(this.trueNameSlot);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new MoodySlotItemHandler(tableInventory, j + i * 2 + 1, 116 + j * 18, 22 + i * 18));
            }
        }

        // player inv
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        // table data
        this.addDataSlots(tableData);
    }

    public UUID getTarget() {
        // maybe i should consider making an extension of containerdata to handle this for me
        return UUIDUtil.uuidFromIntArray(new int[]{tableData.get(0), tableData.get(1), tableData.get(2), tableData.get(3)});
    }

    public boolean feelingMoody() {
        return tableData.get(4) == 0;
    }

    // in theory i don't want this. because it's an inventory. it should keep it
//    @Override
//    public void removed(Player player) {
//        super.removed(player);
//        this.levelAccess.execute((level, blockPos) -> this.clearContainer(player, this.tableSlots));
//    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copyStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack clickedStack = slot.getItem();
            copyStack = clickedStack.copy();
            if (index >= 0 && index < 5) {
                 // move out of table
                if (!this.moveItemStackTo(clickedStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.moveItemStackTo(clickedStack, 0, 5, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
                // move into table
                return ItemStack.EMPTY;
            } else if (index >= 5 && index < 32) {
                // move from inventory to hotbar
                if (!this.moveItemStackTo(clickedStack, 32, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 32 && index < 41) {
                // move from hotbar to inventory
                if (!this.moveItemStackTo(clickedStack, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(clickedStack, 5, 41, false)) {
                // somehow, someway, move from somewhere else to hotbar or inventory
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (clickedStack.getCount() == copyStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, clickedStack);
        }

        return copyStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, FamiliarBlocks.SUMMONING_TABLE_BLOCK.get());
    }

    public class MoodySlotItemHandler extends SlotItemHandler {
        public MoodySlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isHighlightable() {
            if (feelingMoody()) {
                return false;
            } else {
                return super.isHighlightable();
            }
        }
    }
}
