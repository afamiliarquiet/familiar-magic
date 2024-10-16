package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> TRUE_NAME_ITEM = ITEMS.registerSimpleItem("true_name");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(FamiliarItems::mrwBuildCreativeModeTabContents);
    }

    private static void mrwBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        // // oOOooOOo \\ \\ spoidah

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertAfter(
                    Items.CANDLE.getDefaultInstance(),
                    FamiliarBlocks.ENCHANTED_CANDLE_BLOCK.toStack(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    Items.ENCHANTING_TABLE.getDefaultInstance(),
                    FamiliarBlocks.SUMMONING_TABLE_BLOCK.toStack(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}
