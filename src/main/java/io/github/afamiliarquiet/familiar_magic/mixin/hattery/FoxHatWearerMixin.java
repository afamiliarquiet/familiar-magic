package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Fox.class)
public class FoxHatWearerMixin implements HatWearer {
}
