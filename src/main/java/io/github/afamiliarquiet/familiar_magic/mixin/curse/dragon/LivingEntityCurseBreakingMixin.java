package io.github.afamiliarquiet.familiar_magic.mixin.curse.dragon;

import io.github.afamiliarquiet.familiar_magic.FamiliarSounds;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCurseBreakingMixin extends Entity {
    @Shadow
    protected ItemStack activeItemStack;

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract boolean removeStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public LivingEntityCurseBreakingMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "consumeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;spawnConsumptionEffects(Lnet/minecraft/item/ItemStack;I)V"))
    private void consumeItem(CallbackInfo ci) {
        if (!this.getWorld().isClient()
                && this.activeItemStack.isOf(Items.BEETROOT_SOUP)
                && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)
                && FamiliarAttachments.getCurse(this).currentAffliction() == CurseAttachment.Curse.DRAGON) {
            // todo - proper curse stacking and reversion stuff. is that spoilers? don't pay attention to this
            FamiliarAttachments.removeCurse(this);
            // :(
            this.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
            if (((Object) this) instanceof PlayerEntity player) { // new curse. OBJECTIFY
                Vec3d p = this.getPos();
                this.getWorld().playSound(null, p.x, p.y, p.z, FamiliarSounds.CURSE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.7f);
                player.playSoundToPlayer(FamiliarSounds.CURSE_REMOVE_PERSONAL, SoundCategory.PLAYERS, 0.7f, 0.7f);
            }
        }
    }
}
