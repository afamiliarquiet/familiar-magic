package io.github.afamiliarquiet.familiar_magic;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.afamiliarquiet.familiar_magic.client.gooey.FocusRenderLayer;
import io.github.afamiliarquiet.familiar_magic.client.gooey.SummoningRequestLayer;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.item.SingedComponentRecord;
import io.github.afamiliarquiet.familiar_magic.network.FocusPayload;
import io.github.afamiliarquiet.familiar_magic.network.SummoningResponsePayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

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

    public FamiliarMagicClient(IEventBus modClientEventBus) {
        modClientEventBus.addListener(FamiliarMagicClient::mrwClientSetupEvent);
        modClientEventBus.addListener(FamiliarMagicClient::mrwRegisterKeyMappingsEvent);
        modClientEventBus.addListener(FamiliarMagicClient::mrwRegisterGuiLayersEvent);
//        modClientEventBus.addListener(FamiliarMagicClient::mrwEntityRenderersEventAddLayers);
    }

    private static void mrwClientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                FamiliarItems.TRUE_NAME_ITEM.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "singed"),
                (stack, level, entity, seed) -> {
                    SingedComponentRecord component = stack.get(FamiliarItems.SINGED_COMPONENT);
                    return component != null && component.singed() ? 1.0f : 0.0f;
                }
        ));
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
            Minecraft minceraft = Minecraft.getInstance();
            LocalPlayer player = minceraft.player;
            Options options = minceraft.options;
            if (player == null) {
                // this shouldn't happen don't do this to me please
                return;
            }

            boolean shouldUpdate = false;
            boolean focusedNow = player.getData(FamiliarAttachments.FOCUSED);
            boolean buttonHeldNow = FOCUS_MAPPING.get().isDown();

            // process the togglizer (may be out of date, so less precedence)
            while (FOCUS_TOGGLE_MAPPING.get().consumeClick()) {
                shouldUpdate = true;
                focusedNow = !focusedNow;
            }

            // process the pressulator (should be the latest news. it gets final say)
            if (buttonHeldNow != player.getData(FamiliarAttachments.FOCUS_KEY_HELD)) {
                if (buttonHeldNow != focusedNow) {
                    shouldUpdate = true;
                    focusedNow = buttonHeldNow;
                }

                player.setData(FamiliarAttachments.FOCUS_KEY_HELD, buttonHeldNow);
            }

            // accept/reject summoning (a little bit weird to snag option keys isDown but..)
            if (focusedNow && player.hasData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION)) {
                if (options.keyShift.isDown()) {
                    sendReply(player, false);
                } else if (options.keyJump.isDown()) {
                    sendReply(player, true);
                }
            }

            if (shouldUpdate) {
                player.setData(FamiliarAttachments.FOCUSED, focusedNow);
                PacketDistributor.sendToServer(new FocusPayload(focusedNow));
            }
        }

        public static void sendReply(LocalPlayer player, boolean accepted) {
            PacketDistributor.sendToServer(new SummoningResponsePayload(player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION), accepted));
        }

//        @SubscribeEvent
//        private static void mrwLivingEventLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
//            // i feel like i shouldn't need to look at every single entity jump event but whatever, if it works it works
//            if (event.getEntity() instanceof LocalPlayer player) {
//                if (player.getData(FamiliarAttachments.FOCUSED) && player.hasData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION)) {
//                    PacketDistributor.sendToServer(new SummoningResponsePayload(player.getData(FamiliarAttachments.FAMILIAR_SUMMONING_DESTINATION)));
//                }
//            }
//        }
    }
}
