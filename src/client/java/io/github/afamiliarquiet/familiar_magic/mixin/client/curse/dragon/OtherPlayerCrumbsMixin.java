package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.dragon;

import com.mojang.authlib.GameProfile;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OtherClientPlayerEntity.class)
public abstract class OtherPlayerCrumbsMixin extends AbstractClientPlayerEntity {
    // this is getting a bit silly. i shouldn't need this many mixins, right?

    public OtherPlayerCrumbsMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        BlockBreakingInfo info = this.clientWorld.worldRenderer.blockBreakingInfos.get(this.getId());
        if (info != null && CurseAttachment.Curse.shouldMaw(this) && this.getWorld().getTime() % 4 == 0) {
            BlockState eatingState = this.clientWorld.getBlockState(info.getPos());
            ItemStack eatingParticleStack = eatingState.getBlock().getPickStack(this.clientWorld, info.getPos(), eatingState);
            if (!eatingParticleStack.isEmpty()) {
                this.spawnItemParticles(eatingParticleStack, 5);
            }
        }
    }
}
