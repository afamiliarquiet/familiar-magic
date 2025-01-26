package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.CatHatFeature;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntityRenderer.class)
public abstract class CatHatRendererMixin extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
    public CatHatRendererMixin(EntityRendererFactory.Context context, CatEntityModel<CatEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new CatHatFeature(this, context.getHeldItemRenderer()));
    }
}
