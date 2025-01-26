package io.github.afamiliarquiet.familiar_magic.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class FocusBlockParticleMixin {
    // i don't particularly like this but. i can kinda see some sense in it.
    // i can't add the focus check to the block because i can't get client player with client code being separated,
    // so i'm resorting to doing it much more like barriers and lights. probably not much different, just annoying to mixin

    @Shadow
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {

    }

    @Inject(method = "randomBlockDisplayTick", at = @At("TAIL"))
    private void randomBLockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, Block block, BlockPos.Mutable pos, CallbackInfo ci, @Local(ordinal = 4) int i, @Local(ordinal = 5) int j, @Local(ordinal = 6) int k, @Local(ordinal = 0) BlockState blockState) {
        if (FamiliarAttachments.isFocused(MinecraftClient.getInstance().player) && blockState.getBlock() == FamiliarBlocks.SMOKE_WISP) {
            this.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, blockState), (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, 0.0, 0.0, 0.0);
        }
    }
}
