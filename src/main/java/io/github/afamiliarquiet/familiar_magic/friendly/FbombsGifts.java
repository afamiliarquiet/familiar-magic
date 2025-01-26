package io.github.afamiliarquiet.familiar_magic.friendly;

import io.github.afamiliarquiet.familiar_magic.item.ClothingItem;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;

public class FbombsGifts {
    public static final RegistryKey<Item> MEGUMINS_HAT_KEY = FamiliarItems.key("megumins_hat");
    public static final Item MEGUMINS_HAT = FamiliarItems.register(MEGUMINS_HAT_KEY, new ClothingItem(
            ArmorItem.Type.HELMET, new Item.Settings().maxDamage(1204)
    ));

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(
                (itemGroup) -> itemGroup.addBefore(Items.LEATHER_HELMET, MEGUMINS_HAT)
        );
    }
}
