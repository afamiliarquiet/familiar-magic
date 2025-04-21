package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.another_of_many;

import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class FoxthingEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public FoxthingEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("HEAD"), method = "renderLeftArm", cancellable = true)
    private void noLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, CallbackInfo ci) {
        // todo - this may be where ears claws are rendering and getting mixed up?
        if (FamiliarAttachments.getCurse(player).currentAffliction().equals(CurseAttachment.Curse.FAMILIAR_BITE)) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderRightArm", cancellable = true)
    private void noRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, CallbackInfo ci) {
        // todo - this may be where ears claws are rendering and getting mixed up?
        if (FamiliarAttachments.getCurse(player).currentAffliction().equals(CurseAttachment.Curse.FAMILIAR_BITE)) {
            ci.cancel();
        }
    }
}
