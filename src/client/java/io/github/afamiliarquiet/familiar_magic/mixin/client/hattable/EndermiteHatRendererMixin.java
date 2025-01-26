package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.EndermiteHatFeature;
import net.minecraft.client.render.entity.EndermiteEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.mob.EndermiteEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermiteEntityRenderer.class)
public abstract class EndermiteHatRendererMixin extends MobEntityRenderer<EndermiteEntity, EndermiteEntityModel<EndermiteEntity>> {
    public EndermiteHatRendererMixin(EntityRendererFactory.Context context, EndermiteEntityModel<EndermiteEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new EndermiteHatFeature(this, context.getHeldItemRenderer()));
    }
}
