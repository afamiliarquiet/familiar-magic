package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.client.SilverfishHatLayer;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.world.entity.monster.Silverfish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SilverfishRenderer.class)
public abstract class SilverfishHatRendererMixin extends MobRenderer<Silverfish, SilverfishModel<Silverfish>> {
    public SilverfishHatRendererMixin(EntityRendererProvider.Context context, SilverfishModel<Silverfish> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new SilverfishHatLayer(this, context.getItemInHandRenderer()));
    }
}
