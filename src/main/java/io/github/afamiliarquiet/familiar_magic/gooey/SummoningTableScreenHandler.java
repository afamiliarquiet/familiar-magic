package io.github.afamiliarquiet.familiar_magic.gooey;

import com.mojang.datafixers.util.Pair;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTablePropertyDelegate;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SummoningTableScreenHandler extends ScreenHandler {
    private static final Identifier EMPTY_TRUE_NAME = FamiliarMagic.id("item/empty_slot_true_name");

    private final ScreenHandlerContext context;
    private final SummoningTablePropertyDelegate tableData;
    final Slot trueNameSlot;

    public SummoningTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(5), new SummoningTablePropertyDelegate(17), ScreenHandlerContext.EMPTY);
    }

    public SummoningTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory tableInventory, SummoningTablePropertyDelegate tableData, ScreenHandlerContext context) {
        super(FamiliarScreenery.SUMMONING_TABLE_HANDLER_TYPE, syncId);
        this.context = context;
        this.tableData = tableData;

        // table slots
        this.trueNameSlot = new NameSlot(tableInventory, 0, 44, 45);
        this.addSlot(this.trueNameSlot);
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                this.addSlot(new LockingSlot(tableInventory, column + row * 2 + 1, 116 + column * 18, 36 + row * 18));
            }
        }
        checkDataCount(tableData, 17);
        this.addProperties(tableData);

        // player inv
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 105 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 163));
        }
    }

    public byte[] getWorldNybbles() {
        return this.tableData.getNybbles();
    }

    public boolean isModifiable() {
        return this.tableData.isModifiable();
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack copyStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack()) {
            ItemStack clickedStack = slot.getStack();
            copyStack = clickedStack.copy();
            if (slotIndex >= 0 && slotIndex < 5) {
                // move out of table
                if (!this.insertItem(clickedStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.insertItem(clickedStack, 0, 5, false)) {
                // move into table
                return ItemStack.EMPTY;
            } else if (slotIndex >= 5 && slotIndex < 32) {
                // move from inv to hotbar
                if (!this.insertItem(clickedStack, 32, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 32 && slotIndex < 41) {
                // move from hotbar to inv
                if (!this.insertItem(clickedStack, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(clickedStack, 5, 41, false)) {
                // idk where else it could come from but throw it in the inv i suppose
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (clickedStack.getCount() == copyStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, clickedStack);
        }

        return copyStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, FamiliarBlocks.SUMMONING_TABLE);
    }

    class LockingSlot extends Slot {

        public LockingSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canBeHighlighted() {
            return SummoningTableScreenHandler.this.tableData.isModifiable() && super.canBeHighlighted();
        }
    }

    class NameSlot extends LockingSlot {

        public NameSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(FamiliarItems.TRUE_NAME);
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_TRUE_NAME);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }
}
