package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class FoxHatFeature extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public FoxHatFeature(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy. i fear the markings in this tomb. tread cautiously
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            FoxEntity fox,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(fox);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        if (fox.isBaby()) {
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(0f, 0.5f, 0.209375f);
        }

        ModelPart head = this.getContextModel().head;
        matrices.translate(head.pivotX / 16.0f, head.pivotY / 16.0f, head.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(head.roll, head.yaw, head.pitch));
        matrices.scale(head.xScale, head.yScale, head.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(-0.0625f, 0f, -0.125f);
        matrices.scale(0.5625f, 0.5625f, 0.5625f);

        this.actuallyItsAHatRenderer.renderItem(fox, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
