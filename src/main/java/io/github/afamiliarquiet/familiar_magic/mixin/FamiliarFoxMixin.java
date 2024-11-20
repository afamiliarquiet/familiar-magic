package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.entity.ai.goal.FoxActivateSummoningTableGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Fox.class)
public abstract class FamiliarFoxMixin extends Animal {
    protected FamiliarFoxMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("TAIL"), method = "registerGoals")
    private void registerGoals(CallbackInfo ci) {
        // a slightly more relieving casting than (Fox)(Object).. i'm kinda glad this fest is on neo, i'm liking it here
        this.goalSelector.addGoal(6, new FoxActivateSummoningTableGoal((Fox)this.self(), 1, 12));
    }
}
