package io.github.afamiliarquiet.familiar_magic.data;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SummoningRequestData(RegistryKey<World> tableWorldKey, BlockPos tablePos, Optional<List<ItemStack>> offerings) {
    public static final PacketCodec<RegistryByteBuf, SummoningRequestData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(World.CODEC),
            SummoningRequestData::tableWorldKey,
            BlockPos.PACKET_CODEC,
            SummoningRequestData::tablePos,
            PacketCodecs.optional(ItemStack.OPTIONAL_LIST_PACKET_CODEC),
            SummoningRequestData::offerings,
            SummoningRequestData::new
    );

    public boolean isSameRequester(@Nullable SummoningRequestData other) {
        return other == null
                || this.tableWorldKey.getRegistry().compareTo(other.tableWorldKey.getRegistry()) == 0
                && this.tableWorldKey.getValue().compareTo(other.tableWorldKey.getValue()) == 0
                && this.tablePos.equals(other.tablePos);
    }

    @Override
    public String toString() {
        return "SummoningRequestData{" +
                "tableWorldKey=" + tableWorldKey +
                ", tablePos=" + tablePos +
                ", offerings=" + offerings +
                '}';
    }
}
