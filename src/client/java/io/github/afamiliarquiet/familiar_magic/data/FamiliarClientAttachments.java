package io.github.afamiliarquiet.familiar_magic.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.id;

@SuppressWarnings("UnstableApiUsage")
public class FamiliarClientAttachments {
    public static final AttachmentType<Boolean> FOCUS_KEY_HELD = AttachmentRegistry.createDefaulted(
            id("focus_held"), () -> false
    );

    public static void initialize() {

    }

    public static boolean isFocusKeyHeld(@Nullable Entity entity) {
        return entity != null && entity.getAttachedOrCreate(FOCUS_KEY_HELD);
    }

    public static void setFocusKeyHeld(@NotNull Entity entity, boolean held) {
        entity.setAttached(FOCUS_KEY_HELD, held);
    }
}
