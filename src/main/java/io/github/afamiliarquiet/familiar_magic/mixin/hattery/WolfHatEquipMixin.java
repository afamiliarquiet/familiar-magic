package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.item.ClothingItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.hasHat;

@Mixin(Wolf.class)
public abstract class WolfHatEquipMixin extends TamableAnimal {
    protected WolfHatEquipMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // there's some nonsense going on in wolf's mobinteract. let's just skip that if we've got hat business
        if (player.isSecondaryUseActive() && (player.getItemInHand(hand).getItem() instanceof ClothingItem || hasHat(this))) {
            cir.setReturnValue(super.mobInteract(player, hand));
        }
    }
}
