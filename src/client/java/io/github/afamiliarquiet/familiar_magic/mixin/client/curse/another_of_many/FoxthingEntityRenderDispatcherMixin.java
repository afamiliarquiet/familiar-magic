package io.github.afamiliarquiet.familiar_magic.mixin.client.curse.another_of_many;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// thank you evelyn thank you im going to spare you of my evil curses
// this continues the grand morphing theft
@Mixin(EntityRenderDispatcher.class)
public abstract class FoxthingEntityRenderDispatcherMixin {
    @Inject(at = @At("HEAD"), method = "render")
    private void youreAFoxNow(Entity entity, double x, double y, double z, float yaw, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, CallbackInfo ci,
                              @Local(argsOnly = true)LocalRef<Entity> entityRef,
                              @Share("disguised") LocalRef<Entity> originalEntity) {
        CurseAttachment curse = FamiliarAttachments.getCurse(entity);
        if (curse.currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE && entity instanceof PlayerEntity player) {
            entityRef.set(curse.asFox(player));
            originalEntity.set(entity);
        }
    }
}
