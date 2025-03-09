package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.TadpoleHatFeature;
import net.minecraft.client.render.entity.TadpoleEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.entity.passive.TadpoleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TadpoleEntityRenderer.class)
public abstract class TadpoleHatRendererMixin extends MobEntityRenderer<TadpoleEntity, TadpoleEntityModel<TadpoleEntity>> {
    public TadpoleHatRendererMixin(EntityRendererFactory.Context context, TadpoleEntityModel<TadpoleEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new TadpoleHatFeature(this, context.getHeldItemRenderer()));
    }
}
