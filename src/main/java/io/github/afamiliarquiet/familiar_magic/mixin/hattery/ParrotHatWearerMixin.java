package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Parrot.class)
public class ParrotHatWearerMixin implements HatWearer {
}
