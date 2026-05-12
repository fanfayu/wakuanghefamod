package com.example.autominer.client.gui;

import com.example.autominer.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@Environment(EnvType.CLIENT)
public class AutoMinerSettingsScreen extends Screen {
    private final Screen parent;
    
    public AutoMinerSettingsScreen(Screen parent) {
        super(Text.literal("Auto Miner Settings"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int y = 40;
        
        // Start/Pause button
        ButtonWidget startButton = ButtonWidget.builder(
                Text.literal(ModConfig.isRunning ? (ModConfig.isPaused ? "Resume" : "Pause") : "Start Mining"),
                button -> {
                    if (!ModConfig.isRunning) {
                        ModConfig.isRunning = true;
                        ModConfig.isPaused = false;
                    } else {
                        ModConfig.togglePause();
                    }
                    close();
                }
        ).dimensions(centerX - 100, y, 200, 20).build();
        this.addDrawableChild(startButton);
        
        y += 30;
        
        // Stop button
        ButtonWidget stopButton = ButtonWidget.builder(
                Text.literal("Stop Mining"),
                button -> {
                    ModConfig.isRunning = false;
                    ModConfig.isPaused = false;
                    close();
                }
        ).dimensions(centerX - 100, y, 200, 20).build();
        this.addDrawableChild(stopButton);
        
        y += 30;
        
        // Enable flying checkbox
        CheckboxWidget flyCheckbox = CheckboxWidget.builder(
                Text.literal("Enable Auto Flying"),
                this.textRenderer
        ).pos(centerX - 100, y).checked(ModConfig.enableFlying).build();
        flyCheckbox.setChangeListener((widget, checked) -> ModConfig.enableFlying = checked);
        this.addDrawableChild(flyCheckbox);
        
        y += 25;
        
        // Enable block placing checkbox
        CheckboxWidget placeCheckbox = CheckboxWidget.builder(
                Text.literal("Enable Block Placing"),
                this.textRenderer
        ).pos(centerX - 100, y).checked(ModConfig.enableBlockPlacing).build();
        placeCheckbox.setChangeListener((widget, checked) -> ModConfig.enableBlockPlacing = checked);
        this.addDrawableChild(placeCheckbox);
        
        y += 40;
        
        // Ore selection section
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Configure Target Ores"),
                button -> {
                    // Open ore selection sub-screen
                    client.setScreen(new OreSelectionScreen(this));
                }
        ).dimensions(centerX - 100, y, 200, 20).build());
        
        y += 30;
        
        // Done button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> close()
        ).dimensions(centerX - 100, this.height - 40, 200, 20).build());
    }
    
    private void close() {
        client.setScreen(parent);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
