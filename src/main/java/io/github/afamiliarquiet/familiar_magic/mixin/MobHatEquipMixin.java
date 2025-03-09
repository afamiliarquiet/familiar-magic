package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.item.ClothingItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobHatEquipMixin extends LivingEntity {
    protected MobHatEquipMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "interactWithItem", cancellable = true)
    private void interactWithItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (FamiliarAttachments.isHattable(this) && player.isSneaking()) {
            boolean cliently = this.getWorld().isClient();
            ItemStack handStack = player.getStackInHand(hand);
            boolean hatted = FamiliarAttachments.hasHat(this);

            if (handStack.isEmpty() && hatted) {
                // taking hat from entity
                if (!cliently) {
                    ItemStack hat = FamiliarAttachments.removeHat(this);
                    player.setStackInHand(hand, hat);
                }
                cir.setReturnValue(ActionResult.success(cliently));
            } else if (handStack.getItem() instanceof ClothingItem && !hatted) {
                // placing hat on entity
                if (!cliently) {
                    FamiliarAttachments.setHat(this, handStack.copyWithCount(1));
                    handStack.decrement(1);
                }
                cir.setReturnValue(ActionResult.success(cliently));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "dropLoot")
    private void dropLoot(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        if (FamiliarAttachments.hasHat(this)) {
            this.dropStack(FamiliarAttachments.removeHat(this));
        }
    }
}
