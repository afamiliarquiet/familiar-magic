package io.github.afamiliarquiet.familiar_magic;

import com.mojang.logging.LogUtils;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.client.gooey.FamiliarGUIStuffs;
import io.github.afamiliarquiet.familiar_magic.command.PlaceCandlesCommand;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.HatWearer;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.network.FamiliarPacketeering;
import io.github.afamiliarquiet.familiar_magic.network.HattedPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import static io.github.afamiliarquiet.familiar_magic.FamiliarTricks.getHat;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FamiliarMagic.MOD_ID)
public class FamiliarMagic {
    public static final String MOD_ID = "familiar_magic";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FamiliarMagic(IEventBus modEventBus, ModContainer modContainer) {
        // woah!! i just mined a registrite ore and got a ton of registries!
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(FamiliarPacketeering::mrwRegisterPayloadHandlersEvent);

        FamiliarItems.register(modEventBus);
        FamiliarBlocks.register(modEventBus);
        FamiliarGUIStuffs.register(modEventBus);
        FamiliarAttachments.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = MOD_ID)
    public static class ImReallyGonnaDoItImGonnaExplode {
        // i should probably. clean up all these events n stuff instead of having all this crap around
        // but .. i also don't really care right now that's a later problem for post-fest me

        @SubscribeEvent
        private static void mrwPlayerEventStartTracking(PlayerEvent.StartTracking event) {
            Entity theEntity = event.getTarget();
            ItemStack hat = getHat(theEntity);
            if (theEntity instanceof HatWearer && !hat.isEmpty()) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    // idk why this ever wouldn't be the case.. whatever
                    PacketDistributor.sendToPlayer(player, new HattedPayload(hat, theEntity.getId()));
                }
            }
        }

        @SubscribeEvent
        private static void mrwLivingDropsEvent(LivingDropsEvent event) {
            Entity theEntity = event.getEntity();
            ItemStack hat = getHat(theEntity);
            if (theEntity instanceof HatWearer && !hat.isEmpty()) {
                theEntity.spawnAtLocation(hat);
                hat.shrink(1);
            }
        }

        @SubscribeEvent
        private static void mrwRegisterCommandsEvent(RegisterCommandsEvent event) {
            PlaceCandlesCommand.register(event.getDispatcher());
        }
    }
}
