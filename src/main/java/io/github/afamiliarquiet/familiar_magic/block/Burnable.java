package io.github.afamiliarquiet.familiar_magic.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;

public interface Burnable {
    // i miss getToolModifiedState..
    BlockState onIgnition(BlockState state, ItemUsageContext context);

    boolean canIgnite(BlockState clickedState);
}
