package io.github.afamiliarquiet.familiar_magic.mixin.client.hattable;

import io.github.afamiliarquiet.familiar_magic.entity.hat.SlimeHatFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntityRenderer.class)
public abstract class SlimeHatRendererMixin extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
    public SlimeHatRendererMixin(EntityRendererFactory.Context context, SlimeEntityModel<SlimeEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new SlimeHatFeature(this, context.getHeldItemRenderer()));
    }
}
