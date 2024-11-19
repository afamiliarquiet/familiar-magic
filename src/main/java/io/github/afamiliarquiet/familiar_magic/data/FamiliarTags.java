package io.github.afamiliarquiet.familiar_magic.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarTags {
    public static final TagKey<EntityType<?>> HATTABLE = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "hattable")
    );

    public static void register(IEventBus modEventBus) {
        // behold.
    }
}
