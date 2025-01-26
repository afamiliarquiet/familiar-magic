package io.github.afamiliarquiet.familiar_magic;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.command.FamiliarCommands;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarTags;
import io.github.afamiliarquiet.familiar_magic.friendly.FamiliarFriends;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import io.github.afamiliarquiet.familiar_magic.network.FamiliarPacketeering;
import io.github.afamiliarquiet.familiar_magic.gooey.FamiliarScreenery;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FamiliarMagic implements ModInitializer {
	public static final String MOD_ID = "familiar_magic";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FamiliarConfig CONFIG = FamiliarConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", "familiar_magic", FamiliarConfig.class);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// t ime to surf line length wahooooo catch the wave! (it getse better the more i inflate my FamiliarThings)
		FamiliarPacketeering.initialize();
		FamiliarAttachments.initialize();
		FamiliarComponents.initialize();
		FamiliarScreenery.initialize();
		FamiliarCommands.initialize();
		FamiliarFriends.initialize();
		FamiliarBlocks.initialize();
		FamiliarItems.initialize();
		FamiliarTags.initialize();
	}

	public static Identifier id(String thing) {
		return Identifier.of(MOD_ID, thing);
	}
}