package io.github.afamiliarquiet.familiar_magic.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SingedComponentRecord(boolean singed) {
    public static final Codec<SingedComponentRecord> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("singed").forGetter(SingedComponentRecord::singed)
            ).apply(instance, SingedComponentRecord::new)
    );

    public static final StreamCodec<ByteBuf, SingedComponentRecord> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SingedComponentRecord::singed,
            SingedComponentRecord::new
    );
}
