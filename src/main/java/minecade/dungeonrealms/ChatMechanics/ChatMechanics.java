package minecade.dungeonrealms.ChatMechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.ChatMechanics.commands.CommandGL;
import minecade.dungeonrealms.ChatMechanics.commands.CommandGR;
import minecade.dungeonrealms.ChatMechanics.commands.CommandL;
import minecade.dungeonrealms.ChatMechanics.commands.CommandSC;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.EcashMechanics.EcashMechanics;
import minecade.dungeonrealms.GuildMechanics.GuildMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.MonsterMechanics.Hologram;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;
import minecade.dungeonrealms.database.ConnectionPool;
import minecade.dungeonrealms.jsonlib.JSONMessage;
import minecade.dungeonrealms.managers.PlayerManager;
import net.minecraft.server.v1_8_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class ChatMechanics implements Listener {
    public static Logger log = Logger.getLogger("Minecraft");

    public static ConcurrentHashMap<String, Long> mute_list = new ConcurrentHashMap<String, Long>();
    public static ConcurrentHashMap<String, Long> hologram_chat = new ConcurrentHashMap<String, Long>();
    // public static CopyOnWriteArrayList<String> sending_message = new CopyOnWriteArrayList<String>();
    public static List<String> recent_death = new ArrayList<String>();
    public static List<String> staff_only = new ArrayList<String>();
    
    public static int GChat_Delay = 2;

    public static List<String> bad_words = new ArrayList<String>(Arrays.asList("shit", "fuck", "cunt", "bitch", "whore", "slut", "wank", "asshole", "cock",
            "dick", "clit", "homo", "fag", "queer", "nigger", "dike", "dyke", "retard", "motherfucker", "vagina", "boob", "pussy", "rape", "gay", "penis",
            "cunt", "titty", "anus", "faggot")); // Faggot is not a food in this case!

    public static volatile CopyOnWriteArrayList<String> async_mute_update = new CopyOnWriteArrayList<String>();
    // Controls the LoginProcessThread to getMuteSQL().

    Thread LoginProcessThread;

    public void onEnable() {
        LoginProcessThread = new LoginProcessThread();
        LoginProcessThread.start();

        Main.plugin.getCommand("l").setExecutor(new CommandL());
        Main.plugin.getCommand("gl").setExecutor(new CommandGL());
        Main.plugin.getCommand("gr").setExecutor(new CommandGR());
        Main.plugin.getCommand("sc").setExecutor(new CommandSC());

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);

        // Inform muted players when their mutes expire.
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
            public void run() {
                for (Entry<String, Long> data : mute_list.entrySet()) {
                    String p_name = data.getKey();
                    long minutes_left = data.getValue();
                    minutes_left = minutes_left - 1;

                    if (minutes_left <= 0) {
                        mute_list.remove(p_name);
                        if (Bukkit.getPlayer(p_name) != null) {
                            Player p = Bukkit.getPlayer(p_name);
                            p.sendMessage("\n" + ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has expired." + "\n");
                        }
                    } else if (minutes_left > 0) {
                        mute_list.put(p_name, minutes_left);
                    }

                }
            }
        }, 60 * 20L, 60 * 20L);

        // Dynamnically change the global chat delay based on population
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
            public void run() {
                if (Hive.player_count <= 20) {
                    GChat_Delay = 2;
                }
                if (Hive.player_count > 20) {
                    GChat_Delay = 4;
                }
                if (Hive.player_count > 30) {
                    GChat_Delay = 8;
                }
                if (Hive.player_count > 50) {
                    GChat_Delay = 10;
                }
                if (Hive.player_count > 50) {
                    GChat_Delay = 15;
                }
                if (Hive.player_count > 75) {
                    GChat_Delay = 20;
                }
                if (Hive.player_count > 100) {
                    GChat_Delay = 25;
                }
                if (Hive.player_count > 125) {
                    GChat_Delay = 30;
                }
            }
        }, 30 * 20L, 120 * 20L);

        log.info("[ChatMechanics] has been enabled.");
    }

    public void onDisable() {
        log.info("[ChatMechanics] has been disabled.");
    }

    public static void setMuteStateSQL(String p_name) {
        long unmute_time = 0;

        if (mute_list.containsKey(p_name)) {
            unmute_time = mute_list.get(p_name);
        }
        try (PreparedStatement pst = ConnectionPool.getConnection().prepareStatement("UPDATE mute_map SET unmute = ? WHERE pname = ?")) {
            pst.setLong(1, unmute_time);
            pst.setString(2, p_name);
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Hive.sql_query.add("INSERT INTO mute_map (pname, unmute)" + " VALUES" + "('" + p_name + "', '" + unmute_time +
        // "') ON DUPLICATE KEY UPDATE unmute = '" + unmute_time + "'");
    }

    public static void getMuteStateSQL(String p_name) {
        try (PreparedStatement pst = ConnectionPool.getConnection().prepareStatement("SELECT unmute FROM mute_map WHERE pname = ?")) {
            pst.setString(1, p_name);
            ResultSet rst = pst.executeQuery();
            if (!rst.first()) {
                // Not muted so no mute >.>
                mute_list.remove(p_name);
                updateFirstMuteMap(p_name);
            } else {
                mute_list.put(p_name, rst.getLong("unmute"));
            }
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Connection con = null;
         *
         * PreparedStatement pst = null;
         *
         * try { con = DriverManager.getConnection(Config.sql_url, Config.sql_user, Config.sql_password); pst = con.prepareStatement(
         * "SELECT unmute FROM mute_map WHERE pname = '" + p_name + "'");
         *
         * pst.execute(); ResultSet rs = pst.getResultSet(); if(!rs.next()) { mute_list.remove(p_name); return; } long unmute = rs.getLong("unmute");
         * mute_list.put(p_name, unmute);
         *
         * } catch(SQLException ex) { log.log(Level.SEVERE, ex.getMessage(), ex);
         *
         * } finally { try { if(pst != null) { pst.close(); } if(con != null) { con.close(); }
         *
         * } catch(SQLException ex) { log.log(Level.WARNING, ex.getMessage(), ex); } }
         */
    }

    public static void updateFirstMuteMap(String p_name) {
        try (PreparedStatement pre = ConnectionPool.getConnection().prepareStatement(
                "INSERT IGNORE INTO mute_map(pname, unmute) VALUES (?, ?) ON DUPLICATE KEY UPDATE unmute = ?")) {
            pre.setString(1, p_name);
            pre.setLong(2, 0);
            pre.setLong(3, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DEPRECIATED, mute_list's value is now minutes until unmute (time
    // conversion issues)
    /*
     * public static int minutesUntilUnmute(Player p){ if(!(mute_list.containsKey(p.getName()))){ return 0; } int min_left = (int) (((mute_list.get(p.getName())
     * - System.currentTimeMillis()) / 60000.0D)); if(min_left <= 0){ min_left = 1; } return min_left; }
     */

    public static boolean hasAdultFilter(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getToggleList() != null && PlayerManager.getPlayerModel(p_name).getToggleList().contains("filter")) {
            return false; // Filter
            // is
            // disabled.
        }
        return true;
    }

    public static String fixCapsLock(String msg) {
        StringTokenizer st = new StringTokenizer(msg);
        String new_msg = "";
        int exception = 0;
        while (st.hasMoreTokens()) {
            String a = st.nextToken();
            if (a.equals(a.toUpperCase()) && !((a.startsWith(":") || a.startsWith(";")))) {
                exception++;
                if (exception <= 1) {
                    new_msg += a + " ";
                    continue;
                }
                a = a.charAt(0) + a.substring(1).toLowerCase();
            }

            new_msg += a + " ";
        }

        if (new_msg.endsWith(" ")) {
            new_msg = new_msg.substring(0, new_msg.length() - 1);
        }

        return new_msg;
    }

    public static ChatColor getPlayerColor(Player p, Player in_relation) {
        String rank = PermissionMechanics.getRank(p.getName());

        if (p.isOp() || rank.equalsIgnoreCase("GM")) {
            if (p.getName().equalsIgnoreCase("Bradez1571")) {
                return ChatColor.DARK_AQUA;
            } else if (p.getName().equalsIgnoreCase("Vilsol")) {
                return ChatColor.DARK_PURPLE;
            } else if (Main.isDev(p.getName())) {
                return ChatColor.DARK_AQUA;
            }
            return ChatColor.AQUA;
        }

        if (CommunityMechanics.isPlayerOnBuddyList(in_relation, p.getName())) {
            return ChatColor.GREEN;
        }

        String align = KarmaMechanics.getRawAlignment(p.getName());
        ChatColor c = ChatColor.GRAY;

        if (align == null) {
            c = ChatColor.GRAY;
            return c;
        }

        if (align.equalsIgnoreCase("good")) {
            c = ChatColor.GRAY;
            // return; // Do nothing if they're good, stay white plz.
        }
        if (align.equalsIgnoreCase("neutral")) {
            c = ChatColor.YELLOW;
        }
        if (align.equalsIgnoreCase("evil")) {
            c = ChatColor.RED;
        }

        return c;
    }

    public static ChatColor getPlayerColor(String p_name, String p_name_in_relation) {
        String rank = PermissionMechanics.getRank(p_name);

        if (rank.equalsIgnoreCase("GM")) {
            if (p_name.equalsIgnoreCase("Bradez1571")) {
                return ChatColor.DARK_AQUA;
            } else if (p_name.equalsIgnoreCase("Vilsol")) {
                return ChatColor.DARK_PURPLE;
            } else if (Main.isDev(p_name)) {
                return ChatColor.DARK_AQUA;
            }
            return ChatColor.AQUA;
        }

        if (CommunityMechanics.isPlayerOnBuddyList(p_name_in_relation, p_name)) {
            return ChatColor.GREEN;
        }

        String align = KarmaMechanics.getRawAlignment(p_name);
        ChatColor c = ChatColor.GRAY;

        if (align == null) {
            c = ChatColor.GRAY;
            return c;
        }

        if (align.equalsIgnoreCase("good")) {
            c = ChatColor.GRAY;
            // return; // Do nothing if they're good, stay white plz.
        }
        if (align.equalsIgnoreCase("neutral")) {
            c = ChatColor.YELLOW;
        }
        if (align.equalsIgnoreCase("evil")) {
            c = ChatColor.RED;
        }

        return c;
    }

    public static String getPlayerPrefix(Player p) {
        String rank = PermissionMechanics.getRank(p.getName());
        String return_string = "";

        return_string = return_string + ChatColor.WHITE + GuildMechanics.getGuildPrefix(p.getName()) + ChatColor.RESET;

        if (p.isOp() || rank.equalsIgnoreCase("GM")) {
            if (Main.isDev(p.getName())) {
                return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "DEV" + " " + ChatColor.GRAY;
            } else if (p.getName().equalsIgnoreCase("Bradez1571")) {
                return_string += ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "CM " + ChatColor.RED;
            } else {
                return_string += "" + ChatColor.AQUA + ChatColor.BOLD.toString() + "GM" + " " + ChatColor.AQUA;
            }
        }

        if (rank.equalsIgnoreCase("PMOD")) {
            return_string += "" + ChatColor.WHITE + ChatColor.BOLD + "PMOD" + " ";
        }

        if (rank.equalsIgnoreCase("SUB")) {
            return_string += "" + ChatColor.GREEN + ChatColor.BOLD + "S" + " ";
        }

        if (rank.equalsIgnoreCase("SUB+")) {
            return_string += "" + ChatColor.GOLD + ChatColor.BOLD + "S+" + " ";
        }

        if (rank.equalsIgnoreCase("SUB++")) {
            return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "S++" + " ";
        }

        if (rank.equalsIgnoreCase("WD")) {
            return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "BUILDER" + " ";
        }

        if (rank.equalsIgnoreCase("DEFAULT")) {
            return_string += ChatColor.GRAY;
        }

        return return_string;
    }

    public static String getPlayerPrefix(String p_name) {
        String rank = PermissionMechanics.getRank(p_name);
        String return_string = "";
        return_string = return_string + ChatColor.WHITE + GuildMechanics.getGuildPrefix(p_name) + ChatColor.RESET;

        if (Bukkit.getOfflinePlayer(p_name).isOp() || rank.equalsIgnoreCase("GM")) {
            if (Main.isDev(p_name)) {
                return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "DEV" + " " + ChatColor.GRAY;
            } else if (p_name.equalsIgnoreCase("Bradez1571")) {
                return_string += ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "CM " + ChatColor.RED;
            } else {
                return_string += "" + ChatColor.AQUA + ChatColor.BOLD.toString() + "GM" + " " + ChatColor.AQUA;
            }
        }

        if (rank.equalsIgnoreCase("PMOD")) {
            return_string += "" + ChatColor.WHITE + ChatColor.BOLD + "PMOD" + " ";
        }

        if (rank.equalsIgnoreCase("SUB")) {
            return_string += "" + ChatColor.GREEN + ChatColor.BOLD + "S" + " ";
        }

        if (rank.equalsIgnoreCase("SUB+")) {
            return_string += "" + ChatColor.GOLD + ChatColor.BOLD + "S+" + " ";
        }

        if (rank.equalsIgnoreCase("SUB++")) {
            return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "S++" + " ";
        }

        if (rank.equalsIgnoreCase("WD")) {
            return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "BUILDER" + " ";
        }

        if (rank.equalsIgnoreCase("DEFAULT")) {
            return_string += ChatColor.GRAY;
        }

        return return_string;
    }
    
    public static String getPlayerPrefix(String p_name, boolean guild_prefix) {
        String rank = PermissionMechanics.getRank(p_name);
        OfflinePlayer op = Bukkit.getOfflinePlayer(p_name);

        String return_string = "";
        if (guild_prefix) {
            return_string = return_string + ChatColor.WHITE + GuildMechanics.getGuildPrefix(p_name) + ChatColor.RESET;
        }

        if (op.isOp() || rank.equalsIgnoreCase("GM")) {
            if (Main.isDev(p_name)) {
                return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "DEV" + " " + ChatColor.GRAY;
            } else if (p_name.equalsIgnoreCase("Bradez1571")) {
                return_string += ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "CM " + ChatColor.RED;
            } else {
                return return_string + ChatColor.AQUA + ChatColor.BOLD.toString() + "GM" + " " + ChatColor.AQUA;
            }
        }

        if (rank.equalsIgnoreCase("PMOD")) {
            return return_string + ChatColor.WHITE + ChatColor.BOLD + "PMOD" + " ";
        }

        if (rank.equalsIgnoreCase("SUB")) {
            return return_string + ChatColor.GREEN + ChatColor.BOLD + "S" + " ";
        }

        if (rank.equalsIgnoreCase("SUB+")) {
            return return_string + ChatColor.GOLD + ChatColor.BOLD + "S+" + " ";
        }

        if (rank.equalsIgnoreCase("SUB++")) {
            return return_string += "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "S++" + " ";
        }

        if (rank.equalsIgnoreCase("WD")) {
            return return_string + ChatColor.DARK_AQUA + ChatColor.BOLD + "BUILDER" + " ";
        }

        if (rank.equalsIgnoreCase("DEFAULT")) {
            return return_string += ChatColor.GRAY;
        }

        return "";
    }

    public static boolean hasBadWord(String msg) {
        for (String s : msg.split(" ")) {
            for (String bad : bad_words) {
                if (s.toLowerCase().contains(bad.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String censorMessage(String msg) {
        String personal_msg = "";
        if (msg == null) {
            return "";
        }
        if (!(msg.contains(" "))) {
            msg += " ";
        }
        for (String s : msg.split(" ")) {
            for (String bad : bad_words) {
                if (s.toLowerCase().contains(bad.toLowerCase())) {
                    int letters = bad.length();
                    String replace_char = "";
                    while (letters > 0) {
                        replace_char += "*";
                        letters--;
                    }
                    int censor_start = 0;
                    int censor_end = 1;

                    censor_start = s.toLowerCase().indexOf(bad);
                    censor_end = censor_start + bad.length();

                    String real_bad_word = s.substring(censor_start, censor_end);

                    s = s.replaceAll(real_bad_word, replace_char);
                }
            }
            personal_msg += s + " ";
        }

        if (personal_msg.endsWith(" ")) {
            personal_msg = personal_msg.substring(0, personal_msg.lastIndexOf(" "));
        }

        return personal_msg;
    }

    public static boolean hasTradeKeyword(String msg) {
        msg = msg.toLowerCase();
        if (msg.startsWith("trade") || msg.startsWith("selling") || msg.startsWith("buying") || msg.startsWith("wts") || msg.startsWith("wtb")
                || msg.startsWith("wtt") || msg.startsWith("trading") || msg.startsWith("shops")) {
            return true;
        }
        return false;
    }

    public static boolean hasGuildKeyword(String msg) {
        msg = msg.toLowerCase();
        if (msg.startsWith("guild") || msg.startsWith("recruiting") || msg.startsWith("lf") || msg.startsWith("guilds")) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        if (!(mute_list.containsKey(p.getName()))) {
            async_mute_update.add(p.getName());
        }
        hologram_chat.put(p.getName(), 0L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        setMuteStateSQL(e.getPlayer().getName());
        if (hologram_chat.containsKey(e.getPlayer().getName())) {
            hologram_chat.remove(e.getPlayer().getName());
        }
    }

    @EventHandler
    // TODO: Make this toggleable.
    public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent e) {
        final Player p = e.getPlayer();

        if (PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("tabchat")) {
            return;
        }

        String msg = e.getChatMessage();
        String rank = PermissionMechanics.getRank(p.getName());
        // sending_message.add(p.getName());
        p.closeInventory();
        if (e.getChatMessage().startsWith("/?") || e.getChatMessage().startsWith("/help") || e.getChatMessage().startsWith("/server")
                || e.getChatMessage().startsWith("?") || e.getChatMessage().startsWith("help") || e.getChatMessage().startsWith("server")) {
            for (int i = 0; i < 200; i++) {
                p.sendMessage("");
            }
            return;
        }
        if (TutorialMechanics.onTutorialIsland(p) && !(p.isOp())) {
            p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " chat while on tutorial island.");
            p.sendMessage(ChatColor.GRAY + "Either finish the tutorial or type /skip to enable chat.");
            return;
        }

        if (mute_list.containsKey(p.getName())) {
            long time_left = mute_list.get(p.getName());
            p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + time_left
                    + " minute(s).");
            return;
        }

        if (PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("global")) {
            p.sendMessage(ChatColor.RED + "You currently have global messaging " + ChatColor.BOLD + "DISABLED." + ChatColor.RED
                    + " Type '/toggleglobal' to re-enable.");
            return;
        }

        if (PlayerManager.getPlayerModel(p).getGlobalChatDelay() != 0) {
            long old_time = PlayerManager.getPlayerModel(p).getGlobalChatDelay();
            long cur_time = System.currentTimeMillis();

            int personal_delay = GChat_Delay;
            ItemStack global_amp = EcashMechanics.tickGlobalAmplifier(p);
            if (global_amp != null) {
                // They have one!
                personal_delay *= 0.50D;

                // EcashMechanics.setMessagesLeftOnGlobalAmplifier(global_amp,
                // -1, true);
                // It will subtract from the item in getGlobalAmplifier.
            }

            if ((cur_time - old_time) < (personal_delay * 1000) && !(p.isOp()) && !(rank.equalsIgnoreCase("GM"))
                    && !(rank.equalsIgnoreCase("PMOD") && !(rank.equalsIgnoreCase("WD")))) {
                int s_delay_left = personal_delay - (int) ((cur_time - old_time) / 1000);
                p.sendMessage(ChatColor.RED + "You can send another GLOBAL MESSAGE in " + s_delay_left + ChatColor.BOLD + "s");
                return;
            }
        }

        PlayerManager.getPlayerModel(p).setGlobalChatDelay(System.currentTimeMillis());

        boolean trade = false;
        if (hasTradeKeyword(msg)) {
            trade = true;
        }

        if (trade && PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("tchat")) {
            p.sendMessage(ChatColor.RED + "You currently have trade chat messaging " + ChatColor.BOLD + "DISABLED." + ChatColor.RED
                    + " Type '/toggletradechat' to re-enable.");
            return;
        }

        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if (CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
                continue; // Either sender has the sendie ignored or vise versa,
                          // no need for them to be able to see each other's
                          // messages.
            }
            if (!trade && PlayerManager.getPlayerModel(pl).getToggleList() != null && PlayerManager.getPlayerModel(pl).getToggleList().contains("global")) {
                continue; // They have global off, and only want to hear from
                          // their buds.
            }
            if (trade && PlayerManager.getPlayerModel(pl).getToggleList() != null && PlayerManager.getPlayerModel(pl).getToggleList().contains("tchat")) {
                continue; // They have trade chat off, and only want to hear
                          // from their buds.
            }
            if (TutorialMechanics.onTutorialIsland(pl)) {
                continue; // Don't send global chat to players on tutorial
                          // island.
            }

            ChatColor p_color = getPlayerColor(p, pl);
            String prefix = getPlayerPrefix(p);

            String personal_msg = msg;
            if (hasAdultFilter(pl.getName())) {
                personal_msg = censorMessage(msg);
            }

            personal_msg = fixCapsLock(personal_msg);

            if (personal_msg.endsWith(" ")) {
                personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
            }

            if (trade == false) {
                pl.sendMessage(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + ">" + " " + prefix + p_color + p.getName() + ": "
                        + ChatColor.WHITE + personal_msg);
            }
            if (trade == true) {
                pl.sendMessage(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + ">" + " " + prefix + p_color + p.getName() + ": "
                        + ChatColor.WHITE + personal_msg);
            }
        }

        String prefix = getPlayerPrefix(p);

        log.info(ChatColor.stripColor("" + "<" + "G" + ">" + " " + prefix + p.getName() + ": " + msg));

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        recent_death.remove(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage("");
        Player dead = (Player) e.getEntity();

        String death_reason = " has died";

        if (dead.getLastDamageCause() != null && !(recent_death.contains(dead.getName()))) {

            recent_death.add(dead.getName());

            if (dead.getLastDamageCause().getCause() == DamageCause.SUICIDE) {
                death_reason = " ended their own life.";
            }
            if (dead.getLastDamageCause().getCause() == DamageCause.FALL) {
                death_reason = " fell to their death";
            }
            if (dead.getLastDamageCause().getCause() == DamageCause.FIRE || dead.getLastDamageCause().getCause() == DamageCause.FIRE_TICK
                    || dead.getLastDamageCause().getCause() == DamageCause.LAVA) {
                death_reason = " burned to death";
            }
            if (dead.getLastDamageCause().getCause() == DamageCause.SUFFOCATION) {
                death_reason = " was crushed to death";
            }
            if (dead.getLastDamageCause().getCause() == DamageCause.DROWNING) {
                death_reason = " drowned to death";
            }
            if (dead.getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dead.getLastDamageCause();
                if (event.getDamager() instanceof Projectile) {
                    Projectile proj = (Projectile) event.getDamager();
                    LivingEntity shooter = (LivingEntity) proj.getShooter();
                    if (proj instanceof Arrow) {
                        death_reason = " was shot to death with an arrow";
                    }
                    if (shooter instanceof Player) {
                        Player killer = (Player) proj.getShooter();
                        ChatColor p_color = getPlayerColor(killer, killer);
                        String prefix = getPlayerPrefix(killer);
                        ItemStack ko_weapon = null;
                        String ko_weapon_name = null;
                        if (killer.isOnline()) {
                            ko_weapon = killer.getItemInHand();
                            if (ko_weapon.hasItemMeta() && ko_weapon.getItemMeta().hasDisplayName()) {
                                ko_weapon_name = ko_weapon.getItemMeta().getDisplayName();
                            } else {
                                ko_weapon_name = ko_weapon.getType().name().substring(0, 1).toUpperCase()
                                        + ko_weapon.getType().name().substring(1, ko_weapon.getType().name().length()).toLowerCase();
                            }
                        }
                        if (dead.getInventory().getArmorContents().length > 0 && !DuelMechanics.isDamageDisabled(dead.getLocation())) {
                            LevelMechanics.addXP(killer, (int) (LevelMechanics.getPlayerLevel(dead) * 3.5));
                        }
                        death_reason = " was killed by " + p_color + prefix + killer.getName() + ChatColor.WHITE.toString() + " with a(n) " + ko_weapon_name;
                    } else {
                        LivingEntity le_shooter = (LivingEntity) shooter;
                        String mob_name = shooter.getType().getName();
                        if (shooter.hasMetadata("mobname")) {
                            mob_name = le_shooter.getMetadata("mobname").get(0).asString();
                        }
                        death_reason = " was killed by a(n) " + ChatColor.UNDERLINE + mob_name;
                    }
                }

            }
            if (dead.getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dead.getLastDamageCause();
                if (event.getDamager() instanceof Player) {
                    Player killer = (Player) event.getDamager();
                    ChatColor p_color = getPlayerColor(killer, killer);
                    String prefix = getPlayerPrefix(killer);
                    if (dead.getInventory().getArmorContents().length > 0) {
                        LevelMechanics.addXP(killer, (int) (LevelMechanics.getPlayerLevel(dead) * 3.5));
                    }
                    ItemStack ko_weapon = null;
                    String ko_weapon_name = null;
                    if (killer.isOnline()) {
                        ko_weapon = killer.getItemInHand();
                        if (ko_weapon.hasItemMeta() && ko_weapon.getItemMeta().hasDisplayName()) {
                            ko_weapon_name = ko_weapon.getItemMeta().getDisplayName();
                        } else {
                            ko_weapon_name = ko_weapon.getType().name().substring(0, 1).toUpperCase()
                                    + ko_weapon.getType().name().substring(1, ko_weapon.getType().name().length()).toLowerCase();
                        }
                    }
                    death_reason = " was killed by " + p_color + prefix + killer.getName() + ChatColor.WHITE.toString() + " with a(n) " + ko_weapon_name;
                    // dead.sendMessage(p_color + "" + prefix + ChatColor.BOLD +
                    // dead.getName() + ChatColor.RED + " was killed by " +
                    // killer.getName());
                } else {
                    String mob_name = "";
                    Entity enti = event.getDamager();
                    if (enti.hasMetadata("mobname")) {
                        LivingEntity le = (LivingEntity) enti;
                        mob_name = le.getMetadata("mobname").get(0).asString();
                    } else {
                        mob_name = enti.getType().getName();
                        if (mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("zombie")) {
                            LivingEntity le = (LivingEntity) enti;
                            if (le.getEquipment().getHelmet().getType() == Material.SKULL_ITEM) {
                                ItemStack is = le.getEquipment().getHelmet();
                                net.minecraft.server.v1_8_R1.ItemStack mItem = CraftItemStack.asNMSCopy(is);
                                NBTTagCompound tag = mItem.getTag();
                                String skin_name = tag.getString("SkullOwner");
                                if (skin_name.equalsIgnoreCase("dEr_t0d") || skin_name.equalsIgnoreCase("niv330")) {
                                    mob_name = "Goblin";
                                }
                                if (skin_name.equalsIgnoreCase("Dullion") || skin_name.equalsIgnoreCase("TheNextPaladin")) {
                                    mob_name = "Bandit";
                                }
                                if (skin_name.equalsIgnoreCase("ArcadiaMovies") || skin_name.equalsIgnoreCase("Malware")) {
                                    mob_name = "Forest Troll";
                                }
                                if (skin_name.equalsIgnoreCase("Das_Doktor")) {
                                    mob_name = "Naga";
                                }
                                if (skin_name.equalsIgnoreCase("xmattpt")) {
                                    mob_name = "Tripoli Soldier";
                                }
                                if (skin_name.equalsIgnoreCase("_Kashi_")) {
                                    mob_name = "Lizardman";
                                }
                                if (mob_name == "") {
                                    mob_name = "Monster";
                                }
                            }
                        }

                        if (enti.getType() == EntityType.IRON_GOLEM) {
                            mob_name = "Iron Golem";
                        }
                    }

                    death_reason = " was killed by a(n) " + ChatColor.UNDERLINE + mob_name;
                }
            }

            Location ldeath_loc = PlayerManager.getPlayerModel(dead).getDeathLocation();
            if (ldeath_loc == null) {
                ldeath_loc = e.getEntity().getLocation();
            }
            if (MonsterMechanics.player_locations.size() > 1) {
                for (Entry<String, Location> data : MonsterMechanics.player_locations.entrySet()) {
                    Player pl = Bukkit.getPlayer(data.getKey());
                    if (pl == null) {
                        continue;
                    }
                    Location l = data.getValue();
                    if (!l.getWorld().getName().equalsIgnoreCase(ldeath_loc.getWorld().getName())) {
                        continue;
                    }
                    if (dead.getName().equalsIgnoreCase(pl.getName())) {
                        continue;
                    }
                    if (ldeath_loc.distanceSquared(l) > 16384) {
                        continue;
                    }
                    ChatColor p_color = getPlayerColor(dead, pl);
                    String prefix = getPlayerPrefix(dead);
                    pl.sendMessage(p_color + "" + prefix + dead.getName() + ChatColor.WHITE + death_reason);
                }
            }

            ChatColor p_color = getPlayerColor(dead, dead);
            String prefix = getPlayerPrefix(dead);
            dead.sendMessage(p_color + "" + prefix + dead.getName() + ChatColor.WHITE + death_reason);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("server") || e.getMessage().equalsIgnoreCase("/server")) {
            e.setMessage("suicide");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String msg = e.getMessage();
        final Player p = e.getPlayer();

        if (mute_list.containsKey(p.getName())) {
            if (mute_list.get(p.getName()) == 0)
                mute_list.remove(p.getName());
        }

        if (mute_list.containsKey(p.getName())) {
            long time_left = mute_list.get(p.getName());
            p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + time_left
                    + " minute(s).");
            return;
        }

        if (PartyMechanics.party_only.contains(p.getName())) {
            p.performCommand("p " + msg);

            return;
        }

        if (GuildMechanics.guild_only.contains(p.getName())) {
            p.performCommand("g " + msg);
            return;
        }

        if (PlayerManager.getPlayerModel(p).getToggleList().contains("globalchat")) {
            p.performCommand("gl  " + msg);
            return;
        }

        if (ChatMechanics.staff_only.contains(p.getName())) {
        	p.performCommand("sc " + msg);
        	return;
        }
        
        List<Player> to_send = new ArrayList<Player>();
        List<Player> secret_send = new ArrayList<Player>();

        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if (pl.getName().equalsIgnoreCase(p.getName())) {
                continue;
            }
            if (!pl.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) {
                continue;
            }
            if (pl.getLocation().distanceSquared(p.getLocation()) > 16384) {
                continue;
            }
            if (CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
                continue; // Either sender has the sendie ignored or vise versa,
                          // no need for them to be able to see each other's
                          // messages.
            }

            if (ModerationMechanics.isPlayerVanished(pl.getName())) {
                secret_send.add(pl);
            } else {
                to_send.add(pl);
            }
            continue;
        }

        if ((msg.startsWith("*") || msg.startsWith(" *")) && msg.length() > 1) { // EMOTE!
            String raw_msg = msg.replaceAll("\\*", "");
            if (raw_msg.length() <= 1) {
                return;
            }
            // String raw_msg = msg.substring(msg.indexOf("*") + 1,
            // msg.length());
            if (raw_msg.substring(0, 1).equalsIgnoreCase(" ")) {
                raw_msg = raw_msg.substring(1, raw_msg.length());
            }

            if (to_send.size() <= 0) {
                ChatColor p_color = getPlayerColor(p, p);
                p.sendMessage(ChatColor.GRAY + "" + p_color + p.getName() + ChatColor.GRAY + " " + raw_msg);
                p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No one saw you.");
                return;
            }

            for (Player pl : to_send) {
                ChatColor p_color = getPlayerColor(p, pl);

                if (CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
                    continue; // Either sender has the sendie ignored or vise
                              // versa, no need for them to be able to see
                              // each other's messages.
                }

                String personal_msg = raw_msg;
                if (hasAdultFilter(pl.getName())) {
                    personal_msg = censorMessage(personal_msg);
                }

                if (personal_msg.endsWith(" ")) {
                    personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
                }

                personal_msg = fixCapsLock(personal_msg);

                pl.sendMessage(ChatColor.GRAY + "" + p_color + p.getName() + ChatColor.GRAY + " " + personal_msg);
            }

            for (Player pl : secret_send) {
                ChatColor p_color = getPlayerColor(p, pl);

                if (CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
                    continue; // Either sender has the sendie ignored or vise
                              // versa, no need for them to be able to see
                              // each other's messages.
                }

                String personal_msg = raw_msg;
                if (hasAdultFilter(pl.getName())) {
                    personal_msg = censorMessage(personal_msg);
                }

                if (personal_msg.endsWith(" ")) {
                    personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
                }

                personal_msg = fixCapsLock(personal_msg);

                pl.sendMessage(ChatColor.GRAY + "" + p_color + p.getName() + ChatColor.GRAY + " " + personal_msg);
            }

            if (to_send.size() <= 0) {
                return; // Don't show debug.
            }

            ChatColor p_color = getPlayerColor(p, p);
            p.sendMessage(ChatColor.GRAY + "" + p_color + p.getName() + ChatColor.GRAY + " " + raw_msg);
            log.info(ChatColor.stripColor("EMOTE: " + p.getName() + " " + raw_msg));
            return;
        }

        String prefix = getPlayerPrefix(p);

        String personal_msg = msg;
        if (hasAdultFilter(p.getName())) {
            personal_msg = censorMessage(msg);
        }

        personal_msg = fixCapsLock(personal_msg);

        if (personal_msg.endsWith(" ")) {
            personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
        }

        String message = fixCapsLock(e.getMessage());

        JSONMessage filter = null;
        JSONMessage normal = null;
        String aprefix = p.getName() + ": " + ChatColor.WHITE;
        if (message.contains("@i@") && e.getPlayer().getItemInHand().getType() != Material.AIR) {
            String[] split = message.split("@i@");
            String after = "";
            String before = "";
            if (split.length > 0)
                before = split[0];
            if (split.length > 1)
                after = split[1];

            normal = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
            normal.addText(before + "");
            normal.addItem(e.getPlayer().getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
            normal.addText(after);

            filter = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
            filter.addText(censorMessage(before) + "");
            filter.addItem(e.getPlayer().getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
            filter.addText(censorMessage(after));
        } 

        // Normal message sending starts here.
        if (to_send.size() <= 0) {
            ChatColor p_color = getPlayerColor(p, p);
            if (normal != null) {
                JSONMessage toSend = normal;
                if (hasAdultFilter(p.getName())) {
                    toSend = filter;
                }

                toSend.setText(prefix + p_color + aprefix);

                toSend.sendToPlayer(p);
            } else {
                p.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
            }
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No one heard you.");
            sendHologramChat(p, personal_msg, prefix, p_color);
        }

        for (Player pl : to_send) {
            if (normal != null) {
                JSONMessage toSend = normal;
                if (hasAdultFilter(pl.getName())) {
                    toSend = filter;
                }
                ChatColor p_color = getPlayerColor(p, pl);

                toSend.setText(prefix + p_color + aprefix);

                toSend.sendToPlayer(pl);
            } else {
                ChatColor p_color = getPlayerColor(p, p);
                pl.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
            }
        }

        for (Player pl : secret_send) {
            if (normal != null) {
                JSONMessage toSend = normal;
                if (hasAdultFilter(pl.getName())) {
                    toSend = filter;
                }
                ChatColor p_color = getPlayerColor(p, pl);

                toSend.setText(prefix + p_color + aprefix);

                toSend.sendToPlayer(pl);
            } else {
                ChatColor p_color = getPlayerColor(p, p);
                pl.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
            }
        }

        if (to_send.size() <= 0) {
            return; // Don't show debug.
        }

        ChatColor p_color = getPlayerColor(p, p);

        if (normal != null) {

            JSONMessage toSend = normal;
            if (hasAdultFilter(p.getName())) {
                toSend = filter;
            }

            toSend.setText(prefix + p_color + aprefix);
            toSend.sendToPlayer(p);
        } else {
            p.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
        }

        sendHologramChat(p, personal_msg, prefix, p_color);

        log.info(ChatColor.stripColor("" + p.getName() + ": " + msg));
    }

    public boolean canShowAnotherHologram(Player p) {
        // The timer has expired
        if (!p.isOp() && !PermissionMechanics.isGM(p.getName()) && PermissionMechanics.getRank(p.getName()).equalsIgnoreCase("default")) {
            return false;
        }
        if (hologram_chat.containsKey(p.getName()) && (hologram_chat.get(p.getName()) <= System.currentTimeMillis())) {
            return true;
        }
        return false;
    }

    public void sendHologramChat(Player p, String personal_msg, String prefix, ChatColor p_color) {
        if (canShowAnotherHologram(p)) {
            Hologram hologram = null;
            List<String> lines = new ArrayList<String>();
            lines.add(prefix + p_color + p.getName());
            lines.add(" ");
            if (personal_msg.length() < 20) {
                lines.add(personal_msg);
            }
            if (personal_msg.length() >= 20 && personal_msg.length() < 40) {
                String double_string = personal_msg;
                lines.add(double_string.substring(0, personal_msg.length()));
            }
            if (personal_msg.length() >= 40 && personal_msg.length() < 60) {
                String otherString = personal_msg;
                lines.add(otherString.substring(40, personal_msg.length()));
            }
            if (personal_msg.length() >= 60 && personal_msg.length() <= 80) {
                String otherString = personal_msg;
                lines.add(otherString.substring(40, personal_msg.length()));
            }
            hologram = new Hologram(Main.plugin, lines);
            hologram.show(p.getLocation().add(0, 1.2, 0), 20, null);
            hologram_chat.put(p.getName(), System.currentTimeMillis() + (1000 * 10));
        }
    }
    
    public static void sendAllStaffMessage(Player sender, String raw_message) {
    	List<Player> to_send_local = new ArrayList<Player>();
    	
    	for (Player pl : Bukkit.getOnlinePlayers()) {
    		if (PermissionMechanics.isStaff(pl)) {
    			to_send_local.add(pl);
    		}
    	}
    	
    	for (Player staff : to_send_local) {
    		ChatColor pColor = getPlayerColor(sender, staff);
    		String prefix = getPlayerPrefix(sender);
    		
    		String message = raw_message;
    		if (hasAdultFilter(staff.getName())) {
    			message = censorMessage(message);
    		}
    		if (message.endsWith(" ")) {
    			message = message.substring(0, message.length() -1);
    		}
    		
    		message = fixCapsLock(message);
    		
    		staff.sendMessage(ChatColor.GOLD + "<" + ChatColor.BOLD + "SC" + ChatColor.GOLD + "> " + prefix + pColor + sender.getName() + ": " + message);
    	}
    	
    	log.info("<SC>" +  sender.getName() + ": " + raw_message);
    	
    	String local_server = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
    	String message_to_send  = "&staffchat/" + local_server + "*" + sender.getName() + ":" + raw_message;
    	sendAllServersStaffMessage(message_to_send);
    }
    
    public static void sendAllServersStaffMessage(String message) {
    	List<Object> query = new ArrayList<Object>();
    	query.add(message);
    	query.add(null);
    	query.add(true);
    	CommunityMechanics.social_query_list.put(GuildMechanics.nextSessionId(), query);
    }
}