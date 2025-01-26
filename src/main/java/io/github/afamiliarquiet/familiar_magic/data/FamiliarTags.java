package io.github.afamiliarquiet.familiar_magic.data;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class FamiliarTags {
    public static final TagKey<EntityType<?>> HATTABLE = TagKey.of(
            Registries.ENTITY_TYPE.getKey(),
            FamiliarMagic.id("hattable")
    );

    public static void initialize() {

    }
}
