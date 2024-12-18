package io.github.afamiliarquiet.familiar_magic.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

@ParametersAreNonnullByDefault
public class SilverfishHatLayer extends RenderLayer<Silverfish, SilverfishModel<Silverfish>> {
    private final ItemInHandRenderer actuallyItsAHatRenderer;

    public SilverfishHatLayer(RenderLayerParent<Silverfish, SilverfishModel<Silverfish>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.actuallyItsAHatRenderer = itemInHandRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Silverfish silverfish,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack hat = getHat(silverfish);
        if (hat.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        if (!this.getParentModel().root().hasChild("segment0")) {
            return;
        }
        ModelPart littleFishyHead = this.getParentModel().root().getChild("segment0");
        poseStack.translate(littleFishyHead.x / 16.0f, littleFishyHead.y / 16.0f, littleFishyHead.z / 16.0f);
        poseStack.mulPose(new Quaternionf().rotationZYX(littleFishyHead.zRot, littleFishyHead.yRot, littleFishyHead.xRot));

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        poseStack.translate(0f, -0.05f, 0f);

        poseStack.scale(0.25f, 0.25f, 0.25f);

        this.actuallyItsAHatRenderer.renderItem(silverfish, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
