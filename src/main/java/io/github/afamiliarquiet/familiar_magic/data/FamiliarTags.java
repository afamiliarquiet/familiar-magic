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

//    public static final TagKey<Block> PRETTY_LIGHTS = TagKey.of(
//            Registries.BLOCK.getKey(),
//            FamiliarMagic.id("pretty_lights")
//    );
//
//    public static final TagKey<Block> PRETTY_PLANTS = TagKey.of(
//            Registries.BLOCK.getKey(),
//            FamiliarMagic.id("pretty_plants")
//    );
//
//    public static final TagKey<Block> PRETTY_ROCKS = TagKey.of(
//            Registries.BLOCK.getKey(),
//            FamiliarMagic.id("pretty_rocks")
//    );
//
//    public static final TagKey<Block> OBJECTS_OF_PERSONAL_POWER = TagKey.of(
//            Registries.BLOCK.getKey(),
//            FamiliarMagic.id("objects_of_personal_power")
//    );

    public static void initialize() {

    }
}
