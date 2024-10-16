package io.github.afamiliarquiet.familiar_magic.gooey;

import com.mojang.datafixers.util.Pair;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.MethodsReturnNonnullByDefault;
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

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SummoningTableMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_TRUE_NAME = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_slot_true_name");

    private final ContainerLevelAccess levelAccess;
    protected final ContainerData tableData;
    private final SlotItemHandler trueNameSlot;

    public SummoningTableMenu(int containerId, Inventory playerInv) {
        this(containerId,  playerInv, new ItemStackHandler(1), new SimpleContainerData(4), ContainerLevelAccess.NULL);
    }
    public SummoningTableMenu(int containerId, Inventory playerInventory, IItemHandler tableInventory, ContainerData tableData, ContainerLevelAccess levelAccess) {
        super(FamiliarGUIStuffs.SUMMONING_TABLE_MENU.get(), containerId);
        checkContainerDataCount(tableData, 4);
        this.levelAccess = levelAccess;
        this.tableData = tableData;

        // table inv
        this.trueNameSlot = new SlotItemHandler(tableInventory, 0, 26, 31) {
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
            if (index == 0) {
                 // move out of table
                if (!this.moveItemStackTo(clickedStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.moveItemStackTo(clickedStack, 0, 1, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
                // move into table
                return ItemStack.EMPTY;
            } else if (index >= 1 && index < 28) {
                // move from inventory to hotbar
                if (!this.moveItemStackTo(clickedStack, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37) {
                // move from hotbar to inventory
                if (!this.moveItemStackTo(clickedStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(clickedStack, 1, 37, false)) {
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
}
