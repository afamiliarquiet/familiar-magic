package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final Tier SOMEWHAT_FAMILIAR_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            1031,
            6f,
            1f,
            31,
            () -> Ingredient.of(Items.STRING)
    );

    public static final Holder<ArmorMaterial> SOMEWHAT_FAMILIAR_MATERIAL = ARMOR_MATERIALS.register(
            "somewhat_familiar",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.CHESTPLATE, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.BODY, 3); // for one-piece critters
                    }),
                    31,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(Items.STRING),
                    List.of(

                    ),
                    0,
                    0
            )
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SingedComponentRecord>> SINGED_COMPONENT = COMPONENTS.registerComponentType(
        "singed",
        builder -> builder
                .persistent(SingedComponentRecord.CODEC)
                .networkSynchronized(SingedComponentRecord.STREAM_CODEC)
    );

    public static final DeferredItem<Item> TRUE_NAME_ITEM = ITEMS.register(
            "true_name",
            () -> new Item(new Item.Properties()
                    .component(SINGED_COMPONENT.get(), new SingedComponentRecord(false))
            )
    );
    public static final DeferredItem<Item> BIG_HAT = ITEMS.register(
            "big_hat",
            () -> new ClothingItem(
                    SOMEWHAT_FAMILIAR_MATERIAL,
                    ArmorItem.Type.HELMET,
                    new Item.Properties().durability(1031)
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ARMOR_MATERIALS.register(eventBus);
        COMPONENTS.register(eventBus);
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

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.insertBefore(
                    Items.LEATHER_HELMET.getDefaultInstance(),
                    BIG_HAT.toStack(),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}
