package io.github.afamiliarquiet.familiar_magic;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FamiliarParticles {
    public static final SimpleParticleType ENCHANTED_FLAME = simply("enchanted_flame");

    public static void initialize() {
        // hi i decided i want my purple
    }

    public static SimpleParticleType simply(String thing) {
        return Registry.register(Registries.PARTICLE_TYPE, FamiliarMagic.id(thing), FabricParticleTypes.simple());
    }
}
