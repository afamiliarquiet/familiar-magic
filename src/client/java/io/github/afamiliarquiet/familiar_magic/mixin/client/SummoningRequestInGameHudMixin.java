package io.github.afamiliarquiet.familiar_magic.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.afamiliarquiet.familiar_magic.gooey.FamiliarClientScreenery;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InGameHud.class)
public abstract class SummoningRequestInGameHudMixin {
    // this does not feel advisable. but it's just until i update the mod it's fine i swear
    @WrapMethod(method = "renderPlayerList")
    private void alsoRenderRequestPlease(DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
        FamiliarClientScreenery.SUMMONING_REQUEST_RENDER_LAYER.onHudRender(context, tickCounter);
        original.call(context, tickCounter);
    }
}
