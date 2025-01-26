package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.WolfHatFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntityRenderer.class)
public abstract class WolfHatRendererMixin extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    public WolfHatRendererMixin(EntityRendererFactory.Context context, WolfEntityModel<WolfEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new WolfHatFeature(this, context.getHeldItemRenderer()));
    }
}
