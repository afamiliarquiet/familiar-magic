package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.item.ClothingItem;
import io.github.afamiliarquiet.familiar_magic.network.HattedPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.*;

@Mixin(Mob.class)
public abstract class MobHatEquipMixin extends LivingEntity {
    protected MobHatEquipMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (isHattable(this)) {
            boolean isClient = this.level().isClientSide;
            if (player.isSecondaryUseActive()) {
                if (player.getItemInHand(hand).getItem() instanceof ClothingItem && !hasHat(this)) {
                    if (!isClient) {
                        setHat(this, player.getItemInHand(hand).copy());
                        PacketDistributor.sendToPlayersTrackingEntity(this, new HattedPayload(getHat(this), this.getId()));
                        player.getItemInHand(hand).shrink(1);
                    }
                    cir.setReturnValue(InteractionResult.sidedSuccess(isClient));

                } else if (player.getItemInHand(hand).isEmpty() && hasHat(this)) {
                    if (!isClient) {
                        ItemStack hat = getHat(this);
                        setHat(this, ItemStack.EMPTY);
                        PacketDistributor.sendToPlayersTrackingEntity(this, new HattedPayload(getHat(this), this.getId()));
                        player.setItemInHand(hand, hat);

                    }
                    cir.setReturnValue(InteractionResult.sidedSuccess(isClient));
                }
            }
        }
    }
}
