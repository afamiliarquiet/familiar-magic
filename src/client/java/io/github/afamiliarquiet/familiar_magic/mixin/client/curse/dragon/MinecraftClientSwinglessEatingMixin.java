package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.dragon;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientSwinglessEatingMixin {
    @Shadow
    public HitResult crosshairTarget;

    @WrapWithCondition(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private boolean notIfImHungry(ClientPlayerEntity instance, Hand hand) {
        return !CurseAttachment.Curse.shouldMaw(instance);
    }

    @WrapWithCondition(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private boolean iInsistNotIfImHungry(ClientPlayerEntity instance, Hand hand) {
        return !(this.crosshairTarget.getType() == HitResult.Type.BLOCK && CurseAttachment.Curse.shouldMaw(instance));
    }
}
