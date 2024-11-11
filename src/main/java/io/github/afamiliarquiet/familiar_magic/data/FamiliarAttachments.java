package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarAttachments {
    // my file structure is.. utterly unclouded

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    // no serialization because focus shouldn't be a persistent thing - used on both client n server, but only c2s
    public static final Supplier<AttachmentType<Boolean>> FOCUSED = ATTACHMENT_TYPES.register(
            "focused", () -> AttachmentType.builder(() -> false).build()
    );
    // this one very client only don't worry about it. this is my solution to focus persisting when you disconnect from a server
    // now that i think about it i should maybe look into registering these things only on client....... later problem
    public static final Supplier<AttachmentType<Boolean>> FOCUS_KEY_HELD = ATTACHMENT_TYPES.register(
            "focus_held", () -> AttachmentType.builder(() -> false).build()
    );

    public static final Supplier<AttachmentType<ItemStackHandler>> HAT = ATTACHMENT_TYPES.register(
            "hat", () -> AttachmentType.serializable(() -> new ItemStackHandler(1)).build()
    );

    // also not persistent - client only - these defaults shouldn't ever really be seen. if they are uhh.. report bug <3
    // todo - make these not disappear when changing dimension, and make table cancel when target disconnects mayyybe.. maybe not because then i'd have to store on animals too and cancel when they unload and that sounds unnecessary but maybe later
    // i feel like the serialization would help with that but. for now, not a big issue. bugfix maybe
    public static final Supplier<AttachmentType<SummoningRequestData>> SUMMONING_REQUEST = ATTACHMENT_TYPES.register(
            "summoning_request", () -> AttachmentType.builder(() -> SummoningRequestData.DEFAULT).build()
    );

    public static final Supplier<AttachmentType<Boolean>> WILLING_FAMILIAR = ATTACHMENT_TYPES.register(
            "willing_familiar", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
