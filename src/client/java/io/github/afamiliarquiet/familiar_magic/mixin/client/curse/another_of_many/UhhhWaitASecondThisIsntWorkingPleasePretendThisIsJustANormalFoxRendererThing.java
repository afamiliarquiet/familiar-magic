package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.another_of_many;

import com.mojang.authlib.GameProfile;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class UhhhWaitASecondThisIsntWorkingPleasePretendThisIsJustANormalFoxRendererThing extends PlayerEntity {
    public UhhhWaitASecondThisIsntWorkingPleasePretendThisIsJustANormalFoxRendererThing(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "getSkinTextures", cancellable = true)
    private void yetAnotherFox(CallbackInfoReturnable<SkinTextures> cir) {
        if (CurseAttachment.Curse.shouldYip(this)) {
            cir.setReturnValue(new PlayerListEntry(FamiliarMagicClient.it_that_shapes_the_craft, false).getSkinTextures());
        }
    }
}
