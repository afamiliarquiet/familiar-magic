package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class SquidHatFeature extends FeatureRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public SquidHatFeature(FeatureRendererContext<SquidEntity, SquidEntityModel<SquidEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            SquidEntity squid,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(squid);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        ModelPart body = this.getContextModel().root;
        matrices.translate(body.pivotX / 16.0f, body.pivotY / 16.0f, body.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(body.roll, body.yaw, body.pitch));
        matrices.scale(body.xScale, body.yScale, body.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, -0.375f, 0.03125f);
        matrices.scale(0.875f, 0.875f, 0.875f);
        matrices.multiply(new Quaternionf().rotationX(MathHelper.PI * -0.0625f));

        this.actuallyItsAHatRenderer.renderItem(squid, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
