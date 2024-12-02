package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class TrueNameTagMixin {
    // catgirl at its second rodeo: heh, this ain't my first rodeo >:]

    @Inject(at = @At("HEAD"), method = "interactLivingEntity", cancellable = true)
    private void interactLivingEntity(ItemStack nameTag, Player player, LivingEntity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Component component = nameTag.get(DataComponents.CUSTOM_NAME);
        if (component == null) {
            if (!player.level().isClientSide && target.isAlive()) {
                ItemStack trueName = FamiliarItems.TRUE_NAME_ITEM.toStack();
                trueName.set(DataComponents.CUSTOM_NAME, Component.literal(FamiliarTricks.uuidToTrueName(target.getUUID())));

                ItemStack handStack = ItemUtils.createFilledResult(nameTag.copy(), player, trueName, false);
                player.setItemInHand(hand, handStack);
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
        }
    }
}
