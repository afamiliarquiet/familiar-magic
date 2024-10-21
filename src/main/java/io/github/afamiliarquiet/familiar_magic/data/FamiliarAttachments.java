package io.github.afamiliarquiet.familiar_magic.data;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarAttachments {
    // my file structure is.. utterly unclouded

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    // no serialization because focus shouldn't be a persistent thing
    public static final Supplier<AttachmentType<Boolean>> FOCUSED = ATTACHMENT_TYPES.register(
            "focused", () -> AttachmentType.builder(() -> false).build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
