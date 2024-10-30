package io.github.afamiliarquiet.familiar_magic.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

@ParametersAreNonnullByDefault
public class FoxHatLayer extends RenderLayer<Fox, FoxModel<Fox>> {
    private final ItemInHandRenderer actuallyItsAHatRenderer;

    public FoxHatLayer(RenderLayerParent<Fox, FoxModel<Fox>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.actuallyItsAHatRenderer = itemInHandRenderer;
    }

    // do NOT copy from the fox held item layer i dont know WHAT they were cooking with all that translation and rotation
    // just copy the head part's translation/rotation/scale(baby)
    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Fox fox,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack hat = getHat(fox);
        if (hat.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        ModelPart littleFishyHead = this.getParentModel().head;
        poseStack.translate(littleFishyHead.x / 16.0f, littleFishyHead.y / 16.0f, littleFishyHead.z / 16.0f);
        poseStack.mulPose(new Quaternionf().rotationZYX(littleFishyHead.zRot, littleFishyHead.yRot, littleFishyHead.xRot));
        poseStack.scale(littleFishyHead.xScale, littleFishyHead.yScale, littleFishyHead.zScale);

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.0625f, 0f, -0.125f);

        poseStack.scale(0.5625f, 0.5625f, 0.5625f);

        this.actuallyItsAHatRenderer.renderItem(fox, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
