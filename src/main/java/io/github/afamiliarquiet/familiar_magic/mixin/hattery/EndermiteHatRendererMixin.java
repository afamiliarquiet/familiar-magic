package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.client.hat.EndermiteHatLayer;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.monster.Endermite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermiteRenderer.class)
public abstract class EndermiteHatRendererMixin extends MobRenderer<Endermite, EndermiteModel<Endermite>> {
    public EndermiteHatRendererMixin(EntityRendererProvider.Context context, EndermiteModel<Endermite> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new EndermiteHatLayer(this, context.getItemInHandRenderer()));
    }
}
