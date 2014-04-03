package me.vaqxine.HearthstoneMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.kumpelblase2.remoteentities.api.thinking.DeathBehavior;

public class HearthstoneMechanics implements Listener {
    public static ConcurrentHashMap<String, Hearthstone> hearthstone_map = new ConcurrentHashMap<String, Hearthstone>();
    
    public static ConcurrentHashMap<String, Location> hearthstone_location = new ConcurrentHashMap<String, Location>();
    //Player name, location they called it at.
    public static ConcurrentHashMap<String, Integer> hearthstone_timer = new ConcurrentHashMap<String, Integer>();
    //Countdown timer for the stone calling
    public static HashMap<String, Location> spawn_map = new HashMap<String, Location>();
    //Location Name, Location - Used to get locations fromt he name in the sql
    String templatePath_s = "plugins/HearthstoneMechanics/spawn_locations.dat";

    public void onEnable() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
        new BukkitRunnable(){
            public void run() {
                for(Entry<String, Integer> timer_data : hearthstone_timer.entrySet()){
                    int time_left = timer_data.getValue();
                    String p_name = timer_data.getKey();
                    if(Bukkit.getPlayer(p_name) == null){
                        removeHearthstoneTimer(p_name);
                        continue;
                    }
                    Player p = Bukkit.getPlayer(p_name);
                    if(!isLocationsEqual(p.getLocation(), hearthstone_location.get(p_name))){
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "Hearthstone " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "cancelled");
                        p.sendMessage(ChatColor.GRAY + "");
                    }
                    time_left -= 1;
                    if(time_left <= 0){
                        //Teleport
                    }else{
                        
                   
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void Prelogin(AsyncPlayerPreLoginEvent e) {
        // They werent able to login for some reason.
        if (e.getLoginResult() != Result.ALLOWED)
            return;
        //Loads the data and saves it into a hashmap
        new Hearthstone(e.getEventName());
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if(!hearthstone_map.containsKey(p)){
            
        }
    }
    
    @EventHandler
    public void onHearthStoneEvent(PlayerInteractEvent e){
        
    }
    public void loadSpawnLocationTemplate() {
        spawn_map.put("Spawn", Bukkit.getWorlds().get(0).getSpawnLocation());
        if (!(new File(templatePath_s).exists())) {
            try {
                new File(templatePath_s).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return; // Nothing to load.
        }
        int count = 0;
        try {
            File file = new File(templatePath_s);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    String loc_name = line.split("@name@")[1];
                    if(spawn_map.containsKey(loc_name)){
                        System.out.print("[HearthStoneMechanics] Duplicate entry for the name " + loc_name);
                        continue; 
                    }
                    String[] cords = line.split(",");
                    Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]),
                            Double.parseDouble(cords[2]));
                    spawn_map.put(loc_name, loc);
                    count++;
                }
            }
            reader.close();
            System.out.print("[Hearthstone] " + count + " SPAWN LOCATIONS have been LOADED.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void removeHearthstoneTimer(String p_name){
        hearthstone_location.remove(p_name);
        hearthstone_timer.get(p_name);
    }
    public boolean isLocationsEqual(Location first, Location second) {
        if ((first.getBlockX() == second.getBlockX()) && (first.getBlockY() == second.getBlockY()) && (first.getBlockZ() == second.getBlockZ()) && first.getWorld().getName().equalsIgnoreCase(second.getWorld().getName())) {
            return true;
        }
        return false;
    }

}
