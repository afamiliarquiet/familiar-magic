package io.github.afamiliarquiet.familiar_magic.item.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;

public class ClothingMaterial {
    // i think this becomes a tad more relevant as a class in later mc versions? idk whatever
    // hey first comment of familiar fabric wahoooo breakin ground here !
    // good to see you again stick around and enjoy the ride
    // or perhaps, i hope you've been enjoying the ride? this can't be the first thing you're reading
    // we do minecraft lets plays on this java comment thread come back next week for the nether episode
    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.HELMET, 1,
                    ArmorItem.Type.CHESTPLATE, 3,
                    ArmorItem.Type.LEGGINGS, 2,
                    ArmorItem.Type.BOOTS, 1,
                    ArmorItem.Type.BODY, 3
            ),
            31,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(Items.STRING),
            List.of(),
            0,
            0
    );
}
