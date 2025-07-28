package io.github.noskinbadname.opsponges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OPSponges implements ModInitializer {
	public static final String MOD_ID = "opsponges";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	protected static List<DelayedAction> delayedActions = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Hello World!");

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> new ArrayList<>(delayedActions).forEach(DelayedAction::onTick));

		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> delayedActions = new ArrayList<>());
	}
}