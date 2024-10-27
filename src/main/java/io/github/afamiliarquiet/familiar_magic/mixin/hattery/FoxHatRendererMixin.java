package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.client.FoxHatLayer;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxRenderer.class)
public abstract class FoxHatRendererMixin extends MobRenderer<Fox, FoxModel<Fox>> {
    public FoxHatRendererMixin(EntityRendererProvider.Context context, FoxModel<Fox> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new FoxHatLayer(this, context.getItemInHandRenderer()));
    }
}
