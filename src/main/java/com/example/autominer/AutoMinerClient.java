package com.example.autominer;

import com.example.autominer.client.gui.AutoMinerSettingsScreen;
import com.example.autominer.config.ModConfig;
import com.example.autominer.mining.MiningManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AutoMinerClient implements ClientModInitializer {
    public static KeyBinding openSettingsKey;
    public static KeyBinding toggleMiningKey;
    private static int jumpCount = 0;
    private static long lastJumpTime = 0;
    
    @Override
    public void onInitializeClient() {
        // Register key bindings
        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autominer.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.autominer.general"
        ));
        
        toggleMiningKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autominer.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.autominer.general"
        ));
        
        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            // Handle key presses
            while (openSettingsKey.wasPressed()) {
                client.setScreen(new AutoMinerSettingsScreen(client.currentScreen));
            }
            
            while (toggleMiningKey.wasPressed()) {
                ModConfig.toggleRunning();
            }
            
            // Handle double jump to fly
            if (client.options.jumpKey.isPressed()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastJumpTime < 250) { // 250ms window for double tap
                    jumpCount++;
                    if (jumpCount >= 2 && ModConfig.enableFlying) {
                        // Double jump detected, toggle flying
                        if (!client.player.getAbilities().flying) {
                            client.player.getAbilities().flying = true;
                        }
                        jumpCount = 0;
                    }
                } else {
                    jumpCount = 1;
                }
                lastJumpTime = currentTime;
            }
            
            // Run mining logic
            MiningManager.tick();
        });
    }
}
