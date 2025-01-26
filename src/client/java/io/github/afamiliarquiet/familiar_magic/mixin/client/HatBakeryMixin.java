package io.github.afamiliarquiet.familiar_magic.mixin.client;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class HatBakeryMixin {
    @Shadow
    protected abstract void loadItemModel(ModelIdentifier id);

    // woe
    @Inject(at = @At("TAIL"), method = "<init>")
    private void mmFreshHotHat(
            BlockColors blockColors,
            Profiler profiler,
            Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
            Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates,
            CallbackInfo ci
    ) {
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(FamiliarMagic.id("big_hat_worn")));
        if (FabricLoader.getInstance().isModLoaded("fbombs")) {
            this.loadItemModel(ModelIdentifier.ofInventoryVariant(FamiliarMagic.id("megumins_hat_worn")));
        }
    }
}
