package minecade.dungeonrealms.SubscriberMechanics;

import java.util.logging.Logger;

public class SubscriberMechanics {
	Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		log.info("[SubscriberMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[SubscriberMechanics] has been disabled.");
	}
	
}
