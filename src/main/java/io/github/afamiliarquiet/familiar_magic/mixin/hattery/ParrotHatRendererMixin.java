package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.client.hat.ParrotHatLayer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParrotRenderer.class)
public abstract class ParrotHatRendererMixin extends MobRenderer<Parrot, ParrotModel> {
    public ParrotHatRendererMixin(EntityRendererProvider.Context context, ParrotModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new ParrotHatLayer(this, context.getItemInHandRenderer()));
    }
}
