package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.another_of_many;

import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class Yip {
    @Shadow protected TextFieldWidget chatField;

    @Inject(at = @At("HEAD"), method = "onChatFieldUpdate", cancellable = true)
    private void yip(String chatText, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && CurseAttachment.Curse.shouldYip(MinecraftClient.getInstance().player)) {
            String yipping = CurseAttachment.Curse.yipify(chatText);
            if (!yipping.equals(chatText)) { // no stack overflows please!
                chatField.setText(yipping);
                ci.cancel();
            }
        }
    }
}
