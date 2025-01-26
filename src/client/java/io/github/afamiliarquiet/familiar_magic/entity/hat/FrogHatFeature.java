package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class FrogHatFeature extends FeatureRenderer<FrogEntity, FrogEntityModel<FrogEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public FrogHatFeature(FeatureRendererContext<FrogEntity, FrogEntityModel<FrogEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            FrogEntity frog,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(frog);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        ModelPart body = this.getContextModel().body;
        matrices.translate(body.pivotX / 16.0f, body.pivotY / 16.0f, body.pivotZ / 16.0f);
        matrices.scale(body.xScale, body.yScale, body.zScale);
        matrices.translate(0f, 1.3125f, -0.1875f);
        matrices.multiply(new Quaternionf().rotationZYX(body.roll, body.yaw, body.pitch));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.scale(0.5625f, 0.5625f, 0.5625f);

        this.actuallyItsAHatRenderer.renderItem(frog, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
