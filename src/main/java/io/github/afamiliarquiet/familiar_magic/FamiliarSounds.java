package io.github.afamiliarquiet.familiar_magic;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class FamiliarSounds {
    // ENTITY_EXPERIENCE_ORB_PICKUP
    public static final SoundEvent BLOCK_SUMMONING_TABLE_BIND = registerSound("block.familiar_magic.summoning_table.bind");
    // BLOCK_AMETHYST_BLOCK_RESONATE
    public static final SoundEvent BLOCK_SUMMONING_TABLE_BIND_PENDING = registerSound("block.familiar_magic.summoning_table.bind.pending");
    // ENTITY_PLAYER_LEVELUP
    public static final SoundEvent BLOCK_SUMMONING_TABLE_BIND_CONFIRM = registerSound("block.familiar_magic.summoning_table.bind.confirm");
    // BLOCK_PORTAL_TRIGGER
    public static final SoundEvent BLOCK_SUMMONING_TABLE_BIND_CONFIRM_PERSONAL = registerSound("block.familiar_magic.summoning_table.bind.confirm.personal");
    // BLOCK_CAMPFIRE_CRACKLE
    public static final SoundEvent BLOCK_SUMMONING_TABLE_BURN = registerSound("block.familiar_magic.summoning_table.burn");
    // BLOCK_ENCHANTMENT_TABLE_USE
    public static final SoundEvent BLOCK_SUMMONING_TABLE_SUMMON = registerSound("block.familiar_magic.summoning_table.summon");
    // BLOCK_RESPAWN_ANCHOR_AMBIENT
    public static final SoundEvent BLOCK_SUMMONING_TABLE_SUMMON_PENDING = registerSound("block.familiar_magic.summoning_table.summon.pending");
    // BLOCK_FIRE_EXTINGUISH
    public static final SoundEvent BLOCK_SUMMONING_TABLE_FIZZLE = registerSound("block.familiar_magic.summoning_table.fizzle");
    // BLOCK_GLASS_BREAK
    public static final SoundEvent BLOCK_SUMMONING_TABLE_DISMISS = registerSound("block.familiar_magic.summoning_table.dismiss");

    // UI_CARTOGRAPHY_TABLE_TAKE_RESULT
    public static final SoundEvent UI_SUMMONING_TABLE_REQUEST_WRITE = registerSound("ui.familiar_magic.summoning_table.request.write");

    // BLOCK_PORTAL_AMBIENT
    public static final SoundEvent CURSE_APPLY = registerSound("entity.familiar_magic.generic.curse.apply");
    // BLOCK_END_PORTAL_SPAWN
    public static final SoundEvent CURSE_APPLY_PERSONAL = registerSound("entity.familiar_magic.generic.curse.apply.personal");
    // BLOCK_PORTAL_AMBIENT
    public static final SoundEvent CURSE_REMOVE = registerSound("entity.familiar_magic.generic.curse.remove");
    // BLOCK_ENDER_CHEST_OPEN
    public static final SoundEvent CURSE_REMOVE_PERSONAL = registerSound("entity.familiar_magic.generic.curse.remove.personal");

    // ITEM_FIRECHARGE_USE
    public static final SoundEvent CURSE_DRAGON_FIRE_BREATH = registerSound("entity.familiar_magic.generic.curse.dragon.fire_breath");

    public static void initialize() {
        // i'm not actually doing anything terribly interesting with sounds i just want subtitles to be appropriate
    }

    private static SoundEvent registerSound(String thing) {
        Identifier id = FamiliarMagic.id(thing);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
