package com.example.autominer.mining;

import com.example.autominer.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MiningManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static BlockPos currentTarget = null;
    private static int jumpTimer = 0;
    private static boolean wasOnGround = false;
    
    public static void tick() {
        if (!ModConfig.isRunning || ModConfig.isPaused || client.player == null || client.world == null) {
            return;
        }
        
        PlayerEntity player = client.player;
        World world = client.world;
        
        // Check pickaxe durability first
        ItemStack pickaxe = player.getInventory().getStack(0);
        if (pickaxe.isEmpty() || !pickaxe.getItem().isTool()) {
            stopMining();
            return;
        }
        
        if (pickaxe.getMaxDamage() - pickaxe.getDamage() <= ModConfig.minPickaxeDurability) {
            stopMining();
            return;
        }
        
        // Find target ore
        if (currentTarget == null || !world.isLoaded(currentTarget) || 
            world.getBlockState(currentTarget).isAir() || 
            !ModConfig.isTargetOre(world.getBlockState(currentTarget).getBlock())) {
            currentTarget = findNearestVisibleOre();
            if (currentTarget == null) {
                // No visible ore, search for any ore in radius
                currentTarget = findNearestOreInRadius();
                if (currentTarget == null) {
                    // No more ores found, stop
                    stopMining();
                    return;
                }
            }
        }
        
        // Move towards target
        moveToTarget(player, world);
        
        // If we're close enough, mine it
        double distance = player.squaredDistanceTo(currentTarget.toCenterPos());
        if (distance <= 4.0) {
            mineBlock(player, world, currentTarget);
        }
    }
    
    private static BlockPos findNearestVisibleOre() {
        if (client.player == null || client.world == null) return null;
        
        PlayerEntity player = client.player;
        World world = client.world;
        
        List<BlockPos> visibleOres = new ArrayList<>();
        
        // Scan in vision range
        for (int x = -ModConfig.visionRange; x <= ModConfig.visionRange; x++) {
            for (int y = -ModConfig.visionRange; y <= ModConfig.visionRange; y++) {
                for (int z = -ModConfig.visionRange; z <= ModConfig.visionRange; z++) {
                    BlockPos pos = player.getBlockPos().add(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    
                    if (ModConfig.isTargetOre(block)) {
                        // Check if we can see this block
                        if (hasLineOfSight(player, pos)) {
                            visibleOres.add(pos);
                        }
                    }
                }
            }
        }
        
        // Find the nearest one
        if (!visibleOres.isEmpty()) {
            return visibleOres.stream()
                    .min(Comparator.comparingDouble(pos -> player.squaredDistanceTo(pos.toCenterPos())))
                    .orElse(null);
        }
        
        return null;
    }
    
    private static BlockPos findNearestOreInRadius() {
        if (client.player == null || client.world == null) return null;
        
        PlayerEntity player = client.player;
        World world = client.world;
        
        List<BlockPos> nearbyOres = new ArrayList<>();
        
        // Scan in search radius
        for (int x = -ModConfig.searchRadius; x <= ModConfig.searchRadius; x++) {
            for (int y = -ModConfig.searchRadius; y <= ModConfig.searchRadius; y++) {
                for (int z = -ModConfig.searchRadius; z <= ModConfig.searchRadius; z++) {
                    BlockPos pos = player.getBlockPos().add(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    
                    if (ModConfig.isTargetOre(block)) {
                        nearbyOres.add(pos);
                    }
                }
            }
        }
        
        // Find the nearest one
        if (!nearbyOres.isEmpty()) {
            return nearbyOres.stream()
                    .min(Comparator.comparingDouble(pos -> player.squaredDistanceTo(pos.toCenterPos())))
                    .orElse(null);
        }
        
        return null;
    }
    
    private static boolean hasLineOfSight(PlayerEntity player, BlockPos pos) {
        Vec3d eyePos = player.getEyePos();
        Vec3d targetPos = pos.toCenterPos();
        
        RaycastContext context = new RaycastContext(
                eyePos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        );
        
        BlockHitResult hit = client.world.raycast(context);
        return hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(pos);
    }
    
    private static void moveToTarget(PlayerEntity player, World world) {
        if (currentTarget == null) return;
        
        Vec3d targetPos = currentTarget.toCenterPos();
        Vec3d playerPos = player.getPos();
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        // Handle flying
        if (ModConfig.enableFlying && player.getAbilities().flying) {
            // Fly towards target
            player.setVelocity(direction.x * 0.2, direction.y * 0.2, direction.z * 0.2);
            player.velocityModified = true;
        } else {
            // Normal movement
            double dx = targetPos.x - playerPos.x;
            double dz = targetPos.z - playerPos.z;
            double dy = targetPos.y - playerPos.y;
            
            // Horizontal movement
            player.setMovementForDirection((float)Math.atan2(dz, dx), 0.98f);
            
            // Jumping if needed
            boolean onGround = player.isOnGround();
            if (onGround && !wasOnGround) {
                jumpTimer = 0;
            }
            wasOnGround = onGround;
            
            if (dy > 0.5 && onGround) {
                // Need to jump up
                player.jump();
                jumpTimer = 10;
            } else if (jumpTimer > 0) {
                jumpTimer--;
            }
            
            // Place blocks if needed and enabled
            if (ModConfig.enableBlockPlacing && !player.getAbilities().flying) {
                placeBlocksIfNeeded(player, world);
            }
        }
        
        // Look at the target
        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;
        
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)(Math.atan2(dz, dx) * 180 / Math.PI) - 90;
        float pitch = (float)(-(Math.atan2(dy, horizontalDistance) * 180 / Math.PI));
        
        player.setYaw(yaw);
        player.setPitch(pitch);
    }
    
    private static void placeBlocksIfNeeded(PlayerEntity player, World world) {
        // Simple block placement logic for when we can't reach
        BlockPos below = player.getBlockPos().down();
        if (world.isAir(below)) {
            // Try to place a block below us to prevent falling
            // This is a simplified version
        }
    }
    
    private static void mineBlock(PlayerEntity player, World world, BlockPos pos) {
        // Check if we can reach it
        if (!hasLineOfSight(player, pos)) {
            // Need to break some stone to get to it
            breakPathBlocks(player, world, pos);
            return;
        }
        
        // Mine the ore
        if (!client.interactionManager.isBreakingBlock()) {
            client.interactionManager.attackBlock(pos, player.getHorizontalFacing());
        }
    }
    
    private static void breakPathBlocks(PlayerEntity player, World world, BlockPos targetPos) {
        // Find the path and break stone blocks if needed
        Vec3d eyePos = player.getEyePos();
        Vec3d targetPosVec = targetPos.toCenterPos();
        
        // Raycast to find the first block in the way
        RaycastContext context = new RaycastContext(
                eyePos,
                targetPosVec,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        );
        
        BlockHitResult hit = world.raycast(context);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();
            Block hitBlock = world.getBlockState(hitPos).getBlock();
            
            // Only break stone blocks to get to the ore
            if (hitBlock == Blocks.STONE || hitBlock == Blocks.COBBLESTONE || 
                hitBlock == Blocks.DEEPSLATE || hitBlock == Blocks.COBBLED_DEEPSLATE) {
                if (!client.interactionManager.isBreakingBlock()) {
                    client.interactionManager.attackBlock(hitPos, hit.getSide());
                }
            }
        }
    }
    
    private static void stopMining() {
        ModConfig.isRunning = false;
        ModConfig.isPaused = false;
        currentTarget = null;
        
        if (client.player != null) {
            // Stop all movement
            client.player.setVelocity(0, 0, 0);
            client.player.velocityModified = true;
        }
    }
    
    public static void reset() {
        currentTarget = null;
        jumpTimer = 0;
    }
}
