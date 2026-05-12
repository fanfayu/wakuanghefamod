# Auto Miner Mod for Minecraft 1.21.11 (Fabric)

An automatic mining mod that helps you mine ores automatically with pathfinding.

## Features

- **Automatic Ore Mining**: Automatically finds and mines ores
- **Vision-based Detection**: Only mines ores that are visible to the player (no X-ray!)
- **Pathfinding**: Automatically navigates to ores, breaking stone blocks if needed
- **Smart Tool Usage**: Uses the first item in your hotbar as the pickaxe
- **Durability Check**: Stops mining when pickaxe durability is low
- **Auto Flying**: Double-tap space to enable flying (configurable)
- **Block Placing**: Places blocks to help you reach ores (when not flying)
- **Configurable GUI**: Settings screen to configure what ores to mine and other options

## Controls

- **M Key**: Open settings screen
- **N Key**: Toggle mining on/off
- **Double-tap Space**: Toggle flying (when auto-flying is enabled)

## Settings

In the settings screen you can:
- Start/Pause/Stop mining
- Toggle auto-flying
- Toggle block placing
- Configure which ores to mine

## How it Works

1. The mod first looks for ores that are directly visible to you (line of sight)
2. If no visible ores are found, it searches for ores within a 64-block radius
3. It automatically moves towards the nearest ore
4. If flying is enabled, it will fly directly to the ore
5. If not flying, it will walk and jump, placing blocks if needed
6. When it reaches the ore, it will mine it
7. If there are blocks blocking the way to the ore, it will break stone blocks to clear a path
8. It stops when no more ores are found in the search radius

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.18.1+
- Fabric API
- Java 21+

## Building from Source

1. Make sure you have Java 21 installed
2. Run `./gradlew build` (or `gradlew.bat build` on Windows)
3. The built jar will be in `build/libs/`

## Installation

1. Install Fabric Loader for 1.21.11
2. Install Fabric API
3. Place the `autominer-1.0.0.jar` file in your mods folder

## Notes

- This mod is client-side only
- It only uses normal vision detection, no X-ray cheats
- It ignores mob attacks as requested
- It will not break blocks other than stone and the target ores unless necessary
