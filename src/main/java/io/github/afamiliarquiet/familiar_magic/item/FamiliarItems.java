package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.id;

public class FamiliarItems {
    // behold, mine Future Proofing - this Key shall soon become a Key of Great Need! but for now im in 1.21 land
    public static final RegistryKey<Item> TRUE_NAME_KEY = key("true_name");
    public static final Item TRUE_NAME = register(TRUE_NAME_KEY, new TrueNameItem(new Item.Settings()
            .component(FamiliarComponents.SINGED_COMPONENT, false)
    ));

    public static final RegistryKey<Item> BIG_HAT_KEY = key("big_hat");
    public static final Item BIG_HAT = register(BIG_HAT_KEY, new ClothingItem(
            ArmorItem.Type.HELMET, new Item.Settings().maxDamage(1031)
    ));

    public static final RegistryKey<Item> ODD_TRINKET_KEY = key("odd_trinket");
    public static final Item ODD_TRINKET = register(ODD_TRINKET_KEY, new OddTrinketItem(new Item.Settings().maxCount(1)));

    public static final RegistryKey<Item> CURIOUS_VIAL_KEY = key("curious_vial");
    public static final Item CURIOUS_VIAL = register(CURIOUS_VIAL_KEY, new CuriousVialItem(new Item.Settings()));

    public static void initialize() {
        // now i know this was a bad thing on neo because of thread stuffs. but is it also bad on fabric? find out..
        DispenserBlock.registerBehavior(TRUE_NAME, new TrueNameDispenserBehavior());

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(
                (itemGroup) -> itemGroup.addBefore(Items.LEATHER_HELMET, BIG_HAT)
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.addAfter(Items.FIRE_CHARGE, FamiliarBlocks.SMOKE_WISP.asItem());
            itemGroup.addAfter(Items.BRUSH, ODD_TRINKET);
            itemGroup.addAfter(Items.NAME_TAG, TRUE_NAME);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(
                (itemGroup) -> itemGroup.addBefore(Items.OMINOUS_BOTTLE, CURIOUS_VIAL)
        );

        // wanna see a fummy trick? im gonna possibly initialize familiarblocks here instead kinda sorta
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.addAfter(Items.CANDLE, FamiliarBlocks.ENCHANTED_CANDLE.asItem());
            itemGroup.addAfter(Items.ENCHANTING_TABLE, FamiliarBlocks.SUMMONING_TABLE.asItem());
        });
    }

    public static Item register(RegistryKey<Item> registryKey, Item item) {
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
    }

    public static RegistryKey<Item> key(String thing) {
        return RegistryKey.of(RegistryKeys.ITEM, id(thing));
    }
}
