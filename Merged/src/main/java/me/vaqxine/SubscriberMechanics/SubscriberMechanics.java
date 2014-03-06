package me.vaqxine.SubscriberMechanics;

import java.util.logging.Logger;

import me.vaqxine.Main;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SubscriberMechanics implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run(){
				
			}
		}, 10 * 20L, 5L);

		log.info("[SubscriberMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[SubscriberMechanics] has been disabled.");
	}
	
}
