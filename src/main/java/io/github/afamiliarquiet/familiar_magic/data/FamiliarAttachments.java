package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.id;

@SuppressWarnings({"UnstableApiUsage", "UnusedReturnValue"})
// haha don't worry this is SOOO stable. don't mind the ravine below us lol :clueless:
public class FamiliarAttachments {
    // scritchy scratching my evil little marks onto things

    public static final AttachmentType<Boolean> FOCUSED = AttachmentRegistry.create(
            id("focused"), (builder) -> builder
                    .initializer(() -> false)
                    //.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly())
    );

    @SuppressWarnings("Convert2MethodRef") // shush your face intellij i do what i want and you stay out of my way
    public static final AttachmentType<SummoningRequestData> SUMMONING_REQUEST = AttachmentRegistry.create(
            id("summoning_request"), (builder) -> builder
                    //.syncWith(SummoningRequestData.PACKET_CODEC, AttachmentSyncPredicate.targetOnly())
                    .copyOnDeath() // will this help with dim change? idk! find out
    );

    public static final AttachmentType<ItemStack> HAT = AttachmentRegistry.create(
            id("hat"), (builder) -> builder
                    .initializer(() -> ItemStack.EMPTY)
                    .persistent(ItemStack.OPTIONAL_CODEC)
                    .syncWith(ItemStack.OPTIONAL_PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<Boolean> WILLING_FAMILIAR = AttachmentRegistry.create(
            id("willing_familiar"), (builder) -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
    );

    public static void initialize() {

    }


    // i'm doing this to try to make things less annoying for meself in the future if i decide to try neo again
    public static boolean isFocused(@Nullable Entity entity) {
        return entity != null && entity.getAttachedOrCreate(FOCUSED);
    }

    public static void setFocused(@NotNull Entity entity, boolean focused) {
        entity.setAttached(FOCUSED, focused);
    }

    public static boolean isHattable(@Nullable Entity entity) {
        return entity != null && entity.getType().isIn(FamiliarTags.HATTABLE);
    }

    public static boolean hasRequest(@Nullable PlayerEntity player) {
        return player != null && player.hasAttached(SUMMONING_REQUEST);
    }

    public static @Nullable SummoningRequestData getRequest(@Nullable PlayerEntity player) {
        return !hasRequest(player) ? null : player.getAttached(SUMMONING_REQUEST);
    }

    public static @Nullable SummoningRequestData removeRequest(@Nullable PlayerEntity player) {
        return player == null ? null : player.removeAttached(SUMMONING_REQUEST);
    }

    public static void setRequest(@Nullable PlayerEntity player, SummoningRequestData request) {
        if (player != null) {
            player.setAttached(SUMMONING_REQUEST, request);
        }
    }

    public static boolean hasHat(@Nullable Entity entity) {
        return entity != null && entity.hasAttached(HAT) && !getHat(entity).isEmpty();
    }

    public static @NotNull ItemStack getHat(@NotNull Entity entity) {
        return entity.getAttachedOrCreate(HAT) ;
    }

    public static @NotNull ItemStack removeHat(@NotNull Entity entity) {
        ItemStack hat = getHat(entity);
        entity.removeAttached(HAT);
        return hat;
    }

    public static void setHat(@NotNull Entity entity, @Nullable ItemStack hat) {
        // null hat will just be equivalent to removeHat :shrug:
        entity.setAttached(HAT, hat);
    }

    public static boolean isWillingFamiliar(@Nullable Entity target) {
        return !FamiliarMagic.CONFIG.useWillingTag || target != null && target.getAttachedOrCreate(WILLING_FAMILIAR);
    }
}
