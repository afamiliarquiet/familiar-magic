package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class SilverfishHatFeature extends FeatureRenderer<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public SilverfishHatFeature(FeatureRendererContext<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            SilverfishEntity silverfish,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(silverfish);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        if (!this.getContextModel().getPart().hasChild("segment0")) {
            return;
        }
        ModelPart littleFishyHead = this.getContextModel().getPart().getChild("segment0");
        matrices.translate(littleFishyHead.pivotX / 16.0f, littleFishyHead.pivotY / 16.0f, littleFishyHead.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(littleFishyHead.roll, littleFishyHead.yaw, littleFishyHead.pitch));
        matrices.scale(littleFishyHead.xScale, littleFishyHead.yScale, littleFishyHead.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, -0.05f, 0f);
        matrices.scale(0.3125f, 0.3125f, 0.3125f);

        this.actuallyItsAHatRenderer.renderItem(silverfish, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
