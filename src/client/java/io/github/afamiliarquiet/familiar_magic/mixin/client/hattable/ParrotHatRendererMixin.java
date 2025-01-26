package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.ParrotHatFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParrotEntityRenderer.class)
public abstract class ParrotHatRendererMixin extends MobEntityRenderer<ParrotEntity, ParrotEntityModel> {
    public ParrotHatRendererMixin(EntityRendererFactory.Context context, ParrotEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new ParrotHatFeature(this, context.getHeldItemRenderer()));
    }
}
