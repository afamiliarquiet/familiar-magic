package io.github.afamiliarquiet.familiar_magic.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SummoningRequestData(ResourceKey<Level> tableLevelKey, BlockPos tablePos, Optional<List<ItemStack>> offerings) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SummoningRequestData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Level.RESOURCE_KEY_CODEC),
            SummoningRequestData::tableLevelKey,
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            SummoningRequestData::tablePos,
            ByteBufCodecs.optional(ItemStack.OPTIONAL_LIST_STREAM_CODEC),
            SummoningRequestData::offerings,
            SummoningRequestData::new
    );

    // this should never ever be seen, only there because it's kinda required because attachments are silly
    public static final SummoningRequestData DEFAULT = new SummoningRequestData(Level.OVERWORLD, BlockPos.ZERO, Optional.empty());

    public boolean isSameRequester(@Nullable SummoningRequestData other) {
        return other != null && this.tableLevelKey.compareTo(other.tableLevelKey) == 0 && this.tablePos.equals(other.tablePos);
    }

    @Override
    public String toString() {
        return "SummoningRequestData{" +
                "tableLevelKey=" + tableLevelKey +
                ", tablePos=" + tablePos +
                ", offerings=" + offerings +
                '}';
    }
}
