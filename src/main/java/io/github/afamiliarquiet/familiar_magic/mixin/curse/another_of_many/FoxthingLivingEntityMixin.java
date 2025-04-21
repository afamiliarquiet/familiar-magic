package io.github.afamiliarquiet.familiar_magic.mixin.curse.another_of_many;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// would you believe me if i told you this was stolen?
// thank you evelyn!
@Mixin(LivingEntity.class)
public abstract class FoxthingLivingEntityMixin extends Entity {
    @Unique
    private boolean familiar_magic$wasFoxthing = false;

    public FoxthingLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void foxthingTick(CallbackInfo ci) {
        if (FamiliarAttachments.hasCurse(this) && FamiliarAttachments.getCurse(this).currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE) {
            FamiliarAttachments.getCurse(this).tick(this);
            familiar_magic$wasFoxthing = true;
        } else if (familiar_magic$wasFoxthing) {
            familiar_magic$wasFoxthing = false;
            this.calculateDimensions();
        }
    }

    @Inject(at = @At("HEAD"), method = "getDimensions", cancellable = true)
    private void morphDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (FamiliarAttachments.hasCurse(this)) {
            var component = FamiliarAttachments.getCurse(this);
            if (component.currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE) {
                var disguise = component.getFox();
                if (disguise != null) {
                    cir.setReturnValue(disguise.getDimensions(pose));
                }
            }
        }
    }

    @WrapOperation(method = { "playHurtSound", "onDamaged" }, at = @At(
            value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;"
    ))
    private SoundEvent morphHurtSound(LivingEntity instance, DamageSource source, Operation<SoundEvent> original) {
        var component = FamiliarAttachments.getCurse(this);
        if (component.currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE) {
            var disguise = component.getFox();
            if (disguise != null) {
                return SoundEvents.ENTITY_FOX_HURT;
            }
        }
        return original.call(instance, source);
    }
}
