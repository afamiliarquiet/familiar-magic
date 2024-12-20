package io.github.afamiliarquiet.familiar_magic.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

@ParametersAreNonnullByDefault
public class EndermiteHatLayer extends RenderLayer<Endermite, EndermiteModel<Endermite>> {
    private final ItemInHandRenderer actuallyItsAHatRenderer;

    public EndermiteHatLayer(RenderLayerParent<Endermite, EndermiteModel<Endermite>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.actuallyItsAHatRenderer = itemInHandRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Endermite endermite,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack hat = getHat(endermite);
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

        poseStack.scale(0.3125f, 0.3125f, 0.3125f);

        this.actuallyItsAHatRenderer.renderItem(endermite, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
