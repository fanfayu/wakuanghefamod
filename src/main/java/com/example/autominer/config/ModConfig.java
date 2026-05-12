package com.example.autominer.config;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import java.util.HashSet;
import java.util.Set;

public class ModConfig {
    public static boolean isRunning = false;
    public static boolean isPaused = false;
    public static boolean enableFlying = true;
    public static boolean enableBlockPlacing = true;
    public static int minPickaxeDurability = 20;
    public static int searchRadius = 64;
    public static int visionRange = 16;
    
    public static Set<Block> targetOres = new HashSet<>();
    
    static {
        // Default target ores
        targetOres.add(Blocks.DIAMOND_ORE);
        targetOres.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        targetOres.add(Blocks.EMERALD_ORE);
        targetOres.add(Blocks.DEEPSLATE_EMERALD_ORE);
        targetOres.add(Blocks.GOLD_ORE);
        targetOres.add(Blocks.DEEPSLATE_GOLD_ORE);
        targetOres.add(Blocks.IRON_ORE);
        targetOres.add(Blocks.DEEPSLATE_IRON_ORE);
        targetOres.add(Blocks.LAPIS_ORE);
        targetOres.add(Blocks.DEEPSLATE_LAPIS_ORE);
        targetOres.add(Blocks.REDSTONE_ORE);
        targetOres.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        targetOres.add(Blocks.COPPER_ORE);
        targetOres.add(Blocks.DEEPSLATE_COPPER_ORE);
        targetOres.add(Blocks.COAL_ORE);
        targetOres.add(Blocks.DEEPSLATE_COAL_ORE);
    }
    
    public static void toggleRunning() {
        isRunning = !isRunning;
        if (!isRunning) {
            isPaused = false;
        }
    }
    
    public static void togglePause() {
        isPaused = !isPaused;
    }
    
    public static boolean isTargetOre(Block block) {
        return targetOres.contains(block);
    }
    
    public static void addTargetOre(Block block) {
        targetOres.add(block);
    }
    
    public static void removeTargetOre(Block block) {
        targetOres.remove(block);
    }
    
    public static void clearTargetOres() {
        targetOres.clear();
    }
}
