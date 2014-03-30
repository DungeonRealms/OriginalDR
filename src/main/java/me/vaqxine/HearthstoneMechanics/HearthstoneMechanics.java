package me.vaqxine.HearthstoneMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;

public class HearthstoneMechanics implements Listener {
    public static ConcurrentHashMap<String, Hearthstone> hearthstone_map = new ConcurrentHashMap<String, Hearthstone>();
    public static HashMap<String, Location> spawn_map = new HashMap<String, Location>();
    String templatePath_s = "plugins/HearthstoneMechanics/spawn_locations.dat";

    public void onEnable() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void Prelogin(AsyncPlayerPreLoginEvent e) {
        // They werent able to login for some reason.
        if (e.getLoginResult() != Result.ALLOWED)
            return;
        Hearthstone hs = new Hearthstone(e.getEventName());
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e) {

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

}
