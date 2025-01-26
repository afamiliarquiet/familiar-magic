package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.id;

public class FamiliarComponents {
    public static final ComponentType<Boolean> SINGED_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            id("singed"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build()
    );

    public static void initialize() {

    }
}
