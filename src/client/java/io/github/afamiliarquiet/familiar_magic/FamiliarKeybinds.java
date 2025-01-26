package io.github.afamiliarquiet.familiar_magic;

import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarClientAttachments;
import io.github.afamiliarquiet.familiar_magic.network.C2SFocusLuggage;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class FamiliarKeybinds {
    public static final KeyBinding FOCUS_HOLD = new KeyBinding(
            "key.familiar_magic.focus_hold",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.familiar_magic.familiar_magic"
    );

    public static final KeyBinding FOCUS_TOGGLE = new KeyBinding(
            "key.familiar_magic.focus_toggle",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.familiar_magic.familiar_magic"
    );

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(FOCUS_HOLD);
        KeyBindingHelper.registerKeyBinding(FOCUS_TOGGLE);
    }

    public static boolean updateFocus(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return false;
        }

        boolean shouldUpdate = false;
        boolean focusedNow = FamiliarAttachments.isFocused(player);
        boolean buttonHeldNow = FOCUS_HOLD.isPressed();

        while (FOCUS_TOGGLE.wasPressed()) {
            shouldUpdate = true;
            focusedNow = !focusedNow;
        }

        if (buttonHeldNow != FamiliarClientAttachments.isFocusKeyHeld(player)) {
            if (buttonHeldNow != focusedNow) {
                shouldUpdate = true;
                focusedNow = buttonHeldNow;
            }

            FamiliarClientAttachments.setFocusKeyHeld(player, buttonHeldNow);
        }

        if (shouldUpdate) {
            FamiliarAttachments.setFocused(player, focusedNow);
            // send packet? or.. do i? what if i just... Sync it with fabric attachment?
            // fabric attachment sync only goes from server -> client(s), gotta packit
            ClientPlayNetworking.send(new C2SFocusLuggage(focusedNow));
        }

        return focusedNow;
    }
}
