package io.github.afamiliarquiet.familiar_magic.mixin.curse.another_of_many;

import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class Abandonment {
    // heeeeyyy auditor whats up haha don't mind my ServerPlayNetworkHandler mixin here lol!
    // haha im just joking around we keep it so safe and simple in here dont even worry about it
    // thanks for coming by uhh please don't stay too long. it wasn't my fault i swear
    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At("HEAD"), method = "cleanUp")
    private void onContractBroken(CallbackInfo ci) {
        // evil antipersistence. this curse stuff is structured so poorly i'm going to have a lot of debt to pay
        if (FamiliarAttachments.getCurse(this.player).currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE) {
            FamiliarAttachments.removeCurse(this.player);
            this.player.getAttributes().removeModifiers(CurseAttachment.FAMILIAR_BITE_ATTRIBUTES);
        }
    }
}
