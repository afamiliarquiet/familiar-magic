package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.entity.ai.goal.FoxActivateSummoningTableGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FamiliarFoxMixin extends AnimalEntity {
    protected FamiliarFoxMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    private void initGoals(CallbackInfo ci) {
        this.goalSelector.add(6, new FoxActivateSummoningTableGoal(this, 1, 12));
    }
}
