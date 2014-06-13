package minecade.dungeonrealms.HearthstoneMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.HearthstoneMechanics.commands.CommandAddHearthstone;
import minecade.dungeonrealms.HearthstoneMechanics.commands.CommandListHearthstone;
import minecade.dungeonrealms.Hive.ParticleEffect;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class HearthstoneMechanics implements Listener {
    public static ConcurrentHashMap<String, Hearthstone> hearthstone_map = new ConcurrentHashMap<String, Hearthstone>();
    public static ConcurrentHashMap<String, Integer> hearthstone_price = new ConcurrentHashMap<String, Integer>();
    public static ConcurrentHashMap<String, Location> hearthstone_location = new ConcurrentHashMap<String, Location>();
    // Player name, location they called it at.
    public static ConcurrentHashMap<String, Integer> hearthstone_timer = new ConcurrentHashMap<String, Integer>();
    // Countdown timer for the stone calling
    public static HashMap<String, Location> spawn_map = new HashMap<String, Location>();

    // Location Name, Location - Used to get locations fromt he name in the sql
    public ConcurrentHashMap<String, String> changing_homes = new ConcurrentHashMap<String, String>();
    // P_name, Location name
    static String templatePath_s = "plugins/HearthstoneMechanics/spawn_locations.dat";

    public void onEnable() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
        loadSpawnLocationTemplate();
        Main.plugin.getCommand("addhearthstone").setExecutor(new CommandAddHearthstone());
        Main.plugin.getCommand("listhearthstone").setExecutor(new CommandListHearthstone());
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
                        p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 3 minute cooldown timer.");
                        // 5 minutes
                        getHearthStone(p_name).setTimer(60 * 3);
                        hearthstone_location.remove(p_name);
                        hearthstone_timer.remove(p_name);
                        return;
                    }
                    time_left -= 1;
                    if (time_left <= 0) {
                        p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);
                        p.teleport(getHearthStone(p.getName()).getLocation());
                        int timer = (PermissionMechanics.getRank(p.getName()) != null && !PermissionMechanics.getRank(p.getName()).equalsIgnoreCase("default") && !p
                                .isOp()) ? 25 : 15;
                        getHearthStone(p.getName()).setTimer(60 * timer);
                        p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a " + ChatColor.UNDERLINE + timer + ChatColor.GRAY
                                + " minute cooldown timer.");
                        hearthstone_location.remove(p.getName());
                        hearthstone_timer.remove(p.getName());
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
                for (Entry<String, Hearthstone> h_entries : hearthstone_map.entrySet()) {
                    Hearthstone hs = h_entries.getValue();
                    String p_name = h_entries.getKey();
                    int time_left = hs.getTimer();
                    if (time_left == 0) {
                        // They dont need to be alerted
                        continue;
                    }
                    time_left -= 1;
                    Player p = hs.getPlayer();
                    if (p == null) {
                        hearthstone_map.remove(p_name);
                        continue;
                    }

                    if (time_left <= 0) {
                        p.sendMessage(ChatColor.RED + "Your Hearthstone is ready.");
                        hs.setTimer(0);
                    } else {
                        // Tick down
                        hs.setTimer(time_left);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 0, 20L);

        new BukkitRunnable() {
            public void run() {
                for (Entry<String, String> entry : changing_homes.entrySet()) {
                    String p_name = entry.getKey();
                    if (Bukkit.getPlayer(p_name) == null) {
                        // They are offline
                        changing_homes.remove(p_name);
                        continue;
                    }
                    Player p = Bukkit.getPlayer(p_name);
                    boolean inkeeper_nearby = false;
                    for (Entity e : p.getNearbyEntities(15, 15, 15)) {
                        if (e instanceof Player) {
                            Player to_check = (Player) e;
                            if (isInnkeeper(to_check)) {
                                inkeeper_nearby = true;
                            }
                        }
                    }
                    if (!inkeeper_nearby) {
                        p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "Maybe another time traveler.");
                        p.sendMessage(ChatColor.RED + "Town Change - " + ChatColor.BOLD + "CANCELLED");
                        changing_homes.remove(p.getName());
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 0, 20 * 10);
    }

    public void onDisable() {
        for (Hearthstone hs : hearthstone_map.values()) {
            hs.saveData();
        }
        hearthstone_map.clear();
    }


    public static void saveData(String p_name) {
        getHearthStone(p_name).saveData();
        hearthstone_location.remove(p_name);
        hearthstone_map.remove(p_name);
        hearthstone_timer.remove(p_name);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void Prelogin(AsyncPlayerPreLoginEvent e) {
        // Loads the data and saves it into a hashmap
        new Hearthstone(e.getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        for (ItemStack is : new ArrayList<ItemStack>(event.getDrops())) {
            if (is.getType() == Material.QUARTZ) {
                event.getDrops().remove(is);
            }
        }
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        if (!hearthstone_map.containsKey(p.getName())) {
            new Hearthstone(p.getName());
        }
        hearthstone_map.get(p.getName()).setPlayer(p);
        new BukkitRunnable() {
            public void run() {
                checkInventoryAndReset(p);
            }
        }.runTaskLater(Main.plugin, 15L);

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        new BukkitRunnable() {
            public void run() {
                checkInventoryAndReset(p);
            }
        }.runTaskLater(Main.plugin, 15L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e instanceof Player) {
            Player p = ((Player) e).getPlayer();
            if (hearthstone_timer.containsKey(p.getName())) {
                removeHearthstoneTimer(p.getName());
                p.sendMessage(ChatColor.RED + "Hearthstone -" + ChatColor.BOLD + " CANCELLED");
                p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 3 minute cooldown timer.");
                // 5 minutes
                getHearthStone(p.getName()).setTimer(60 * 3);
            }
        }
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent e) {
        Player p = e.getPlayer();
        if (hearthstone_timer.containsKey(p.getName())) {
            removeHearthstoneTimer(p.getName());
            p.sendMessage(ChatColor.RED + "Hearthstone -" + ChatColor.BOLD + " CANCELLED");
            p.sendMessage(ChatColor.GRAY + "Your Hearthstone has been put on a 3 minute cooldown timer.");
            // 5 minutes
            getHearthStone(p.getName()).setTimer(60 * 3);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPLayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player) {
            Player clicked = (Player) e.getRightClicked();
            Player p = e.getPlayer();
            if (!isInnkeeper(clicked))
                return;
            ItemStack is = clicked.getItemInHand();
            if (changing_homes.containsKey(p.getName())) {
                p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "You are already changing your home!");
                return;
            }

            String town_name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
            if (!hearthstone_price.containsKey(town_name)) {
                p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "Im sorry traveler! Theres seems to be a problem with my calculations.");
                return;
            }
            int price = hearthstone_price.get(town_name);
            if (!RealmMechanics.doTheyHaveEnoughMoney(p, price)) {
                p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "Im sorry traveler but you cant afford this!");
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "COST: " + ChatColor.RED + price + "G");
                return;
            }
            if (getHearthStone(p.getName()).getName().equalsIgnoreCase(town_name)) {
                p.sendMessage(ChatColor.GRAY + "Innkeeper:" + ChatColor.WHITE + " Traveler! It seems like your home is already set here!");
                return;
            }
            p.sendMessage(ChatColor.GRAY + "Changing your location to " + ChatColor.GREEN + "'" + town_name + "'" + ChatColor.GRAY + " will cost "
                    + ChatColor.GREEN + ChatColor.BOLD + price + " GEM(s)" + ChatColor.GRAY + ".");
            p.sendMessage(ChatColor.GRAY + "Please type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm.");
            changing_homes.put(p.getName(), town_name);

        }
    }

    // Last to get called
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTalk(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (changing_homes.containsKey(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("Y")) {
                String town_name = changing_homes.get(p.getName());
                int price = hearthstone_price.get(town_name);
                RealmMechanics.subtractMoney(p, price);
                p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "Welcome to the town traveler!");
                p.sendMessage(ChatColor.GRAY + "Hearthstone set to: " + ChatColor.GREEN + changing_homes.get(p.getName()));
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "-" + ChatColor.RED + price + ChatColor.BOLD + "G");
                getHearthStone(p.getName()).setLocationName(changing_homes.get(p.getName()));
                getHearthStone(p.getName()).setLocation(spawn_map.get(changing_homes.get(p.getName())));
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
                checkInventoryAndReset(p);
            } else {
                p.sendMessage(ChatColor.GRAY + "Innkeeper: " + ChatColor.WHITE + "Maybe another day traveler!");
                p.sendMessage(ChatColor.RED + "Town Change - " + ChatColor.BOLD + "CANCELLED");
            }
            changing_homes.remove(p.getName());
        }
    }

    public boolean isInnkeeper(Player p) {
        if (!p.hasMetadata("NPC")) {
            return false;
        }
        if (p.getName().contains("Innkeeper")) {
            // Jackpot
            if (p.getItemInHand() != null) {
                ItemStack is = p.getItemInHand();
                if (is.getType() != Material.QUARTZ) {
                    return false;
                }
                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    return true;
                }
            }
        }
        return false;
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

        e.setCancelled(true);
        Player p = e.getPlayer();
        if (TutorialMechanics.onTutorialIsland(e.getPlayer())) {
            p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item on Tutorial Island.");
            return;
        }
        if (KarmaMechanics.getAlignment(p.getName()).contains("evil")) {
            p.sendMessage(ChatColor.RED + "You cannot do this while chaotic!");
            return;
        }
        if (InstanceMechanics.isInstance(p.getWorld().getName())) {
            p.sendMessage(ChatColor.RED + "You cannot use this in an instance.");
            return;
        }
        if (changing_homes.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You cannot do this while changing towns!");
            return;
        }
        if (hearthstone_map.containsKey(p.getName()) && hearthstone_map.get(p.getName()).getTimer() > 0) {
            p.sendMessage(ChatColor.RED + "You must " + ChatColor.UNDERLINE + "wait" + ChatColor.RED + " another " + ChatColor.UNDERLINE
                    + hearthstone_map.get(p.getName()).getTimer() + ChatColor.RED + " seconds to use this again.");
            return;
        }
        hearthstone_location.put(p.getName(), p.getLocation());
        hearthstone_timer.put(p.getName(), 10);
        p.sendMessage(ChatColor.WHITE.toString() + ChatColor.BOLD + "TELEPORTING - " + ChatColor.AQUA + hearthstone_map.get(p.getName()).getName()
                + ChatColor.WHITE + " ... 10s");
    }

    public static void loadSpawnLocationTemplate() {
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
                if (!line.contains("#price#")) {
                    System.out.print("No price for " + line);
                    continue;
                }
                String loc_name = line.split("@name@")[1].split("#price#")[0].replace("_", " ");
                int price = 1000;
                try {
                    price = Integer.parseInt(line.split("#price#")[1]);
                } catch (Exception e) {
                    System.out.print("Couldnt parse in for " + line);
                }
                if (line.contains(",")) {
                    if (spawn_map.containsKey(loc_name)) {
                        System.out.print("[HearthStoneMechanics] Duplicate entry for the name " + loc_name);
                    }
                    String[] cords = line.split(",");
                    Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]),
                            Double.parseDouble(cords[2].split("@")[0]));
                    spawn_map.put(loc_name, loc);
                    hearthstone_price.put(loc_name, price);
                    count++;
                }
            }
            reader.close();
            System.out.print("[Hearthstone] " + count + " SPAWN LOCATIONS have been LOADED.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Scans and overwrites the old Hearthstone. If they dont have one it makes one.
     * 
     * @param p
     */
    public void checkInventoryAndReset(Player p) {
        if (p.getInventory().all(Material.QUARTZ).size() > 1) {
            for (Entry<Integer, ? extends ItemStack> data : p.getInventory().all(Material.QUARTZ).entrySet()) {
                p.getInventory().remove(data.getValue());
                System.out.print("[RealmMechanics] Player " + p.getName() + " had more than one realm rune, removed all but 1!");
            }
        }
        int slot = -1;
        if (p.getInventory().contains(Material.QUARTZ)) {
            slot = p.getInventory().first(Material.QUARTZ);
        }

        if (slot == -1) {
            if (p.getInventory().getItem(6) == null || p.getInventory().getItem(6).getType() == Material.AIR) {
                p.getInventory().setItem(6, getHearthstoneItem(p));
            } else {
                if (p.getInventory().firstEmpty() != -1) {
                    p.getInventory().setItem(p.getInventory().firstEmpty(), getHearthstoneItem(p));
                } else {
                    // They didnt have a hearthstone or any room ... gg
                    p.getInventory().setItem(35, getHearthstoneItem(p));
                }
            }
        } else {
            p.getInventory().setItem(slot, getHearthstoneItem(p));
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
            all_dat += loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "@name@" + name.replace(" ", "_") + "#price#"
                    + hearthstone_price.get(name) + "\r\n";
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

    public static void reloadSpawn() {
        saveSpawnLocationData();
        loadSpawnLocationTemplate();
    }

    public void removeHearthstoneTimer(String p_name) {
        hearthstone_location.remove(p_name);
        hearthstone_timer.remove(p_name);
    }

    public static boolean isLocationsEqual(Location first, Location second) {
        if ((first.getBlockX() == second.getBlockX()) && (first.getBlockY() == second.getBlockY()) && (first.getBlockZ() == second.getBlockZ())
                && first.getWorld().getName().equalsIgnoreCase(second.getWorld().getName())) {
            return true;
        }
        return false;
    }

    public static ItemStack getHearthstoneItem(Player p) {
        Hearthstone hs = getHearthStone(p.getName());
        if (hs == null) {
            return createCustomItem(new ItemStack(Material.QUARTZ), ChatColor.YELLOW.toString() + ChatColor.BOLD + "Hearthstone", Arrays.asList(ChatColor.GRAY
                    + "Teleports you to your home town.", ChatColor.GRAY + "Talk to an Innkeeper to change your home town.", ChatColor.GREEN
                    + "Location: Cyrennica"));
        }
        ItemStack hearthstone_item = createCustomItem(new ItemStack(Material.QUARTZ), ChatColor.YELLOW.toString() + ChatColor.BOLD + "Hearthstone",
                Arrays.asList(ChatColor.GRAY + "Teleports you to your home town.", ChatColor.GRAY + "Talk to an Innkeeper to change your home town.",
                        ChatColor.GREEN + "Location: " + hs.getName()));

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
