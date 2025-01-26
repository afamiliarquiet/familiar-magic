package io.github.afamiliarquiet.familiar_magic.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class FamiliarCommands {
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            PlaceCandlesCommand.register(commandDispatcher);
        }));
    }
}
