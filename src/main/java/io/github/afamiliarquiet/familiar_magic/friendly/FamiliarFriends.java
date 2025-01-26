package io.github.afamiliarquiet.familiar_magic.friendly;

import net.fabricmc.loader.api.FabricLoader;

public class FamiliarFriends {
    public static void initialize() {
        if (FabricLoader.getInstance().isModLoaded("fbombs")) {
            FbombsGifts.initialize();
        }
    }
}
