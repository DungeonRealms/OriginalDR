package me.vaqxine.SubscriberMechanics;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SubscriberMechanics extends JavaPlugin implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run(){
				
			}
		}, 10 * 20L, 5L);

		log.info("[SubscriberMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[SubscriberMechanics] has been disabled.");
	}
	
}
