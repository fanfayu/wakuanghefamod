package com.example.autominer.client.gui;

import com.example.autominer.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class OreSelectionScreen extends Screen {
    private final Screen parent;
    private final List<OreEntry> oreEntries = new ArrayList<>();
    
    private static class OreEntry {
        Block block;
        String name;
        boolean selected;
        
        OreEntry(Block block, String name) {
            this.block = block;
            this.name = name;
            this.selected = ModConfig.isTargetOre(block);
        }
    }
    
    public OreSelectionScreen(Screen parent) {
        super(Text.literal("Select Target Ores"));
        this.parent = parent;
        
        // Initialize ore entries
        oreEntries.add(new OreEntry(Blocks.DIAMOND_ORE, "Diamond Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_DIAMOND_ORE, "Deepslate Diamond Ore"));
        oreEntries.add(new OreEntry(Blocks.EMERALD_ORE, "Emerald Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_EMERALD_ORE, "Deepslate Emerald Ore"));
        oreEntries.add(new OreEntry(Blocks.GOLD_ORE, "Gold Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_GOLD_ORE, "Deepslate Gold Ore"));
        oreEntries.add(new OreEntry(Blocks.IRON_ORE, "Iron Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_IRON_ORE, "Deepslate Iron Ore"));
        oreEntries.add(new OreEntry(Blocks.LAPIS_ORE, "Lapis Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_LAPIS_ORE, "Deepslate Lapis Ore"));
        oreEntries.add(new OreEntry(Blocks.REDSTONE_ORE, "Redstone Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_REDSTONE_ORE, "Deepslate Redstone Ore"));
        oreEntries.add(new OreEntry(Blocks.COPPER_ORE, "Copper Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_COPPER_ORE, "Deepslate Copper Ore"));
        oreEntries.add(new OreEntry(Blocks.COAL_ORE, "Coal Ore"));
        oreEntries.add(new OreEntry(Blocks.DEEPSLATE_COAL_ORE, "Deepslate Coal Ore"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int leftX = this.width / 4;
        int rightX = this.width * 3 / 4;
        int y = 40;
        
        // Split ores into two columns
        int mid = oreEntries.size() / 2;
        
        // Left column
        for (int i = 0; i < mid; i++) {
            if (y >= this.height - 60) break;
            
            OreEntry entry = oreEntries.get(i);
            CheckboxWidget checkbox = CheckboxWidget.builder(
                    Text.literal(entry.name),
                    this.textRenderer
            ).pos(leftX, y).checked(entry.selected).build();
            
            int index = i;
            checkbox.setChangeListener((widget, checked) -> {
                entry.selected = checked;
                if (checked) {
                    ModConfig.addTargetOre(entry.block);
                } else {
                    ModConfig.removeTargetOre(entry.block);
                }
            });
            
            this.addDrawableChild(checkbox);
            y += 20;
        }
        
        // Right column
        y = 40;
        for (int i = mid; i < oreEntries.size(); i++) {
            if (y >= this.height - 60) break;
            
            OreEntry entry = oreEntries.get(i);
            CheckboxWidget checkbox = CheckboxWidget.builder(
                    Text.literal(entry.name),
                    this.textRenderer
            ).pos(rightX, y).checked(entry.selected).build();
            
            int index = i;
            checkbox.setChangeListener((widget, checked) -> {
                entry.selected = checked;
                if (checked) {
                    ModConfig.addTargetOre(entry.block);
                } else {
                    ModConfig.removeTargetOre(entry.block);
                }
            });
            
            this.addDrawableChild(checkbox);
            y += 20;
        }
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> client.setScreen(parent)
        ).dimensions(this.width / 2 - 50, this.height - 40, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
