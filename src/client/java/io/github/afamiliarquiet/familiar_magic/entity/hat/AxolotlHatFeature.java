package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class AxolotlHatFeature extends FeatureRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public AxolotlHatFeature(FeatureRendererContext<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AxolotlEntity axolotl,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(axolotl);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        if (axolotl.isBaby()) {
            matrices.scale(0.875f, 0.875f, 0.875f);
            matrices.translate(0f, 0.5f, 0.3125f);
        }

        ModelPart head = this.getContextModel().head;
        ModelPart body = this.getContextModel().body;
        matrices.translate(body.pivotX / 16.0f, body.pivotY / 16.0f, body.pivotZ / 16.0f);
        matrices.scale(body.xScale, body.yScale, body.zScale);
        matrices.multiply(new Quaternionf().rotationZYX(body.roll, body.yaw, body.pitch));

        matrices.translate(head.pivotX / 16.0f, head.pivotY / 16.0f, head.pivotZ / 16.0f);
        matrices.scale(head.xScale, head.yScale, head.zScale);
        matrices.multiply(new Quaternionf().rotationZYX(head.roll, head.yaw, head.pitch));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, 0f, -0.0625f);
        matrices.scale(0.5625f, 0.5625f, 0.5625f);

        this.actuallyItsAHatRenderer.renderItem(axolotl, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
