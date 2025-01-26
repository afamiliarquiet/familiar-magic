package io.github.afamiliarquiet.familiar_magic;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarClientAttachments;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import io.github.afamiliarquiet.familiar_magic.data.SummoningRequestData;
import io.github.afamiliarquiet.familiar_magic.gooey.FocusRenderLayer;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.network.FamiliarClientPacketeering;
import io.github.afamiliarquiet.familiar_magic.network.SillySummoningRequestLuggage;
import io.github.afamiliarquiet.familiar_magic.gooey.FamiliarClientScreenery;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class FamiliarMagicClient implements ClientModInitializer {
	public static final Identifier FOCUS_OVERLAY = FamiliarMagic.id("textures/misc/focus.png");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		FamiliarClientPacketeering.initialize();
		FamiliarClientAttachments.initialize();
		FamiliarClientScreenery.initialize();
		FamiliarKeybinds.initialize();
		initializeModelPredicates();

		ClientTickEvents.END_CLIENT_TICK.register(FamiliarMagicClient::clientTickEnd);
	}

	public static void clientTickEnd(MinecraftClient client) {
		boolean focusedNow = FamiliarKeybinds.updateFocus(client);

		if (focusedNow) {
			focusedClientTickEnd(client);
		}
	}

	public static void focusedClientTickEnd(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			return;
		}

		Random random = player.getWorld().getRandom();
		for (int i = 0; i < 6; i++) {
			player.getWorld().addParticle(
					ParticleTypes.WITCH,
					player.getX() + 9 * random.nextTriangular(0, 2),
					player.getY() + 6 * random.nextTriangular(0, 2),
					player.getZ() + 9 * random.nextTriangular(0, 2),
					0, 0, 0
			);
		}

		SummoningRequestData request = FamiliarAttachments.getRequest(player);
		if (request != null) {
			GameOptions options = client.options;
			if (options.sneakKey.isPressed()) {
				ClientPlayNetworking.send(new SillySummoningRequestLuggage(request, false));
			} else if (options.jumpKey.isPressed()) {
				ClientPlayNetworking.send(new SillySummoningRequestLuggage(request, true));
			}
		}
	}

	public static void initializeModelPredicates() {
		// here's another thing neo wanted to worry about threads for. that sounds like a neo thing? find out!
		ModelPredicateProviderRegistry.register(
				FamiliarItems.TRUE_NAME,
				FamiliarMagic.id("singed"),
				(stack, world, entity, seed) -> {
					Boolean singed = stack.get(FamiliarComponents.SINGED_COMPONENT);
					return singed != null && singed ? 1.0f : 0.0f;
				}
		);
	}
}