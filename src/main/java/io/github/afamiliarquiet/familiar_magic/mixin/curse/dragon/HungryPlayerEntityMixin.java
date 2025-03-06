package io.github.afamiliarquiet.familiar_magic.mixin.curse.dragon;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class HungryPlayerEntityMixin extends LivingEntity {
    @Shadow
    protected HungerManager hungerManager;

    protected HungryPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapMethod(method = "getBlockBreakingSpeed")
    private float addHungriness(BlockState block, Operation<Float> original) {
        // the hungrier you are, the faster you mine - from 200% up to 400% (bad idea? but funny)
        if (CurseAttachment.Curse.shouldMaw(this)) {
            return original.call(block) * 2f + (20 - this.hungerManager.getFoodLevel()) * 0.1f;
        } else {
            return original.call(block);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        if (this.getWorld().isClient) {
            if (FamiliarAttachments.getCurse(this).currentAffliction() == CurseAttachment.Curse.DRAGON && random.nextFloat() < 0.013) {
                this.getWorld().addParticle(ParticleTypes.FLAME,
                        this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5),
                        (random.nextFloat() - 0.5) * 0.031, random.nextFloat() * 0.031, (random.nextFloat() - 0.5) * 0.031);
            }
        }
    }
}
