package io.github.afamiliarquiet.familiar_magic.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FamiliarEntities {
    public static final Identifier FIRE_BREATH_ID = FamiliarMagic.id("fire_breath");
    public static final EntityType<FireBreathEntity> FIRE_BREATH_TYPE = register(FIRE_BREATH_ID, EntityType.Builder
            .create(FireBreathEntity::create, SpawnGroup.MISC)
            .dimensions(0.05f, 0.05f)
            .maxTrackingRange(4).trackingTickInterval(20) // copying arrows i guess? iunno whatever
            .build()
    );

    public static void initialize() {

    }

    public static <T extends Entity> EntityType<T> register(Identifier id, EntityType<T> thing) {
        return Registry.register(Registries.ENTITY_TYPE, id, thing);
    }
}
