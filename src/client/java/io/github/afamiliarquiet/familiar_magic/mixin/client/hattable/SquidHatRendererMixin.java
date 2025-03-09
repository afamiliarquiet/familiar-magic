package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.SquidHatFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.SquidEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SquidEntityRenderer.class)
public abstract class SquidHatRendererMixin extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    public SquidHatRendererMixin(EntityRendererFactory.Context context, SquidEntityModel<SquidEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, SquidEntityModel<SquidEntity> model, CallbackInfo ci) {
        this.addFeature(new SquidHatFeature(this, context.getHeldItemRenderer()));
    }
}
