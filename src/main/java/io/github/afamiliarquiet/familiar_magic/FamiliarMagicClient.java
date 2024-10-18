package io.github.afamiliarquiet.familiar_magic;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.afamiliarquiet.familiar_magic.network.FamiliarPacketeering;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@Mod(value = "familiar_magic", dist = Dist.CLIENT)
public class FamiliarMagicClient {
    public static final ModelResourceLocation BIG_HAT_ON_HEAD_MODEL = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "big_hat_on_head"));

    public static final Lazy<KeyMapping> FOCUS_MAPPING = Lazy.of(() ->
            new KeyMapping(
                "key.familiar_magic.focus",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.familiar_magic.familiar_magic"
            )
    );

    private static final AtomicBoolean focusedLastTick = new AtomicBoolean(false);

    public FamiliarMagicClient(IEventBus modClientEventBus) {
        modClientEventBus.addListener(FamiliarMagicClient::mrwRegisterKeyMappingsEvent);
        //modClientEventBus.addListener(FamiliarMagicClient::mrwClientTickEventPost);

        focusedLastTick.set(false); // does this matter? idk, whatever
    }

    private static void mrwRegisterKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        // zoinks! a RegisterKeyMappingsEvent!
        event.register(FOCUS_MAPPING.get());
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = MOD_ID, value = Dist.CLIENT)
    public static class ArghImGoingToBlowUp {
        @SubscribeEvent
        private static void mrwClientTickEventPost(ClientTickEvent.Pre event) {
            boolean focusedNow = FOCUS_MAPPING.get().isDown();

            if (focusedNow != focusedLastTick.get()) {
                PacketDistributor.sendToServer(new FamiliarPacketeering.FocusPayload(focusedNow));

                focusedLastTick.set(focusedNow);
            }
        }
    }
}
