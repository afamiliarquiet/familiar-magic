package io.github.afamiliarquiet.familiar_magic.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DummyTableItemHandler extends ItemStackHandler {
    // can you feel her guiding voice? our lady luna speaks these methods into my ears, i am but a vessel
    // doing this makes the client agree w/ server on what can be placed where and how much and when
    // eventually i should probably make both of them use this but whatever, it works for now
    private TableContainerData data = new TableContainerData(5);

    public DummyTableItemHandler(int size) {
        super(size);
    }

    public void setContainerData(TableContainerData newData) {
        this.data = newData;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? 1 : super.getSlotLimit(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack toReturn = stack;

        if (this.data.isModifiable()) {
            toReturn = super.insertItem(slot, stack, simulate);
        }

        return toReturn;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack toReturn = ItemStack.EMPTY;

        if (this.data.isModifiable()) {
            toReturn = super.extractItem(slot, amount, simulate);
        }

        return toReturn;
    }
}
