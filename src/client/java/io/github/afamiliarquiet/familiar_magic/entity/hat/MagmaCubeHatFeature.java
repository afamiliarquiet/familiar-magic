package io.github.afamiliarquiet.familiar_magic.entity.hat;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class MagmaCubeHatFeature extends FeatureRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
    private final HeldItemRenderer actuallyItsAHatRenderer;

    public MagmaCubeHatFeature(FeatureRendererContext<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> context, HeldItemRenderer handRenderer) {
        super(context);
        this.actuallyItsAHatRenderer = handRenderer;
    }

    // a copy of a copy of a copy of a copy of..
    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            MagmaCubeEntity magmaCube,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        ItemStack hat = FamiliarAttachments.getHat(magmaCube);
        if (hat.isEmpty()) {
            return;
        }

        matrices.push();

        if (!this.getContextModel().getPart().hasChild("cube0")) {
            return;
        }
        ModelPart cube = this.getContextModel().getPart().getChild("cube0");
        matrices.translate(cube.pivotX / 16.0f, cube.pivotY / 16.0f, cube.pivotZ / 16.0f);
        matrices.multiply(new Quaternionf().rotationZYX(cube.roll, cube.yaw, cube.pitch));
        matrices.scale(cube.xScale, cube.yScale, cube.zScale);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        matrices.translate(0f, -1.125f, 0f);
        matrices.scale(0.5625f, 0.5625f, 0.5625f);

        this.actuallyItsAHatRenderer.renderItem(magmaCube, hat, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
