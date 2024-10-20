package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagicClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class HatBakeryMixin {
    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation modelLocation);

    // it kinda sounds like this could be replaced with ModelEvent.RegisterAdditional but. ehhhh...
    // this works and that might not. hey u reading this, if that'd work then tell me n i'll try it
    @Inject(at = @At("TAIL"), method = "<init>")
    private void mmFreshHotHat(BlockColors blockColors,
                               ProfilerFiller profilerFiller,
                               Map<ResourceLocation, BlockModel> modelResources,
                               Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources,
                               CallbackInfo ci) {
        this.loadSpecialItemModelAndDependencies(FamiliarMagicClient.BIG_HAT_ON_HEAD_MODEL);
    }
}
