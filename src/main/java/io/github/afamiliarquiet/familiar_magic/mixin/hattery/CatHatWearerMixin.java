package io.github.afamiliarquiet.familiar_magic.mixin.hattery;

import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Cat.class)
public class CatHatWearerMixin implements HatWearer {
}
