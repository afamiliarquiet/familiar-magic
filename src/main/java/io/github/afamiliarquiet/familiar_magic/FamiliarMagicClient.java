package io.github.afamiliarquiet.familiar_magic;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@Mod(value = "familiar_magic", dist = Dist.CLIENT)
public class FamiliarMagicClient {
    public static final ModelResourceLocation BIG_HAT_ON_HEAD_MODEL = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "big_hat_on_head"));
    public FamiliarMagicClient(IEventBus modBus) {

    }
}
