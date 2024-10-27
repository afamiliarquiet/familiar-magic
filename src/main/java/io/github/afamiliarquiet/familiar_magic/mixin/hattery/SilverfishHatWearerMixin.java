package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import net.minecraft.world.entity.monster.Silverfish;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Silverfish.class)
public class SilverfishHatWearerMixin implements HatWearer {
    // the hat will totally just disappear if the fish goes into a block. that's a later todo
}
