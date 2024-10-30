package io.github.afamiliarquiet.familiar_magic;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.afamiliarquiet.familiar_magic.client.*;
import io.github.afamiliarquiet.familiar_magic.network.FocusPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@Mod(value = "familiar_magic", dist = Dist.CLIENT)
public class FamiliarMagicClient {
    public static final ModelResourceLocation BIG_HAT_ON_HEAD_MODEL = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "big_hat_on_head"));
    public static final ResourceLocation FOCUS_OVERLAY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/misc/focus.png");
    public static final ResourceLocation FOCUS_LAYER = ResourceLocation.fromNamespaceAndPath(MOD_ID, "focus");
    public static final ResourceLocation SUMMONING_REQUEST_LAYER = ResourceLocation.fromNamespaceAndPath(MOD_ID, "summoning_request");

    public static final Lazy<KeyMapping> FOCUS_MAPPING = Lazy.of(() ->
            new KeyMapping(
                "key.familiar_magic.focus",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.familiar_magic.familiar_magic"
            )
    );

    public static final Lazy<KeyMapping> FOCUS_TOGGLE_MAPPING = Lazy.of(() ->
            new KeyMapping(
                    "key.familiar_magic.focus_toggle",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    InputConstants.UNKNOWN.getValue(),
                    "category.familiar_magic.familiar_magic"
            )
    );

    public static final AtomicBoolean FOCUSED_LAST_TICK = new AtomicBoolean(false);
    public static final AtomicBoolean FOCUS_HELD_LAST_TICK = new AtomicBoolean(false);

    public FamiliarMagicClient(IEventBus modClientEventBus) {
        modClientEventBus.addListener(FamiliarMagicClient::mrwRegisterKeyMappingsEvent);
        modClientEventBus.addListener(FamiliarMagicClient::mrwRegisterGuiLayersEvent);
//        modClientEventBus.addListener(FamiliarMagicClient::mrwEntityRenderersEventAddLayers);

        FOCUS_HELD_LAST_TICK.set(false); // does this matter? idk, whatever
        // i really gotta figure out uhh leaving a server or something to reset summoning stuff. wurgh.
    }

    private static void mrwRegisterKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        // zoinks! a RegisterKeyMappingsEvent!
        event.register(FOCUS_MAPPING.get());
        event.register(FOCUS_TOGGLE_MAPPING.get());
    }

    private static void mrwRegisterGuiLayersEvent(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, FOCUS_LAYER, new FocusRenderLayer());
        event.registerBelow(VanillaGuiLayers.EFFECTS, SUMMONING_REQUEST_LAYER, new SummoningRequestLayer());
    }

    // i'd love to take the events route instead of mixin spam but it gets mad at the casting so idk how to add layers
//    private static void mrwEntityRenderersEventAddLayers(EntityRenderersEvent.AddLayers event) {
//        ItemInHandRenderer handyHattyRenderer = event.getContext().getItemInHandRenderer();
//
//        EntityRenderer<Cat> catEntityRenderer = event.getRenderer(EntityType.CAT);
//        if (catEntityRenderer instanceof LivingEntityRenderer<Cat, CatModel<Cat>> catRenderer) {
//            catRenderer.addLayer(new CatHatLayer(catRenderer, handyHattyRenderer));
//        }
//
//        EntityRenderer<Fox> foxEntityRenderer = event.getRenderer(EntityType.FOX);
//        if (foxEntityRenderer instanceof LivingEntityRenderer<Fox, FoxModel<Fox>> foxRenderer) {
//            foxRenderer.addLayer(new FoxHatLayer(foxRenderer, handyHattyRenderer));
//        }
//
//        EntityRenderer<Frog> frogEntityRenderer = event.getRenderer(EntityType.FROG);
//        if (frogEntityRenderer instanceof LivingEntityRenderer<Frog, FrogModel<Frog>> frogRenderer) {
//            frogRenderer.addLayer(new FrogHatLayer(frogRenderer, handyHattyRenderer));
//        }
//
//        EntityRenderer<Parrot> parrotEntityRenderer = event.getRenderer(EntityType.PARROT);
//        if (parrotEntityRenderer instanceof LivingEntityRenderer<Parrot, ParrotModel> parrotRenderer) {
//            parrotRenderer.addLayer(new ParrotHatLayer(parrotRenderer, handyHattyRenderer));
//        }
//
//        EntityRenderer<Silverfish> silverfishEntityRenderer = event.getRenderer(EntityType.SILVERFISH);
//        if (silverfishEntityRenderer instanceof LivingEntityRenderer<Silverfish, SilverfishModel<Silverfish>> silverfishRenderer) {
//            silverfishRenderer.addLayer(new SilverfishHatLayer(silverfishRenderer, handyHattyRenderer));
//        }
//
//        EntityRenderer<Wolf> wolfEntityRenderer = event.getRenderer(EntityType.WOLF);
//        if (wolfEntityRenderer instanceof LivingEntityRenderer<Wolf, WolfModel<Wolf>> wolfRenderer) {
//            wolfRenderer.addLayer(new WolfHatLayer(wolfRenderer, handyHattyRenderer));
//        }
//    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = MOD_ID, value = Dist.CLIENT)
    public static class ArghImGoingToBlowUp {
        @SubscribeEvent
        private static void mrwClientTickEventPost(ClientTickEvent.Pre event) {
            boolean shouldUpdate = false;
            boolean focusedNow = FOCUSED_LAST_TICK.get();
            boolean buttonHeldNow = FOCUS_MAPPING.get().isDown();

            // process the togglizer (may be out of date, so less precedence)
            while (FOCUS_TOGGLE_MAPPING.get().consumeClick()) {
                shouldUpdate = true;
                focusedNow = !focusedNow;
            }

            // process the pressulator (should be the latest news. it gets final say)
            if (buttonHeldNow != FOCUS_HELD_LAST_TICK.get()) {
                if (buttonHeldNow != FOCUSED_LAST_TICK.get()) {
                    shouldUpdate = true;
                    focusedNow = buttonHeldNow;
                }

                FOCUS_HELD_LAST_TICK.set(buttonHeldNow);
            }

            if (shouldUpdate) {
                FOCUSED_LAST_TICK.set(focusedNow);
                PacketDistributor.sendToServer(new FocusPayload(focusedNow));
            }
        }

        @SubscribeEvent
        private static void mrwPlayerEventClone(PlayerEvent.Clone event) {
            if (event.isWasDeath() && FOCUSED_LAST_TICK.get()) {
                FOCUSED_LAST_TICK.set(false);
                // don't need to send update packet because it will have reset on its own
            }
        }
    }
}
