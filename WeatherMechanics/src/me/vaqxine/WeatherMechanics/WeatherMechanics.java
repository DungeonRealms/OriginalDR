package me.vaqxine.WeatherMechanics;

import java.util.HashMap;
import java.util.logging.Logger;

import me.vaqxine.DuelMechanics.DuelMechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherMechanics extends JavaPlugin implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	public static volatile HashMap<String, WeatherType> player_weather = new HashMap<String, WeatherType>();
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Player pl : getServer().getOnlinePlayers()){
					if(getRegionName(pl.getLocation()).startsWith("rain_") && pl.getPlayerWeather() != WeatherType.DOWNFALL){
						pl.setPlayerWeather(WeatherType.DOWNFALL);
						continue;
					}
					else if(!(getRegionName(pl.getLocation()).startsWith("rain_")) && pl.getPlayerWeather() == WeatherType.DOWNFALL){
						pl.setPlayerWeather(WeatherType.CLEAR);
						continue;
					}
				}
			}
		}, 10 * 20L, 5L);

		log.info("[WeatherMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[WeatherMechanics] has been disabled.");
	}
	
	public static String getRegionName(Location loc){
		return DuelMechanics.getRegionName(loc);
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e){
		Player pl = e.getPlayer();
		if(!pl.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
			if(pl.getPlayerWeather() == WeatherType.DOWNFALL){
				pl.setPlayerWeather(WeatherType.CLEAR);
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player)sender;
		if(!(p.isOp())){
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("drweather")){
			if(args[0].equalsIgnoreCase("rain")){
				p.setPlayerWeather(WeatherType.DOWNFALL);
			}
			else{
				p.setPlayerWeather(WeatherType.CLEAR);
			}
		}
		
		return true;
	}
}
