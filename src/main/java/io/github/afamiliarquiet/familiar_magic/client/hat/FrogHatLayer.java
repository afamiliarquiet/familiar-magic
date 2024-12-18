package io.github.afamiliarquiet.familiar_magic.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

@ParametersAreNonnullByDefault
public class FrogHatLayer extends RenderLayer<Frog, FrogModel<Frog>> {
    private final ItemInHandRenderer actuallyItsAHatRenderer;

    public FrogHatLayer(RenderLayerParent<Frog, FrogModel<Frog>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.actuallyItsAHatRenderer = itemInHandRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Frog frog,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack hat = getHat(frog);
        if (hat.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        ModelPart head = this.getParentModel().head;
        poseStack.translate(head.x / 16.0f, head.y / 16.0f, head.z / 16.0f);
        poseStack.mulPose(new Quaternionf().rotationZYX(head.zRot, head.yRot, head.xRot));
        poseStack.scale(head.xScale, head.yScale, head.zScale);

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0f, -1.25f, 0f);

        poseStack.scale(0.5f, 0.5f, 0.5f);

        this.actuallyItsAHatRenderer.renderItem(frog, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
