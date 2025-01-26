package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class ParrotHatFeature extends FeatureRenderer<ParrotEntity, ParrotEntityModel> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public ParrotHatFeature(FeatureRendererContext<ParrotEntity, ParrotEntityModel> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            ParrotEntity parrot,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(parrot);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        ModelPart head = this.getContextModel().head;
        matrices.translate(head.pivotX / 16.0f, head.pivotY / 16.0f, head.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(head.roll, head.yaw, head.pitch));
        matrices.scale(head.xScale, head.yScale, head.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, 0.0625f, -0.0625f);
        matrices.scale(0.3125f, 0.3125f, 0.3125f);

        this.actuallyItsAHatRenderer.renderItem(parrot, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
