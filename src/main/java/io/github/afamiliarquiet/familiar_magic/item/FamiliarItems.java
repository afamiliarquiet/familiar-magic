package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class FamiliarItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        // i feel like i should be able to get away with using the @EventBusSubscriber instead of having to pass it in
        // but i guess i have to call something here anyway so whatever
    }

    @SubscribeEvent
    private static void mrwBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        // // oOOooOOo \\ \\ spoidah

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertBefore(
                    Items.CANDLE.getDefaultInstance(),
                    FamiliarBlocks.ENCHANTED_CANDLE_BLOCK.toStack(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}
