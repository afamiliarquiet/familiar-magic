package io.github.afamiliarquiet.familiar_magic.mixin.curse.another_of_many;

import io.github.afamiliarquiet.familiar_magic.data.FoxthingLimbAnimator;
import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// thank you evelyn. thats right this is stolen, except for that variable name
@Mixin(LimbAnimator.class)
public class FoxthingLimbAnimatorMixin implements FoxthingLimbAnimator {
    @Shadow private float prevSpeed;

    @Shadow private float speed;

    @Shadow private float pos;

    @Override
    public void familiar_magic$copyFrom(LimbAnimator animator) {
        var sorryEvelynIRefuseThatName = (FoxthingLimbAnimator) animator;
        prevSpeed = sorryEvelynIRefuseThatName.familiar_magic$getPrevSpeed();
        speed = sorryEvelynIRefuseThatName.familiar_magic$getSpeed();
        pos = sorryEvelynIRefuseThatName.familiar_magic$getPos();
    }

    @Override
    public float familiar_magic$getPrevSpeed() {
        return prevSpeed;
    }

    @Override
    public float familiar_magic$getSpeed() {
        return speed;
    }

    @Override
    public float familiar_magic$getPos() {
        return pos;
    }
}
