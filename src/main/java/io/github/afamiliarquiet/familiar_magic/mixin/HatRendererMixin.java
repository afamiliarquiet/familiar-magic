package io.github.afamiliarquiet.familiar_magic.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class HatRendererMixin {

    @Shadow
    private ItemModelShaper itemModelShaper;

    @Shadow
    public static VertexConsumer getFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean noEntity, boolean withGlint) {
        return null;
    }

    @Shadow
    public void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer) {

    }

    // could maybe replace this with a @modifyvariable n grab itemstack n display context via @local? might try later
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        if (itemStack.is(FamiliarItems.BIG_HAT) && displayContext == ItemDisplayContext.HEAD) {
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                p_model = this.itemModelShaper.getModelManager().getModel(FamiliarMagicClient.BIG_HAT_ON_HEAD_MODEL);

                p_model = net.neoforged.neoforge.client.ClientHooks.handleCameraTransforms(poseStack, p_model, displayContext, leftHand);
                poseStack.translate(-0.5F, -0.5F, -0.5F);

                for (var model : p_model.getRenderPasses(itemStack, true)) {
                    for (var rendertype : model.getRenderTypes(itemStack, true)) {
                        VertexConsumer vertexconsumer;
                        vertexconsumer = getFoilBufferDirect(bufferSource, rendertype, true, itemStack.hasFoil());

                        this.renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, vertexconsumer);
                    }
                }


                poseStack.popPose();
            }

            ci.cancel();
        }
    }
}
