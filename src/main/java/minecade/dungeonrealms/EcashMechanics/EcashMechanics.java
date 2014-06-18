package minecade.dungeonrealms.EcashMechanics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.EcashMechanics.commands.CommandCheckECash;
import minecade.dungeonrealms.EcashMechanics.commands.CommandECash;
import minecade.dungeonrealms.EcashMechanics.commands.CommandFireWand;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.Hive.ParticleEffect;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemGenerators;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.PetMechanics.PetMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.config.Config;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.block.CraftJukebox;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class EcashMechanics implements Listener {
    static Logger log = Logger.getLogger("Minecraft");

    public static volatile HashMap<ItemStack, Integer> firework_use_count = new HashMap<ItemStack, Integer>();
    // Item, counts # of uses of firework wand, at 5 uses starts cooldown.

    public static volatile HashMap<ItemStack, Long> firework_cooldown = new HashMap<ItemStack, Long>();
    // Handles cooldown of firework wand.

    public static volatile HashMap<ItemStack, Long> firework_last_use = new HashMap<ItemStack, Long>();
    // Last time firework was used, if >20s, use_count is reset.

    public static volatile HashMap<String, Integer> destruction_use_count = new HashMap<String, Integer>();
    // Item, counts # of uses of destruction wand, at 5 uses starts cooldown.

    public static volatile HashMap<String, Long> destruction_cooldown = new HashMap<String, Long>();
    // Handles cooldown of destruction wand.

    public static volatile HashMap<String, Long> destruction_last_use = new HashMap<String, Long>();
    // Last time destruction was used, if >20s, use_count is reset.

    public static List<String> flame_trail = new ArrayList<String>();
    // List of all player names with active flame_trail effect.

    public static List<String> flaming_armor = new ArrayList<String>();
    // List of all player names with active flame_trail effect.

    public static List<String> music_spirits = new ArrayList<String>();
    // List of all player names with active musicial_spirit effect.

    public static List<String> demonic_aura = new ArrayList<String>();
    // List of all player names with active demonic aura effect.

    public static volatile HashMap<String, Location> music_box_placement = new HashMap<String, Location>();
    // Where to put the music box once they pick a song.

    /*
     * public static volafle HashMap<String, Material> music_box_record = new HashMap<String, Material>(); // Where to put the music box once they pick a song.
     */

    public static volatile HashMap<Block, Long> music_box_timeout = new HashMap<Block, Long>();
    // Controls the poofing of music boxes after 60 seconds.

    public static volatile ConcurrentHashMap<String, Long> music_box_cooldown = new ConcurrentHashMap<String, Long>();
    // Prevents player from placing 1 music box faster than X seconds.

    public static volatile HashMap<Block, String> music_box_ownership = new HashMap<Block, String>();
    // Prevents player from placing 1 music box faster than X seconds.

    public static Inventory music_select;
    // Music selection inventory.

    public static List<String> sending_global_chat = new ArrayList<String>();
    // Players entering their global chat message.

    public static List<String> confirm_loot_buff = new ArrayList<String>();
    // Players confirming use of 1.20x loot for 30 minutes.

    public static List<String> confirm_profession_buff = new ArrayList<String>();
    // Players confirming use of 1.20x exp for 30 minutes.

    public static HashMap<String, ItemStack> item_lore_being_added = new HashMap<String, ItemStack>();
    // Item that is having a lore line being added to it.

    public static HashMap<String, ItemStack> item_name_change = new HashMap<String, ItemStack>();
    // Item that is having a lore line being added to it.

    public static HashMap<String, String> personal_clones_msg = new HashMap<String, String>();
    // Player_name, Time when npc was spawned -- used for npc despawn and npc respawn

    // public static HashMap<String, NPC> personal_clones = new HashMap<String, NPC>();
    // Player_name, Time when npc was spawned -- used for npc despawn and npc respawn

    public static HashMap<String, Location> personal_clone_location = new HashMap<String, Location>();
    // Player_name, Time when npc was spawned -- used for npc despawn and npc respawn

    public static HashMap<String, String> personal_clones_msg_pending = new HashMap<String, String>();
    // Player_name, Time when npc was spawned -- used for npc despawn and npc respawn

    public static ConcurrentHashMap<Location, Material> gold_curse_blocks = new ConcurrentHashMap<Location, Material>();
    // Location, Material to transform back to.

    public static ConcurrentHashMap<Location, Integer> gold_curse_blocks_timing = new ConcurrentHashMap<Location, Integer>();
    // Location, Material to transform back to.

    public static ConcurrentHashMap<String, String> ecash_storage_map = new ConcurrentHashMap<String, String>();
    // Player_Name, Ecash_Storage_Contents (string)

    public static List<String> gold_curse = new ArrayList<String>();
    // Players with active 'gold_curse' on them.

    EcashMechanics instance;

    @SuppressWarnings("deprecation")
    public void onEnable() {
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);

        Main.plugin.getCommand("checkecash").setExecutor(new CommandCheckECash());
        Main.plugin.getCommand("ecash").setExecutor(new CommandECash());
        Main.plugin.getCommand("firewand").setExecutor(new CommandFireWand());

        music_select = Bukkit.createInventory(null, 18, "Music Selection");
        music_select.addItem(new ItemStack(Material.getMaterial(2256)));
        music_select.addItem(new ItemStack(Material.getMaterial(2257)));
        music_select.addItem(new ItemStack(Material.RECORD_3));
        music_select.addItem(new ItemStack(Material.RECORD_4));
        music_select.addItem(new ItemStack(Material.RECORD_5));
        music_select.addItem(new ItemStack(Material.RECORD_6));
        music_select.addItem(new ItemStack(Material.RECORD_7));
        music_select.addItem(new ItemStack(Material.RECORD_8));
        music_select.addItem(new ItemStack(Material.RECORD_9));
        music_select.addItem(new ItemStack(Material.RECORD_10));
        music_select.addItem(new ItemStack(Material.RECORD_11));
        music_select.addItem(new ItemStack(Material.RECORD_12));
        music_select.addItem(new ItemStack(Material.getMaterial(2256)));
        music_select.addItem(new ItemStack(Material.getMaterial(2257)));
        music_select.addItem(new ItemStack(Material.RECORD_3));
        music_select.addItem(new ItemStack(Material.RECORD_4));
        music_select.addItem(new ItemStack(Material.RECORD_5));
        music_select.addItem(new ItemStack(Material.RECORD_6));

        int index = 0;
        for (ItemStack is : music_select.getContents()) {
            if (is == null || is.getType() == Material.AIR) {
                music_select.setItem(index, ItemMechanics.signCustomItem(Material.PISTON_MOVING_PIECE, (short) 0, "", ""));
            }
            index++;
        }

        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                for (Entry<Block, Long> data : music_box_timeout.entrySet()) {
                    long timeout = data.getValue();
                    if ((System.currentTimeMillis() - timeout) >= 360 * 1000) {
                        // Despawn.
                        final Block b = data.getKey();
                        new BukkitRunnable() {
                            public void run() {
                                CraftJukebox cj = (CraftJukebox) b.getState();
                                cj.setPlaying(Material.AIR);
                                cj.setRawData((byte) 0x0);
                                b.setType(Material.AIR);
                            }
                        }.runTask(Main.plugin);

                        Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(b.getLocation().getX()),
                                (int) Math.round(b.getLocation().getY()), (int) Math.round(b.getLocation().getZ()), 84, false);
                        ((CraftServer) Bukkit.getServer())
                                .getServer()
                                .getPlayerList()
                                .sendPacketNearby(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 24,
                                        ((CraftWorld) b.getWorld()).getHandle().dimension, particles);

                        String owner = music_box_ownership.get(b);
                        music_box_placement.remove(owner);

                        music_box_ownership.remove(b);
                        music_box_timeout.remove(b);
                    } else {
                        Block b = data.getKey();
                        if (b.getType() == Material.JUKEBOX) {
                            try {
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, b.getLocation().add(0.50, 0.95, 0.50), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.5F, 40);
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, b.getLocation().add(1.50, 1.0, 0.50), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.02F, 2);
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, b.getLocation().add(-1.50, 1.0, 0.50), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.02F, 2);
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, b.getLocation().add(0.50, 1.0, 1.50), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.02F, 2);
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, b.getLocation().add(0.50, 1.0, -1.50), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.02F, 2);
                            } catch (Exception err) {
                                continue;
                            }
                        }
                    }
                }

            }
        }, 10 * 20L, 5L);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                for (Entry<Location, Integer> data : gold_curse_blocks_timing.entrySet()) {
                    Location loc = data.getKey();
                    int seconds_left = data.getValue();
                    seconds_left--;
                    if (seconds_left <= 0) {
                        Material m = gold_curse_blocks.get(loc);
                        loc.getBlock().setType(m);
                        gold_curse_blocks_timing.remove(loc);
                        gold_curse_blocks.remove(loc);
                        continue;
                    }
                    gold_curse_blocks_timing.put(loc, seconds_left);

                }
            }
        }, 20 * 20L, 3L);

        /*
         * this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { public void run() { for(Entry<Location,Material> data :
         * gold_curse_blocks.entrySet()){ Location loc = data.getKey(); if(gold_curse_blocks_timing.containsKey(loc) && gold_curse_blocks_timing.get(loc) <= 0){
         * Material m = data.getValue(); loc.getBlock().setType(m); gold_curse_blocks_timing.remove(loc); gold_curse_blocks.remove(loc); } } } }, 20 * 20L, 3L);
         */

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
            public void run() {
                for (String s : flaming_armor) {
                    if (Bukkit.getServer().getPlayer(s) != null) {
                        Player pl = Bukkit.getServer().getPlayer(s);
                        try {
                            pl.getWorld().playEffect(pl.getLocation().add(0, 1, 0), Effect.MOBSPAWNER_FLAMES, 1, 20);
                        } catch (ConcurrentModificationException cme) {
                            continue;
                        }
                    }
                }

            }
        }, 10 * 20L, 5L);

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
            public void run() {
                for (String s : demonic_aura) {
                    try {
                        if (Bukkit.getServer().getPlayer(s) != null) {
                            Player pl = Bukkit.getServer().getPlayer(s);

                            if ((System.currentTimeMillis() - RealmMechanics.recent_movement.get(s)) <= 200) {
                                ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation().add(0, 0.25, 0), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 1F, 10);

                            } else {
                                ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation().add(0, 0.25, 0), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.2F, 2);
                            }

                            if (new Random().nextInt(100) == 77) {
                                int sound = new Random().nextInt(3);
                                if (sound == 0) {
                                    pl.getWorld().playSound(pl.getLocation(), Sound.ENDERMAN_STARE, 1F, 1F);
                                }
                                if (sound == 1) {
                                    pl.getWorld().playSound(pl.getLocation(), Sound.ENDERMAN_SCREAM, 1F, 3F);
                                }
                                if (sound == 2) {
                                    pl.getWorld().playSound(pl.getLocation(), Sound.GHAST_MOAN, 1F, 1F);
                                }
                            }
                        }
                    } catch (Exception err) {
                        continue;
                    }
                }
                for (String s : flame_trail) {
                    try {
                        if (Bukkit.getServer().getPlayer(s) != null) {
                            if ((System.currentTimeMillis() - RealmMechanics.recent_movement.get(s)) <= 200) {
                                Player pl = Bukkit.getServer().getPlayer(s);
                                ParticleEffect.sendToLocation(ParticleEffect.FLAME, pl.getLocation().add(0, 0.1, 0), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.02F, 10);
                            }
                        }
                    } catch (Exception err) {
                        continue;
                    }
                }
                for (String s : music_spirits) {
                    try {
                        if (Bukkit.getServer().getPlayer(s) != null) {
                            if ((System.currentTimeMillis() - RealmMechanics.recent_movement.get(s)) <= 100) {
                                Player pl = Bukkit.getServer().getPlayer(s);
                                ParticleEffect.sendToLocation(ParticleEffect.NOTE, pl.getLocation().add(0, 0.75, 0), new Random().nextFloat(),
                                        new Random().nextFloat(), new Random().nextFloat(), 0.5F, 3);

                                Sound sd = null;
                                // int ran_s = new Random().nextInt(7);
                                /*
                                 * if(ran_s == 0){ sd = Sound.NOTE_BASS; } if(ran_s == 1){ sd = Sound.NOTE_BASS_DRUM; } if(ran_s == 2){ sd =
                                 * Sound.NOTE_BASS_GUITAR; } if(ran_s == 3){ sd = Sound.NOTE_PIANO; } if(ran_s == 4){ sd = Sound.NOTE_PLING; } if(ran_s == 5){
                                 * sd = Sound.NOTE_SNARE_DRUM; } if(ran_s == 6){ sd = Sound.NOTE_STICKS; }
                                 */
                                sd = Sound.NOTE_PIANO;

                                float ran_pitch = 1F + (new Random().nextFloat()) - (new Random().nextFloat());
                                float ran_vol = 0.35F + (new Random().nextFloat()) - (new Random().nextFloat());

                                pl.getWorld().playSound(pl.getLocation(), sd, ran_vol, ran_pitch);
                            }
                        }
                    } catch (Exception err) {
                        continue;
                    }
                }
            }
        }, 10 * 20L, 2L);

        log.info("[ECASHMechanics] has been enabled.");
    }

    public void onDisable() {
        log.info("[ECASHMechanics] has been disabled.");
    }

    public static boolean isGlobalLootBuff(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Global Loot Buff")) {
            return true;
        }
        return false;
    }

    public static boolean isGChatModifier(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Global Chat Amplifier")) {
            return true;
        }
        return false;
    }

    public static boolean isGlobalMessanger(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Global Messenger")) {
            return true;
        }
        return false;
    }

    public static boolean isMusicBox(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Mobile Musicbox")) {
            return true;
        }
        return false;
    }

    public boolean isMusicalSpirit(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Musical Spirit")) {
            return true;
        }
        return false;
    }

    public boolean isFireworkWand(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Firework Wand")) {
            return true;
        }
        return false;
    }

    public boolean isGoldenCurse(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Golden Curse")) {
            return true;
        }
        return false;
    }

    public boolean isDestructionWand(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Destructo Wand")) {
            return true;
        }
        return false;
    }

    public boolean isFlameTrail(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Flame Trail")) {
            return true;
        }
        return false;
    }

    public boolean isDemonicAura(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Demonic Aura")) {
            return true;
        }
        return false;
    }

    public boolean isPersonalClone(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Personal Clone")) {
            return true;
        }
        return false;
    }

    public boolean isFlameArmor(ItemStack is) {
        if (is != null
                && is.hasItemMeta()
                && is.getItemMeta().hasDisplayName()
                && ((ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Blazing Armor")) || ChatColor.stripColor(
                        is.getItemMeta().getDisplayName()).equalsIgnoreCase("Lava Armor"))) {
            return true;
        }
        return false;
    }

    public static boolean isZombieHorseSkin(ItemStack is) {
        if (is != null && is.getType() == Material.ROTTEN_FLESH && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Zombie Horse Skin"))) {
            return true;
        }
        return false;
    }

    public static boolean isSkeletonHorseSkin(ItemStack is) {
        if (is != null && is.getType() == Material.BONE && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Skeleton Horse Skin"))) {
            return true;
        }
        return false;
    }

    public boolean isItemLoreTag(ItemStack is) {
        if (is != null && is.getType() == Material.ENCHANTED_BOOK && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Item Lore Book"))) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isItemOwnershipTag(ItemStack is) {
        if (is != null && is.getType() == Material.getMaterial(421) && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Item Name Tag"))) {
            return true;
        }
        return false;
    }

    public boolean isGlobalProfessionBoost(ItemStack is) {
        if (is != null && is.getType() == Material.GOLDEN_CARROT && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Global Skill EXP Buff"))) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isItemLoreRemoval(ItemStack is) {
        if (is != null && is.getType() == Material.getMaterial(373) && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Item Lore Removal"))) {
            return true;
        }
        return false;
    }

    public static ItemStack removeCustomLoreLines(ItemStack is) {
        if (is != null && is.hasItemMeta() && getLoreLines(is) > 0) {
            List<String> lore = is.getItemMeta().getLore();
            List<String> new_lore = new ArrayList<String>();
            for (String s : lore) {
                if (s.startsWith(ChatColor.GOLD.toString() + ChatColor.ITALIC.toString())) {
                    continue; // Skip.
                }
                new_lore.add(s);
            }

            ItemMeta im = is.getItemMeta();
            im.setLore(new_lore);
            is.setItemMeta(im);
        }

        return is;
    }

    public static int getLoreLines(ItemStack is) {
        int count = 0;
        if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
            List<String> lore = is.getItemMeta().getLore();
            for (String s : lore) {
                if (s.startsWith(ChatColor.GOLD.toString() + ChatColor.ITALIC.toString())) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int getEmptyInventorySlots(Player pl) {
        int empty_slots = 0;

        for (ItemStack is : pl.getInventory()) {
            if (is == null || is.getType() == Material.AIR) {
                empty_slots++;
            }
            continue;
        }

        return empty_slots;
    }

    public static ItemStack setMessagesLeftOnGlobalAmplifier(ItemStack is, int new_amount, boolean subtract_one) {
        if (!(isGChatModifier(is))) {
            return is;
        }

        List<String> lore = is.getItemMeta().getLore();
        List<String> new_lore = new ArrayList<String>();

        for (String s : lore) {
            if (ChatColor.stripColor(s).startsWith("Messages:")) {
                String s_nc = ChatColor.stripColor(s);
                // Hijack.
                int messages_left = Integer.parseInt(s_nc.substring(s_nc.indexOf(" ") + 1, s_nc.length()));

                if (subtract_one) {
                    messages_left = messages_left - 1;
                } else {
                    messages_left = new_amount;
                }

                if (messages_left <= 0) {
                    // Destroy item.
                    return new ItemStack(Material.AIR);
                }

                new_lore.add(ChatColor.GOLD + "Messages: " + ChatColor.GRAY.toString() + messages_left);
            } else {
                new_lore.add(s);
            }
        }

        ItemMeta im = is.getItemMeta();
        im.setLore(new_lore);
        is.setItemMeta(im);
        return is;
    }

    public static boolean hasGlobalAmplifier(Player pl) {
        for (ItemStack is : pl.getInventory()) {
            if (is != null && isGChatModifier(is)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack tickGlobalAmplifier(Player pl) {
        int index = 0;
        for (ItemStack is : pl.getInventory()) {
            if (is != null && isGChatModifier(is)) {
                ItemStack new_is = setMessagesLeftOnGlobalAmplifier(is, -1, true);
                pl.getInventory().setItem(index, new_is);
                return new_is;
            }
            index++;
        }
        return null;
    }

    public static int getMessagesLeftOnGlobalAmplifier(ItemStack is) {
        if (!(isGChatModifier(is))) {
            return 0;
        }

        List<String> lore = is.getItemMeta().getLore();
        for (String s : lore) {
            s = ChatColor.stripColor(s);
            if (s.startsWith("Messages:")) {
                int messages_left = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
                return messages_left;
            }
        }

        return 0;
    }

    public static void sendGlobalMessage(String msg, String from_server, String sender_string) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            String personal_msg = msg;
            if (ChatMechanics.hasAdultFilter(pl.getName())) {
                personal_msg = ChatMechanics.censorMessage(msg);
            }

            personal_msg = ChatMechanics.fixCapsLock(personal_msg);

            if (personal_msg.endsWith(" ")) {
                personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
            }

            pl.sendMessage("");
            pl.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> (" + from_server + ") " + ChatColor.RESET + sender_string + ": " + ChatColor.GOLD
                    + personal_msg);
            pl.sendMessage("");
            pl.playSound(pl.getLocation(), Sound.BAT_TAKEOFF, 1F, 4F);
        }
    }

    public static boolean hasSkeletonHorseSkin(Player pl) {
        for (ItemStack is : pl.getInventory()) {
            if (is == null) {
                continue;
            }
            if (isSkeletonHorseSkin(is)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasZombieHorseSkin(Player pl) {
        for (ItemStack is : pl.getInventory()) {
            if (is == null) {
                continue;
            }
            if (isZombieHorseSkin(is)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("E-Cash Storage")) {
            Player pl = (Player) e.getPlayer();
            // pl.setMetadata("ecash_storage", new FixedMetadataValue(Hive.instance, Hive.convertInventoryToString(null, e.getInventory(), false)));
            ecash_storage_map.put(pl.getName(), Hive.convertInventoryToString(null, e.getInventory(), false));
            pl.sendMessage(ChatColor.GRAY + "E-Cash Storage: " + ChatColor.WHITE + "Your trinkets are safe with me!");
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void eatRollbackFood(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && e.hasItem()
                && e.getItem().hasItemMeta()
                && e.getItem().getItemMeta().hasLore()
                && e.getItem().getItemMeta().getLore().get(e.getItem().getItemMeta().getLore().size() - 1)
                        .equalsIgnoreCase(ChatColor.GREEN.toString() + "Rollback Appology")) {
            e.setCancelled(true);
            Player pl = e.getPlayer();
            if (pl.getItemInHand().getAmount() <= 1) {
                pl.setItemInHand(new ItemStack(Material.AIR));
            } else {
                ItemStack new_cookie = pl.getItemInHand();
                new_cookie.setAmount(new_cookie.getAmount() - 1);
                pl.setItemInHand(new_cookie);
            }
            pl.playSound(pl.getLocation(), Sound.EAT, 1F, 1F);

            try {
                ParticleEffect.sendToLocation(ParticleEffect.HEART, pl.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(),
                        new Random().nextFloat(), 1F, 5);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            pl.updateInventory();
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onRollbackBackerClick(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked() instanceof Player) {
            Player trader = (Player) e.getRightClicked();
            if (!(trader.hasMetadata("NPC"))) {
                return;
            } // Only NPC's matter.
            if (!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Rollback_Baker"))) {
                return;
            } // Only 'Trader' should do anything.
            e.setCancelled(true);

            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(ItemGenerators.customGenerator("rollback_cookie"));
                p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
                p.updateInventory();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void controlEcashStorageContent(InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("E-Cash Storage")) {
            Player pl = (Player) e.getWhoClicked();
            if (!(e.isLeftClick() || e.isRightClick() || e.isShiftClick())) {
                e.setCancelled(true);
                pl.updateInventory();
                return;
            }
            if ((e.getCursor() != null && e.getCursor().getType() != Material.AIR && !(PetMechanics.isPermUntradeable(e.getCursor())))
                    || ((e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && !(PetMechanics.isPermUntradeable(e.getCurrentItem()))))) {
                if ((e.getCurrentItem() != null && MountMechanics.isMount(e.getCurrentItem()))
                        || (e.getCursor() != null && MountMechanics.isMount(e.getCursor()))) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + "store a mount in the E-Case Storage.");
                    e.setCancelled(true);
                    pl.updateInventory();
                    return;
                }
                if ((e.getCurrentItem() != null && MountMechanics.isMule(e.getCurrentItem()))
                        || (e.getCursor() != null && MountMechanics.isMule(e.getCursor()))) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + "store a mule in the E-Cash Storage.");
                    e.setCancelled(true);
                    pl.updateInventory();
                    return;
                }
                e.setCancelled(true);
                pl.updateInventory();
                pl.sendMessage(ChatColor.RED + "The E-Cash Storage only accept " + ChatColor.UNDERLINE + "Permenant Untradeable" + ChatColor.RED + " items.");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        for (ItemStack is : pl.getInventory()) {
            if (is == null || is.getType() == Material.AIR || !is.hasItemMeta() || !is.getItemMeta().hasDisplayName()) {
                continue;
            }
            ItemMeta im = is.getItemMeta();
            if (im.getDisplayName().contains("Lava Armor")) {
                im.setDisplayName(ChatColor.GOLD + "Blazing Armor");
                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Adds a firey effect around the player.");
                lore.add(ChatColor.GRAY + "Permenant Untradeable");
                im.setLore(lore);
                is.setItemMeta(im);
            }
        }
    }

    @EventHandler
    public void onConfirmProfessionBuff(AsyncPlayerChatEvent e) {
        Player pl = e.getPlayer();
        if (confirm_profession_buff.contains(pl.getName())) {
            e.setCancelled(true);
            String msg = e.getMessage();
            final String lserver = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));

            if (msg.equalsIgnoreCase("y")) {
                // BUFF TIME BOYS!
                confirm_profession_buff.remove(pl.getName());
                final ChatColor p_color = ChatMechanics.getPlayerColor(pl, pl);
                final String prefix = ChatMechanics.getPlayerPrefix(pl);
                final String sender_string = prefix + p_color + pl.getName();

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        CommunityMechanics.sendPacketCrossServer("[professionbuff]" + sender_string + "@" + lserver, -1, true);
                    }
                });
                t.start();

                Bukkit.getServer().broadcastMessage("");
                Bukkit.getServer().broadcastMessage(
                        ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + "(" + lserver + ") " + ChatColor.RESET + sender_string + ChatColor.GOLD
                                + " has just activated " + ChatColor.UNDERLINE + "+20% Global Mining / Fishing EXP Rates" + ChatColor.GOLD
                                + " for 30 minutes by using 'Global Profession Buff' from the E-CASH store!");
                Bukkit.getServer().broadcastMessage("");

                ProfessionMechanics.profession_buff = true;
                ProfessionMechanics.profession_buff_timeout = System.currentTimeMillis() + (1800 * 1000); // 30 minutes.
            } else {
                confirm_profession_buff.remove(pl.getName());
                if (pl.getInventory().firstEmpty() != -1) {
                    confirm_profession_buff.remove(pl.getName());
                    pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.profession_exp_boost));
                    pl.sendMessage(ChatColor.RED + "Global Profession EXP 20% Multiplier - " + ChatColor.BOLD + "CANCELLED");
                } else {
                    pl.sendMessage(ChatColor.RED + "Your inventory is currently full. Cannot readd Global Profession Buff until you clear space.");
                    pl.sendMessage(ChatColor.GRAY + "Clear some space and type 'cancel' again.");
                }
            }
        }
    }

    @EventHandler
    public void onConfirmLootBuff(AsyncPlayerChatEvent e) {
        Player pl = e.getPlayer();
        if (confirm_loot_buff.contains(pl.getName())) {
            e.setCancelled(true);
            String msg = e.getMessage();
            final String lserver = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));

            if (msg.equalsIgnoreCase("y")) {
                // BUFF TIME BOYS!
                confirm_loot_buff.remove(pl.getName());
                final ChatColor p_color = ChatMechanics.getPlayerColor(pl, pl);
                final String prefix = ChatMechanics.getPlayerPrefix(pl);
                final String sender_string = prefix + p_color + pl.getName();

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        CommunityMechanics.sendPacketCrossServer("[lootbuff]" + sender_string + "@" + lserver, -1, true);
                    }
                });
                t.start();

                Bukkit.getServer().broadcastMessage("");
                Bukkit.getServer().broadcastMessage(
                        ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + "(" + lserver + ") " + ChatColor.RESET + sender_string + ChatColor.GOLD
                                + " has just activated " + ChatColor.UNDERLINE + "+20% Global Drop Rates" + ChatColor.GOLD
                                + " for 30 minutes by using 'Global Loot Buff' from the E-CASH store!");
                Bukkit.getServer().broadcastMessage("");

                MonsterMechanics.loot_buff_timeout = System.currentTimeMillis() + (1800 * 1000); // 30 minutes.
                MonsterMechanics.loot_buff = true;
            } else {
                confirm_loot_buff.remove(pl.getName());
                if (pl.getInventory().firstEmpty() != -1) {
                    confirm_loot_buff.remove(pl.getName());
                    pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.increased_drops));
                    pl.sendMessage(ChatColor.RED + "Global Loot 20% Multiplier - " + ChatColor.BOLD + "CANCELLED");
                } else {
                    pl.sendMessage(ChatColor.RED + "Your inventory is currently full. Cannot read Global Loot Buff until you clear space.");
                    pl.sendMessage(ChatColor.GRAY + "Clear some space and type 'cancel' again.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSendGlobalMessage(AsyncPlayerChatEvent e) {
        Player pl = e.getPlayer();
        if (sending_global_chat.contains(pl.getName())) {
            e.setCancelled(true);
            // We're sending some global chat w/ a messenger!
            String msg = e.getMessage();
            if (msg.contains(".com") || msg.contains(".net") || msg.contains(".org") || msg.contains("http://") || msg.contains("www.")) {
                if (!pl.isOp()) {
                    pl.sendMessage(ChatColor.RED + "No " + ChatColor.UNDERLINE + "URL's" + ChatColor.RED + " in your global messages please!");
                    return;
                }
            }

            if (msg.equalsIgnoreCase("cancel")) {
                if (pl.getInventory().firstEmpty() != -1) {
                    sending_global_chat.remove(pl.getName());
                    pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.global_microphone));
                    pl.sendMessage(ChatColor.RED + "Global Message - " + ChatColor.BOLD + "CANCELLED");
                } else {
                    pl.sendMessage(ChatColor.RED + "Your inventory is currently full. Cannot read Global Messenger until you clear space.");
                    pl.sendMessage(ChatColor.GRAY + "Clear some space and type 'cancel' again.");
                }
                return;
            }

            sending_global_chat.remove(pl.getName()); // Prevent spam abuse.

            final String lserver = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));

            final ChatColor p_color = ChatMechanics.getPlayerColor(pl, pl);
            final String prefix = ChatMechanics.getPlayerPrefix(pl);
            final String sender_string = prefix + p_color + pl.getName();

            final String f_msg = msg;

            sendGlobalMessage(msg, lserver, sender_string);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    CommunityMechanics.sendPacketCrossServer("[globalmessage]" + sender_string + "@" + lserver + ":" + f_msg, -1, true);
                }
            });
            t.start();
        }
    }

    @EventHandler
    public void onPlayerUsePersonalClone(PlayerInteractEvent e) {
        if (e.hasItem() && isPersonalClone(e.getItem()) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Result.DENY);
            e.setUseItemInHand(Result.DENY);
            Player pl = e.getPlayer();
            pl.sendMessage(ChatColor.RED + "This item is " + ChatColor.UNDERLINE + "temporarily" + ChatColor.RED + " disabled due to a 1.7 conversion issue.");

            /*
             * if(!personal_clones_msg_pending.containsKey(pl.getName())){ // Ok let's get a new message setup, then we'll move NPC if needed or just spawn one.
             * personal_clones_msg_pending.put(pl.getName(), ""); personal_clone_location.put(pl.getName(), pl.getTargetBlock(null, 4).getLocation().add(0, 1,
             * 0)); pl.sendMessage(ChatColor.GOLD + "Please enter a short message you'd like your clone to send when interacted with.");
             * pl.sendMessage(ChatColor.GRAY + "Alternatively, type '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to void ths summon."); } else
             * if(personal_clones_msg_pending.containsKey(pl.getName())){ personal_clone_location.put(pl.getName(), pl.getTargetBlock(null,
             * 4).getLocation().add(0, 1, 0)); pl.sendMessage(ChatColor.GOLD +
             * "Please enter a short message you'd like your clone to send when interacted with."); pl.sendMessage(ChatColor.GRAY + "Alternatively, type '" +
             * ChatColor.RED + "cancel" + ChatColor.GRAY + "' to void ths summon."); }
             */

            return;
        }
    }

    @EventHandler
    public void onPlayerUseLootBuff(PlayerInteractEvent e) {
        if (e.hasItem() && isGlobalLootBuff(e.getItem())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Result.DENY);
            e.setUseItemInHand(Result.DENY);
            Player pl = e.getPlayer();
            if (MonsterMechanics.loot_buff) {
                pl.sendMessage(ChatColor.RED + "There is already an experience buff active.");
                return;
            }
            if (!(confirm_loot_buff.contains(pl.getName()))) {
                pl.setItemInHand(new ItemStack(Material.AIR));
                confirm_loot_buff.add(pl.getName());
                long diff = System.currentTimeMillis() - Hive.serverStart;
                diff = (4 * 60 * 60 * 1000) - diff;
                int minutes = (int) (diff / 60000 % 60);
                pl.sendMessage("");
                if (minutes < 30) {
                    pl.sendMessage(ChatColor.RED + "This server will reboot in " + minutes
                            + " mins and you may not be able to use this buff to it's full potential");
                }

                pl.sendMessage(ChatColor.YELLOW
                        + "Are you sure you want to use this item? It will apply a 20% buff to all loot drops across all servers for 30 minutes. This cannot be undone once it has begun.");
                pl.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.GREEN + "Y" + ChatColor.GRAY + "' to confirm, or any other message to cancel.");
            }
        }
    }

    @EventHandler
    public void onPlayerUseProfessionBuff(PlayerInteractEvent e) {
        if (e.hasItem() && isGlobalProfessionBoost(e.getItem())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Result.DENY);
            e.setUseItemInHand(Result.DENY);
            Player pl = e.getPlayer();
            if (ProfessionMechanics.profession_buff) {
                pl.sendMessage(ChatColor.RED + "There is already a Profession buff active.");
                return;
            }
            if (!(confirm_profession_buff.contains(pl.getName()))) {
                pl.setItemInHand(new ItemStack(Material.AIR));
                confirm_profession_buff.add(pl.getName());
                long diff = System.currentTimeMillis() - Hive.serverStart;
                diff = (4 * 60 * 60 * 1000) - diff;
                int minutes = (int) (diff / 60000 % 60);
                pl.sendMessage("");
                if (minutes < 30) {
                    pl.sendMessage(ChatColor.RED + "This server will reboot in " + minutes
                            + " mins and you may not be able to use this buff to it's full potential");
                }

                pl.sendMessage(ChatColor.YELLOW
                        + "Are you sure you want to use this item? It will apply a 20% buff to all mining and fishing experience gains across all servers for 30 minutes. This cannot be undone once it has begun.");
                pl.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.GREEN + "Y" + ChatColor.GRAY + "' to confirm, or any other message to cancel.");
            }
        }
    }

    @EventHandler
    public void onPlayerUseGlobalMessenger(PlayerInteractEvent e) {
        if (e.hasItem() && isGlobalMessanger(e.getItem())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Result.DENY);
            e.setUseItemInHand(Result.DENY);
            Player pl = e.getPlayer();
            if (!(sending_global_chat.contains(pl.getName()))) {
                pl.setItemInHand(new ItemStack(Material.AIR)); // Remove the smega, if they cancel we'll return it.
                sending_global_chat.add(pl.getName());
                pl.sendMessage("");
                pl.sendMessage(ChatColor.YELLOW + "Please enter the message you'd like to send to " + ChatColor.UNDERLINE + "all servers" + ChatColor.YELLOW
                        + " -- think before you speak!");
                pl.sendMessage(ChatColor.GRAY + "Type 'cancel' (no apostrophes) to cancel this and get your Global Messenger back.");
                pl.sendMessage("");
            } else {
                pl.sendMessage(ChatColor.RED + "You already have a pending global messenger -- type 'cancel' before trying to send another.");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerUseEnderPerl(PlayerInteractEvent e) {
        if (e.hasItem() && isGChatModifier(e.getItem())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Result.DENY);
            e.setUseItemInHand(Result.DENY);
            Player pl = e.getPlayer();
            pl.sendMessage(ChatColor.RED + "This item is automatically used as long as it's in your inventory while you send a global message.");
            pl.updateInventory();
        }
    }

    /*
     * @EventHandler public void onPersonalCloneMessageEnter(AsyncPlayerChatEvent e){ Player pl = e.getPlayer();
     * if(personal_clones_msg_pending.containsKey(pl.getName())){ e.setCancelled(true); String msg = e.getMessage(); if(msg.equalsIgnoreCase("cancel")){
     * personal_clones_msg_pending.remove(pl.getName()); personal_clone_location.remove(pl.getName()); pl.sendMessage(ChatColor.RED + "Personal Clone - " +
     * ChatColor.BOLD + "CANCELLED"); return; }
     * 
     * Location spawn_loc = personal_clone_location.get(pl.getName());
     * 
     * if(MoneyMechanics.isThereABankChestNear(spawn_loc.getBlock(), 12)){ pl.sendMessage(ChatColor.RED + "You're too close to a bank chest.");
     * pl.sendMessage(ChatColor.GRAY + "Please move >12 blocks away from the chest and try again."); personal_clones_msg_pending.remove(pl.getName());
     * personal_clone_location.remove(pl.getName()); }
     * 
     * for(NPC nearby : personal_clones.values()){ if(nearby.getBukkitEntity().getLocation().distanceSquared(spawn_loc) <= 4){ pl.sendMessage(ChatColor.RED +
     * "You're too close to another player's clone!"); pl.sendMessage(ChatColor.GRAY + "Please move >2 blocks away and try again.");
     * personal_clones_msg_pending.remove(pl.getName()); personal_clone_location.remove(pl.getName()); } }
     * 
     * personal_clones_msg_pending.remove(pl.getName()); personal_clone_location.remove(pl.getName());
     * 
     * if(personal_clones.containsKey(pl.getName())){ NPC n = personal_clones.get(pl.getName());
     * n.getBukkitEntity().getWorld().spawnParticle(n.getBukkitEntity().getLocation(), Particle.MAGIC_CRIT, 1F, 10); n.removeFromWorld(); }
     * 
     * personal_clones_msg.put(pl.getName(), msg);
     * 
     * NPC n = Hive.m.spawnHumanNPC(ChatColor.GOLD.toString() + pl.getName(), pl.getTargetBlock(null, 4).getLocation().add(0, 1, 0));
     * n.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(this, "")); n.getBukkitEntity().setMetadata("clone", new FixedMetadataValue(this, ""));
     * 
     * Player pl_npc = (org.bukkit.entity.Player)n.getBukkitEntity(); pl_npc.setPlayerListName(""); HealthMechanics.setOverheadHP(pl_npc,
     * HealthMechanics.getPlayerHP(pl.getName())); pl.playSound(pl.getLocation(), Sound.HURT_FLESH, 1F, 0.25F); personal_clones.put(pl.getName(), n);
     * 
     * pl.sendMessage(ChatColor.GOLD + "Your clone has been summoned. Punch it to despawn, or right click to interact."); } }
     */

    public void lookAtEntity(Entity to_look_at, EntityLiving ent) {
        Location entLoc = ent.getBukkitEntity().getLocation(), target = to_look_at.getLocation();
        double xDiff = target.getX() - entLoc.getX();
        double yDiff = target.getY() - entLoc.getY();
        double zDiff = target.getZ() - entLoc.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        double newPitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
        if (zDiff < 0.0) {
            newYaw = newYaw + (Math.abs(180 - newYaw) * 2);
        }
        ent.yaw = (float) (newYaw - 90);
        ent.pitch = (float) newPitch;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemLoreEnter(AsyncPlayerChatEvent e) {
        Player pl = e.getPlayer();
        if (item_lore_being_added.containsKey(pl.getName())) {
            e.setCancelled(true);
            String msg = e.getMessage();

            if (msg.equalsIgnoreCase("cancel")) {
                int free_slots = getEmptyInventorySlots(pl);

                if (free_slots >= 2) {
                    // 1 slot for item lore, 1 for the item itself.
                    pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_lore_tag));
                    pl.getInventory().addItem(item_lore_being_added.get(pl.getName()));
                } else {
                    pl.sendMessage(ChatColor.RED + "Please ensure you have empty space in your inventory before trying to cancel this opperation.");
                    return;
                }

                item_lore_being_added.remove(pl.getName());
                pl.sendMessage(ChatColor.RED + "Item Lore Tag - " + ChatColor.BOLD + "CANCELLED");
                return;
            }

            msg = ChatMechanics.censorMessage(msg);
            // Censor all cuss words.

            if (msg.length() > 40) {
                pl.sendMessage(ChatColor.RED + "Your message is " + msg.length() + "/40 characters. Please shorten it.");
                return;
            }
            if(containsSymbols(msg)){
                pl.sendMessage(ChatColor.RED + "Your message " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " contain symbols.");
                return;
            }
            ItemStack is = item_lore_being_added.get(pl.getName());
            ItemMeta im = null;
            try {
                im = is.getItemMeta();
            } catch (NullPointerException err) {
                // No meta data, get some.
                im = MerchantMechanics.item_lore_tag.getItemMeta();
                im.setLore(new ArrayList<String>());
                im.setDisplayName(is.getType().name().substring(0, 1).toUpperCase()
                        + is.getType().name().substring(1, is.getType().name().length()).toLowerCase());
            }

            List<String> new_lore = new ArrayList<String>();

            if (im.hasLore()) {
                new_lore = im.getLore();
            }

            new_lore.add(ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + msg);
            im.setLore(new_lore);
            is.setItemMeta(im);

            pl.getInventory().addItem(is);
            pl.playSound(pl.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F);
            pl.updateInventory();

            item_lore_being_added.remove(pl.getName());
        }

        if (item_name_change.containsKey(pl.getName())) {
            e.setCancelled(true);
            String msg = e.getMessage();

            if (msg.equalsIgnoreCase("cancel")) {
                int free_slots = getEmptyInventorySlots(pl);

                if (free_slots >= 2) {
                    // 1 slot for item lore, 1 for the item itself.
                    pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_ownership_tag));
                    pl.getInventory().addItem(item_name_change.get(pl.getName()));
                } else {
                    pl.sendMessage(ChatColor.RED + "Please ensure you have empty space in your inventory before trying to cancel this opperation.");
                    return;
                }

                item_name_change.remove(pl.getName());
                pl.sendMessage(ChatColor.RED + "Item Name Change - " + ChatColor.BOLD + "CANCELLED");
                return;
            }

            msg = ChatColor.stripColor(ChatMechanics.censorMessage(msg));
            // Censor all cuss words.
            if (containsSymbols(msg)) {
                pl.sendMessage(ChatColor.RED + "Your message cannot contain symbols. Please type your name again.");
                return;
            }
            if (msg.length() > 40) {
                pl.sendMessage(ChatColor.RED + "Your message is " + msg.length() + "/40 characters. Please shorten it.");
                return;
            }

            ItemStack is = item_name_change.get(pl.getName());
            ItemMeta im = null;
            try {
                im = is.getItemMeta();
            } catch (NullPointerException err) {
                // No meta data, get some.
                im = MerchantMechanics.item_lore_tag.getItemMeta();
                im.setLore(new ArrayList<String>());
                im.setDisplayName(is.getType().name().substring(0, 1).toUpperCase()
                        + is.getType().name().substring(1, is.getType().name().length()).toLowerCase());
            }

            ChatColor cc = ProfessionMechanics.getTierColor(ItemMechanics.getItemTier(is));

            String i_name = cc.toString() + ChatColor.ITALIC + msg + ChatColor.RESET + cc.toString() + ChatColor.BOLD + " EC";
            if (im.hasDisplayName()) {
                String old_name = im.getDisplayName();
                if (old_name.startsWith(ChatColor.RED.toString() + "[+")) {
                    // Carry over scrolled status.
                    String scrolled_status = old_name.substring(0, old_name.indexOf(" "));
                    i_name = scrolled_status + " " + i_name;
                }
            }

            im.setDisplayName(i_name);

            is.setItemMeta(im);

            pl.getInventory().addItem(is);
            pl.playSound(pl.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F);
            pl.updateInventory();
            AchievementMechanics.addAchievement(pl.getName(), "Its Personal");

            item_name_change.remove(pl.getName());
        }
    }

    public boolean containsSymbols(String s) {
        if (s.contains("-") || s.contains("[") || s.contains("]") || s.contains("%") || s.contains("$") || s.contains("#") || s.contains("@")
                || s.contains("!") || s.contains("^") || s.contains("+") || s.contains("=")) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        if (item_lore_being_added.containsKey(pl.getName())) {
            // Give them stuff back.
            if (getEmptyInventorySlots(pl) >= 2) {
                pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_lore_tag));
                pl.getInventory().addItem(item_lore_being_added.get(pl.getName()));
            }
        }
        item_lore_being_added.remove(pl.getName());

        if (item_name_change.containsKey(pl.getName())) {
            // Give them stuff back.
            if (getEmptyInventorySlots(pl) >= 2) {
                pl.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_ownership_tag));
                pl.getInventory().addItem(item_name_change.get(pl.getName()));
            }
        }
        item_name_change.remove(pl.getName());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemLoreRemovalUse(InventoryClickEvent e) {
        final Player pl = (Player) e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            // Only allow in bank / inventory
            if (e.getCursor() != null && isItemLoreRemoval(e.getCursor()) && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                // Proceed.

                ItemStack o_current = e.getCurrentItem();
                e.setCancelled(true);
                ItemStack x_current = e.getCurrentItem();

                if (!RealmMechanics.isItemTradeable(x_current)) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item on an untradeable item.");
                    return;
                }

                if (x_current.getType() == Material.WRITTEN_BOOK || x_current.getType() == Material.NETHER_STAR || x_current.getType() == Material.QUARTZ) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " modify this item.");
                    return;
                }

                if (o_current.getAmount() > 1) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this on stacked items.");
                    return;
                }

                if (getLoreLines(x_current) <= 0) {
                    pl.sendMessage(ChatColor.RED + "This item does not have " + ChatColor.UNDERLINE + "any" + ChatColor.RED + " custom lore lines.");
                    return;
                }

                x_current.setAmount(1);

                ItemStack is = e.getCursor();
                ItemStack return_is = new ItemStack(Material.AIR);
                if (is.getAmount() > 1) {
                    // Subtract
                    int current_amount = is.getAmount();
                    is.setAmount(current_amount - 1);
                    return_is = is;
                }

                e.setCursor(return_is);

                e.setCurrentItem(new ItemStack(Material.AIR));
                if (o_current.getAmount() <= 1) {
                    o_current = new ItemStack(Material.AIR);
                } else if (o_current.getAmount() > 1) {
                    o_current = e.getCurrentItem();
                    int current_amount = o_current.getAmount();
                    o_current.setAmount(current_amount - 1);
                    pl.getInventory().addItem(o_current);
                    pl.updateInventory();
                }

                e.setCurrentItem(removeCustomLoreLines(x_current));
                pl.playSound(pl.getLocation(), Sound.WATER, 1F, 1F);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemLoreUse(InventoryClickEvent e) {
        final Player pl = (Player) e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            // Only allow in bank / inventory
            if (e.getCursor() != null && isItemLoreTag(e.getCursor()) && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                // Proceed.

                ItemStack o_current = e.getCurrentItem();
                e.setCancelled(true);
                ItemStack x_current = e.getCurrentItem();

                if (!RealmMechanics.isItemTradeable(x_current)) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item on an untradeable item.");
                    return;
                }
                if (ProfessionMechanics.isSkillItem(x_current)) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " modify this item.");
                    return;
                }
                if (x_current.getType() == Material.WRITTEN_BOOK || x_current.getType() == Material.NETHER_STAR || x_current.getType() == Material.QUARTZ) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " modify this item.");
                    return;
                }

                if (o_current.getAmount() > 1) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this on stacked items.");
                    return;
                }

                if (getLoreLines(x_current) >= 5) {
                    // Don't let them, enough lore lines.
                    pl.sendMessage(ChatColor.RED + "This item already has " + ChatColor.UNDERLINE + "5" + ChatColor.RED
                            + " custom lore lines. You cannot add more.");
                    return;
                }

                x_current.setAmount(1);

                item_lore_being_added.put(pl.getName(), x_current);

                ItemStack is = e.getCursor();
                ItemStack return_is = new ItemStack(Material.AIR);
                if (is.getAmount() > 1) {
                    // Subtract
                    int current_amount = is.getAmount();
                    is.setAmount(current_amount - 1);
                    return_is = is;
                }

                e.setCursor(return_is);

                e.setCurrentItem(new ItemStack(Material.AIR));
                if (o_current.getAmount() <= 1) {
                    o_current = new ItemStack(Material.AIR);
                } else if (o_current.getAmount() > 1) {
                    o_current = e.getCurrentItem();
                    int current_amount = o_current.getAmount();
                    o_current.setAmount(current_amount - 1);
                    pl.getInventory().addItem(o_current);
                    pl.updateInventory();
                }

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    public void run() {
                        pl.closeInventory();
                        pl.sendMessage(ChatColor.GOLD + "Please enter a " + ChatColor.UNDERLINE + "1 LINE" + ChatColor.GOLD
                                + " description to add to this item.");
                        pl.sendMessage(ChatColor.RED + "This opperation is non-refundable and non-reversable, type 'cancel' to void it.");
                    }
                }, 2L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemNametagUse(InventoryClickEvent e) {
        final Player pl = (Player) e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            // Only allow in bank / inventory
            if (e.getCursor() != null && isItemOwnershipTag(e.getCursor()) && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                // Proceed.
                ItemStack o_current = e.getCurrentItem();
                e.setCancelled(true);
                ItemStack x_current = e.getCurrentItem();

                if (!RealmMechanics.isItemTradeable(x_current)) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item on an untradeable item.");
                    return;
                }
                if (ProfessionMechanics.isSkillItem(o_current)) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " modify this item.");
                    return;
                }
                if (ItemMechanics.getDamageData(x_current).equalsIgnoreCase("no") && ItemMechanics.getArmorData(x_current).equalsIgnoreCase("no")
                        && !ProfessionMechanics.isSkillItem(x_current)) {
                    pl.sendMessage(ChatColor.RED + "You can only use this E-CASH item on weapon and armor.");
                    return;
                }

                if (o_current.getAmount() > 1) {
                    pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this on stacked items.");
                    return;
                }

                x_current.setAmount(1);
                item_name_change.put(pl.getName(), x_current);

                ItemStack is = e.getCursor();
                ItemStack return_is = new ItemStack(Material.AIR);
                if (is.getAmount() > 1) {
                    // Subtract
                    int current_amount = is.getAmount();
                    is.setAmount(current_amount - 1);
                    return_is = is;
                }

                e.setCursor(return_is);

                e.setCurrentItem(new ItemStack(Material.AIR));
                if (o_current.getAmount() <= 1) {
                    o_current = new ItemStack(Material.AIR);
                } else if (o_current.getAmount() > 1) {
                    o_current = e.getCurrentItem();
                    int current_amount = o_current.getAmount();
                    o_current.setAmount(current_amount - 1);
                    pl.getInventory().addItem(o_current);
                    pl.updateInventory();
                }

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    public void run() {
                        pl.closeInventory();
                        pl.sendMessage(ChatColor.GOLD + "Please enter up to a " + ChatColor.UNDERLINE + "40 CHARACTER" + ChatColor.GOLD
                                + " name to assign this item.");
                        pl.sendMessage(ChatColor.RED + "This opperation is non-refundable and non-reversable, type 'cancel' to void it.");
                    }
                }, 2L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMusicSelect(InventoryClickEvent e) {
        final Player pl = (Player) e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("Music Selection")) {
            e.setCancelled(true);
            if (e.getRawSlot() >= 18) {
                pl.updateInventory();
                return;
            }
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.PISTON_MOVING_PIECE) {
                ItemStack record = e.getCurrentItem();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    public void run() {
                        pl.closeInventory();
                    }
                }, 2L);
                if (music_box_placement.containsKey(pl.getName())) {
                    Location loc = music_box_placement.get(pl.getName());
                    if (loc.getBlock().getType() == Material.AIR) {
                        // Place it, start cooldown!
                        music_box_cooldown.put(pl.getName(), System.currentTimeMillis() + (120 * 1000));
                        loc.getBlock().setType(Material.JUKEBOX);
                        CraftJukebox jb = (CraftJukebox) loc.getBlock().getState();
                        jb.setPlaying(record.getType());
                        music_box_ownership.put(loc.getBlock(), pl.getName());
                        music_box_timeout.put(loc.getBlock(), System.currentTimeMillis()); // 6 minute timeout.
                    } else {
                        // Check if it's jukebox, if so let's play music.
                        if (loc.getBlock().getType() == Material.JUKEBOX) {
                            CraftJukebox jb = (CraftJukebox) loc.getBlock().getState();
                            jb.setPlaying(record.getType());
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onMusicBoxPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.PORTAL) {
            e.setCancelled(true);
            return;
        }
        if (isMusicBox(e.getItemInHand())) {
            e.setCancelled(true);
            Player pl = e.getPlayer();
            Block b = e.getBlock();

            if (!DuelMechanics.isDamageDisabled(b.getLocation())) {
                pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place the Mobile Jukebox outside of safe zones.");
                pl.updateInventory();
                return;
            }

            if (music_box_cooldown.containsKey(pl.getName())) {
                long last_use = music_box_cooldown.get(pl.getName());
                if ((System.currentTimeMillis() - last_use) < 0) { // On cooldown.
                    int seconds_left = (int) ((last_use - System.currentTimeMillis()) / 1000.0D);
                    if (seconds_left > 0) {
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place this item again for " + seconds_left
                                + ChatColor.BOLD + "s");
                        pl.updateInventory();
                        return;
                    }
                }
            }

            for (Block bl : music_box_ownership.keySet()) {
                if (bl.getLocation().distanceSquared(b.getLocation()) <= 4) {
                    pl.sendMessage(ChatColor.RED + "You are too close to another " + ChatColor.UNDERLINE + "Mobile Musicbox.");
                    pl.sendMessage(ChatColor.GRAY + "Move a couple of blocks away!");
                    pl.updateInventory();
                    return;
                }
            }

            if (!pl.getWorld().getName().equalsIgnoreCase("DungeonRealms") && !(InstanceMechanics.isInstance(pl.getWorld().getName()))) {
                pl.sendMessage(ChatColor.RED + "You cannot place your music box here.");
                e.setCancelled(true);
                return;
            }

            music_box_placement.put(pl.getName(), b.getLocation());
            pl.openInventory(music_select);
        }
    }

    @EventHandler
    public void onMusicBoxInteract(PlayerInteractEvent e) {
        if (e.hasBlock() && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b.getState() instanceof CraftJukebox) {
                if (music_box_ownership.containsKey(b)) {
                    e.setCancelled(true);
                    e.setUseInteractedBlock(Result.DENY);
                    e.setUseItemInHand(Result.DENY);
                    Player pl = e.getPlayer();
                    if (music_box_ownership.get(b).equalsIgnoreCase(pl.getName())) {
                        pl.openInventory(music_select);
                    }
                }
            }
        }

        if (e.hasBlock() && e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b.getState() instanceof CraftJukebox) {
                if (music_box_ownership.containsKey(b)) {
                    e.setCancelled(true);
                    e.setUseInteractedBlock(Result.DENY);
                    e.setUseItemInHand(Result.DENY);
                    Player pl = e.getPlayer();
                    if (music_box_ownership.get(b).equalsIgnoreCase(pl.getName())) {
                        // Destroy.
                        CraftJukebox cj = (CraftJukebox) b.getState();
                        cj.setPlaying(Material.AIR);
                        cj.setRawData((byte) 0x0);
                        b.setType(Material.AIR);
                        final Block fb = b;
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                            public void run() {
                                Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(fb.getLocation().getX()), (int) Math.round(fb
                                        .getLocation().getY()), (int) Math.round(fb.getLocation().getZ()), 84, false);
                                ((CraftServer) Bukkit.getServer())
                                        .getServer()
                                        .getPlayerList()
                                        .sendPacketNearby(fb.getLocation().getX(), fb.getLocation().getY(), fb.getLocation().getZ(), 24,
                                                ((CraftWorld) fb.getWorld()).getHandle().dimension, particles);
                            }
                        }, 2L);

                        String owner = music_box_ownership.get(b);
                        music_box_placement.remove(owner);

                        music_box_ownership.remove(b);
                        music_box_timeout.remove(b);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCloneIntreact(PlayerInteractEntityEvent e) {
        Player pl = e.getPlayer();
        Entity npc = e.getRightClicked();
        if (npc.hasMetadata("NPC") && npc.hasMetadata("clone")) {
            lookAtEntity((Entity) pl, (EntityLiving) ((CraftEntity) npc).getHandle());
            Player pl_npc = (Player) npc;
            String msg = personal_clones_msg.get(ChatColor.stripColor(pl_npc.getName()));
            pl.sendMessage(ChatColor.GOLD + pl_npc.getName() + ": " + ChatColor.WHITE + ChatMechanics.censorMessage(msg));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player pl = e.getPlayer();
        if (gold_curse.contains(pl.getName()) && pl.getWorld().getName().equalsIgnoreCase(Hive.main_world_name)) {
            Block top_block = pl.getLocation().getBlock();
            Block b = pl.getLocation().subtract(0, 1, 0).getBlock();
            Material m = b.getType();
            if (top_block.getType() == Material.AIR
                    && (m == Material.DIRT || m == Material.GRASS || m == Material.STONE || m == Material.COBBLESTONE || m == Material.GRAVEL
                            || m == Material.LOG || m == Material.LEAVES || m == Material.SMOOTH_BRICK || m == Material.BEDROCK || m == Material.GLASS
                            || m == Material.SANDSTONE || m == Material.SAND || m == Material.BOOKSHELF || m == Material.MOSSY_COBBLESTONE
                            || m == Material.OBSIDIAN || m == Material.SNOW_BLOCK || m == Material.ICE || m == Material.CLAY || m == Material.STAINED_CLAY || m == Material.WOOL)) {

                gold_curse_blocks_timing.put(b.getLocation(), 40);
                gold_curse_blocks.put(b.getLocation(), m);

                b.setType(Material.GOLD_BLOCK);
            }
        }
    }

    @EventHandler
    public void onPlayerTouchBlock(PlayerInteractEvent e) {
        if (e.hasBlock() && gold_curse.contains(e.getPlayer().getName()) && e.getAction() == Action.LEFT_CLICK_BLOCK
                && e.getPlayer().getWorld().getName().equalsIgnoreCase(Hive.main_world_name)) {
            Block b = e.getClickedBlock();
            Block top_block = b.getLocation().add(0, 1, 0).getBlock();
            Material m = b.getType();
            if (top_block.getType() == Material.AIR
                    && (m == Material.DIRT || m == Material.GRASS || m == Material.STONE || m == Material.COBBLESTONE || m == Material.GRAVEL
                            || m == Material.LOG || m == Material.LEAVES || m == Material.SMOOTH_BRICK || m == Material.BEDROCK || m == Material.GLASS
                            || m == Material.SANDSTONE || m == Material.SAND || m == Material.BOOKSHELF || m == Material.MOSSY_COBBLESTONE
                            || m == Material.OBSIDIAN || m == Material.SNOW_BLOCK || m == Material.ICE || m == Material.CLAY || m == Material.STAINED_CLAY || m == Material.WOOL)) {

                gold_curse_blocks_timing.put(b.getLocation(), 40);
                gold_curse_blocks.put(b.getLocation(), m);

                b.setType(Material.GOLD_BLOCK);
            }
        }
    }

    @EventHandler
    public void onGoldenCurseToggle(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isGoldenCurse(is)) {
                Player pl = e.getPlayer();
                if (gold_curse.contains(pl.getName())) {
                    // Turn it OFF.
                    gold_curse.remove(pl.getName());
                    pl.sendMessage(ChatColor.RED + "Golden Curse - " + ChatColor.BOLD + "DISABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 0.5F);
                } else {
                    // Turn it ON.
                    gold_curse.add(pl.getName());
                    pl.sendMessage(ChatColor.GREEN + "Golden Curse - " + ChatColor.BOLD + "ENABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 3F);
                }
            }
        }
    }

    @EventHandler
    public void onMusicalSpiritToggle(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isMusicalSpirit(is)) {
                Player pl = e.getPlayer();
                if (music_spirits.contains(pl.getName())) {
                    // Turn it OFF.
                    music_spirits.remove(pl.getName());
                    pl.sendMessage(ChatColor.RED + "Musical Spirit - " + ChatColor.BOLD + "DISABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 0.5F);
                } else {
                    // Turn it ON.
                    music_spirits.add(pl.getName());
                    pl.sendMessage(ChatColor.GREEN + "Musical Spirit - " + ChatColor.BOLD + "ENABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 3F);
                }
            }
        }
    }

    @EventHandler
    public void onDemonicAuraToggle(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isDemonicAura(is)) {
                Player pl = e.getPlayer();
                if (demonic_aura.contains(pl.getName())) {
                    // Turn it OFF.
                    demonic_aura.remove(pl.getName());
                    pl.sendMessage(ChatColor.RED + "Demonic Aura - " + ChatColor.BOLD + "DISABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ENDERMAN_IDLE, 1F, 0.5F);
                } else {
                    // Turn it ON.
                    demonic_aura.add(pl.getName());
                    pl.sendMessage(ChatColor.GREEN + "Demonic Aura - " + ChatColor.BOLD + "ENABLED");
                    pl.getWorld().playSound(pl.getLocation(), Sound.ENDERMAN_SCREAM, 1F, 3F);
                }
            }
        }
    }

    @EventHandler
    public void onFlameArmorToggle(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isFlameArmor(is)) {
                Player pl = e.getPlayer();
                if (flaming_armor.contains(pl.getName())) {
                    // Turn it OFF.
                    flaming_armor.remove(pl.getName());
                    pl.sendMessage(ChatColor.RED + "Blazing Armor - " + ChatColor.BOLD + "DISABLED");
                    pl.playSound(pl.getLocation(), Sound.LAVA_POP, 0.5F, 0.5F);
                } else {
                    // Turn it ON.
                    flaming_armor.add(pl.getName());
                    pl.sendMessage(ChatColor.GREEN + "Blazing Armor - " + ChatColor.BOLD + "ENABLED");
                    pl.playSound(pl.getLocation(), Sound.LAVA, 1F, 1F);
                }
            }
        }
    }

    @EventHandler
    public void onFlameTrailToggle(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isFlameTrail(is)) {
                Player pl = e.getPlayer();
                if (flame_trail.contains(pl.getName())) {
                    // Turn it OFF.
                    flame_trail.remove(pl.getName());
                    pl.sendMessage(ChatColor.RED + "Flame Trail - " + ChatColor.BOLD + "DISABLED");
                    pl.playSound(pl.getLocation(), Sound.FIZZ, 0.5F, 0.5F);
                } else {
                    // Turn it ON.
                    flame_trail.add(pl.getName());
                    pl.sendMessage(ChatColor.GREEN + "Flame Trail - " + ChatColor.BOLD + "ENABLED");
                    pl.playSound(pl.getLocation(), Sound.FIRE_IGNITE, 4F, 1F);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onFireworkWandUse(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isFireworkWand(is)) {
                Player pl = e.getPlayer();
                if (firework_cooldown.containsKey(is)) {
                    int seconds_left = (int) ((firework_cooldown.get(is) - System.currentTimeMillis()) / 1000.0D);
                    if (seconds_left > 0) {
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item for " + seconds_left
                                + ChatColor.BOLD + "s");
                        return;
                    } else {
                        firework_cooldown.remove(is);
                    }
                }

                int uses = 0;
                if (firework_use_count.containsKey(is)) {
                    uses = firework_use_count.get(is);
                }

                if (firework_last_use.containsKey(is) && (System.currentTimeMillis() - firework_last_use.get(is)) > (6 * 1000)) {
                    // It's been more than 8 seconds, reset use.
                    uses = 0;
                }

                uses += 1;
                if (uses >= 6) {
                    firework_cooldown.put(is, System.currentTimeMillis() + 60 * 1000);
                    firework_use_count.put(is, 0);
                } else {
                    firework_use_count.put(is, uses);
                }

                Color c1 = null;
                Color c2 = null;
                Type t = null;

                int color_type = new Random().nextInt(10);
                if (color_type == 0) {
                    c1 = Color.BLUE;
                }
                if (color_type == 1) {
                    c1 = Color.PURPLE;
                }
                if (color_type == 2) {
                    c1 = Color.NAVY;
                }
                if (color_type == 3) {
                    c1 = Color.AQUA;
                }
                if (color_type == 4) {
                    c1 = Color.FUCHSIA;
                }
                if (color_type == 5) {
                    c1 = Color.RED;
                }
                if (color_type == 6) {
                    c1 = Color.YELLOW;
                }
                if (color_type == 7) {
                    c1 = Color.MAROON;
                }
                if (color_type == 8) {
                    c1 = Color.WHITE;
                }
                if (color_type == 9) {
                    c1 = Color.BLACK;
                }

                color_type = new Random().nextInt(10);
                if (color_type == 0) {
                    c2 = Color.BLUE;
                }
                if (color_type == 1) {
                    c2 = Color.PURPLE;
                }
                if (color_type == 2) {
                    c2 = Color.NAVY;
                }
                if (color_type == 3) {
                    c2 = Color.AQUA;
                }
                if (color_type == 4) {
                    c2 = Color.FUCHSIA;
                }
                if (color_type == 5) {
                    c2 = Color.RED;
                }
                if (color_type == 6) {
                    c2 = Color.YELLOW;
                }
                if (color_type == 7) {
                    c2 = Color.MAROON;
                }
                if (color_type == 8) {
                    c2 = Color.WHITE;
                }
                if (color_type == 9) {
                    c2 = Color.BLACK;
                }

                int effect_type = new Random().nextInt(5);
                if (effect_type == 0) {
                    t = Type.BALL;
                }
                if (effect_type == 1) {
                    t = Type.BALL_LARGE;
                }
                if (effect_type == 2) {
                    t = Type.BURST;
                }
                if (effect_type == 3) {
                    t = Type.CREEPER;
                }
                if (effect_type == 4) {
                    t = Type.STAR;
                }

                Firework fw = (Firework) pl.getWorld().spawnEntity(pl.getTargetBlock(null, 2).getLocation(), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(c1).withFade(c2).with(t).trail(true).build();
                fwm.addEffect(effect);
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);

                firework_last_use.put(is, System.currentTimeMillis());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onDestructoWandUse(PlayerInteractEvent e) {
        if (e.hasItem()) {
            ItemStack is = e.getItem();
            if (isDestructionWand(is)) {
                Player pl = e.getPlayer();
                if (destruction_cooldown.containsKey(pl.getName())) {
                    int seconds_left = (int) ((destruction_cooldown.get(pl.getName()) - System.currentTimeMillis()) / 1000.0D);
                    if (seconds_left > 0) {
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this item for " + seconds_left
                                + ChatColor.BOLD + "s");
                        return;
                    } else {
                        destruction_cooldown.remove(pl.getName());
                    }
                }

                int uses = 0;
                if (destruction_use_count.containsKey(pl.getName())) {
                    uses = destruction_use_count.get(pl.getName());
                }

                if (destruction_last_use.containsKey(pl.getName()) && (System.currentTimeMillis() - destruction_last_use.get(pl.getName())) > (5 * 1000)) {
                    // It's been more than 5 seconds, reset use.
                    uses = 0;
                }

                uses += 1;
                if (uses >= 9) {
                    destruction_cooldown.put(pl.getName(), System.currentTimeMillis() + 60 * 1000);
                    destruction_use_count.put(pl.getName(), 0);
                } else {
                    destruction_use_count.put(pl.getName(), uses);
                }

                if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Location target_loc = pl.getTargetBlock(null, 8).getLocation();
                    try {
                        ParticleEffect.sendToLocation(ParticleEffect.HUGE_EXPLOSION, target_loc.add(0.50, 0.95, 0.50), new Random().nextFloat(),
                                new Random().nextFloat(), new Random().nextFloat(), 0.5F, 40);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    pl.getWorld().playSound(pl.getLocation(), Sound.EXPLODE, 1F, 1F);
                    // pl.getWorld().createExplosion(target_loc.getX(), target_loc.getY() + 2, target_loc.getZ(), 10.0F, false, false);
                }

                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    // Location target_loc = pl.getTargetBlock(null, 16).getLocation();
                    // EntityLightning el = new EntityLightning(((CraftWorld) pl.getWorld()).getHandle(), target_loc.getX(), target_loc.getY(),
                    // target_loc.getZ(), true);
                    // TODO: Fix fake lightning 1.7
                    /*
                     * Packet71Weather packet = new Packet71Weather(el); ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                     * 
                     * for(Entity ent : pl.getNearbyEntities(48, 48, 48)){ if(ent instanceof Player){ Player pl_send = (Player)ent; ((CraftPlayer)
                     * pl_send).getHandle().playerConnection.sendPacket(packet); pl_send.playSound(pl_send.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F); } }
                     */
                }

                destruction_last_use.put(pl.getName(), System.currentTimeMillis());
            }
        }
    }

    public int downloadECASH(String p_name) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DriverManager.getConnection(Config.sql_url, Config.sql_user, Config.sql_password);
            pst = con.prepareStatement("SELECT ecash FROM player_database WHERE p_name = '" + p_name + "'");

            pst.execute();
            ResultSet rs = pst.getResultSet();
            if (!rs.next()) {
                return 0;
            }
            int amount = rs.getInt("ecash");
            return amount;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return 0;
    }

    public static void setECASH_SQL(String p_name, int amount) {

        Hive.sql_query.add("INSERT INTO player_database (p_name, ecash)" + " VALUES" + "('" + p_name + "', '" + amount + "') ON DUPLICATE KEY UPDATE ecash ='"
                + amount + "'");

        log.info("[DonationMechanics] Set " + p_name + "'s ECASH to " + amount);

    }

}