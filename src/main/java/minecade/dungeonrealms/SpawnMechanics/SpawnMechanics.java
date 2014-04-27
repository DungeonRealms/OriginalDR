package minecade.dungeonrealms.SpawnMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.SpawnMechanics.commands.CommandAddSpawn;
import minecade.dungeonrealms.SpawnMechanics.commands.CommandSetVSpawn;
import minecade.dungeonrealms.SpawnMechanics.commands.CommandSpawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static String templatePath_s = "plugins/SpawnMechanics/spawn_locations.dat";
	
	public static List<Location> spawn_map = new ArrayList<Location>();
	
	public void onEnable() {
		loadSpawnLocationTemplate();
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("addspawn").setExecutor(new CommandAddSpawn());
		Main.plugin.getCommand("setvspawn").setExecutor(new CommandSetVSpawn());
		Main.plugin.getCommand("spawn").setExecutor(new CommandSpawn());
		
		log.info("[SpawnMechanics] has been enabled.");
	}
	
	public void onDisable() {
		saveSpawnLocationData();
		log.info("[SpawnMechanics] has been disabled.");
	}
	
	public static void loadSpawnLocationTemplate() {
		if(!(new File(templatePath_s).exists())) {
			try {
				new File(templatePath_s).createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
			return; // Nothing to load.
		}
		int count = 0;
		try {
			File file = new File(templatePath_s);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				if(line.contains(",")) {
					String[] cords = line.split(",");
					Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
					
					spawn_map.add(loc);
					count++;
				}
			}
			reader.close();
			log.info("[SpawnMechanics] " + count + " SPAWN LOCATIONS have been LOADED.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void saveSpawnLocationData() {
		String all_dat = "";
		int count = 0;
		
		for(Location loc : spawn_map) {
			all_dat += loc.getX() + "," + loc.getY() + "," + loc.getZ() + "\r\n";
			count++;
		}
		
		if(all_dat.length() > 1) {
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(templatePath_s), false));
				dos.writeBytes(all_dat + "\n");
				dos.close();
			} catch(IOException e) {}
		}
		
		log.info("[SpawnMechanics] " + count + " SPAWN LOCATIONS have been SAVED.");
	}
	
	public static Location getRandomSpawnPoint(String p_name) {
		Location respawn_location = new Location(Bukkit.getWorlds().get(0), -367, 83, 390); // Default to this if something goes wrong.
		
		if(Hive.first_login.contains(p_name) || HealthMechanics.noob_players.contains(p_name)) { return respawn_location; // They're noobs, spawn them in center.
		}
		
		if(spawn_map.size() > 0) {
			int spawn_location_counts = spawn_map.size();
			int spawn_index = new Random().nextInt(spawn_location_counts);
			Location random_spawn_loc = spawn_map.get(spawn_index);
			if(random_spawn_loc != null) {
				respawn_location = random_spawn_loc;
			}
		}
		return respawn_location;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		
		if(p.getLocation().getWorld().getName().equalsIgnoreCase(e.getRespawnLocation().getWorld().getName()) && p.getLocation().distanceSquared(e.getRespawnLocation()) <= 2) {
			// They're respawning on themselves, ignore.
			// Non-legit death, they won't loose any items.
			return;
		}
		
		Location respawn_location = null;
		if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("evil")) {
			int spawn = new Random().nextInt(KarmaMechanics.evil_spawns.size());
			respawn_location = KarmaMechanics.evil_spawns.get(spawn);
		} else {
			respawn_location = getRandomSpawnPoint(p.getName());
		}
		
		KarmaMechanics.saved_location.put(p.getName(), respawn_location);
		e.setRespawnLocation(respawn_location);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		/*if(cmd.getName().equalsIgnoreCase("tppos")){
			Player p = (Player)sender;
			if(!(p.isOp())){return true;}
			p.teleport(new Location(p.getWorld(), -985, 65, -285));
		}*/
		
		return true;
	}
	
}
