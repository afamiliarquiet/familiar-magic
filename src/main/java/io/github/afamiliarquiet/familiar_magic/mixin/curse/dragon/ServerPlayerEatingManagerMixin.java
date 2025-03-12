package io.github.afamiliarquiet.familiar_magic.mixin.curse.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerEatingManagerMixin {
    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Shadow
    private int tickCounter;

    @Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void tryEatBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local Block breakyBlock) {
        if (CurseAttachment.Curse.shouldMaw(this.player)) {
            // play burp, feed
            this.player.getWorld().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, this.player.getRandom().nextFloat() * 0.1f + 0.9f);
            this.player.getHungerManager().add(MathHelper.ceil(breakyBlock.getHardness()), breakyBlock.getBlastResistance() / 3f);
            CurseAttachment.Curse.simulateDraconicDigestion(player, breakyBlock);
        }
    }

    @Inject(method = "continueMining", at = @At("HEAD"))
    private void continueMunching(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
        if (CurseAttachment.Curse.shouldMaw(this.player)) {
            if (this.tickCounter % 4 == 0) {
                this.player.getWorld().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5f + 0.5f * (float) this.player.getRandom().nextInt(2), (this.player.getRandom().nextFloat() - this.player.getRandom().nextFloat()) * 0.2f + 1.0f);
                this.player.spawnItemParticles(state.getBlock().asItem().getDefaultStack(), 5);
            }
        }
    }

}
