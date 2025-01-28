package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.dragon;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerEatingManagerMixin {
    @SuppressWarnings("ShadowModifiers") // don't care. don't wanna deal with final shadows and initialization crap. shush.
    @Shadow
    private final MinecraftClient client;

    @Shadow
    private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
    @Shadow
    private boolean breakingBlock;

    protected ClientPlayerEatingManagerMixin(MinecraftClient client) {
        this.client = client;
    }

    @WrapWithCondition(method = "cancelBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetLastAttackedTicks()V"))
    private boolean iKeepSayingThisButApparentlyINeedToRepeatMyselfColonNotIfImHungryExclamationMarkExclamationMark(ClientPlayerEntity instance) {
        return !(CurseAttachment.Curse.shouldMaw(instance));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (this.breakingBlock && this.client.player != null && CurseAttachment.Curse.shouldMaw(this.client.player) && this.client.player.getWorld().getTime() % 4 == 0) {
            BlockState eatingState = this.client.player.getWorld().getBlockState(this.currentBreakingPos);
            this.client.player.spawnItemParticles(eatingState.getBlock().getPickStack(this.client.player.getWorld(), this.currentBreakingPos, eatingState), 5);
        }
    }
}
