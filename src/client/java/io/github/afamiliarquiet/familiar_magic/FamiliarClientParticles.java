package io.github.afamiliarquiet.familiar_magic;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.FlameParticle;

public class FamiliarClientParticles {
    public static void initialize() {
        ParticleFactoryRegistry partyTime = ParticleFactoryRegistry.getInstance();
        partyTime.register(FamiliarParticles.ENCHANTED_FLAME, FlameParticle.Factory::new);
        partyTime.register(FamiliarParticles.SMALL_ENCHANTED_FLAME, FlameParticle.SmallFactory::new);
    }
}
