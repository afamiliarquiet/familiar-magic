package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class TurtleHatFeature extends FeatureRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public TurtleHatFeature(FeatureRendererContext<TurtleEntity, TurtleEntityModel<TurtleEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            TurtleEntity turtle,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(turtle);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        if (turtle.isBaby()) {
            matrices.scale(0.25f, 0.25f, 0.25f);
            matrices.translate(0f, 4.6875f, 0f);
        }

        ModelPart body = this.getContextModel().body;
        matrices.translate(body.pivotX / 16.0f, body.pivotY / 16.0f, body.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(body.roll, body.yaw, body.pitch));
        matrices.scale(body.xScale, body.yScale, body.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, -0.8125f, -0.3125f);
        matrices.scale(0.625f, 0.625f, 0.625f);
        matrices.multiply(new Quaternionf().rotationX(MathHelper.PI * 0.4375f));

        this.actuallyItsAHatRenderer.renderItem(turtle, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
