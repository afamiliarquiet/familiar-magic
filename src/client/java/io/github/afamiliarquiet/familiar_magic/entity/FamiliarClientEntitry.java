package io.github.afamiliarquiet.familiar_magic.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

public class FamiliarClientEntitry {
    public static void initialize() {
        EntityRendererRegistry.register(FamiliarEntities.FIRE_BREATH_TYPE, EmptyEntityRenderer::new);
    }
}
