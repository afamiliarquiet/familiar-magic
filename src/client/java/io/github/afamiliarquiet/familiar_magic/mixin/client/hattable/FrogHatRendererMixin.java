package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.FrogHatFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FrogEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.entity.passive.FrogEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrogEntityRenderer.class)
public abstract class FrogHatRendererMixin extends MobEntityRenderer<FrogEntity, FrogEntityModel<FrogEntity>> {
    public FrogHatRendererMixin(EntityRendererFactory.Context context, FrogEntityModel<FrogEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new FrogHatFeature(this, context.getHeldItemRenderer()));
    }
}
