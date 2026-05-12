package com.example.autominer;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMiner implements ModInitializer {
	public static final String MOD_ID = "autominer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Auto Miner mod initialized!");
	}
}
