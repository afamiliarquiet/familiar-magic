package io.github.afamiliarquiet.familiar_magic.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

@ParametersAreNonnullByDefault
public class CatHatLayer extends RenderLayer<Cat, CatModel<Cat>> {
    private final ItemInHandRenderer actuallyItsAHatRenderer;
    public CatHatLayer(RenderLayerParent<Cat, CatModel<Cat>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.actuallyItsAHatRenderer = itemInHandRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Cat cat,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack hat = getHat(cat);
        if (hat.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        ModelPart head = this.getParentModel().head;
        poseStack.translate(head.x / 16.0f, head.y / 16.0f, head.z / 16.0f);
        poseStack.mulPose(new Quaternionf().rotationZYX(head.zRot, head.yRot, head.xRot));
        poseStack.scale(head.xScale, head.yScale, head.zScale);

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0f, 0f, -0.0625f);

        poseStack.scale(0.375f, 0.375f, 0.375f);

        this.actuallyItsAHatRenderer.renderItem(cat, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
