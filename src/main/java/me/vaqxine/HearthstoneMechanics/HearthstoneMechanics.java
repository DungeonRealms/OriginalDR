package me.vaqxine.HearthstoneMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.Main;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.defaults.ClearCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class HearthstoneMechanics implements Listener {
    public static ConcurrentHashMap<String, Hearthstone> hearthstone_map = new ConcurrentHashMap<String, Hearthstone>();

    public static ConcurrentHashMap<String, Location> hearthstone_location = new ConcurrentHashMap<String, Location>();
    // Player name, location they called it at.
    public static ConcurrentHashMap<String, Integer> hearthstone_timer = new ConcurrentHashMap<String, Integer>();
    // Countdown timer for the stone calling
    public static HashMap<String, Location> spawn_map = new HashMap<String, Location>();
    // Location Name, Location - Used to get locations fromt he name in the sql
    static String templatePath_s = "plugins/HearthstoneMechanics/spawn_locations.dat";

    public void onEnable() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
        loadSpawnLocationTemplate();
        new BukkitRunnable() {
            public void run() {
                for (Entry<String, Integer> timer_data : hearthstone_timer.entrySet()) {
                    int time_left = timer_data.getValue();
                    String p_name = timer_data.getKey();
                    if (Bukkit.getPlayer(p_name) == null) {
                        removeHearthstoneTimer(p_name);
                        continue;
                    }
                    final Player p = Bukkit.getPlayer(p_name);
                    if (!isLocationsEqual(p.getLocation(), hearthstone_location.get(p_name))) {
                        p.sendMessage(ChatColor.RED + "Hearthstone -" + ChatColor.BOLD + " CANCELLED");
                        p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 5 minute cooldown timer.");
                        // 5 minutes
                        getHearthStone(p_name).setTimer(60 * 5);

                    }
                    time_left -= 1;
                    if (time_left <= 0) {
                        p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);
                        p.teleport(getHearthStone(p.getName()).getLocation());
                        int timer = PermissionMechanics.getRank(p.getName()).equalsIgnoreCase("default") ? 15 : 10;
                        getHearthStone(p.getName()).setTimer(60 * timer);
                        p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a " + ChatColor.UNDERLINE + timer + ChatColor.GRAY
                                + " minute cooldown timer.");
                    } else {
                        p.sendMessage(ChatColor.BOLD + "TELEPORTING " + ChatColor.WHITE + " ... " + time_left + "s");
                        hearthstone_timer.put(p.getName(), time_left);
                        new BukkitRunnable() {
                            public void run() {
                                try {
                                    ParticleEffect.sendToLocation(ParticleEffect.SPELL, p.getLocation().add(0, 0.15, 0), new Random().nextFloat(),
                                            new Random().nextFloat(), new Random().nextFloat(), 0.5F, 80);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.runTaskAsynchronously(Main.plugin);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
        
        new BukkitRunnable() {
            public void run() {
                // TODO Auto-generated method stub
                for(Entry<String, Hearthstone> h_entries : hearthstone_map.entrySet()){
                    Hearthstone hs = h_entries.getValue();
                    String p_name = h_entries.getKey();
                    int time_left = hs.getTimer();
                    if(time_left == 0){
                        //They dont need to be alerted
                        continue;
                    }
                    time_left -= 1;
                    Player p = hs.getPlayer();
                    if(p == null){
                        hearthstone_map.remove(p_name);
                        continue;
                    }
                    
                    if(time_left <= 0){
                        p.sendMessage(ChatColor.RED + "Your Hearthstone is now usable.");
                        hs.setTimer(0);
                    }else{
                        //Tick down
                        hs.setTimer(time_left);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 0 , 20L);
    }
    public void onDisable(){
        for(Hearthstone hs : hearthstone_map.values()){
            hs.saveData();
        }
        hearthstone_map.clear();
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player p = event.getPlayer();
        getHearthStone(p.getName()).saveData();
        hearthstone_location.remove(p.getName());
        hearthstone_map.remove(p.getName());
        hearthstone_timer.remove(p.getName());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void Prelogin(AsyncPlayerPreLoginEvent e) {
        // They werent able to login for some reason.
        if (e.getLoginResult() != Result.ALLOWED)
            return;
        // Loads the data and saves it into a hashmap
        new Hearthstone(e.getEventName());
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (!hearthstone_map.containsKey(p)) {
            System.out.print("NO HEARTHSTONE DATA FOR " + e.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if(e instanceof Player){
            Player p = ((Player) e).getPlayer();
            if(hearthstone_timer.containsKey(p.getName())){
                removeHearthstoneTimer(p.getName());
                p.sendMessage(ChatColor.RED + "Hearthstone -" + ChatColor.BOLD + " CANCELLED");
                p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 3 minute cooldown timer.");
                // 5 minutes
                getHearthStone(p.getName()).setTimer(60 * 3);
            }
        }
    }
    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent e){
        Player p = e.getPlayer();
        if(hearthstone_location.containsKey(p.getName())){
            removeHearthstoneTimer(p.getName());
            p.sendMessage(ChatColor.RED + "Hearthstone -" + ChatColor.BOLD + " CANCELLED");
            p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 3 minute cooldown timer.");
            // 5 minutes
            getHearthStone(p.getName()).setTimer(60 * 3);
        }
    }
    @EventHandler
    public void onHearthStoneEvent(PlayerInteractEvent e) {
        if (!e.hasItem())
            return;
        // Not quartz so no point
        if (!e.getItem().getType().equals(Material.QUARTZ))
            return;
        if (!e.getItem().hasItemMeta())
            return;
        if (!e.getItem().getItemMeta().hasDisplayName())
            return;
        if (!e.getItem().getItemMeta().getDisplayName().contains("Hearthstone"))
            return;
        Player p = e.getPlayer();
        if (hearthstone_map.containsKey(p.getName()) && hearthstone_map.get(p.getName()).getTimer() > 0) {
            p.sendMessage(ChatColor.RED + "You must " + ChatColor.UNDERLINE + "wait" + ChatColor.RED + " another " + ChatColor.UNDERLINE
                    + hearthstone_map.get(p.getName()).getTimer() + ChatColor.RED + "seconds to use this again.");
            return;
        }
        hearthstone_location.put(p.getName(), p.getLocation());
        hearthstone_timer.put(p.getName(), 10);
        p.sendMessage(ChatColor.WHITE.toString() + ChatColor.BOLD + "TELEPORTING - " + ChatColor.AQUA + hearthstone_map.get(p.getName()).getName()
                + ChatColor.WHITE + " ... 10s");
    }

    public static void loadSpawnLocationTemplate() {
        spawn_map.put("Cyrennica", Bukkit.getWorlds().get(0).getSpawnLocation());
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
                if (!line.contains("@name@")) {
                    System.out.print("No name for the line: " + line);
                    continue;
                }
                String loc_name = line.split("@name@")[1].replace("_", " ");
                if (line.contains(",")) {
                    if (spawn_map.containsKey(loc_name)) {
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

    public static void saveSpawnLocationData() {
        String all_dat = "";
        int count = 0;

        for (Entry<String, Location> entry : spawn_map.entrySet()) {
            Location loc = entry.getValue();
            if (!loc.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
                continue;
            }
            String name = entry.getKey();
            all_dat += loc.getX() + "," + loc.getY() + "," + loc.getZ() + "@name@" + name + "\r\n";
            count++;
        }

        if (all_dat.length() > 1) {
            try {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(templatePath_s, false));
                dos.writeBytes(all_dat + "\n");
                dos.close();
                System.out.print("[HearthstoneMechanics] Saved " + count + " spawns.");
            } catch (IOException e) {
            }
        }
    }
    public static void reloadSpawn(){
        saveSpawnLocationData();
        loadSpawnLocationTemplate();
    }

    public void removeHearthstoneTimer(String p_name) {
        hearthstone_location.remove(p_name);
        hearthstone_timer.remove(p_name);
    }

    public boolean isLocationsEqual(Location first, Location second) {
        if ((first.getBlockX() == second.getBlockX()) && (first.getBlockY() == second.getBlockY()) && (first.getBlockZ() == second.getBlockZ())
                && first.getWorld().getName().equalsIgnoreCase(second.getWorld().getName())) {
            return true;
        }
        return false;
    }

    public static ItemStack getHearthstone(Player p) {
        ItemStack hearthstone_item = createCustomItem(
                new ItemStack(Material.QUARTZ),
                ChatColor.YELLOW.toString() + ChatColor.BOLD + "Hearthstone",
                Arrays.asList(ChatColor.GRAY + "Teleports you to your home town.", ChatColor.GRAY + "You can change your home", ChatColor.GRAY
                        + "by talking to the Innkeeper in cities.", ChatColor.GREEN + "Location: " + getHearthStone(p.getName()).getName()));

        return hearthstone_item;
    }

    public static ItemStack createCustomItem(ItemStack is, String name, List<String> lore) {
        ItemMeta im = is.getItemMeta();
        if (name != null) {
            im.setDisplayName(name);
        }
        if (lore != null) {
            im.setLore(lore);
        }
        is.setItemMeta(im);
        return is;
    }

    public static Hearthstone getHearthStone(String p_name) {
        return hearthstone_map.get(p_name);
    }
}
