package minecade.dungeonrealms.AchievementMechanics;

import java.util.Random;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.GuildMechanics.GuildMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.Hive.ParticleEffect;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.PetMechanics.PetMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AchievementMechanics implements Listener {
    static Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);

        // Check all current player zones / locations for 'exploration' achievs.
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    String region = DuelMechanics.getRegionName(pl.getLocation());
                    if (region.equalsIgnoreCase("cityofcyrennica")) {
                        addAchievement(pl.getName(), "Explorer: Cyrennica");
                        continue;
                    }
                    if (region.equalsIgnoreCase("villagesafe")) {
                        addAchievement(pl.getName(), "Explorer: Harrisons Fields");
                        continue;
                    }
                    if (region.equalsIgnoreCase("plainsofcyrene")) {
                        addAchievement(pl.getName(), "Explorer: Plains of Cyrene");
                        continue;
                    }
                    if (region.equalsIgnoreCase("darkoakwild2")) {
                        addAchievement(pl.getName(), "Explorer: Darkoak");
                        continue;
                    }
                    if (region.equalsIgnoreCase("infrontoftavern")) { // TODO: This  is just an entrance region.
                        addAchievement(pl.getName(), "Explorer: Jagged Rocks");
                        continue;
                    }
                    if (region.equalsIgnoreCase("goblincity")) {
                        addAchievement(pl.getName(), "Explorer: Skullneck");
                        continue;
                    }
                    if (region.equalsIgnoreCase("trollcity1")) {
                        addAchievement(pl.getName(), "Explorer: Trollingor");
                        continue;
                    }
                    if (region.equalsIgnoreCase("crystalpeakt")) {
                        addAchievement(pl.getName(), "Explorer: Crystalpeak Tower");
                        continue;
                    }
                    if (region.equalsIgnoreCase("transitional3")) {
                        addAchievement(pl.getName(), "Explorer: Helmchen");
                        continue;
                    }
                    if (region.equalsIgnoreCase("alsahra")) {
                        addAchievement(pl.getName(), "Explorer: Al Sahra");
                        continue;
                    }
                    if (region.equalsIgnoreCase("savannahsafezone")) {
                        addAchievement(pl.getName(), "Explorer: Tripoli");
                        continue;
                    }
                    if (region.equalsIgnoreCase("swampvillage_2")) {
                        addAchievement(pl.getName(), "Explorer: Dreadwood");
                        continue;
                    }
                    if (region.equalsIgnoreCase("swamp_1")) {
                        addAchievement(pl.getName(), "Explorer: Gloomy Hallows");
                        continue;
                    }
                    if (region.equalsIgnoreCase("crestguard")) {
                        addAchievement(pl.getName(), "Explorer: Avalon Peaks");

                        continue;
                    }
                    if (region.equalsIgnoreCase("cstrip6")) {
                        addAchievement(pl.getName(), "Explorer: The Frozen North");
                        continue;
                    }
                    if (region.equalsIgnoreCase("underworld")) {
                        addAchievement(pl.getName(), "Explorer: The Lost City of Avalon");
                        continue;
                    }
                    if (region.equalsIgnoreCase("Cheifs")) {
                        addAchievement(pl.getName(), "Explorer: Cheif\'s Glory");
                        continue;
                    }
                    if (region.equalsIgnoreCase("Dead_Peaks")) {
                        addAchievement(pl.getName(), "Explorer: Deadpeaks");
                        continue;
                    }
                    if (region.equalsIgnoreCase("Mure")) {
                        addAchievement(pl.getName(), "Explorer: Mure");
                        continue;
                    }
                    if (region.equalsIgnoreCase("Sebrata")) {
                        addAchievement(pl.getName(), "Explorer: Sebrata");
                        continue;
                    }
                    if (pl.getWorld().getName().contains("fireydungeon")) {
                        addAchievement(pl.getName(), "Explorer: The Infernal Abyss");
                        continue;
                    }
                    if (region.equalsIgnoreCase("tutorial_island")) {
                        addAchievement(pl.getName(), "Explorer: Tutorial Island");
                        continue;
                    }
                }
            }
        }, 30 * 20L, 10 * 20L);

        log.info("[AchievementMechanics] V1.0 has been enabled.");
    }

    public void onDisable() {
        log.info("[AchievementMechanics] V1.0 has been disabled.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player pl = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                processLoginAchievements(pl.getName());
            }
        }.runTaskLater(Main.plugin, 20L);
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("Loot Chest")) {
            if (e.getInventory().contains(Material.CHEST)) {
                Player pl = (Player) e.getPlayer();
                addAchievement(pl.getName(), "A chest within a chest");
            }
        }
    }

    public static void addAchievement(String p_name, String a) {
        if (hasAchievement(p_name, a)) {
            return;
        }

        if (PlayerManager.getPlayerModel(p_name).getAchievements() == null) {
            PlayerManager.getPlayerModel(p_name).setAchievements(a);
            return;
        }

        String o_a = PlayerManager.getPlayerModel(p_name).getAchievements();

        if (o_a.endsWith(",")) {
            o_a = o_a + a + ",";
        } else {
            o_a = o_a + "," + a + ",";
        }

        PlayerManager.getPlayerModel(p_name).setAchievements(o_a);

        // LevelMechanics.addXP(p, 1000);
        if (Bukkit.getPlayer(p_name) != null) {
            Player pl = Bukkit.getPlayer(p_name);
            if (a.equalsIgnoreCase("Explorer: The Lost City of Avalon")) {
                LevelMechanics.addXP(pl, 1000);
            } else if (a.equalsIgnoreCase("Explorer: Avalon Peaks")) {
                LevelMechanics.addXP(pl, 750);
            } else if (a.equalsIgnoreCase("Explorer: Gloomy Hallows")) {
                LevelMechanics.addXP(pl, 500);
            }
            if (!Hive.first_login.contains(pl.getName()) && Hive.forum_usergroup.containsKey(pl.getName()) && Hive.forum_usergroup.get(pl.getName()) == -1) {
                pl.sendMessage(ChatColor.RED + "You just earned the '" + ChatColor.UNDERLINE + a + ChatColor.RED
                        + "' Achievement, unfortunetly you can't claim this achievement until you register at " + ChatColor.UNDERLINE
                        + "dungeonrealms.net/forum/register.php");
            } else {
                pl.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD.toString() + ">> " + ChatColor.DARK_AQUA.toString()
                        + ChatColor.UNDERLINE.toString() + "Achievement Unlocked:" + ChatColor.DARK_AQUA.toString() + " '" + ChatColor.GRAY + a
                        + ChatColor.DARK_AQUA.toString() + "'!");
                pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                // pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0),
                // Particle.TOWNAURA, 1F, 10);
                try {
                    ParticleEffect.sendToLocation(ParticleEffect.TOWN_AURA, pl.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(),
                            new Random().nextFloat(), 1F, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("[AchievementMechanics] Added achievement '" + a + "' to player " + p_name + "!");

        int achievement_count = o_a.split(",").length;
        if (achievement_count >= 10) {
            addAchievement(p_name, "Dungeon Realms Novice");
            if (achievement_count >= 25) {
                addAchievement(p_name, "Dungeon Realms Apprentice");
                if (achievement_count >= 50) {
                    addAchievement(p_name, "Dungeon Realms Adept");
                    if (achievement_count >= 100) {
                        addAchievement(p_name, "Dungeon Realms Expert");
                        if (achievement_count >= 200) {
                            addAchievement(p_name, "Dungeon Realms Master");
                        }
                    }
                }
            }
        }

        int explorer_count = getExplorerAchievementCount(p_name);
        if (explorer_count >= 10) {
            addAchievement(p_name, "Tourist");
            if (explorer_count >= 20) {
                addAchievement(p_name, "Adventurer");
            }
        }

    }

    public static int getExplorerAchievementCount(String p_name) {
        int count = 0;
        if (PlayerManager.getPlayerModel(p_name).getAchievements() != null) {
            for (String s : PlayerManager.getPlayerModel(p_name).getAchievements().split(",")) {
                if (s.toLowerCase().contains("explorer:")) {
                    count++;
                }
            }
        }

        return count;
    }

    public static boolean hasAchievement(String p_name, String a) {
        if (PlayerManager.getPlayerModel(p_name).getAchievements() == null) {
            return false;
        }
        String al = PlayerManager.getPlayerModel(p_name).getAchievements();
        for (String achiev : al.split(",")) {
            if (achiev.equalsIgnoreCase(a)) {
                return true;
            }
        }
        return false;
    }

    public static void processLoginAchievements(String p_name) {

        if (!(Hive.first_login.contains(p_name))) {
            AchievementMechanics.addAchievement(p_name, "Explorer: Tutorial Island");
        }

        if (RealmMechanics.realm_tier.containsKey(p_name)) {
            int tier = RealmMechanics.realm_tier.get(p_name);
            if (tier >= 2) {
                AchievementMechanics.addAchievement(p_name, "Expanding I");
                if (tier >= 4) {
                    AchievementMechanics.addAchievement(p_name, "Expanding II");
                    if (tier >= 6) {
                        AchievementMechanics.addAchievement(p_name, "Expanding III");
                        if (tier == 7) {
                            AchievementMechanics.addAchievement(p_name, "Expanding IV");
                        }
                    }
                }
            }
        }

        if (MoneyMechanics.bank_map.containsKey(p_name)) {
            int bank_net = MoneyMechanics.bank_map.get(p_name);
            if (bank_net >= 100) {
                AchievementMechanics.addAchievement(p_name, "Acquire Currency I");
                if (bank_net >= 1000) {
                    AchievementMechanics.addAchievement(p_name, "Acquire Currency II");
                    if (bank_net >= 5000) {
                        AchievementMechanics.addAchievement(p_name, "Acquire Currency III");
                        if (bank_net >= 10000) {
                            AchievementMechanics.addAchievement(p_name, "Acquire Currency IV");
                            if (bank_net >= 50000) {
                                AchievementMechanics.addAchievement(p_name, "Acquire Currency V");
                                if (bank_net >= 100000) {
                                    AchievementMechanics.addAchievement(p_name, "Acquire Currency VI");
                                    if (bank_net >= 500000) {
                                        AchievementMechanics.addAchievement(p_name, "Acquire Currency VII");
                                        if (bank_net >= 1000000) {
                                            AchievementMechanics.addAchievement(p_name, "Acquire Currency IX");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (PetMechanics.player_pets.containsKey(p_name)) {
            if (PetMechanics.player_pets.get(p_name).contains("baby_zombie")
                    || (Hive.forum_usergroup.containsKey(p_name) && Hive.forum_usergroup.get(p_name) == 9)) {
                AchievementMechanics.addAchievement(p_name, "Old Timer");
            }
            if (PetMechanics.player_pets.get(p_name).size() > 0) {
                AchievementMechanics.addAchievement(p_name, "A Companion");
            }
            if (PetMechanics.player_pets.get(p_name).size() >= 3) {
                AchievementMechanics.addAchievement(p_name, "The Tamer");
            }
        }

        if (PermissionMechanics.getRank(p_name).contains("sub")) {
            AchievementMechanics.addAchievement(p_name, "Subscriber");
        }
        if (PermissionMechanics.getRank(p_name).contains("sub+")) {
            AchievementMechanics.addAchievement(p_name, "Subscriber");
            AchievementMechanics.addAchievement(p_name, "Subscriber+");
        }
        if (PermissionMechanics.getRank(p_name).contains("sub++")) {
            AchievementMechanics.addAchievement(p_name, "Subscriber");
            AchievementMechanics.addAchievement(p_name, "Subscriber+");
            AchievementMechanics.addAchievement(p_name, "Lifetime Subscriber");
        }

        if (PermissionMechanics.getRank(p_name).contains("pmod")) {
            AchievementMechanics.addAchievement(p_name, "Eyes and Ears");
        }

        if (GuildMechanics.inGuild(p_name)) {
            AchievementMechanics.addAchievement(p_name, "Guildmember");
        }

        if (Bukkit.getPlayer(p_name) != null) {
            Player pl = Bukkit.getPlayer(p_name);
            int music = pl.getInventory().first(Material.JUKEBOX);
            if (music != -1) {
                ItemStack music_box = pl.getInventory().getItem(music);
                if (music_box.hasItemMeta() && music_box.getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString())) {
                    AchievementMechanics.addAchievement(p_name, "The Bard");
                }
            }
        }
    }

    public static void processArmorAchievements(String p_name, ItemStack[] armor) {
        boolean t1 = true, t2 = true, t3 = true, t4 = true, t5 = true;
        for (ItemStack is : armor) {
            if (is == null || is.getType() == Material.AIR) {
                return; // Nothing.
            }
            if (ItemMechanics.getItemTier(is) != 1) {
                t1 = false;
            }
            if (ItemMechanics.getItemTier(is) != 2) {
                t2 = false;
            }
            if (ItemMechanics.getItemTier(is) != 3) {
                t3 = false;
            }
            if (ItemMechanics.getItemTier(is) != 4) {
                t4 = false;
            }
            if (ItemMechanics.getItemTier(is) != 5) {
                t5 = false;
            }
        }

        if (t1) {
            addAchievement(p_name, "Full T1");
        }
        if (t2) {
            addAchievement(p_name, "Full T2");
        }
        if (t3) {
            addAchievement(p_name, "Full T3");
        }
        if (t4) {
            addAchievement(p_name, "Full T4");
        }
        if (t5) {
            addAchievement(p_name, "Full T5");
        }

        if (!t1 && !t2 && !t3 && !t4 && !t5) {
            addAchievement(p_name, "Mix and Match");
        }

    }

}
