package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class TrueNameItem extends Item {
    public TrueNameItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack startStack, PlayerEntity player, LivingEntity target, Hand hand) {
        Boolean singed = startStack.get(FamiliarComponents.SINGED_COMPONENT);

        if (singed != null && !singed) {
            if (!player.getWorld().isClient() && target.isAlive()) {
                ItemStack namedStack = FamiliarItems.TRUE_NAME.getDefaultStack();
                namedStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(FamiliarTricks.uuidToTrueName(target.getUuid())));

                ItemStack handStack = ItemUsage.exchangeStack(startStack.copy(), player, namedStack, false);
                player.setStackInHand(hand, handStack);
            }
            return ActionResult.success(player.getWorld().isClient());
        } else {
            return super.useOnEntity(startStack, player, target, hand);
        }
    }
}
