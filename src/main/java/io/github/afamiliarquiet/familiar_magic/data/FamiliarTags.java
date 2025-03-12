package io.github.afamiliarquiet.familiar_magic.data;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class FamiliarTags {
    public static final TagKey<EntityType<?>> HATTABLE = TagKey.of(
            Registries.ENTITY_TYPE.getKey(),
            FamiliarMagic.id("hattable")
    );

    public static final TagKey<Block> FAMILIAR_THINGS = TagKey.of(
            Registries.BLOCK.getKey(),
            FamiliarMagic.id("familiar_things")
    );

    public static final TagKey<Block> ESPECIALLY_TASTY_FOR_DRAGONS = TagKey.of(
            Registries.BLOCK.getKey(),
            FamiliarMagic.id("especially_tasty_for_dragons")
    );
    public static final TagKey<Block> ESPECIALLY_GROSS_FOR_DRAGONS = TagKey.of(
            Registries.BLOCK.getKey(),
            FamiliarMagic.id("especially_gross_for_dragons")
    );

    public static void initialize() {

    }
}
