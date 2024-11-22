package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TrueNameItem extends Item {
    public TrueNameItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack nameTag, Player player, LivingEntity target, InteractionHand hand) {
        SingedComponentRecord record = nameTag.get(FamiliarItems.SINGED_COMPONENT);

        if (record != null && !record.singed()) {
            if (!player.level().isClientSide && target.isAlive()) {
                ItemStack trueName = FamiliarItems.TRUE_NAME_ITEM.toStack();
                trueName.set(DataComponents.CUSTOM_NAME, Component.literal(FamiliarTricks.uuidToTrueName(target.getUUID())));

                ItemStack handStack = ItemUtils.createFilledResult(nameTag, player, trueName, false);
                player.setItemInHand(hand, handStack);
            }

            return InteractionResult.sidedSuccess(player.level().isClientSide);
        } else {
            return super.interactLivingEntity(nameTag, player, target, hand);
        }
    }
}
