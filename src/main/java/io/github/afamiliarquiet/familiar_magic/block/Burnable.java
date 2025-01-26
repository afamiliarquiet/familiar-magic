package io.github.afamiliarquiet.familiar_magic.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;

public interface Burnable {
    // i miss getToolModifiedState..
    public BlockState onIgnition(BlockState state, ItemUsageContext context);
}
