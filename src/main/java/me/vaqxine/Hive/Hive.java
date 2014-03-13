package me.vaqxine.Hive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.commands.CommandBenchmark;
import me.vaqxine.Hive.commands.CommandBio;
import me.vaqxine.Hive.commands.CommandDRLoad;
import me.vaqxine.Hive.commands.CommandDRSave;
import me.vaqxine.Hive.commands.CommandHQuery;
import me.vaqxine.Hive.commands.CommandLogout;
import me.vaqxine.Hive.commands.CommandProfile;
import me.vaqxine.Hive.commands.CommandReboot;
import me.vaqxine.Hive.commands.CommandShard;
import me.vaqxine.Hive.commands.CommandSuicide;
import me.vaqxine.Hive.commands.CommandSync;
import me.vaqxine.Hive.commands.CommandWhois;
import me.vaqxine.Hive.commands.CommandWipe;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.LootMechanics.LootMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.SpawnMechanics.SpawnMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import me.vaqxine.database.ConnectionPool;
import me.vaqxine.enums.CC;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEquipment;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;

public class Hive implements Listener {
    //"US-5", "US-6", "US-7", "US-8"
    static List<String> us_public_servers = new ArrayList<String>(Arrays.asList("US-1", "US-2", "US-3", "US-4", "US-11"));
    static List<String> us_private_servers = new ArrayList<String>(Arrays.asList("US-9", "US-10"));
    static List<String> br_servers = new ArrayList<String>(Arrays.asList("BR-1"));

    // CREDENTIAL INFORMATION -- DO NOT CHANGE!
    public static final int FTP_port = 21;
    public static final int SQL_port = 7447; // 9834 11451
    public static final int transfer_port = 6427; // 10521
    public static final int SITE_SQL_PORT = 9108; // 21413

    public static final String Proxy_IP = "69.197.31.34"; // Frontend login server IP.
    public static final String Site_IP = "192.169.82.62"; // Website IP for SQL.
    public static final String Hive_IP = "72.20.40.38"; // Player database backend IP (used for SQL). 72.20.40.38

    public static final String sql_user = "slave_3XNZvi"; // k7ENqtenbZH3
    public static final String sql_password = "SgUmxYSJSFmOdro3"; // S?v<W>JN+XPt{ g04h@Is4F1Uz

    public static final String site_sql_url = "jdbc:mysql://" + Site_IP + ":" + SITE_SQL_PORT + "/vbforum";
    public static final String site_sql_user = "forum_G31FS2"; // website_fIX3vzpw
    public static final String site_sql_password = "9UEAXHK90GmFBwjL"; // 2CvkQ8WcSseR"

    public static final String version= "1.5";

    public static final String ftp_user = "agent";
    public static final String ftp_pass = "9bgsMKsknkJ6OY"; // $WHe4KT`l^S6sc

    public static final String sql_url = "jdbc:mysql://" + Hive_IP + ":" + SQL_port + "/dungeonrealms";
    // CREDENTIAL INFORMATION -- DO NOT CHANGE!

    public static String local_IP = "";

    public static int id = getServerNumFromPrefix(Bukkit.getMotd());

    public static boolean no_shard = false;
    // Do not allow /shard -- toggle.

    public static volatile List<String> offline_servers = new ArrayList<String>();
    // Reported as offline servers, show them as offline in the UI.

    public static volatile boolean server_frozen = false;
    // Set to true when the server is frozen, when it =true, a multithreaded data upload is run.

    public static String main_world_name = "";
    // Thread-safe version of Bukkit.getWorlds().get(0).getName()

    List<Player> local_plist = new ArrayList<Player>();
    // Although currently depreciated, used when connection to the DungeonRealms hive is lost to create a list of all players who are online at the time of the d/c.

    public static List<String> pending_upload = new ArrayList<String>();
    // Players are added to this list when they first logout, and removed once all their data has been saved. 
    // It's used to prevent data loss on server shutdown events -- it lets us know that multithreaded processes are still running.

    public static List<String> no_upload = new ArrayList<String>();
    // DEPRECIATED(?) - Used originally to prevent FTP uploads of .dat information on corrupt players, but no longer applies due to SQL upload.
    // Updated for use w/ possible SQL issues.

    static List<String> being_uploaded = new ArrayList<String>();
    // Players are added to this list on logout, it prevents the players from logging back in locally until AFTER their data has been uploaded.

    static List<String> lockout_players = new ArrayList<String>();
    // List of players who are 'locked' to the local server due to their data not uploading properly.

    public static List<String> first_login = new ArrayList<String>();
    // Contains a list of players who are logging in for the very first time.

    public static List<String> online_today = new ArrayList<String>();
    // Players who are online today, ecash.

    public static List<String> killing_self = new ArrayList<String>();
    // 2-Step confirmation for the /suicide command.

    public static volatile ConcurrentHashMap<String, List<Object>> remote_player_data = new ConcurrentHashMap<String, List<Object>>();
    // Packaged version of player data, created in loadPlayerDataSQL() and accessed throughout login proceedure.

    public static HashMap<Integer, List<Integer>> server_population = new HashMap<Integer, List<Integer>>();
    // Contains min/max players for every server, used for shard menu.
    // US-1, Array(10,150)

    // Local player data -- THREAD SAFE!
    public static volatile HashMap<String, String> local_player_ip = new HashMap<String, String>();
    public static volatile HashMap<String, List<String>> player_ip = new HashMap<String, List<String>>();
    public static volatile HashMap<String, Inventory> player_inventory = new HashMap<String, Inventory>();
    public static volatile HashMap<String, Location> player_location = new HashMap<String, Location>();
    public static volatile HashMap<String, Double> player_hp = new HashMap<String, Double>();
    public static volatile HashMap<String, Integer> player_level = new HashMap<String, Integer>();
    public static volatile HashMap<String, Integer> player_food_level = new HashMap<String, Integer>();
    public static volatile HashMap<String, ItemStack[]> player_armor_contents = new HashMap<String, ItemStack[]>();
    public static volatile HashMap<String, Integer> player_ecash = new HashMap<String, Integer>();
    public static volatile HashMap<String, Integer> player_sdays_left = new HashMap<String, Integer>();
    public static volatile HashMap<String, List<Integer>> player_portal_shards = new HashMap<String, List<Integer>>();
    // Local player data -- THREAD SAFE!

    public static HashMap<String, Long> player_first_login = new HashMap<String, Long>();
    // The LONG-format time a player first logged in. Used for noobie-protection.

    public static HashMap<String, String> player_bio = new HashMap<String, String>();
    // Player Name, Bio(being written)

    // These two hashes are both used for COMBAT-LOGGING NPC management.
    public static HashMap<String, RemoteEntity> player_to_npc = new HashMap<String, RemoteEntity>();

    public static HashMap<String, String> player_to_npc_align = new HashMap<String, String>(); 
    // ^ Largely Depreciated due to issues with onLogin inventories not being cleared, can cause dupes.

    public static HashMap<String, ItemStack> player_item_in_hand = new HashMap<String, ItemStack>();
    // Item in player's hand on death.

    public static HashMap<String, Inventory> player_mule_inventory = new HashMap<String, Inventory>(); 
    // Stores data for mule inventories on combat log.

    public static HashMap<RemoteEntity, List<ItemStack>> npc_inventory = new HashMap<RemoteEntity, List<ItemStack>>();
    // The inventory of a combat logged-NPC.

    public static HashMap<RemoteEntity, List<ItemStack>> npc_armor = new HashMap<RemoteEntity, List<ItemStack>>();
    // The armor of a combat logged-NPC.

    static ConcurrentHashMap<String, Long> logout_time = new ConcurrentHashMap<String, Long>();
    // Saves the time at which a combat-logging player logs out to determine when to despawn the NPC.

    public static HashMap<String, Long> last_sync = new HashMap<String, Long>();
    // Prevents spam of /sync command to manually send data to database.

    public static ConcurrentHashMap<String, Integer> safe_logout = new ConcurrentHashMap<String, Integer>();
    // Countdown for /logout function.

    public static HashMap<String, Location> safe_logout_location = new HashMap<String, Location>();
    // Used to ensure players aren't moving too far from their original safe logout location.

    public static volatile HashMap<String, String> to_kick = new HashMap<String, String>();
    // Used by multi-threading opperations to kick players. The threads add names and reasons to this hashmap, and it kicks the players on the main thread via a scheduler.

    public static HashMap<String, Integer> forum_usergroup = new HashMap<String, Integer>();
    // Locally cached forum group -- only used to give baby_zombie currently.

    public static HashMap<String, Long> login_time = new HashMap<String, Long>();
    // Used by many other plugins to determine when a player has -just- logged in and therfore should be excempt from certain processes.

    public static HashMap<String, String> server_swap = new HashMap<String, String>();
    // Players who are swapping shards. This map is accessed in uploadPlayerData() to skip certain tasks / checks and such.
    // PLAYER_NAME, SERVER_ID

    public static HashMap<String, Location> server_swap_location = new HashMap<String, Location>();
    // Players who are swapping shards. This map is accessed in uploadPlayerData() to skip certain tasks / checks and such.
    // PLAYER_NAME, SERVER_ID

    public static HashMap<String, String> server_swap_pending = new HashMap<String, String>();
    // Prevent abuse from mooman and his evil scripts.

    public static volatile ConcurrentHashMap<Integer, Long> last_ping = new ConcurrentHashMap<Integer, Long>();
    // Last time each server_num sent information to the proxy. If >20 seconds, server is offline.

    public static boolean local_saving = false;
    // Turns on when the HIVE is detected as offline, meaning it's either being DDOS'd or this server is being DDOS'd.

    public static boolean local_ddos = false;
    public static boolean possible_local_ddos = false;
    // =True when local connectivity is lost.

    public static boolean hive_ddos = false;
    public static boolean possible_hive_ddos = false;
    // =True when hive connecivity is lost.

    boolean payload_pending = false;
    // Set to =true after get_payload has been detected as true. It will then begin to check if payload.zip is ready, if it's ready, it'll initiate the download via async scheduler.

    public static boolean restart_inc = false;
    // Set to =true for ping events.

    public static boolean reboot_me = false;
    // Thread-safe reboot command.

    public static boolean get_payload_spoof = false;

    public static boolean get_payload = false;
    // Set to true in ListenThread when it's time to begin process of grabbing payload.zip
    // Sets payload_pending to true in scheduler.

    public static boolean server_lock = false;
    // Server is locked, all logins are disabled, MOTD prefix: [LOCKED]

    public static boolean shutting_down = false;
    // Run onDisable, used to determine when the server is in the process of turning off.

    public static boolean force_kick = false;
    // Thread safe 'kick-all' command.

    public static boolean loading_server = true;
    // Loading server on bootup, don't let players in right away. Initiate stuff.

    public Thread port_listener;
    // Port listener (payload) listener.

    public static String MOTD = "";
    // Cached MOTD.

    public static Hive instance = null;
    // Static plugin reference.

    public static Logger log = Logger.getLogger("Minecraft");

    public static boolean ready_to_die = false;
    // Used for delayed server 'stops'. Makes sure all data is uploaded and sorted before stopping.

    public static volatile long anti_crash_time = System.currentTimeMillis();
    // Used as a reference point to determine if the server has responded in last 30 seconds.

    public static String rootDir = "";

    public static int player_count = 0;
    // Used in a bunch of plugins to determine different spawn rates.

    public static long uptime = 0;
    // Server uptime in 1/4th seconds.

    public static long seconds_to_reboot = 0;
    // Seconds remaining until server reboot.

    public static Inventory ShardMenu = null;
    // Seconds remaining until server reboot.

    public static long last_shard_update = 0;
    // When to requery shard menu.

    public static Thread backup;
    // Controls 15min automated backup of player data.

    public static Thread sync;
    // Controls /sync multithreaded

    public static int last_player_count = 0;
    // For Minecade server stuff

    public static List<String> loaded_players = new ArrayList<String>();
    // Players whose data has been loaded, prevents wipe from quick login/outs.

    public static volatile List<String> sync_queue = new ArrayList<String>();
    // /sync multithreading

    public final static long serverStart = System.currentTimeMillis();
    // Server startup time for monitoring schedule reboots.

    Thread ThreadPool;
    // Controls all new Thread() SQL queries.

    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<String>();
    // All SQL queries to run on ThreadPool.

    public static EntityManager npc_manager = null;

    @SuppressWarnings("deprecation")
    public void onEnable() {
        instance = this;
        log.info(TimeZone.getDefault().toString());
        TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));

        Main.plugin.getCommand("benchmark").setExecutor(new CommandBenchmark());
        Main.plugin.getCommand("bio").setExecutor(new CommandBio());
        Main.plugin.getCommand("drload").setExecutor(new CommandDRLoad());
        Main.plugin.getCommand("drsave").setExecutor(new CommandDRSave());
        Main.plugin.getCommand("hquery").setExecutor(new CommandHQuery());
        Main.plugin.getCommand("logout").setExecutor(new CommandLogout());
        Main.plugin.getCommand("profile").setExecutor(new CommandProfile());
        Main.plugin.getCommand("reboot").setExecutor(new CommandReboot());
        Main.plugin.getCommand("shard").setExecutor(new CommandShard());
        Main.plugin.getCommand("suicide").setExecutor(new CommandSuicide());
        Main.plugin.getCommand("sync").setExecutor(new CommandSync());
        Main.plugin.getCommand("whois").setExecutor(new CommandWhois());
        Main.plugin.getCommand("wipe").setExecutor(new CommandWipe());
        
        local_IP = Bukkit.getIp();
        MOTD = Bukkit.getMotd();

        restoreCorruptShops(false);

        ThreadPool = new ThreadPool();
        ThreadPool.start();

        backup = new BackupPlayerData();
        backup.start();

        sync = new SyncCommand();
        sync.start();

        Thread echo_online = new Thread(new Runnable(){
            public void run(){
                try{Thread.sleep(20000);}catch(InterruptedException ie){}

                CommunityMechanics.sendPacketCrossServer("[online]" + MOTD.substring(0, MOTD.indexOf(" ")), -1, true);

                Socket kkSocket = null;
                PrintWriter out = null;
                try {

                    kkSocket = new Socket();
                    //kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
                    kkSocket.connect(new InetSocketAddress(Proxy_IP, Hive.transfer_port), 2000);
                    out = new PrintWriter(kkSocket.getOutputStream(), true);

                    out.println("[online]" + MOTD.substring(0, MOTD.indexOf(" ")));
                    kkSocket.close();
                } catch (IOException e) {
                    System.err.println("Can't connect to the proxy! Server not whitelisted!");
                }

                if(out != null){
                    out.close();
                }

            }
        });
        echo_online.start();

        makeAllTables();
        setSystemPath();

        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
        Main.plugin.getServer().getMessenger().registerOutgoingPluginChannel(Main.plugin, "BungeeCord");

        main_world_name = Bukkit.getWorlds().get(0).getName();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                updateServerPlayers();
            }
            /* 5 second delay, 10 second increments */
        }, 20 * 5, 20 * 10);

        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            public void run() {
                npc_manager = RemoteEntities.createManager(Main.plugin);
            }
        }, 5 * 20L);

        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            public void run() {
                loading_server = false; // Let people in!
            }
        }, 15 * 20L);

        /*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				updateServerPopulations();
			}
		}, 10 * 20L, 10 * 20L);*/

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                uptime++;
            }
        }, 5L, 5L);

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                if(seconds_to_reboot > 0){
                    seconds_to_reboot--;
                }
                if(MoneyMechanics.no_bank_use){
                    for(Player pl : Main.plugin.getServer().getOnlinePlayers()){
                        if(pl.getInventory().getName().startsWith("Bank Chest") || pl.getInventory().getName().equalsIgnoreCase("Collection Bin")){
                            pl.closeInventory();
                        }
                    }
                }
            }
        }, 1 * 20L, 1 * 20L);

        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                for(Entry<Integer, Long> data : last_ping.entrySet()){
                    long time = data.getValue();
                    int server_num = data.getKey();

                    if((System.currentTimeMillis() - time) > (15 * 1000)){
                        String server_prefix = getServerPrefixFromNum(server_num);
                        if(!(offline_servers.contains(server_prefix))){
                            offline_servers.add(server_prefix);
                        }
                        server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(0, 0)));
                        last_ping.remove(server_num);
                    }
                }
            }
        }, 10 * 20L, 1 * 20L);

        /*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {

				if(possible_local_ddos && hasConnection()){
					possible_local_ddos = false;
				}

				if(possible_hive_ddos && isHiveOnline()){
					possible_hive_ddos = false;
				}

				if(local_ddos){
					// We can't upload our data cause we're being DDOS'd -- players will d/c shortly so we'll lock the server until it's over.
					try {
						if(hasConnection()){ // Make sure all data has been uploaded.
							local_ddos = false;
							possible_local_ddos = false;
							// We have connectivity back!
							// Now all the pending data will upload.
							Bukkit.getServer().broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + ">>" + ChatColor.GREEN + " Local Connectivity has been " + ChatColor.UNDERLINE + "restored" + ChatColor.GREEN + ", uploading all local data then unlocking server.");
							force_kick = false;
							server_lock = false;
							return;
						}
						return; // Do nothing else, let's sort out local ddos situation first.
					} catch (Exception e) {e.printStackTrace();}
				}

				/*if(hive_ddos){
					// Hive is offline, so we need to cache all local data, lock server, and wait to upload it.
					// The upload function will just keep trying to upload until it works.
					if(isHiveOnline()){
						// The hive is back online!
						hive_ddos = false;
						possible_hive_ddos = false;
						Bukkit.getServer().broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + ">>" + ChatColor.GREEN + " Database Connectivity has been " + ChatColor.UNDERLINE + "restored" + ChatColor.GREEN + ", local login servers are now online.");
					}
				}

				if(!local_ddos && !(hasConnection())){
					if(possible_local_ddos == true){
						Bukkit.getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + ">>" + ChatColor.RED + " Local Connectivity has been " + ChatColor.UNDERLINE + "lost" + ChatColor.RED + ", locking server and freezing local data.");
						local_ddos = true;
						force_kick = true;
						server_lock = true;
						possible_local_ddos = false;
						return;
					}
					else{
						possible_local_ddos = true;
					}
				}

				/*if(!hive_ddos && !(isHiveOnline())){
					if(possible_hive_ddos == true){
						Bukkit.getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + ">>" + ChatColor.RED + " Database Connectivity has been " + ChatColor.UNDERLINE + "lost" + ChatColor.RED + ", local login servers have been disabled. Your data will be uploaded once connection is re-established.");
						hive_ddos = true;
						possible_hive_ddos = false;
					}
					else{
						possible_hive_ddos = true;
					}
				}

			}
		}, 10 * 20L, 10 * 20L);*/ // Require at least a 20 second d/c for it to care, otherwise it could just lag spike out and fix itself.

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                player_count = Bukkit.getServer().getOnlinePlayers().length;
            }
        }, 10 * 20L, 5 * 20L);

        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                WebsiteConnectionPool.refresh = true;
            }
        }, 300 * 20L, 300 * 20L);

        // TODO: Is this needed?
        /*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Player pl : getServer().getOnlinePlayers()){
					if(pending_upload.contains(pl.getName()) || server_swap.containsKey(pl.getName())){
						continue; // Do not write new data if the player is uploading or changing shards.
					}
					player_inventory.put(pl.getName(), pl.getInventory());
					player_location.put(pl.getName(), pl.getLocation());
					player_hp.put(pl.getName(), (double)pl.getHealth());
					player_level.put(pl.getName(), pl.getLevel());
					player_food_level.put(pl.getName(), pl.getFoodLevel());
					player_armor_contents.put(pl.getName(), pl.getInventory().getArmorContents());
					// Save data locally so if there's a crash multithread can access.
				}
			}
		}, 10 * 20L, 5 * 20L);*/

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                if(reboot_me == true){
                    reboot_me = false;

                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(!(Hive.server_frozen)){
                            p.saveData();
                        }
                        p.kickPlayer(ChatColor.GREEN.toString() + "You have been safely logged out by the server." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
                    }

                    int count = 0;
                    while(pending_upload.size() > 0 && count <= 200){
                        count++;
                        try {
                            Thread.sleep(100); // Let all pending multi-thread uploads finish.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } 
                    }

                    Main.plugin.getServer().shutdown();
                }
            }
        }, 10 * 20L, 1 * 20L);

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                List<String> to_remove = new ArrayList<String>();
                for(Entry<String, String> data : to_kick.entrySet()){
                    String s = data.getKey();
                    String reason = data.getValue();
                    if(Bukkit.getPlayer(s) != null){
                        Player pl = Bukkit.getPlayer(s);
                        pl.kickPlayer(reason);
                    }
                    to_remove.add(s);
                }

                for(String s : to_remove){
                    to_kick.remove(s);
                }
            }
        }, 20 * 20L, 1 * 20L);


        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                if(Hive.server_lock == true){
                    return;
                }
                Thread t = new FixBrokenLoginCodes();
                t.start();
            }
        }, 10 * 20L, 20 * 20L); // Perform it quickly on launch, then after a while.

        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                if(payload_pending == true){
                    try {
                        URL url = new URL("ftp://" + ftp_user + ":" + ftp_pass + "@" + Hive_IP + "/sdata/payload.zip");
                        url.openConnection();
                        URLConnection urlc = url.openConnection();
                        InputStream is = urlc.getInputStream(); 
                        is.close();
                    } catch (IOException not_ready) {
                        log.info("[HIVE (SLAVE Edition)] payload.zip is not yet ready for pickup, waiting 5s...");
                        return;
                    }

                    try {
                        payload_pending = false;
                        downloadPayload();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(get_payload == true){
                    get_payload = false;
                    payload_pending = true;
                }
            }

        }, 10 * 20L, 5 * 20L);

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {    	
                if(server_lock == true && (force_kick == true || get_payload == true || get_payload_spoof == true) && Main.plugin.getServer().getOnlinePlayers().length > 0){
                    for(Player p : Main.plugin.getServer().getOnlinePlayers()){
                        if(p.isOp() && get_payload == false && get_payload_spoof == false){
                            continue; // Don't kick the OP's.
                        }
                        if(!(Hive.server_frozen)){
                            p.saveData();
                        }
                        if(force_kick == true){
                            p.kickPlayer("\n" + ChatColor.GREEN.toString() + "This " + ChatColor.BOLD.toString() + "Dungeon Realms" + ChatColor.GREEN.toString() + " shard has been temporarily " + ChatColor.UNDERLINE + "LOCKED."
                                    + "\n\n" + ChatColor.GRAY + "Your player data is being synced.");
                        }
                        else if(get_payload == true || get_payload_spoof == true){
                            p.kickPlayer("\n" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Dungeon Realms" + ChatColor.GREEN.toString() + " is running a content patch."
                                    + "\n\n" + ChatColor.GRAY + "This shard is currently downloading a new SNAPSHOT of the server software.");
                        }
                    }
                }
            }
        }, 5 * 20L, 5L);

        Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {    	
                if(ready_to_die == true){
                    if((new File(rootDir + "/" + "payload.zip").exists())){
                        ready_to_die = false;
                        Main.plugin.getServer().shutdown();
                    }
                }
            }
        }, 10 * 20L, 1 * 20L);

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {    	
                List<String> to_remove = new ArrayList<String>();
                for(Entry<String, Integer> data : safe_logout.entrySet()){
                    String p_name = data.getKey();
                    Integer seconds_left = data.getValue();

                    if(Bukkit.getPlayer(p_name) == null){
                        to_remove.add(p_name);
                        continue;
                    }

                    Player p = Bukkit.getPlayer(p_name);

                    if(seconds_left <= 0){
                        if(HealthMechanics.in_combat.containsKey(p_name)){
                            to_remove.add(p_name);
                            continue; // They're in combat...
                        }
                        HealthMechanics.in_combat.remove(p_name);
                        to_remove.add(p_name);
                        p.kickPlayer(ChatColor.GREEN.toString() + "You have safely logged out." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
                        continue;
                    }

                    p.sendMessage(ChatColor.RED + "Logging out in ... " + ChatColor.BOLD + seconds_left + "s");
                    seconds_left = seconds_left - 1;
                    safe_logout.put(p.getName(), seconds_left);
                }

            }
        }, 5 * 20L, 20L);


        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                for(Map.Entry<String, Long> set : logout_time.entrySet()){
                    try{
                        final String p_name = set.getKey();
                        Long log_time = set.getValue();

                        if((System.currentTimeMillis() - log_time) > (20 * 1000)){ 
                            if(!(player_to_npc.containsKey(p_name))){ // If the NPC died or something.
                                logout_time.remove(p_name);

                                log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "[HIVE (SLAVE Edition)] Player " + p_name + "'s NPC has been killed [DEBUG]." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());

                                Thread t = new Thread(new Runnable(){
                                    public void run(){
                                        setCombatLogger(p_name);
                                        Hive.setPlayerOffline(p_name, 5);
                                    }
                                });

                                t.start();
                                continue;
                            }

                            RemoteEntity n = player_to_npc.get(p_name);

                            npc_inventory.remove(n);
                            npc_armor.remove(n);
                            logout_time.remove(p_name); // The player is now safe and may log back in again with their items still intact.
                            player_to_npc.remove(p_name);
                            player_to_npc_align.remove(p_name);

                            log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "[HIVE (SLAVE Edition)] Player " + p_name + "'s NPC has been despawned." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
                            List<Player> lpl = new ArrayList<Player>();
                            for(Entity ent : n.getBukkitEntity().getNearbyEntities(32, 32, 32)){
                                if(ent instanceof Player){
                                    lpl.add((Player)ent);
                                }
                            }

                            n.getBukkitEntity().remove();
                           // ShopMechanics.updateEntity(n.getBukkitEntity(), lpl);; // TODO - Need to update to latest DR Api!

                            Thread t = new Thread(new Runnable(){
                                public void run(){
                                    Hive.setPlayerOffline(p_name, 5);
                                }
                            });

                            t.start();
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                }
            }

        }, 5 * 20L, 20L);

        Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
                anti_crash_time = System.currentTimeMillis();
                // Update variable to determine if server is responding....
            }
        }, 5 * 20L, 1 * 20L); 

        Thread crash_checker = new Thread(new Runnable(){
            @Override
            public void run(){
                long multithread_anti_crash = 0;
                multithread_anti_crash = Hive.anti_crash_time;
                boolean crashed = false;

                while(!crashed){
                    try {Thread.sleep(30 * 1000);} catch (InterruptedException e) {continue;}
                    if(multithread_anti_crash == Hive.anti_crash_time){
                        if(server_frozen == true || shutting_down == true || ShopMechanics.shop_shutdown == true){
                            continue; // Pointless.
                        }

                        // No tick in last 30 seconds, upload local data and reboot.
                        System.out.println("[HIVE (Slave Edition)] Detected no activity in main thread for 30 seconds, uploading local data and locking server.");

                        server_frozen = true;
                        uploadDataOnCrash();

                        Socket kkSocket = null;
                        PrintWriter out = null;
                        try {

                            kkSocket = new Socket();
                            //kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
                            kkSocket.connect(new InetSocketAddress(Proxy_IP, Hive.transfer_port), 2000);
                            out = new PrintWriter(kkSocket.getOutputStream(), true);

                            out.println("[crash]" + MOTD.substring(0, MOTD.indexOf(" ")));
                            kkSocket.close();
                        } catch (IOException e) {
                            System.err.println(CC.RED + "Not connected to proxy!" + CC.DEFAULT);
                        }

                        if(out != null){
                            out.close();
                        }

                        CommunityMechanics.sendPacketCrossServer("[crash]" + MOTD.substring(0, MOTD.indexOf(" ")), -1, true);

                        crashed = true;
                        break;
                    }
                    else if(multithread_anti_crash != Hive.anti_crash_time){
                        multithread_anti_crash = Hive.anti_crash_time;
                        // Update time.
                    }
                }
            }
        });

        crash_checker.start();

        Thread update_population = new Thread(new Runnable(){
            public void run(){
                try{Thread.sleep(10 * 1000);}catch(Exception err){}
                while(true){
                    try{Thread.sleep(10 * 1000);}catch(Exception err){}

                    if(Hive.shutting_down || Hive.server_frozen || Hive.server_lock || Hive.force_kick || Hive.restart_inc){
                        continue; // Do not update population if server is not reachable, so the timeout will occur on the proxy.
                    }

                    updateServerPopulations();
                }
            }
        });

        update_population.start();

        /*if(!(isThisRootMachine())){
			port_listener = new ListenThread();
			port_listener.start();
		}*/

        log.info(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "**************************");
        log.info(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "[HIVE (SLAVE Edition)] has been enabled.");
        log.info(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "**************************" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
    }

    public void onDisable() {
        //backup.interrupt();
        // Interrupts backup process.

        if(shutting_down == true){
            return;
        }

        shutting_down = true;

        for(RemoteEntity n : player_to_npc.values()){
            //n.removeFromWorld();
            n.despawn(DespawnReason.CUSTOM);
        }

        player_to_npc.clear();

        /*int count = 0;
		while(pending_upload.size() > 0 && count <= 200){
			count++;
			log.info("[HIVE (SLAVE Edition)] ONLINE PLAYERS: " + Bukkit.getOnlinePlayers().length);
			log.info("[HIVE (SLAVE Edition)] PENDING UPLOAD: " + pending_upload.size());
			try {
				Thread.sleep(100); // Let all pending multi-thread uploads finish.
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}*/

        //CommunityMechanics.sendPacketCrossServer("[crash]" + MOTD.substring(0, MOTD.indexOf(" ")), -1, true); // TODO Wtf? A crash packet on shutdown?

        Socket kkSocket = null;
        PrintWriter out = null;
        try {

            kkSocket = new Socket();
            //kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
            kkSocket.connect(new InetSocketAddress(Proxy_IP, Hive.transfer_port), 2000);
            out = new PrintWriter(kkSocket.getOutputStream(), true);

            out.println("[crash]" + MOTD.substring(0, MOTD.indexOf(" ")));
            kkSocket.close();
        } catch (IOException e) {
            System.err.println(CC.RED + "Not connected to proxy!" + CC.DEFAULT);
        }

        if(out != null){
            out.close();
        }

        log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "**************************");
        log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[HIVE (SLAVE Edition)] has been disabled.");
        log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "**************************" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
    }

    public void updateServerPlayers() {
        final int playerCount = Bukkit.getOnlinePlayers().length;
        if (playerCount == last_player_count) {
            return;
        }
        last_player_count = playerCount;
        sql_query.add("UPDATE server SET online_players='" + playerCount + "' WHERE id='" + id + "';");
    }

    public void uploadDataOnCrash(){
        server_lock = true;
        force_kick = true;

        for(String s : ShopMechanics.shop_stock.keySet()){
            // This will convert all shop stocks to collection bins in collection_bin, and upload the data of any players not online.
            // uploadShopDatabaseData() will take care of online players.
            ShopMechanics.backupStoreData(s);
        }

        for(String s : player_inventory.keySet()){
            try{
                uploadPlayerDatabaseData(s); // Uploads all player-specific data that is stored locally on this server.
            } catch(SQLException err){
                err.printStackTrace();
            }

            MoneyMechanics.uploadBankDatabaseData(s, false); // Uploads bank records of local players.
            ShopMechanics.uploadShopDatabaseData(s, false); // Uploads collection bin and shop level of all current logged in users. Collection bin will be accurate due to the .backupStoreData(s) above.
            ShopMechanics.asyncSetShopServerSQL(s, -1); // Sets the shop to no longer exist, as the server will be rebooting shortly.

            log.info("[HIVE (RECOVERY)] Uploaded local data for: " +  s);
        }

        RealmMechanics.uploadLocalRealms();
        setAllPlayersAsOffline(); // Sets all players to offline so they can log in again whenever they want now that their data is uploaded.
        Main.plugin.getServer().shutdown();
    }

    public static void runSyncQuery(String query){
        Connection con = null;
        PreparedStatement pst = null;

        try {
            pst = ConnectionPool.getConnection().prepareStatement(query);
            pst.executeUpdate();

            Hive.log.info("[Hive] SYNC Executed query: " + query);

        } catch (SQLException ex) {
            Hive.log.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Hive.log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    public static void restoreCorruptShops(boolean all){
        // Grabs a query of all shops reported to be on this server, if the shop doesn't exist, the shop_backup data is converted into collection_bin data.
        if(MOTD.contains("US-V")){
            return;
        }
        System.err.println(MOTD);
        int lserver_num = Integer.parseInt(MOTD.substring(MOTD.indexOf("-") + 1, MOTD.indexOf(" ")));
        if(MOTD.contains("EU-")){
            lserver_num += 1000;
        }
        if(MOTD.contains("BR-")){
            lserver_num += 2000;
        }
        if(MOTD.contains("US-YT")){
            lserver_num += 3000;
        }

        PreparedStatement pst = null;

        try {

            if(all){
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "SELECT p_name, shop_backup FROM shop_database WHERE shop_backup!='null' && shop_backup IS NOT NULL && shop_backup!='' && (collection_bin IS NULL) && server_num>=0"); //  collection_bin IS NOT NULL && collection_bin!='null' &&

                pst.execute();
            }
            else if(!all){
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "SELECT p_name, shop_backup FROM shop_database WHERE server_num = '" + lserver_num + "' && server_num!=-1 && shop_backup!='null' && shop_backup IS NOT NULL && shop_backup!='' && (collection_bin IS NULL)"); //  collection_bin IS NOT NULL && collection_bin!='null' &&

                pst.execute();
            }

            ResultSet rs = pst.getResultSet();

            if(!(rs.next())){
                log.info("[ShopMechanics] No corrupt shop data found, skipping restore function.");
                return;
            }

            int fix_count = 0;
            rs.beforeFirst();

            while(rs.next()){
                String s_p_name = rs.getString("p_name");
                String s_shop_backup = rs.getString("shop_backup");
                if(s_shop_backup.length() > 0 && !(ShopMechanics.shop_stock.containsKey(s_p_name))){
                    // Convert shop_backup to collection_bin.
                    setCollectionBinSQL(s_p_name, s_shop_backup);
                    fix_count++;
                    log.info("[ShopMechanics] Recovered collection bin data of " + s_p_name + " -- set collection_bin string.");
                }
            }

            log.info("[ShopMechanics] Recovered a total of " + fix_count + " corrupt shops.");

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void setCollectionBinSQL(String p_name, String contents){
        PreparedStatement pst = null;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "INSERT INTO shop_database (p_name, collection_bin, server_num) VALUES('" + p_name + "', '" + contents + "', '-1') ON DUPLICATE KEY UPDATE collection_bin='" + contents + "', server_num='-1'");

            pst.executeUpdate();
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
    }

    public void updateServerPopulations(){
        final String prefix = MOTD.substring(0, MOTD.indexOf(" "));

        if(prefix.equalsIgnoreCase("US-0") || prefix.equalsIgnoreCase("US-99")){
            return;
        }

        int players_on = Main.plugin.getServer().getOnlinePlayers().length;
        int players_max = Main.plugin.getServer().getMaxPlayers();

        if(Hive.shutting_down || Hive.server_frozen || Hive.server_lock || Hive.force_kick || Hive.restart_inc){
            players_on = 0;
            players_max = 0;
            // This will mark the shard as offline.
        }

        CommunityMechanics.sendPacketCrossServer("@population@" + prefix + ":" + players_on + "/" + players_max, -1, true);

        Socket kkSocket = null;
        PrintWriter out = null;
        try {

            kkSocket = new Socket();
            kkSocket.connect(new InetSocketAddress(Proxy_IP, Hive.transfer_port), 1000);
            out = new PrintWriter(kkSocket.getOutputStream(), true);

            out.println("@population@" + prefix + ":" + Main.plugin.getServer().getOnlinePlayers().length + "/" + Main.plugin.getServer().getMaxPlayers());
            kkSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(out != null){
            out.close();
        }

        /*int lserver_num = Integer.parseInt(MOTD.substring(MOTD.indexOf("-") + 1, MOTD.indexOf(" ")));
		if(MOTD.contains("EU-")){
			lserver_num += 1000;
		}
		if(MOTD.contains("BR-")){
			lserver_num += 2000;
		}
		if(MOTD.contains("US-YT")){
			lserver_num += 3000;
		}

		for(Entry<Integer, String> data : CommunityMechanics.server_list.entrySet()){
			int server_num = data.getKey();
			if(server_num == lserver_num){
				continue; // Don't query ourselves. We're realtime.
			}
			String ip = data.getValue();
			try{
				MCQuery mcQuery = new MCQuery(ip, 32778);
				QueryResponse response = mcQuery.basicStat();

				int online_players = response.getOnlinePlayers(); //(int)((double)response.getOnlinePlayers() * 1.30D);
				int max_players = response.getMaxPlayers();

				if(online_players > max_players){
					online_players = max_players;
				}

				String server_prefix = getServerPrefixFromNum(server_num); 

				if(online_players > 0 && offline_servers.contains(server_prefix)){
					offline_servers.remove(server_prefix);
				}
				server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(online_players, max_players)));
			} catch(Exception err){
				//err.printStackTrace();
				server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(0, 0)));
				continue;
			}
		}*/
    }

    public static String getServerPrefixFromNum(int server_num){
        String result = "";
        if(server_num < 1000){
            result = "US-" + server_num; 
        }
        if(server_num >= 1000 && server_num < 2000){
            result = "EU-" + (server_num - 1000);
        }
        if(server_num > 2000){
            result = "BR-" + (server_num - 2000);
        }
        return result;
    }

    @SuppressWarnings("resource")
	public void makeAllTables(){
        Connection con = null;
        PreparedStatement pst = null;

        try {

            // Combines: gems, bank_data, bank_level
            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "bank_database" + "(p_name VARCHAR(18) PRIMARY KEY, content LONGTEXT, level INT, money INT) ENGINE=InnoDB;");
            pst.executeUpdate();

            // Combines: shop_data, cbin_data, shop_level
            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "shop_database" + "(p_name VARCHAR(18) PRIMARY KEY, server_num INT, level INT, collection_bin LONGTEXT, shop_backup LONGTEXT) ENGINE=InnoDB;");
            pst.executeUpdate();

            // Combines player_data, p_login_data, max_health, last_login, align_status, align_time
            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "player_database" + "(p_name VARCHAR(18) PRIMARY KEY, location TEXT, inventory LONGTEXT, hp INT, food_level INT, level INT, guild_name VARCHAR(16), combat_log BIT, last_login_time LONG, rank TINYTEXT, server_num INT, align_status VARCHAR(16), align_time LONG, toggles TEXT, pets TEXT, buddy_list TEXT, ignore_list TEXT, realm_tier INT, realm_title TINYTEXT, realm_loaded TINYINT(1), noob_player TINYINT(1), last_server TINYTEXT, ecash INT, ip TEXT, portal_shards TEXT, saved_gear TEXT, mule_inventory TEXT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "instance" + "(instance_template VARCHAR(18) PRIMARY KEY, times LONGTEXT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "guilds" + "(guild_name VARCHAR(16) PRIMARY KEY, guild_handle VARCHAR(3), guild_color INT, guild_server_num INT, members LONGTEXT, motd LONGTEXT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "reports" + "(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), type CHAR(18), reporter CHAR(18), offender CHAR(18), report TEXT, server VARCHAR(4), time DATETIME, cords TEXT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "statistics" + "(pname CHAR(18) PRIMARY KEY, unlawful_kills INT, lawful_kills INT, deaths INT, mob_kills INT, money INT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "perm_statistics" + "(pname CHAR(18) PRIMARY KEY, unlawful_kills INT, lawful_kills INT, deaths INT, mob_kills INT, money INT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "ban_list" + "(pname CHAR(18) PRIMARY KEY, unban_date DATETIME, ban_reason TEXT, who_banned CHAR(18), ban_date DATETIME, unban_reason TEXT, who_unbanned CHAR(18), rank CHAR(12), ban_count TINYINT) ENGINE=InnoDB;");
            pst.executeUpdate();

            pst = ConnectionPool.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + "mute_map" + "(pname CHAR(18) PRIMARY KEY, unmute LONG, who_muted CHAR(18)) ENGINE=InnoDB;");
            pst.executeUpdate();

        } catch (SQLException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        log.info("[HIVE (SLAVE Edition) Completed creating SQL tables.");
    }

    public void setFirstLoginSQL(String p_name){
        PreparedStatement pst = null;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "INSERT INTO player_database (p_name, first_login) VALUES('" + p_name + "', '" + System.currentTimeMillis() + "') ON DUPLICATE KEY UPDATE first_login=" + System.currentTimeMillis());

            pst.executeUpdate();
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
    }

    public static void setCombatLogger(String p_name){
        PreparedStatement pst = null;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "INSERT INTO player_database (p_name, combat_log) VALUES('" + p_name + "', " + 1 + ") ON DUPLICATE KEY UPDATE combat_log=1");

            pst.executeUpdate();
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
    }

    public static void uploadPlayerDatabaseData(String p_name) throws SQLException{
        Inventory inv = null;
        Location loc = null;

        String location = "";

        int level = -1;
        int food_level = -1;
        double hp = -1;
        String ip = "";

        if(Bukkit.getPlayer(p_name) != null && !(pending_upload.contains(p_name))){
            Player pl = Bukkit.getPlayer(p_name);
            loc = pl.getLocation();
            inv = pl.getInventory();

            level = HealthMechanics.getPlayerHP(pl.getName());
            food_level = pl.getFoodLevel();
            hp = pl.getHealth();
        }
        else{
            if(player_location.containsKey(p_name)){
                loc = player_location.get(p_name);
            }
            if(player_inventory.containsKey(p_name)){
                inv = player_inventory.get(p_name);
            }

            level = player_level.get(p_name);
            hp = player_hp.get(p_name);
            food_level = player_food_level.get(p_name);
        }

        if(loc == null || inv == null){return;} // We don't want to upload null values for inventory / location -- could result in wipe.
        if(!(player_location.containsKey(p_name)) || !(player_inventory.containsKey(p_name))){return;} // ^

        location = convertLocationToString(loc);

        int inv_count = 0;
        for(ItemStack is : inv.getContents()){
            if(is == null || is.getType() == Material.AIR){
                continue;
            }
            inv_count++;
        }

        if(inv_count <= 0){
            log.info("[HIVE] Empty inventory detected on upload for player: " + p_name);
            //return; // Empty inventory being uploaded.
        }

        String inventory = convertInventoryToString(p_name, inv, true);

        long align_time = 0;
        String align_status = "good";

        if(KarmaMechanics.align_map.containsKey(p_name)){
            align_status = KarmaMechanics.align_map.get(p_name);
            if(KarmaMechanics.align_time.containsKey(p_name)){
                align_time = KarmaMechanics.align_time.get(p_name);
            }
        }

        String rank = PermissionMechanics.getRank(p_name);

        int realm_tier = 1;

        try{
            realm_tier = RealmMechanics.realm_tier.get(p_name);
        } catch(NullPointerException npe){
            log.info("[HIVE] Null 'realm tier' value for player: " + p_name);
            realm_tier = 1; // Null? Impossible, but ok bro.
        }

        String realm_title = StringEscapeUtils.escapeSql(RealmMechanics.realm_title.get(p_name));
        long last_login_time = System.currentTimeMillis();

        String buddy_list = "";
        String ignore_list = "";

        if(CommunityMechanics.buddy_list.containsKey(p_name)){
            List<String> lbuddy_list = CommunityMechanics.buddy_list.get(p_name);
            for(String s : lbuddy_list){
                buddy_list += StringEscapeUtils.escapeSql(s + ",");
            }
        }

        if(CommunityMechanics.ignore_list.containsKey(p_name)){
            List<String> lignore_list = CommunityMechanics.ignore_list.get(p_name);
            for(String s : lignore_list){
                ignore_list += StringEscapeUtils.escapeSql(s + ",");
            }
        }

        String toggles = "";

        if(CommunityMechanics.toggle_list.containsKey(p_name)){
            final List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p_name);
            for(String s : ltoggle_list){
                toggles += s + ",";
            }
        }

        boolean new_player = HealthMechanics.noob_players.contains(p_name);
        int i_new_player = 0;
        if(new_player){
            i_new_player = 1;
        }

        if(player_ip.containsKey(p_name)){
            for(String l_ip : player_ip.get(p_name)){
                ip += l_ip + ",";
            }
            if(ip.endsWith(",")){
                ip = ip.substring(0, ip.length() - 1);
            }
        }

        String last_server = MOTD.substring(0, MOTD.indexOf(" "));

        String portal_shard_string = "";
        if(player_portal_shards.containsKey(p_name)){
            for(int i : player_portal_shards.get(p_name)){
                portal_shard_string += i + ",";
            }
            if(portal_shard_string.endsWith(",")){
                portal_shard_string = portal_shard_string.substring(0, portal_shard_string.lastIndexOf(","));
            }
        }

        String saved_gear = "";
        if(KarmaMechanics.saved_gear.containsKey(p_name)){
            saved_gear = StringEscapeUtils.escapeSql(convertInventoryToString(KarmaMechanics.saved_gear.get(p_name)));
        }

        String mule_inventory_string = "";
        if(MountMechanics.mule_inventory.containsKey(p_name)){
            mule_inventory_string = Hive.convertInventoryToString(null, MountMechanics.mule_inventory.get(p_name), false);
        }
        if(mule_inventory_string.equalsIgnoreCase("") && !MountMechanics.mule_inventory.containsKey(p_name)){
            if(MountMechanics.mule_itemlist_string.containsKey(p_name)){
                mule_inventory_string = StringEscapeUtils.escapeSql(MountMechanics.mule_itemlist_string.get(p_name));
            }
        }

        String achievments = "";
        if(AchievmentMechanics.achievment_map.containsKey(p_name)){
            achievments = AchievmentMechanics.achievment_map.get(p_name);
        }

        String ecash_storage = "";
        if(EcashMechanics.ecash_storage_map.containsKey(p_name)){
            ecash_storage = StringEscapeUtils.escapeSql(EcashMechanics.ecash_storage_map.get(p_name));
        }

        PreparedStatement pst = null;

        // 15 KEYS! -- Monster Query.
        pst = ConnectionPool.getConnection().prepareStatement( 
                "INSERT INTO player_database (p_name, location, inventory, hp, food_level, level, last_login_time, rank, align_status, align_time, toggles, buddy_list, ignore_list, realm_tier, realm_title, noob_player, combat_log, last_server, ip, portal_shards, saved_gear, mule_inventory, achievments, ecash_storage) " +
                        "VALUES('" + p_name + "', '" + location + "', '" + StringEscapeUtils.escapeSql(inventory) + "', '" + hp + "', '" + food_level + "', '" + level + "', '" + last_login_time + "', '" + rank+  "', '" + align_status + "', '" +
                        align_time + "', '" + toggles + "', '" + buddy_list + "', '" + ignore_list + "', '" + realm_tier + "', '" + realm_title + "', '" + i_new_player + "', 0, '" + last_server + "', '" + ip + "', '" + portal_shard_string + "', '" + saved_gear + "', '" + mule_inventory_string + "', '" + achievments + "', '" + ecash_storage + "') " +

			"ON DUPLICATE KEY UPDATE location='" + location + "', inventory='" + StringEscapeUtils.escapeSql(inventory) + "', hp='" + hp + "', food_level='" + food_level + "', level='" + level + "', " + 
			"last_login_time='" + last_login_time + "', rank='" + rank + "', align_status='" + align_status + "', align_time='" + align_time + "', toggles='" + toggles + "', buddy_list='" + buddy_list + "', " +
			"ignore_list='" + ignore_list + "', realm_tier='" + realm_tier + "', realm_title='" + realm_title + "', noob_player='" + i_new_player + "', combat_log=0, last_server='" + last_server + "', ip='" + ip + "', portal_shards='" + portal_shard_string + "', saved_gear='" + saved_gear + "', mule_inventory='" + mule_inventory_string + "', achievments='" + achievments + "', ecash_storage='" + ecash_storage + "'");

        pst.executeUpdate();
    }



    public Object downloadPlayerDatabaseData(String p_name){
        // Any # = Don't let them log in, they're on another server or something, the # will = the server. (server_num >= 0)
        // false = Data does not exist / error. (server_num = -2)
        // true = Everything is fine. (server_num = -1)

        online_today.remove(p_name);
        Hive.player_inventory.remove(p_name);
        Hive.player_location.remove(p_name);
        Hive.player_hp.remove(p_name);
        Hive.player_level.remove(p_name);
        Hive.player_food_level.remove(p_name);
        Hive.player_armor_contents.remove(p_name);
        Hive.player_ecash.remove(p_name);
        Hive.player_sdays_left.remove(p_name);
        KarmaMechanics.align_map.remove(p_name);
        KarmaMechanics.align_time.remove(p_name);
        KarmaMechanics.saved_gear.remove(p_name);
        CommunityMechanics.ignore_list.remove(p_name);
        CommunityMechanics.buddy_list.remove(p_name);
        CommunityMechanics.toggle_list.remove(p_name);
        HealthMechanics.noob_player_warning.remove(p_name);
        HealthMechanics.noob_players.remove(p_name);
        RealmMechanics.realm_title.remove(p_name);
        RealmMechanics.realm_tier.remove(p_name);
        MountMechanics.mule_inventory.remove(p_name);
        MountMechanics.mule_itemlist_string.remove(p_name);

        PreparedStatement pst = null;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "SELECT server_num, location, inventory, hp, food_level, level, first_login, combat_log, last_login_time, rank, align_status, align_time, toggles, pets, buddy_list, ignore_list, realm_tier, realm_title, realm_loaded, noob_player, ecash, ip, sdays_left, portal_shards, saved_gear, lost_gear, mule_inventory, achievments, online_today, ecash_storage FROM player_database WHERE p_name = '" + p_name + "'");

            pst.execute();
            ResultSet rs = pst.getResultSet();

            if(!(rs.next())){
                // Could either be problem with SQL (unlikely, would throw exception), or it's a new player.
                log.info("[HIVE (Slave Edition)] No PLAYER DATA found for " + p_name + ", return null.");
                return false;
            }

            List<Object> data = new ArrayList<Object>();

            int server_num = rs.getInt("server_num");
            if(server_num >= 0){
                return server_num; // They're online somewhere, or they're new!
            }

            long first_login = rs.getLong("first_login");
            player_first_login.put(p_name, first_login);

            int combat_log = rs.getInt("combat_log");
            if(combat_log == 1){
                HealthMechanics.combat_logger.add(p_name); // Combat logger, you will enjoy a slow death! (not really!)
            }

            int l_online_today = rs.getInt("online_today");
            if(l_online_today != 1){
                online_today.add(p_name);
            }

            CommunityMechanics.local_last_login.put(p_name, rs.getLong("last_login_time"));
            // Used for certain cooldown events and such.

            PermissionMechanics.setRank(p_name, rs.getString("rank"), false);
            // Set's server rank, needed for cross-server chat events.
            // false = Don't upload the new rank -- we're already pulling it from DB so no need.

            KarmaMechanics.align_time.put(p_name, rs.getInt("align_time"));
            String align_status = rs.getString("align_status");
            if(align_status == null || align_status.equalsIgnoreCase("null")){
                align_status = "good";
            }

            KarmaMechanics.setAlignment(p_name, align_status, 1);
            // Karma alignment and seconds left until it expires.

            List<String> ltoggle_list = new ArrayList<String>();
            String toggles = rs.getString("toggles");
            if(toggles != null){
                for(String s : toggles.split(",")){
                    if(s.length() > 0){
                        ltoggle_list.add(s);
                    }
                }
            }

            CommunityMechanics.toggle_list.put(p_name, ltoggle_list);
            // Toggles for a multitude of different settings.

            List<String> pet_data = new ArrayList<String>();
            String pet_list = rs.getString("pets");
            if(pet_list != null){
                if(pet_list.contains(",")){
                    for(String s : pet_list.split(",")){
                        pet_data.add(s);
                    }
                }
                else{
                    pet_data.add(pet_list);
                }
            }
            PetMechanics.player_pets.put(p_name, pet_data);
            // Sets up pet ownership for the player, will spawn them eggs and such.

            List<String> lbuddy_list = new ArrayList<String>();
            String buddy_list = rs.getString("buddy_list");
            if(buddy_list != null && buddy_list.contains(",")){
                for(String s : buddy_list.split(",")){
                    if(s.length() > 0){
                        lbuddy_list.add(s);
                    }
                }
            }

            CommunityMechanics.buddy_list.put(p_name, lbuddy_list);
            // Friend list!

            List<String> lignore_list = new ArrayList<String>();
            String ignore_list = rs.getString("ignore_list");
            if(ignore_list != null && ignore_list.contains(",")){
                for(String s : ignore_list.split(",")){
                    if(s.length() > 0){
                        lignore_list.add(s);
                    }
                }
            }

            CommunityMechanics.ignore_list.put(p_name, lignore_list);
            // Ignore list!

            RealmMechanics.realm_tier.put(p_name, rs.getInt("realm_tier"));
            RealmMechanics.realm_title.put(p_name, rs.getString("realm_title"));
            RealmMechanics.realm_loaded_status.put(p_name, rs.getBoolean("realm_loaded"));
            // Realm tier (for size) and the description showed on right click.

            int noob_player = rs.getInt("noob_player");
            if(noob_player == 1){
                HealthMechanics.noob_players.add(p_name);
            }

            String loc_s = rs.getString("location");
            Location loc = null;
            if(loc_s == null){
                log.info("[HIVE (Slave Edition)] No LOCATION data found for " + p_name + ", return null.");
                return false;
            }
            else if(loc_s != null){
                loc = convertStringToLocation(loc_s);
            }

            String inventory_s = rs.getString("inventory");
            if(inventory_s == null){
                log.info("[HIVE (Slave Edition)] No INVENTORY data found for " + p_name + ", return null.");
                return false; 
            }

            int ecash = rs.getInt("ecash");
            if(ecash > 0){
                player_ecash.put(p_name, ecash); // Store E-CASH locally -- they'll need to relog to get any new E-CASH.
            }

            int sdays_left = rs.getInt("sdays_left");
            if(sdays_left > 0){
                player_sdays_left.put(p_name, sdays_left);
            }

            List<String> ip_list = new ArrayList<String>();

            String raw_ip_list = rs.getString("ip");
            if(raw_ip_list != null && raw_ip_list.length() > 0){
                if(raw_ip_list.contains(",")){
                    for(String s_ip : raw_ip_list.split(",")){
                        ip_list.add(s_ip);
                    }
                }
                else{ // Only 1 IP in the DB.
                    ip_list.add(raw_ip_list);
                }
            }
            if(local_player_ip.containsKey(p_name) && !ip_list.contains(local_player_ip.get(p_name))){
                ip_list.add(local_player_ip.get(p_name));
            }

            player_ip.put(p_name, ip_list);

            String portal_shard_string = rs.getString("portal_shards");
            List<Integer> portal_shards = new ArrayList<Integer>();
            if(portal_shard_string != null && portal_shard_string.contains(",") && portal_shard_string.split(",").length == 5){
                for(String s : portal_shard_string.split(",")){
                    int i = Integer.parseInt(s);
                    portal_shards.add(i);
                }
                player_portal_shards.put(p_name, portal_shards);
            }
            else{
                player_portal_shards.put(p_name, new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0)));
            }

            int level = rs.getInt("level");
            double hp = rs.getInt("hp");
            int food_level = rs.getInt("food_level");

            String saved_gear = rs.getString("saved_gear");
            if(saved_gear != null && saved_gear.length() > 0){
                List<ItemStack> sg_list = convertStringToInventoryString(saved_gear);
                KarmaMechanics.saved_gear.put(p_name, sg_list);
                if(!HealthMechanics.combat_logger.contains(p_name)){
                    // If combat_logger contains them, they'll be killed on login anyway. We don't want to double kill.
                    HealthMechanics.combat_logger.add(p_name); // Use this method, it actually works.
                }
            }

            String lost_gear = rs.getString("lost_gear");
            if(lost_gear != null && lost_gear.length() > 0){
                KarmaMechanics.lost_gear.put(p_name, lost_gear);
            }

            String mule_inventory = rs.getString("mule_inventory");
            if(mule_inventory != null && mule_inventory.contains("@item@")){
                // We put the ItemStack list into a hashmap, when they OPEN the mule, it will generate the inventory and the slots based on the mule they're using.;
                MountMechanics.mule_itemlist_string.put(p_name, mule_inventory);
            }

            String achievments = rs.getString("achievments");
            if(achievments == null){
                achievments = "";
            }
            AchievmentMechanics.achievment_map.put(p_name, achievments);

            String ecash_storage = rs.getString("ecash_storage");
            if(ecash_storage != null){
                EcashMechanics.ecash_storage_map.put(p_name, ecash_storage);
            }

            data.add(loc);
            data.add(inventory_s);
            data.add(level);
            data.add((double)hp);
            data.add(food_level);

            remote_player_data.put(p_name, data);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex;

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
                return false;
            }
        }


        return true;
    }

    @SuppressWarnings("deprecation")
	public static String convertInventoryToString(List<ItemStack> inv){
        // @item@Slot:ItemID-Amount.Durability#Item_Name#$Item_Lore$[lam1]lam_color[lam2]
        // @item@1:267-1.54#Magic Sword#$DMG: 5 - 7, CRIT: 5%$@item@

        String return_string = "";
        int slot = -1;

        for(ItemStack is : inv){
            slot++;
            if(is == null || is.getType() == Material.AIR){
                continue;
            }

            String i_name = "";
            if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                i_name = is.getItemMeta().getDisplayName();
            }
            else{
                // Default name.
                i_name = "null";
            }

            String i_lore = "";
            if(is.hasItemMeta() && is.getItemMeta().hasLore()){
                for(String s : is.getItemMeta().getLore()){
                    i_lore = i_lore + "," + s;
                }
            }
            else{
                // No lore.
                i_lore = "null";
            }

            return_string = return_string + ("@item@" + slot + ":" + is.getTypeId() + "-" + is.getAmount() + "." + is.getDurability() + "#" + i_name + "#" + "$" + i_lore + "$");
            if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta){
                return_string = return_string + "[lam1]" + ((LeatherArmorMeta)is.getItemMeta()).getColor().asBGR() + "[lam2]";
            }
        }

        return return_string;
    }

    @SuppressWarnings("deprecation")
	public static String convertInventoryToString(String p_name, Inventory inv, boolean player){
        // @item@Slot:ItemID-Amount.Durability#Item_Name#$Item_Lore$[lam1]lam_color[lam2]
        // @item@1:267-1.54#Magic Sword#$DMG: 5 - 7, CRIT: 5%$@item@

        String return_string = "";
        int slot = -1;
        for(ItemStack is : inv.getContents()){
            slot++;
            if(is == null || is.getType() == Material.AIR){
                continue;
            }

            String i_name = "";
            if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                i_name = is.getItemMeta().getDisplayName();
            }
            else{
                // Default name.
                i_name = "null";
            }

            String i_lore = "";
            if(is.hasItemMeta() && is.getItemMeta().hasLore()){
                for(String s : is.getItemMeta().getLore()){
                    i_lore = i_lore + "," + s;
                }
            }
            else{
                // No lore.
                i_lore = "null";
            }

            return_string = return_string + ("@item@" + slot + ":" + is.getTypeId() + "-" + is.getAmount() + "." + is.getDurability() + "#" + i_name + "#" + "$" + i_lore + "$");
            if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta){
                return_string = return_string + "[lam1]" + ((LeatherArmorMeta)is.getItemMeta()).getColor().asBGR() + "[lam2]";
            }
        }

        List<ItemStack> armor_contents = new ArrayList<ItemStack>();
        if(player){
            if(Bukkit.getPlayer(p_name) != null){
                Player owner = Bukkit.getPlayer(p_name);
                for(ItemStack is : owner.getInventory().getArmorContents()){
                    armor_contents.add(is);
                }
            }
            else{
                if(player_armor_contents.containsKey(p_name)){
                    for(ItemStack is : player_armor_contents.get(p_name)){
                        armor_contents.add(is);
                    }
                }
            }

            if(armor_contents.size() > 0){
                for(ItemStack is : armor_contents){
                    slot++;
                    if(is == null){
                        continue;
                    }

                    String i_name = "";
                    if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                        i_name = is.getItemMeta().getDisplayName();
                    }
                    else{
                        // Default name.
                        i_name = "null";
                    }

                    String i_lore = "";
                    if(is.hasItemMeta() && is.getItemMeta().hasLore()){
                        for(String s : is.getItemMeta().getLore()){
                            i_lore = i_lore + "," + s;
                        }
                    }
                    else{
                        // No lore.
                        i_lore = "null";
                    }

                    return_string = return_string + ("@item@" + slot + ":" + is.getTypeId() + "-" + is.getAmount() + "." + is.getDurability() + "#" + i_name + "#" + "$" + i_lore + "$");
                    if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta){
                        return_string = return_string + "[lam1]" + ((LeatherArmorMeta)is.getItemMeta()).getColor().asBGR() + "[lam2]";
                    }
                }
            }
        }

        return return_string;
    }

    @SuppressWarnings("deprecation")
	public static List<ItemStack> convertStringToInventoryString(String inventory_string){
        List<ItemStack> is_list = new ArrayList<ItemStack>();
        //int expected_item_size = inventory_string.split("@item@").length;
        //int slot_cache = -1;

        for(String s : inventory_string.split("@item@")){
            //slot_cache++;

            if(s.length() <= 1){
                continue;
            }

            //int slot = Integer.parseInt(s.substring(0, s.indexOf(":")));
            int item_id = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("-")));
            int amount = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf(".")));
            short durability = Short.parseShort(s.substring(s.indexOf(".") + 1, s.indexOf("#")));

            String i_name = s.substring(s.indexOf("#") + 1, s.lastIndexOf("#"));
            String i_lore = s.substring(s.indexOf("$") + 1, s.lastIndexOf("$"));

            Color leather_armor_color = null;
            if(s.contains("[lam1]")){
                leather_armor_color = Color.fromBGR(Integer.parseInt(s.substring(s.indexOf("[lam1]") + 6, s.lastIndexOf("[lam2]"))));
            }

            ItemStack is = new ItemStack(Material.getMaterial(item_id), amount, durability);

            if(is.getType() == Material.POTION && is.getDurability() > 0){
                // Renames potion to Instant Heal.
                is = ItemMechanics.signNewCustomItem(Material.getMaterial(item_id), durability, i_name, i_lore);
                is_list.add(is);
                continue;
            }

            if(is.getType() == Material.WRITTEN_BOOK){
                continue; // TODO: Code book loading.
            }

            ItemMeta im = is.getItemMeta();

            if(!(i_name.equalsIgnoreCase("null"))){
                // Custom name!
                im.setDisplayName(i_name);
            }

            if(!(i_lore.equalsIgnoreCase("null"))){
                // Lore!
                List<String> all_lore = new ArrayList<String>();
                for(String lore : i_lore.split(",")){
                    if(lore.length() > 1){
                        all_lore.add(lore);
                    }
                }
                im.setLore(all_lore);
            }

            if(!(leather_armor_color == null)){
                ((LeatherArmorMeta)im).setColor(leather_armor_color);
            }

            if(!(i_name.equalsIgnoreCase("null")) || !(i_lore.equalsIgnoreCase("null"))){
                is.setItemMeta(im);
            }

            is_list.add(is);
        }

        return is_list;
    }

    @SuppressWarnings("deprecation")
	public static Inventory convertStringToInventory(Player pl, String inventory_string, String inventory_name, int slots){
        Inventory inv = null;
        //int slot_cache = -1;
        int expected_item_size = inventory_string.split("@item@").length - 1;

        if(pl == null && inventory_name != null){
            // Using inventory.
            inv = Bukkit.createInventory(null, slots, inventory_name);
        }
        for(String s : inventory_string.split("@item@")){
            //slot_cache++;

            if(s.length() <= 1 || s.equalsIgnoreCase("null")){
                continue;
            }

            int slot = Integer.parseInt(s.substring(0, s.indexOf(":")));

            if(inventory_name != null && inventory_name.startsWith("Bank Chest")){
                if(slot > expected_item_size && (slot > (slots - 1))){ // slots - 1, 0 index = start
                    slot = inv.firstEmpty();
                }
            }

            if(s.length() <= 1){
                continue;
            }

            int item_id = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("-")));
            int amount = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf(".")));
            short durability = Short.parseShort(s.substring(s.indexOf(".") + 1, s.indexOf("#")));

            String i_name = s.substring(s.indexOf("#") + 1, s.lastIndexOf("#"));
            String i_lore = s.substring(s.indexOf("$") + 1, s.lastIndexOf("$"));

            Color leather_armor_color = null;
            if(s.contains("[lam1]")){
                leather_armor_color = Color.fromBGR(Integer.parseInt(s.substring(s.indexOf("[lam1]") + 6, s.lastIndexOf("[lam2]"))));
            }

            ItemStack is = new ItemStack(Material.getMaterial(item_id), amount, durability);

            if(is.getType() == Material.POTION && is.getDurability() > 0){
                // Renames potion to Instant Heal.
                is = ItemMechanics.signNewCustomItem(Material.getMaterial(item_id), durability, i_name, i_lore);
                if(pl != null){
                    pl.getInventory().setItem(slot, is);
                }
                else if(inv != null){
                    inv.setItem(slot, is);
                }
                continue;
            }

            if(is.getType() == Material.WRITTEN_BOOK){
                continue; // TODO: Code book loading.
            }

            ItemMeta im = is.getItemMeta();

            if(!(i_name.equalsIgnoreCase("null"))){
                // Custom name!
                im.setDisplayName(i_name);
            }

            if(!(i_lore.equalsIgnoreCase("null"))){
                // Lore!
                List<String> all_lore = new ArrayList<String>();
                for(String lore : i_lore.split(",")){
                    if(lore.length() > 1){
                        all_lore.add(lore);
                    }
                }
                im.setLore(all_lore);
            }

            if(!(leather_armor_color == null)){
                ((LeatherArmorMeta)im).setColor(leather_armor_color);
            }

            if(!(i_name.equalsIgnoreCase("null")) || !(i_lore.equalsIgnoreCase("null"))){
                is.setItemMeta(im);
            }

            if(pl != null){
                pl.getInventory().setItem(slot, is);
            }
            else if(inv != null){
                inv.setItem(slot, is);
            }
        }

        if(inv != null){
            return inv;
        }
        else{
            return null; 
        }
    }

    public static String convertLocationToString(Location l){
        // 0,0,0:yaw$pitch
        DecimalFormat df = new DecimalFormat("#.####");
        return (df.format(l.getBlockX()) + "," + df.format(l.getBlockY()) + "," + df.format(l.getBlockZ()) + ":" + df.format(l.getYaw()) + "$" + df.format(l.getPitch()));
    }

    public Location convertStringToLocation(String s_l){
        double x = Double.parseDouble(s_l.substring(0, s_l.indexOf(",")));
        double y = Double.parseDouble(s_l.substring(s_l.indexOf(",") + 1, s_l.indexOf(",", s_l.indexOf(",") + 1)));
        double z = Double.parseDouble(s_l.substring(s_l.lastIndexOf(",") + 1, s_l.indexOf(":")));

        float yaw = Float.parseFloat(s_l.substring(s_l.indexOf(":") + 1, s_l.indexOf("$")));
        float pitch = Float.parseFloat(s_l.substring(s_l.indexOf("$") + 1, s_l.length()));

        Location loc = new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
        return loc;
    }

    public boolean hasAccountData(String p_name){
        PreparedStatement pst;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "SELECT location FROM player_database WHERE p_name = '" + p_name + "'");

            pst.execute();
            ResultSet rs = pst.getResultSet();

            if(!(rs.next())){
                log.info("[HIVE (Slave Edition)] No PLAYER DATA found for " + p_name + ", return null.");
                return false;
            }

            String s_loc = rs.getString("location");
            if(s_loc != null && s_loc.length() > 1){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static String getServerRank(String p_name){
        
//        rank_forumgroup.put("default", 2);
//        rank_forumgroup.put("pmod", 11);
//        rank_forumgroup.put("sub", 75);
//        rank_forumgroup.put("sub+", 76);
//        rank_forumgroup.put("sub++", 79);
//        rank_forumgroup.put("gm", 72);
//        rank_forumgroup.put("wd", 72);
        
        PreparedStatement pst;

        try {
            pst = ConnectionPool.getConnection().prepareStatement( 
                    "SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");

            pst.execute();
            ResultSet rs = pst.getResultSet();

            if(!(rs.next())){
                log.info("[HIVE (Slave Edition)] No PLAYER DATA found for " + p_name + ", return null.");
                return "default";
            }

            String rank = rs.getString("rank");
            if(rank != null && rank.length() > 1){
                return rank;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return "default";
    }

    /*public boolean isPremiumBetaTester(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			// con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = WebsiteConnectionPool.getConneciton().prepareStatement( 
					"SELECT userid FROM userfield WHERE field5 = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!(rs.next())){
				return false;
				//return "Unregistered";
				// Player is not registered on forums.
			}

			int userid = rs.getInt("userid");
			pst = WebsiteConnectionPool.getConneciton().prepareStatement( 
					"SELECT usergroupid, membergroupids FROM user WHERE userid = '" + userid + "'");

			pst.execute();
			rs = pst.getResultSet();
			if(!(rs.next())){
				return false;
				//return "nominecraftname";
				// Player is not registered on forums.
			}
			int primary_rank = rs.getInt("usergroupid");
			if(primary_rank == 9){
				return true;
			}
			else if(primary_rank != 9){ // 9 == Beta Tester
				String all_groups = rs.getString("membergroupids");
				if(all_groups.contains("9")){
					return true;
				}
			}

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);      

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return false;
	}*/

    public boolean isThisRootMachine(){
        File f = new File("key");
        if(f.exists()){return true;}
        else{return false;}
    }	

    public void setAllPlayersAsOffline(){
        Connection con = null;
        PreparedStatement pst = null;

        try {
            for(String p_name : player_inventory.keySet()){
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "INSERT INTO player_database (p_name, server_num)"
                                + " VALUES"
                                + "('"+ p_name + "', '"+ (-1) +"') ON DUPLICATE KEY UPDATE server_num = '" + (-1) + "'");

                pst.executeUpdate();

            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        log.info("[HIVE (SLAVE Edition)] Set all players to 'offline' on p_login_data table.");

    }

    public void setSystemPath(){
        CodeSource codeSource = Hive.class.getProtectionDomain().getCodeSource();
        File jarFile = null;
        try {jarFile = new File(codeSource.getLocation().toURI().getPath());} catch (URISyntaxException e1) {}
        rootDir = jarFile.getParentFile().getPath();
        int rep = rootDir.contains("/plugins") ? rootDir.indexOf("/plugins") : rootDir.indexOf("\\plugins");
        rootDir = rootDir.substring(0, rep);
    }

    public static void sendTimeout(int time){
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + ">>" + ChatColor.RED + " The server will be " + ChatColor.UNDERLINE + "REBOOTING" + ChatColor.RED + " in " + ChatColor.BOLD + time + "s...");
    }


    public static boolean isPlayerOnline(String p_name){
        int server_num = getPlayerServer(p_name, true);
        if(server_num <= -1){return false;}
        if(pending_upload.contains(p_name)){return false;} // They're not literally online.
        else{
            return true;
        }
    }


    public static int getPlayerServer(String p_name, boolean sql){
        String motd = Bukkit.getMotd();

        if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline()){
            if(motd.contains("US-YT")){
                return 3001;
            }
            int server_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
            if(motd.contains("EU-")){
                server_num += 1000;
            }
            if(MOTD.contains("BR-")){
                server_num += 2000;
            }
            if(motd.contains("US-YT")){
                server_num += 3000;
            }
            return server_num;
        }

        if(CommunityMechanics.player_server_num.containsKey(p_name)){
            return CommunityMechanics.player_server_num.get(p_name);
            // If it doesn't contain, the servers could not be synced properly or they're a -2, just let SQL take care of the rest.
        }

        if(sql == true){
            //p_name = p_name.replaceAll("\"", "\\");

            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "SELECT server_num FROM player_database WHERE p_name = '" + p_name + "'");

                pst.execute();

                ResultSet rs = pst.getResultSet();
                if(!rs.next()){return -2;} // NEVER been online.
                return rs.getInt("server_num");

            } catch (SQLException ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
                return -1; 

            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }

                } catch (SQLException ex) {
                    log.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return -1;

    }

    public void setPlayerServer(final String p_name){
        final String motd = Bukkit.getMotd();
        int lserver_num = 1;
        if(motd.contains("US-YT")){
            lserver_num += 3000;
        }
        try{
            lserver_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
        } catch(NumberFormatException nfe){
            // Thrown by non- CC-## format.
        }

        if(motd.contains("EU-")){
            lserver_num += 1000; 
        }
        if(MOTD.contains("BR-")){
            lserver_num += 2000;
        }

        final int server_num = lserver_num;

        Connection con = null;
        PreparedStatement pst = null;

        try {

            pst = ConnectionPool.getConnection().prepareStatement( 
                    "INSERT INTO player_database (p_name, server_num)"
                            + " VALUES"
                            + "('"+ p_name + "', '"+ server_num +"') ON DUPLICATE KEY UPDATE server_num = '" + server_num + "'");

            pst.executeUpdate();

            CommunityMechanics.player_server_num.put(p_name, server_num);

            List<Object> qdata = new ArrayList<Object>();
            qdata.add("@server_num@" + p_name + ":" + server_num);
            qdata.add(null);
            qdata.add(true);
            CommunityMechanics.social_query_list.put(p_name, qdata);
            //CommunityMechanics.sendPacketCrossServer("@server_num@" + p_name + ":" + server_num, server_num, true);

        } catch (SQLException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            setPlayerServer(p_name);


        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

    }


    public void deletePlayerServer(final String p_name){
        Thread t = new Thread(new Runnable() {
            public void run() {

                Connection con = null;
                PreparedStatement pst = null;

                try {

                    pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM p_login_data WHERE pname = '" + p_name + "'");
                    pst.executeUpdate();

                } catch (SQLException ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                    setPlayerServer(p_name);


                } finally {
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        log.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        });

        t.start();
    }


    public static void setPlayerOffline(final String p_name, int second_delay, boolean ignore_all_checks){
        final int login_delay_s = second_delay * 1000;

        try{			
            if(second_delay > 0){
                Thread.sleep(login_delay_s);
            }

            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "INSERT INTO player_database (p_name, server_num)"
                                + " VALUES"
                                + "('"+ p_name + "', '"+ (-1) +"') ON DUPLICATE KEY UPDATE server_num = '" + (-1) + "'");

                pst.executeUpdate();

                being_uploaded.remove(p_name);

            } catch (SQLException ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);

            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }

                } catch (SQLException ex) {
                    log.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    public static void setPlayerOffline(final String p_name, int second_delay){
        final int login_delay_s = second_delay * 1000;

        try{			
            if(second_delay > 0){
                Thread.sleep(login_delay_s);
            }

            if(lockout_players.contains(p_name)){
                log.info("[HIVE (SLAVE EDITION] Player " + p_name + " has been detected in the lockout_players table, and will not be set as offline this server.");
                return; // Don't set them as offline to prevent a desync.
            }

            if(!(player_to_npc.containsKey(p_name)) && Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline() && shutting_down == false){
                return; // They're back online locally! WOO!
            }

            if(player_to_npc.containsKey(p_name)){
                // Let that event finish and handle when they're 'offline'.
                return;
            }

            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = ConnectionPool.getConnection().prepareStatement( 
                        "INSERT INTO player_database (p_name, server_num)"
                                + " VALUES"
                                + "('"+ p_name + "', '"+ (-1) +"') ON DUPLICATE KEY UPDATE server_num = '" + (-1) + "'");

                pst.executeUpdate();

                //pending_upload.remove(p_name);
                being_uploaded.remove(p_name);

            } catch (SQLException ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);

            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }

                } catch (SQLException ex) {
                    log.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        List<Object> qdata = new ArrayList<Object>();
        qdata.add("@server_num@" + p_name + ":" + -1);
        qdata.add(null);
        qdata.add(true);
        CommunityMechanics.social_query_list.put(p_name, qdata);
        //CommunityMechanics.sendPacketCrossServer("@server_num@" + p_name + ":" + -1, -1, true);
        CommunityMechanics.player_server_num.put(p_name, -1);

    }

    public static int getServerNumFromPrefix(String prefix){
        int server_num = -1;
        if(prefix.contains(" ")){
            prefix = prefix.substring(0, prefix.indexOf(" "));
        }

        try{
            if(prefix.contains("-")){
                server_num = Integer.parseInt(prefix.substring(prefix.indexOf("-") + 1, prefix.length()));
            }
            if(prefix.contains("US")){
                //server_num = server_num; // TODO Wtf?
            }
            else if(prefix.contains("EU")){
                server_num += 1000;
            }
            else if(prefix.contains("BR")){
                server_num += 2000;
            }
        } catch(Exception err){
            err.printStackTrace();
            return 0;
        }

        return server_num;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e){
        if(e.getMessage().equalsIgnoreCase("/stop") && e.getPlayer() != null && (e.getPlayer().getName().equalsIgnoreCase("availer") || e.getPlayer().getName().equalsIgnoreCase("vaquxine"))){
            server_lock = true;
            setAllPlayersAsOffline();

            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(!(Hive.server_frozen)){
                    p.saveData();
                }
                p.kickPlayer("\n" + ChatColor.GREEN.toString() + "This " + ChatColor.BOLD.toString() + "Dungeon Realms" + ChatColor.GREEN.toString() + " shard is rebooting."
                        + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            }
            e.setCancelled(true);

            Thread t = new Thread(new Runnable(){
                public void run(){
                    int count = 0;
                    while(pending_upload.size() > 0 && count <= 200){
                        count++;
                        try {
                            Thread.sleep(100); // Let all pending multi-thread uploads finish.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } 
                    }
                    ready_to_die = true;
                }
            });

            t.start();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player pl = e.getPlayer();
        Location loc = pl.getLocation();
        if(HealthMechanics.getPlayerHP(pl.getName()) > 0 || pl.getHealth() > 0){
            // Invalid death, so let's not drop ANYTHING, and just spawn them back.
            e.setRespawnLocation(loc); // Set location back to where they are.
            /*pl.setHealth(20);
			pl.setLevel(HealthMechanics.getMaxHealthValue(pl.getName()));*/
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        Player pl = e.getEntity();
        if(server_swap.containsKey(pl.getName())){
            e.getDrops().clear();
        }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent e){
        if(e.getCommand().equalsIgnoreCase("stop") && !(e.getSender() instanceof Player)){
            shutting_down = true;
            server_lock = true;

            if(Bukkit.getMotd().contains("US-0")){
                try{
                    for(Entity ent : Bukkit.getWorld(main_world_name).getEntities()){
                        if(ent.getType() == EntityType.DROPPED_ITEM){
                            ent.remove();
                            log.info("[HIVE] Removing dropped item: " + ent.toString());
                        }
                    }

                    Bukkit.getWorld("DungeonRealms").save();
                } catch(Exception err){
                    err.printStackTrace();
                }

            }

            setAllPlayersAsOffline();

            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(!(Hive.server_frozen)){
                    p.saveData();
                }
                p.kickPlayer("\n" + ChatColor.GREEN.toString() + "This " + ChatColor.BOLD.toString() + "Dungeon Realms" + ChatColor.GREEN.toString() + " shard is rebooting."
                        + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            }

            e.setCommand(""); // Nullify stop.

            Thread t = new Thread(new Runnable(){
                public void run(){
                    int count = 0;
                    while(pending_upload.size() > 0 && count <= 200){
                        count++;
                        try {
                            Thread.sleep(100); // Let all pending multi-thread uploads finish.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } 
                    }
                    ready_to_die = true;
                }
            });

            t.start();
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
    public void onPlayerAsyncChatEvent(AsyncPlayerChatEvent e){
        Player pl = e.getPlayer();

        if(player_bio.containsKey(pl.getName())){
            // They're writing their guild bio!
            e.setCancelled(true);

            // Add on to the bio.
            String bio = player_bio.get(pl.getName());

            String msg = ChatMechanics.censorMessage(e.getMessage());
            if(msg.equalsIgnoreCase("cancel")){
                pl.sendMessage(ChatColor.RED + "/bio - " + ChatColor.BOLD + "CANCELLED");
                player_bio.remove(pl.getName());
                return;
            }
            if(msg.equalsIgnoreCase("confirm")){
                bio = StringEscapeUtils.escapeSql(bio);
                Hive.sql_query.add("INSERT INTO player_database (p_name, biography) VALUES('" + pl.getName() + "', '" + bio + "') ON DUPLICATE KEY UPDATE biography='" + bio + "'");
                pl.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Profile Biography Submitted.");
                pl.sendMessage(ChatColor.GRAY + bio);
                player_bio.remove(pl.getName());
                return;
            }

            if((bio.length() + msg.length()) > 255){
                // Too long.
                int length = (bio.length() + msg.length());
                int overflow = length - 255;
                pl.sendMessage(ChatColor.RED + "Your profile biography would be " + length + " characters long with this addition, that's " + ChatColor.UNDERLINE + overflow + " more characters than allowed.");
                pl.sendMessage(ChatColor.GRAY + "No additional text has been added to the biography.");
                return;
            }

            if(bio.length() > 0){
                bio += " ";
            }
            bio += msg;

            player_bio.put(pl.getName(), bio);
            pl.sendMessage(ChatColor.GREEN + "Biography appended. " + ChatColor.BOLD.toString() + bio.length() + "/512 characters.");
        }

        if(killing_self.contains(pl.getName())){
            e.setCancelled(true);
            String msg = e.getMessage();
            if(msg.equalsIgnoreCase("y")){
                KarmaMechanics.plast_hit.remove(pl.getName());
                KarmaMechanics.last_hit_time.remove(pl.getName());

                pl.setLastDamageCause(new EntityDamageEvent(pl, DamageCause.SUICIDE, 0)); // Sets death message to suicide.
                //pl.setLevel(0);
                HealthMechanics.setPlayerHP(pl.getName(), 0);
                pl.setHealth(0);
                pl.setMetadata("hp", new FixedMetadataValue(Main.plugin, 0));
                killing_self.remove(pl.getName());
            }
            else{
                pl.sendMessage(ChatColor.YELLOW + "/suicide - " + ChatColor.BOLD + "CANCELLED");
                killing_self.remove(pl.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e){
        String p_name = e.getName();

        if(e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED || e.getKickMessage().length() > 0){
            return; // They failed a login check.
        }

        /*if(p_name.equalsIgnoreCase("Vaquxine")){
			return;
		}*/

        if(loaded_players.contains(p_name) && Main.plugin.getServer().getPlayer(p_name) == null){
            loaded_players.remove(p_name);
        }

        login_time.put(p_name, System.currentTimeMillis());
        OfflinePlayer of = Bukkit.getOfflinePlayer(p_name);

        if(Bukkit.getMotd().contains("DESYNC") || local_ddos || hive_ddos){
            e.setKickMessage(ChatColor.RED.toString() + "This " + ChatColor.BOLD + "Dungeon Realms" + ChatColor.RED.toString() + " server is currently desynced from the hive -- local login servers are offline."
                    + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(payload_pending == true){
            e.setKickMessage(ChatColor.YELLOW.toString() + "This " + ChatColor.BOLD + "Dungeon Realms" + ChatColor.YELLOW.toString() + " server is currently downloading a server snapshot."
                    + "\n" + ChatColor.GRAY + "The shard will be back online in about " + (seconds_to_reboot + 10) + ChatColor.BOLD + "s"
                    + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(restart_inc == true){
            e.setKickMessage(ChatColor.YELLOW.toString() + "This " + ChatColor.BOLD + "Dungeon Realms" + ChatColor.YELLOW.toString() + " server is currently rebooting."
                    + "\n" + ChatColor.GRAY + "The shard will be back online in about " + (seconds_to_reboot + 10) + ChatColor.BOLD + "s"
                    + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(server_lock == true || force_kick == true){
            if(!(of.isOp())){
                e.setKickMessage(ChatColor.RED.toString() + "This server is currently " + ChatColor.BOLD.toString() + "LOCKED" + ChatColor.RED.toString() + ", only Dungeon Realms developers are authorized to login.");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }
            else{
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
            }
        }

        if(Bukkit.hasWhitelist()){
            if(Bukkit.getMotd().contains("DEVELOPER") && (!(Bukkit.getWhitelistedPlayers().contains(of)))){ // !of.isOp() || 
                e.setKickMessage(ChatColor.RED.toString() + "You are currently " + ChatColor.BOLD.toString() + "NOT" + ChatColor.RED.toString() + " authorized to login to this #DungeonRealms DEVELOPEMENT server." + "\n" + "\n" + ChatColor.GRAY.toString() + "CONTACT: staff@dungeonrealms.net");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                return;
            }
            if(Bukkit.getMotd().contains("-YT") && !(Bukkit.getWhitelistedPlayers().contains(of))){
                e.setKickMessage(ChatColor.RED.toString() + "You are currently " + ChatColor.BOLD.toString() + "NOT" + ChatColor.RED.toString() + " authorized to login to this #DungeonRealms YOUTUBE VIP server." + "\n" + "\n" + ChatColor.GRAY.toString() + "CONTACT: staff@dungeonrealms.net");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                return;
            }
        }

        if(loading_server == true){
            e.setKickMessage(ChatColor.AQUA.toString() + "This " + ChatColor.BOLD + "Dungeon Realms" + ChatColor.AQUA.toString() + " shard is loading objects into memory for " + ChatColor.UNDERLINE + "maximum" + ChatColor.AQUA + " performance. (" + getLoadPercent() + "%)"
                    + "\n" + ChatColor.GRAY + "You may join as soon as this process is complete."
                    + "\n\n" + ChatColor.GRAY.toString() + ChatColor.UNDERLINE.toString() + "www.dungeonrealms.net");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(logout_time.containsKey(p_name)){
            e.setKickMessage(ChatColor.GRAY.toString() + "You recently logged out " + ChatColor.UNDERLINE.toString() + "WHILE IN COMBAT" + ChatColor.GRAY.toString() + ", so you must wait " + ChatColor.BOLD.toString() + "15" + ChatColor.GRAY.toString() + " seconds before logging in." + "\n\n" + ChatColor.RED.toString() + "To avoid this in the future, use " + ChatColor.BOLD.toString() + "/logout to safetly exit the game.");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        /*if(being_uploaded.contains(p_name) || RealmMechanics.uploading_realms.contains(p_name)){
			e.setKickMessage(ChatColor.RED.toString() + "ERROR: Logged in too quickly after last logout." + "\n" + ChatColor.GRAY.toString() + "Please wait a few seconds and try again.");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			return;
		}*/

        if(new File(main_world_name + "/players/" + p_name + ".dat").exists()){
            String motd = Bukkit.getMotd();
            int server_num_local = 1;
            try{
                server_num_local = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
            } catch(NumberFormatException nfe){
                server_num_local += 3000;
            }
            String server_name = motd.substring(0, motd.indexOf("-") + 1);


            if(server_num_local > 3000){
                server_name = "US-YT";
                server_num_local = 0;
            }

            e.setKickMessage(ChatColor.YELLOW.toString() + "The account " + ChatColor.BOLD.toString() + p_name + ChatColor.YELLOW.toString() + " is already loaded on " + ChatColor.UNDERLINE.toString() + server_name + server_num_local + "." + "\n\n" + ChatColor.GRAY.toString() + "If you have just recently changed servers, your character data is being synced -- " + ChatColor.UNDERLINE.toString() + "wait a few seconds" + ChatColor.GRAY.toString() + " before reconnecting.");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        boolean VIP = false;
        String rank = getServerRank(p_name);

        if(!rank.equalsIgnoreCase("default")){
            VIP = true;
        }
        
        /*if(group == 75 || group == 76 || group == 79 || group == 6 || group == 7 || group == 5 || group == 10 || group == 11 || group == 72){
            VIP = true; // Let them in no matter the slots!
        }*/

        int server_num = Integer.parseInt(Bukkit.getMotd().substring(Bukkit.getMotd().indexOf("-") + 1, Bukkit.getMotd().indexOf(" ")));
        if(server_num == 9 || server_num == 10){
            // VIP server
            if(!(VIP)){
                e.setKickMessage(ChatColor.RED + "You are " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " authorized to connect to subscriber only servers." + "\n\n" + ChatColor.GRAY + "Subscribe at http://dungeonrealms.net/shop to gain instant access!");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            }
        }

        // No longer req. forum account! Woohoo!
        /*if(group == -1){
			e.setKickMessage(ChatColor.RED.toString() + "You " + ChatColor.UNDERLINE.toString() + "MUST" + ChatColor.RED.toString() + " have an active forum account to login to Dungeon Realms." + "\n" + "\n" + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "REGISTER:" + ChatColor.UNDERLINE.toString() + " http://dungeonrealms.net/forum/register.php");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			return;
		}*/

        int players_online = Main.plugin.getServer().getOnlinePlayers().length;
        if(players_online >= Bukkit.getMaxPlayers()){
            if(VIP == false){
                e.setKickMessage(ChatColor.RED.toString() + "This Dungeon Realms server is currently FULL." + "\n" + ChatColor.GRAY.toString() + "You can subscribe at http://dungeonrealms.net/buy/ to get instant access.");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }
            else if(VIP == true){
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
            }
        }

        remote_player_data.remove(p_name);
        RealmMechanics.player_god_mode.remove(p_name); // Remove any remnant god mode data.

        local_player_ip.put(p_name, e.getAddress().getHostAddress());

        int server_num_on = -1;
        Object result = downloadPlayerDatabaseData(p_name);
        if(result instanceof Exception){
            // Something went wrong, an exception was thrown.
            no_upload.add(p_name);
            e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD player data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 015");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        if(result instanceof Integer){
            // The result is the server_num they're on.
            server_num_on = (Integer)result;
        }
        if(result instanceof Boolean){
            Boolean bresult = (Boolean)result;
            if(bresult == false){
                server_num_on = -2;
            }
        }

        if(server_num_on >= 0){
            // They're online a server.
            //int oserver_num_on = server_num_on;

            //String motd = Bukkit.getMotd();
            String prefix = "US-";

            if(server_num_on > 1000 && server_num_on < 2000){
                // EU server. 1001, 1002, etc.
                server_num_on -= 1000;
                prefix = "EU-";
            }

            if(server_num_on > 2000 && server_num_on < 3000){
                server_num_on -= 2000;
                prefix = "BR-";
            }

            if(server_num_on > 3000){
                // EU server. 1001, 1002, etc.
                server_num_on -= 3000;
                prefix = "US-YT";
            }

            e.setKickMessage(ChatColor.YELLOW.toString() + "The account " + ChatColor.BOLD.toString() + p_name + ChatColor.YELLOW.toString() + " is already logged in on " + ChatColor.UNDERLINE.toString() + prefix + server_num_on + "." + "\n\n" + ChatColor.GRAY.toString() + "If you have just recently changed servers, your character data is being synced -- " + ChatColor.UNDERLINE.toString() + "wait a few seconds" + ChatColor.GRAY.toString() + " before reconnecting.");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(server_num_on == -2){
            // This just means that they have no data in player_database, not that they've never logged in.
            // Let's check to make sure they don't have any player_data.
            if(!hasAccountData(p_name)){
                first_login.add(p_name);
                setFirstLoginSQL(p_name);
                player_first_login.put(p_name, System.currentTimeMillis());

                // Setup all variables that downloadPlayerDatabaseData didn't get cause it returned -2.
                HealthMechanics.noob_players.add(p_name);
                KarmaMechanics.align_map.put(p_name, "good");
                HealthMechanics.health_data.put(p_name, 50);
                PermissionMechanics.rank_map.put(p_name, "default");
                RealmMechanics.realm_tier.put(p_name,  1); // was 0
                PetMechanics.pet_map.put(p_name, new ArrayList<Entity>());
                PetMechanics.player_pets.put(p_name, PetMechanics.downloadPetData(p_name));
            }
            else{
                // A value of -2 means that the function returned false on downloadPlayerDatabase(), so there could be some issues going on here if account data EXISTS and it returned -2.
                no_upload.add(p_name);
                e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD player data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 019");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }
        }

        if(CommunityMechanics.local_last_login.containsKey(p_name)){
            CommunityMechanics.local_last_login.remove(p_name);
        }

        try {	
            setPlayerServer(p_name);

            if(MoneyMechanics.downloadBankDatabaseData(p_name) == false){
                // Either new player or failed to download. Let's see.
                if(server_num_on != -2){
                    // Not a new player!
                    no_upload.add(p_name);
                    e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD bank data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 013D");
                    log.info("Problematic server: " + server_num_on);
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    setPlayerOffline(p_name, 1);
                    return;
                }
                else{
                    MoneyMechanics.bank_map.put(p_name, 0);
                    MoneyMechanics.bank_level.put(p_name, 0);
                    MoneyMechanics.bank_contents.put(p_name, new ArrayList<Inventory>(Arrays.asList(Bukkit.createInventory(null, 9, "Bank Chest"))));
                }
            }

            if(ShopMechanics.downloadShopDatabaseData(p_name) == false){
                // Either new player or failed to download. Let's see.
                if(server_num_on != -2){
                    // Not a new player!
                    no_upload.add(p_name);
                    e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD shop data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 014");
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    setPlayerOffline(p_name, 1);
                    return;
                }
                else{
                    ShopMechanics.shop_level.put(p_name, 0);
                }
            }

            GuildMechanics.setupGuildData(p_name, GuildMechanics.getPlayerGuildSQL(p_name));

            /*if(VIP && PermissionMechanics.getRank(p_name).equalsIgnoreCase("default")){
                // Set permissions.
                if(group == 75){
                    PermissionMechanics.setRank(p_name, "sub", true);
                }
                if(group == 76){
                    PermissionMechanics.setRank(p_name, "sub+", true);
                }
                if(group == 79){ 
                    PermissionMechanics.setRank(p_name, "sub++", true);
                }
            }
            else if(!VIP && (PermissionMechanics.getRank(p_name).equalsIgnoreCase("sub") || PermissionMechanics.getRank(p_name).equalsIgnoreCase("sub+") || PermissionMechanics.getRank(p_name).equalsIgnoreCase("sub++"))){
                // Take away subscriber rank.
                PermissionMechanics.setRank(p_name, "default", true);
            }*/

            if(!(player_ecash.containsKey(p_name))){
                // They have none, set to 0.
                player_ecash.put(p_name, 0);
            }

            log.info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[HIVE (SLAVE Edition)] Player data for " + p_name + " downloaded." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());

        } catch (Exception err) {
            err.printStackTrace();
            no_upload.add(p_name);
            e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD player data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 003");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            new File(main_world_name + "/players/" + p_name + ".dat").delete();
            setPlayerOffline(p_name, 2);
            return;

        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e){
        //final Player pl = e.getPlayer();
        if(e.getTo().getWorld().getName().equalsIgnoreCase(main_world_name) && !(e.getFrom().getWorld().getName().equalsIgnoreCase(e.getTo().getWorld().getName()))){
            // Set dimension.
            /*this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					InstanceMechanics.setPlayerEnvironment(pl, Environment.NETHER);
				}
			}, 2L);*/
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent e) throws IOException{
        final Player p = e.getPlayer();
        final String p_name = p.getName();

        if(remote_player_data.containsKey(p_name)){
            /*data.set(0, loc);
			data.set(1, inventory_s);
			data.set(2, level);
			data.set(3, hp);
			data.set(4, food_level);*/

            List<Object> data = remote_player_data.get(p_name);
            final Location loc = (Location)data.get(0);
            p.getInventory().clear();
            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                public void run(){
                    p.teleport(loc.add(0,1,0));
                    // Run TP after a second.
                }
            }, 10L);

            String inventory_s = (String)data.get(1);
            convertStringToInventory(p, inventory_s, null, 0);

            final int level = (Integer)data.get(2);
            Double hp = (Double)data.get(3);
            int food_level = (Integer)data.get(4);

            //p.setLevel(level);

            p.setFoodLevel(food_level);
            p.setLevel(100);
            p.setExp(1.0F);

            if(hp < 0){
                hp = 0.0D;
            }

            final double f_hp = hp;

            player_inventory.put(p_name, p.getInventory());
            player_location.put(p_name, loc);
            player_hp.put(p_name, hp);
            player_level.put(p_name, level);
            player_food_level.put(p_name, food_level);

            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                public void run() {
                    p.setHealth((int)f_hp);
                    if(f_hp > 0){
                        HealthMechanics.setPlayerHP(p.getName(), level);
                    }
                }
            }, 1L);

            /*this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					InstanceMechanics.setPlayerEnvironment(p, Environment.NETHER);
				}
			}, 2L);*/

            // Depreciated.
            // This hack fixes issues with the game thinking it's their first login ever. 
            /*p.saveData();
			p.loadData();*/
            // ^
        }
        else if(!(first_login.contains(p.getName()))){
            e.setResult(Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED.toString() + "Failed to LOAD player data from database." + "\n" + ChatColor.GRAY.toString() + "Please try again later. " + "\n\n" + ChatColor.BOLD.toString() + "ERROR CODE: 065");
            return;
        }

        for(Player p_online : Bukkit.getOnlinePlayers()){
            p_online.showPlayer(p);
        }

        log.info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[HIVE (SLAVE Edition)] Player data for " + p.getName() + " LOADED." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());

        if(first_login.contains(p.getName())){
            log.info("[HIVE (SLAVE Edition)] " + p.getName() + " has logged in for the first time. No data downloaded.");
            //p.setLevel(50);
            HealthMechanics.setPlayerHP(p.getName(), 50);
            p.teleport(TutorialMechanics.tutorialSpawn);

            /*Integer groupID = forum_usergroup.get(p_name);

            if(groupID == 9){ // Premium user.
                PetMechanics.addPetToPlayer(p.getName(), "baby_zombie");
            }*/

            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLoginLoaded(PlayerJoinEvent e){
        final Player p = e.getPlayer();
        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
            public void run() {
                if(!(loaded_players.contains(p.getName()))){
                    loaded_players.add(p.getName());
                }
            }
        }, 10L);
    }

    public void removeGemIcons(Player pl){
        int index = -1;
        for(ItemStack is : pl.getInventory()){
            index++;
            if(is == null){
                continue;
            }
            if(is.getType() == Material.EMERALD && is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                String name = is.getItemMeta().getDisplayName();
                if(name.toLowerCase().contains("gem(s)")){
                    pl.getInventory().setItem(index, new ItemStack(Material.AIR));
                    log.info("(FLAG) Player " + pl.getName() + " has illegal time (bank statis cash) in inventory."); 
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onPlayerFirstJoin(PlayerJoinEvent e){
        final Player p = e.getPlayer();
        final String p_name = p.getName();
        e.setJoinMessage("");
        removeGemIcons(p);
        
        if(first_login.contains(p.getName())){
            //p.teleport(TutorialMechanics.tutorialSpawn, TeleportCause.PLUGIN);
        	Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                public void run() {
                    p.teleport(TutorialMechanics.tutorialSpawn, TeleportCause.PLUGIN);
                }
            }, 10L);
            return;      	
        }
        else if(remote_player_data.containsKey(p.getName())){
            //p.teleport(((Location)remote_player_data.get(p.getName()).get(0)).add(0, 1, 0), TeleportCause.PLUGIN);
        }

        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
            public void run(){
                Player p = Bukkit.getPlayer(p_name);
                if(p != null){
                    Location l = p.getLocation();

                    if(l.distanceSquared((new Location(Bukkit.getWorlds().get(0), -981, 163, -292))) <= 100){ // 50 blocks from the bugged mountain spawn.
                        // They're in debug plains somehow. This isn't good!
                        if(HealthMechanics.getPlayerHP(p.getName()) < 50){
                            p.teleport(TutorialMechanics.tutorialSpawn, TeleportCause.PLUGIN);
                        }
                        else{
                            p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()), TeleportCause.PLUGIN);
                        }
                    }

                    l.setY(0);
                    if(l.distanceSquared((new Location(Bukkit.getWorlds().get(0), 0, 0, 0))) <= 2500){ // 50 blocks from the bugged mountain spawn.
                        // They're in debug plains somehow. This isn't good!
                        p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()), TeleportCause.PLUGIN);
                    }
                }
            }
        }, 20L);

        // Depreciated, is this even possible with the new shard system?
        Thread t = new Thread(new Runnable(){
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(Bukkit.getPlayer(p.getName()) == null){
                    return; // No need to ban, they're offline!
                }

                int server_num_reported = getPlayerServer(p.getName(), true);
                String motd = Bukkit.getMotd();
                int server_num_local = 1;

                if(motd.contains("US-YT")){
                    server_num_local = server_num_local + 3000;
                }
                try{
                    server_num_local =Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
                } catch(NumberFormatException nfe){
                    // Thrown on non- CC-## format.
                }
                if(motd.contains("EU-")){
                    // It's a european server, so add 1000. 1 = 1001.
                    server_num_local = server_num_local + 1000;
                }
                if(motd.contains("BR-")){
                    // It's a brazillian server, so add 2000. 1 = 2001.
                    server_num_local = server_num_local + 2000;
                }

                if(server_num_reported != -1 && server_num_reported != server_num_local){
                    // They're on two servers at once. Ban for 72 hours.
                    long unban_date = -1L; //(System.currentTimeMillis() + (1000 * (72 * 3600)));
                    String reason = "[AUTO] Dupe Exploit";
                    ModerationMechanics.BanPlayer(p.getName(), unban_date, reason, "Console", false);
                    if(Bukkit.getPlayer(p_name) != null){
                        Player banned = Bukkit.getPlayer(p_name);
                        if(reason == ""){
                            to_kick.put(banned.getName(), ChatColor.RED.toString() + "Your account has been " + ChatColor.UNDERLINE + "TEMPORARILY" + ChatColor.RED + " locked due to suspisious activity." + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
                        }
                        else if(reason.length() > 0){
                            to_kick.put(banned.getName(), ChatColor.RED.toString() + "Your account has been " + ChatColor.UNDERLINE + "TEMPORARILY" + ChatColor.RED + " locked due to " + reason + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
                        }
                    }

                    CommunityMechanics.sendPacketCrossServer("@ban@" + p_name, server_num_reported, false);
                    log.info("[HIVE (SLAVE Edition)] Detected a player trying to dupe by multiple logging. Banned: " + p.getName());
                }
            }

        });

        t.start();

        if(!(TutorialMechanics.onTutorialIsland(p))){
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "              Dungeon Realms Patch " + version);
            //p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "              Dungeon Realms Halloween Patch");
            p.sendMessage(ChatColor.GRAY + "                    " + "http://www.dungeonrealms.net/");
            p.sendMessage("");
            p.sendMessage(ChatColor.YELLOW + "                 " + "You are on the " + ChatColor.BOLD + MOTD.substring(0, MOTD.indexOf(" ")) + ChatColor.YELLOW + " shard.");
            p.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " Use " + ChatColor.YELLOW + "/shard" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " to change your server instance at any time.");

            if(MOTD.contains("RP")){
                p.sendMessage("");
                p.sendMessage(ChatColor.DARK_AQUA + "This is a " + ChatColor.UNDERLINE + "ROLEPLAY" + ChatColor.DARK_AQUA + " server. Local chat should always be in character, Global/Trade chat may be OOC.");
                p.sendMessage(ChatColor.GRAY + "Please be respectful to those who want to roleplay. You " + ChatColor.UNDERLINE + "will" + ChatColor.GRAY + " be banned for trolling / local OOC.");
            }

            if(MOTD.contains("BR")){
                p.sendMessage("");
                p.sendMessage(ChatColor.DARK_AQUA + "This is a " + ChatColor.UNDERLINE + "Brazillian" + ChatColor.DARK_AQUA + " server.");
                p.sendMessage(ChatColor.GRAY + "The official language of this server is " + ChatColor.UNDERLINE + "Portuguese.");
            }

        }

        if(player_sdays_left.containsKey(p.getName()) || PermissionMechanics.rank_map.get(p.getName()).equalsIgnoreCase("sub++")){
        	Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                public void run() {
                    if(!PermissionMechanics.rank_map.get(p.getName()).equalsIgnoreCase("sub++")){
                        p.sendMessage(ChatColor.GOLD + "You have " + ChatColor.UNDERLINE.toString() + player_sdays_left.get(p.getName()) + " day(s)" + ChatColor.GOLD + " left until your subscription expires.");
                    }
                    else{
                        p.sendMessage(ChatColor.GOLD + "You have " + ChatColor.UNDERLINE.toString() + "UNLIMITED" + " day(s)" + ChatColor.GOLD + " left until your subscription expires.");
                    }
                }
            }, 10L);
        }

        if(p.isOp()){
            RealmMechanics.player_god_mode.put(p.getName(), System.currentTimeMillis());
            //p.setLevel(9999);
            HealthMechanics.setPlayerHP(p.getName(), 9999);
            p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "                GM INVINSIBILITY (infinite)");
            ModerationMechanics.vanish_list.add(p.getName());
            p.sendMessage(ChatColor.GREEN + "You are now " + ChatColor.BOLD + "invisible.");
        }

        if(online_today.contains(p.getName()) && !first_login.contains(p.getName())){
        	Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                public void run() {
                    //if(forum_usergroup.containsKey(p.getName()) && forum_usergroup.get(p.getName()) != -1){
                    int amount_to_give = new Random().nextInt(5) + 10;
                    p.sendMessage("");
                    p.sendMessage(ChatColor.GOLD + "You have gained " + ChatColor.BOLD + amount_to_give + "EC" + ChatColor.GOLD + " for logging into Dungeon Realms today!");
                    p.sendMessage(ChatColor.GRAY + "Use /ecash to spend your EC, you can obtain more e-cash by logging in daily or by visiting " + ChatColor.GOLD + ChatColor.UNDERLINE + "http://dungeonrealms.net/shop");
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 0.25F);

                    if(Hive.player_ecash.containsKey(p.getName())){
                        int current = Hive.player_ecash.get(p.getName());
                        current += amount_to_give;
                        Hive.player_ecash.put(p.getName(), current);
                    }
                    else{
                        Hive.player_ecash.put(p.getName(), amount_to_give);
                    }

                    Hive.sql_query.add("INSERT INTO player_database(p_name,online_today) values('" + p.getName() + "', 1) on duplicate key update online_today=1");
                    online_today.remove(p.getName());

                    EcashMechanics.setECASH_SQL(p_name, Hive.player_ecash.get(p.getName()));
                    //}
                    /*else{
						// No forum account!
						int amount_to_give = new Random().nextInt(5) + 10;
						p.sendMessage("");
						p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "could" + ChatColor.RED + " have gained " + ChatColor.BOLD + amount_to_give + "EC" + ChatColor.RED + " for logging into Dungeon Realms today!");
						p.sendMessage(ChatColor.GRAY + "To claim this free /ecash, register at " + ChatColor.UNDERLINE + "http://dungeonrealms.net/login/do/register");

					}*/
                }
            }, 30L);

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKickEvent(PlayerKickEvent e){
        e.setLeaveMessage("");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(safe_logout.containsKey(p.getName())){
            if(!safe_logout_location.get(p.getName()).getWorld().getName().equalsIgnoreCase(p.getLocation().getWorld().getName()) || safe_logout_location.get(p.getName()).distanceSquared(p.getLocation()) > 2.0D){
                safe_logout.remove(p.getName());
                safe_logout_location.remove(p.getName());
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Logout - CANCELLED");
            }
        }
        if(server_swap_location.containsKey(p.getName())){
            e.setCancelled(true);
            p.teleport(server_swap_location.get(p.getName()));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            Player hurt = (Player)e.getEntity();
            if(player_to_npc.containsKey(hurt.getName())){
                /*e.setCancelled(false);
				e.setDamage(1);*/
                hurt.damage(hurt.getHealth());
                hurt.playEffect(EntityEffect.DEATH);
            }
            if(safe_logout.containsKey(hurt.getName())){
                safe_logout.remove(hurt.getName());
                safe_logout_location.remove(hurt.getName());
                hurt.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Logout - CANCELLED");
            }
            if(server_swap.containsKey(hurt.getName())){
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
        if(e.getDamager() instanceof Player){
            Player damager = (Player)e.getDamager();
            if(server_swap.containsKey(damager.getName())){
                // No damage for server hoppers!
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void setPlayerAsQuitting(PlayerQuitEvent e){
        Player p = e.getPlayer();

        if(!(Hive.server_frozen)){
            p.saveData();
        }

        first_login.remove(p.getName());

        if(!(loaded_players.contains(p.getName()))){
            return;
        }

        if(server_swap.containsKey(p.getName())){
            return;
        }

        pending_upload.add(p.getName());
        being_uploaded.add(p.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) throws IOException{
        String pname = "";
        e.setQuitMessage("");
        final Player p = e.getPlayer();

        if(server_swap.containsKey(p.getName())){
        	new BukkitRunnable() {
				@Override
				public void run() {
					Hive.server_swap.remove(p.getName());
                    Hive.server_swap_location.remove(p.getName());
                    File data = new File(Hive.rootDir + "/" + Hive.main_world_name + "/players/" + p.getName() + ".dat");
                    data.delete();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 20L);
            return;
        }

        if(!(loaded_players.contains(p.getName()))){
            log.info("[Hive] Skipping data upload for " + p.getName() + ", not in loaded_players table.");
            /*Hive.player_inventory.remove(p.getName());
			Hive.player_location.remove(p.getName());
			Hive.player_hp.remove(p.getName());
			Hive.player_level.remove(p.getName());
			Hive.player_food_level.remove(p.getName());
			Hive.player_armor_contents.remove(p.getName());
			Hive.player_ecash.remove(p.getName());
			KarmaMechanics.align_map.remove(p.getName());
			KarmaMechanics.align_time.remove(p.getName());
			CommunityMechanics.ignore_list.remove(p.getName());
			CommunityMechanics.buddy_list.remove(p.getName());
			CommunityMechanics.toggle_list.remove(p.getName());
			HealthMechanics.noob_player_warning.remove(p.getName());
			HealthMechanics.noob_players.remove(p.getName());
			RealmMechanics.realm_title.remove(p.getName());
			//RealmfdMechanics.reafdslm_tier.remove(p.getName());*/
            return;
        }

        player_inventory.put(p.getName(), p.getInventory());
        player_location.put(p.getName(), p.getLocation());
        player_hp.put(p.getName(), (double)p.getHealth());
        player_level.put(p.getName(), HealthMechanics.getPlayerHP(p.getName()));
        player_food_level.put(p.getName(), p.getFoodLevel());
        player_armor_contents.put(p.getName(), p.getInventory().getArmorContents());
        // Update local data.

        if(!(server_swap.containsKey(p.getName())) && !(safe_logout.containsKey(p.getName())) && !(no_upload.contains(p.getName())) && HealthMechanics.getPlayerHP(p.getName()) > 0 && p.getHealth() > 0 && HealthMechanics.in_combat.containsKey(p.getName()) && !(DuelMechanics.isDamageDisabled(p.getLocation())) && p.getWorld().getName().equalsIgnoreCase(main_world_name) && (server_lock == false && shutting_down == false)){ // They're in combat, let's spawn an NPC.
            logout_time.put(p.getName(), System.currentTimeMillis());
            Location loc = p.getLocation();
            RemoteEntity re = npc_manager.createNamedEntity(RemoteEntityType.Human, loc, p.getName(), true);
            re.setPushable(false);
            re.setStationary(true);

            Player playerNPC = (Player) re.getBukkitEntity();
            HealthMechanics.setPlayerHP(playerNPC.getName(), HealthMechanics.getPlayerHP(p.getName()));
            //playerNPC.setLevel(HealthMechanics.getPlayerHP(p.getName()));
            playerNPC.setExp(1.0F);
            playerNPC.setGameMode(GameMode.SURVIVAL);

            player_to_npc.put(p.getName(), re);
            player_to_npc_align.put(p.getName(), KarmaMechanics.getRawAlignment(p.getName()));
            player_item_in_hand.put(p.getName(), p.getItemInHand());

            if(MountMechanics.mule_inventory.containsKey(p.getName())){
                // They have a mule inventory, store it incase we need it later.
                boolean has_mule = false;
                for(ItemStack is : p.getInventory().getContents()){
                    if(MountMechanics.isMule(is)){
                        has_mule = true;
                    }
                    break;
                }

                if(has_mule){
                    player_mule_inventory.put(p.getName(), MountMechanics.mule_inventory.get(p.getName()));
                }
            }

            List<Player> lpnear = new ArrayList<Player>();
            for(Entity ent : p.getNearbyEntities(48, 48, 48)){
                if(ent instanceof Player){
                    lpnear.add((Player)ent);
                }
            }

            KarmaMechanics.sendAlignColor(playerNPC, playerNPC);

            final List<Player> safe_lpnear = new ArrayList<Player>(lpnear);
            List<ItemStack> l_is = new ArrayList<ItemStack>();
            List<ItemStack> armor_list = new ArrayList<ItemStack>();

            for(ItemStack is : p.getInventory().getContents()){
                if(is == null || is.getType() == Material.AIR || is.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(is) || !RealmMechanics.isItemTradeable(is)){
                    continue;
                }
                l_is.add(is);
            }

            for(ItemStack is : p.getInventory().getArmorContents()){
                if(is == null || is.getType() == Material.AIR){
                    continue;
                }
                armor_list.add(is);
            }

            npc_armor.put(re, armor_list);
            npc_inventory.put(re, l_is);

            log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "[HIVE (SLAVE Edition)] Player " + p.getName() + " logged out in combat, NPC spawned." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());

            final Entity ent = re.getBukkitEntity();

            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                public void run() {
                    Player playerNPC = ((Player)ent);

                    EntityPlayer origin_p = ((CraftPlayer) p).getHandle();
                    net.minecraft.server.v1_7_R1.ItemStack weapon = null, boots = null, legs = null, chest = null, head = null;

                    if(origin_p.getEquipment(0) != null){
                        weapon = origin_p.getEquipment(0);
                    }
                    if(origin_p.getEquipment(1) != null){
                        boots = origin_p.getEquipment(1);
                    }
                    if(origin_p.getEquipment(2) != null){
                        legs = origin_p.getEquipment(2);
                    }
                    if(origin_p.getEquipment(3) != null){
                        chest = origin_p.getEquipment(3);
                    }
                    if(origin_p.getEquipment(4) != null){
                        head = origin_p.getEquipment(4);
                    }

                    EntityPlayer ent_p_edited = ((CraftPlayer) playerNPC).getHandle();

                    List<Packet> pack_list = new ArrayList<Packet>();
                    if(weapon != null){
                        pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 0, weapon));
                    }
                    if(boots != null){
                        pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 1, boots));
                    }
                    if(legs != null){
                        pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 2, legs));
                    }
                    if(chest != null){
                        pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 3, chest));
                    }
                    if(head != null){
                        pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 4, head));
                    }

                    for(Player pl : safe_lpnear){
                        if(pl != null){
                            for(Packet pa : pack_list){
                                ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(pa);
                            }
                        }
                    }

                    try {
                        Field underlyingEntityField = CraftEntity.class.getDeclaredField("entity");
                        underlyingEntityField.setAccessible(true);
                        Object underlyingPlayerObj = underlyingEntityField.get(playerNPC);
                        if (underlyingPlayerObj instanceof EntityPlayer) {
                            EntityPlayer underlyingPlayer = (EntityPlayer) underlyingPlayerObj;
                            underlyingPlayer.invulnerableTicks = 1;
                        }
                    } catch (Exception e) {
                        log.info("LoginInvulnerabilityFix exception: " + e.getMessage());
                        e.printStackTrace();
                    }

                }
            }, 4L);

        }

        safe_logout.remove(p.getName());
        safe_logout_location.remove(p.getName());

        pname = p.getName();
        final String safe_pname = pname;

        /*this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
			public void run() {
				UploadPlayerData.uploadData(safe_pname);
			}
		}, 0L);*/

        Thread t = new UploadPlayerData(safe_pname);
        t.start();
    }


    public String addPatchVersion(String motd){
        // It's 48 characters to get to where we need to be. (from 0)
        String motd_with_space = motd + "";
        String patch_string = ChatColor.GRAY + "Patch " + version;
        int needed_space = (int) (58 - ((ChatColor.stripColor(motd).length()) * 1.25));
        while(needed_space > 0){
            needed_space--;
            motd_with_space += " ";
        }
        motd_with_space += patch_string;
        return motd_with_space;
    }

    public int getLoadPercent(){
        double percent = (uptime * (6.6 / 4));
        if(percent > 100){
            percent = 100;
        }
        return (int)Math.round(percent);
    }

    public int getTimeToReboot(){
        return (int) seconds_to_reboot;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerListPingEvent(ServerListPingEvent e){
        if(local_ddos == true || hive_ddos == true){
            String motd = ChatColor.RED.toString() + "[DESYNC] " + ChatColor.WHITE.toString() + e.getMotd();
            motd = addPatchVersion(motd);

            e.setMotd(motd);
            return;
        }
        if(loading_server == true){
            String motd = ChatColor.AQUA.toString() + "LOADING " + getLoadPercent() + "% " + ChatColor.WHITE.toString() + e.getMotd();
            motd = addPatchVersion(motd);

            e.setMotd(motd);
            return;
        }
        if(restart_inc == true){
            String motd = ChatColor.RED.toString() + "REBOOT " + (int) seconds_to_reboot + ChatColor.BOLD + "s" + ChatColor.RED + " " + ChatColor.WHITE.toString() + e.getMotd();
            motd = addPatchVersion(motd);

            e.setMotd(motd);
            return;
        }
        if(server_lock == true){
            String motd = ChatColor.DARK_GRAY.toString() + "[LOCKED] " + ChatColor.WHITE.toString() + e.getMotd();
            motd = addPatchVersion(motd);

            e.setMotd(motd);
            return;
        }
        else{
            String motd = ChatColor.WHITE.toString() + e.getMotd();
            motd = addPatchVersion(motd);

            e.setMotd(motd);
        }  
    }

    public static boolean hasConnection() {
        Socket socket = null;
        boolean reachable = false;
        try {
            socket = new Socket("google.com", 80);
            reachable = true;
        } catch(Exception err){
        } finally {            
            if (socket != null) try { socket.close(); } catch(IOException e) {}
        }

        return reachable;
    }

    public static boolean isHiveOnline() {
        Socket socket = null;
        try {  
            socket = new Socket(Hive_IP, SQL_port);
            socket.close(); 
            return true;
        } catch(Exception err){
        } finally {            
            if (socket != null) try { socket.close(); } catch(Exception e) {}
        }
        return false;
    }

    public void downloadPayload() throws IOException{
        new File(rootDir + "/" + "payload.zip").delete();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = new URL("ftp://" + ftp_user + ":" + ftp_pass + "@" + Hive_IP + "/sdata/payload.zip");
        URLConnection urlc;

        try {
            urlc = url.openConnection();

            InputStream is = urlc.getInputStream(); 
            OutputStream out = new FileOutputStream(rootDir + "/" + "payload.zip");

            byte buf[]=new byte[1024];
            int len;

            while((len=is.read(buf))>0){
                out.write(buf,0,len);
            }

            out.close();
            is.close();

        } catch (Exception err) {
            err.printStackTrace();
            log.info("[HIVE (SLAVE Edition) An error has occured in attempting to retrieve file payload from hive.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            downloadPayload();
            return;
        }

        log.info("[HIVE (SLAVE Edition)] Payload recieved.");

        while(!(new File(rootDir + "/" + "payload.zip").exists())){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        processPayload();
    }

    public void processPayload(){
        log.info("[HIVE (SLAVE Edition)] Payload processing...");

        Thread t = new Thread(new Runnable(){
            public void run(){
                int count = 0;
                while(pending_upload.size() > 0 && count <= 200){
                    count++;
                    try {
                        Thread.sleep(100); // Let all pending multi-thread uploads finish.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } 
                }

                ready_to_die = true;
            }
        });

        t.start();
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void unzip(String archive, File baseFolder, String[] ignoreExtensions) {
        FileInputStream fin;
        try {
            fin = new FileInputStream(archive);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                File destinationFile = new File(baseFolder, ze.getName());
                unpackEntry(destinationFile, zin);
            }
            zin.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void unpackEntry(File destinationFile, ZipInputStream zin) {
        createParentFolder(destinationFile);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(destinationFile);
            for (int c = zin.read(); c != -1; c = zin.read()) {
                fout.write(c);
                zin.closeEntry();
                fout.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createParentFolder(File destinationFile) {
        File parent = new File(destinationFile.getParent());
        parent.mkdirs();
    }

    public static Inventory getShardInventory(){
        if((System.currentTimeMillis() - last_shard_update) > 10 * 1000){
            ShardMenu = generateShardMenu();
            last_shard_update = System.currentTimeMillis();
        }
        return ShardMenu;
    }

    public static ItemStack generateShardItem(String server_prefix){
        // TODO: Show where friends/guildies are
        int server_num = -1;
        ItemStack icon = null;
        ChatColor cc = null;
        boolean vip_server = false;
        boolean rp_server = false;

        server_num = Integer.parseInt(server_prefix.substring(server_prefix.indexOf("-") + 1, server_prefix.length()));
        if(server_prefix.contains("US")){
            //server_num = server_num; // TODO Wtf?
        }
        if(server_prefix.contains("EU")){
            server_num = server_num + 1000;
        }
        if(server_prefix.contains("BR")){
            server_num = server_num + 2000;
        }

        if(server_num == 9 || server_num == 10){
            vip_server = true;
        }

        if(server_num == 11){
            rp_server = true;
        }

        //String IP = CommunityMechanics.server_list.get(server_num);

        int online_players = 0;
        int max_players = 0;

        if(server_population.containsKey(server_num)){
            online_players = server_population.get(server_num).get(0);
            max_players = server_population.get(server_num).get(1);
        }

        // TODO: Ping the server, make sure it's online.
        if(server_prefix.equalsIgnoreCase(MOTD.substring(0, MOTD.indexOf(" ")))){
            // This is the server they're on. Green dye to show they're connected.
            icon = new ItemStack(Material.WOOL, 1, (short)5);
            cc = ChatColor.GREEN;
            online_players = (int)Math.round((double)Bukkit.getOnlinePlayers().length);
            max_players = Bukkit.getMaxPlayers();
        }
        else{
            boolean server_open = true; // BungeeCord.getInstance().getServerInfo(server_prefix).canAccess((net.md_5.bungee.api.CommandSender) pl);
            if((online_players == 0 && max_players == 0) || offline_servers.contains(server_prefix) || !last_ping.containsKey(server_num)){
                online_players = 0;
                max_players = 0;
                server_open = false;
            }
            if(server_open){
                icon = new ItemStack(Material.WOOL, 1, (short)0);
                cc = ChatColor.WHITE;
            }
            else if(!(server_open)){
                icon = new ItemStack(Material.WOOL, 1, (short)14);
                cc = ChatColor.RED;
            }
        }

        ItemMeta im = icon.getItemMeta();

        online_players = (int)Math.round(online_players); 

        if(online_players > max_players){
            // So if  the spoofed amount is > the maximum, we're going to take away 5-15 of the online count so more players can join.
            online_players = max_players - (new Random().nextInt(15 - 5) + 5);
        }

        if(Bukkit.getOnlinePlayers().length + 10 >= max_players){
            // Ok, now if the actual length+10 more is > maximum, we're full for real, so no more spoofing is needed.
            online_players = Bukkit.getOnlinePlayers().length;
        }

        if(Bukkit.getOnlinePlayers().length <= 5){
            // Less than 5 people on, don't spoof. 5 -> 6 / 13
            online_players = Bukkit.getOnlinePlayers().length;
        }

        if(online_players > 0 || max_players > 0){
            im.setDisplayName(cc.toString() + server_prefix + ChatColor.GRAY + " "  + online_players + "/" + max_players + "");			
        }
        else{
            im.setDisplayName(cc.toString() + server_prefix);
        }
        List<String> lore = new ArrayList<String>();

        if(vip_server && cc != ChatColor.RED){
            lore.add(ChatColor.GREEN.toString() + ChatColor.GREEN + "S" + ChatColor.GREEN.toString() + "ubscriber " + ChatColor.GREEN + "S" + ChatColor.GREEN.toString() + "erver");
        }

        if(rp_server && cc != ChatColor.RED){
            lore.add(ChatColor.AQUA + "Roleplay Server");
        }

        if(server_prefix.contains("BR")){
            lore.add(ChatColor.DARK_AQUA + "Language: Portuguese");
        }

        if(cc == ChatColor.GREEN){
            lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "You are currently in this shard.");
        }
        else if(cc == ChatColor.WHITE){
            lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Click to join this shard.");
        }
        else if(cc == ChatColor.RED){
            lore.add(ChatColor.RED.toString() + ChatColor.ITALIC + "Shard Offline");
        }


        im.setLore(lore);
        icon.setItemMeta(im);
        return icon;
    }

    public static Inventory generateShardMenu(){
        ItemStack divider = ItemMechanics.signCustomItem(Material.THIN_GLASS, (short)0, " ", "");
        ItemStack minecade_lobby = ItemMechanics.signCustomItem(Material.SKULL_ITEM, (short)3, ChatColor.WHITE + "Minecade Lobby", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Go back to the Minecade lobby.");
        Inventory shard_menu = Bukkit.createInventory(null, 9, "Shard Selection");
        int index = 0;
        for(String s : us_public_servers){
            shard_menu.setItem(index, generateShardItem(s));
            index++;
        }

        //index = 9; // Move to next row for BR servers.

        for(String s : br_servers){
            shard_menu.setItem(index, generateShardItem(s));
            index++;
        }

        //index = 18;

        for(String s : us_private_servers){
            shard_menu.setItem(index, generateShardItem(s));
            index++;
        }

        shard_menu.setItem(8, minecade_lobby);

        int x = 0;
        for(ItemStack is : shard_menu.getContents()){
            if(is == null || is.getType() == Material.AIR){
                shard_menu.setItem(x, CraftItemStack.asCraftCopy(divider));
            }
            x++;
        }

        return shard_menu;
    }

    public boolean vipServer(ItemStack is){
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
            List<String> lore = is.getItemMeta().getLore();
            for(String s : lore){
                s = ChatColor.stripColor(s);
                if(s.equalsIgnoreCase("Subscriber Server")){
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack setECASHPrice(ItemStack i, double price){
        boolean rename = false;
        String o_name = "";
        try {
            try {
                o_name = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getString("Name");
                rename = true;
                // log.info(o_name);
            } catch (NullPointerException npe) {
                rename = false;
            }
        } catch (ClassCastException cce) {
            rename = false;
        }

        List<String> old_lore = new ArrayList<String>();
        ItemMeta im = i.getItemMeta();

        if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
            for(String s : im.getLore()){
                old_lore.add(s);
            }

            if(rename == true && o_name.length() > 0){
                im.setDisplayName(o_name);
            } 

            old_lore.add(ChatColor.WHITE.toString() + price + ChatColor.GREEN.toString() + " E-CASH");
            im.setLore(old_lore);
            i.setItemMeta(im);
        }

        if(i != null && !(i.hasItemMeta())){
            old_lore.add(ChatColor.WHITE.toString() + price + ChatColor.GREEN.toString() + " E-CASH");
            im.setLore(old_lore);
            i.setItemMeta(im);
        }

        return i;
    }

    public static double getECASHPrice(ItemStack is){
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
            for(String s : is.getItemMeta().getLore()){
                if (s.contains("E-CASH")) {
                    return Double.parseDouble(ChatColor.stripColor((s.substring(0, s.indexOf(" ")))));
                }
            }
        }
        return 0;
    }

    public static boolean doTheyHaveEnoughECASH(String p_name, int needed){
        if(player_ecash.containsKey(p_name) && player_ecash.get(p_name) >= needed){
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory().getName().equalsIgnoreCase("Shard Selection")){
            e.setCancelled(true);
            final Player pl = (Player)e.getWhoClicked();
            if(e.getRawSlot() >= 27){
                return;
            }
            if(e.getCurrentItem() == null){
                return;
            }
            
            ItemStack cur_item = e.getCurrentItem();

            if(cur_item.getType() == Material.SKULL_ITEM || (cur_item.getType() == Material.WOOL && cur_item.hasItemMeta() && cur_item.getItemMeta().hasDisplayName() && cur_item.getItemMeta().hasLore())){
                // Hop servers?
                short durability = cur_item.getDurability();
                if(durability == 5){
                    // Current server, do nothing.
                    pl.sendMessage(ChatColor.YELLOW + "You are already on the " + ChatColor.BOLD + MOTD.substring(0, MOTD.indexOf(" ")) + ChatColor.YELLOW + " shard.");
                    Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        public void run() {
                            pl.closeInventory();
                        }
                    }, 2L);

                }
                if(durability == 14){
                    // Current server, do nothing.
                    pl.sendMessage(ChatColor.RED + "This shard is currently " + ChatColor.UNDERLINE + "unavailable.");
                    Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        public void run() {
                            pl.closeInventory();
                        }
                    }, 2L);
                }

                if(durability == 0 || cur_item.getType() == Material.SKULL_ITEM){
                    // Ok, need to move them to new server.

                    boolean deny = false;

                    if(!deny && no_shard){
                        pl.sendMessage(ChatColor.RED + "This feature is " + ChatColor.UNDERLINE + "temporarily" + ChatColor.RED + " disabled while we troubleshoot.");
                        deny = true;
                    }
                    if(!deny && TutorialMechanics.onTutorialIsland(pl)){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change game shards while on Tutorial Island.");
                        deny = true;
                    }
                    if(!deny && HealthMechanics.in_combat.containsKey(pl.getName())){
                        double seconds_left = 0;
                        long dif = ((HealthMechanics.HealthRegenCombatDelay * 1000) + HealthMechanics.in_combat.get(pl.getName())) - System.currentTimeMillis();
                        seconds_left = (dif / 1000.0D) + 0.5D;
                        seconds_left = Math.round(seconds_left);

                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in combat.");
                        pl.sendMessage(ChatColor.GRAY + "Try again in approx. " + seconds_left + ChatColor.BOLD + "s");
                        deny = true;
                    }
                    if(!deny && !DuelMechanics.isDamageDisabled(pl.getLocation()) && LootMechanics.isMonsterNearPlayer(pl, 16)){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards with hostile monsters nearby.");
                        pl.sendMessage(ChatColor.GRAY + "Eliminate all monsters in a 16x16 area and try again.");
                        deny = true;
                    }
                    if(!deny && (Hive.seconds_to_reboot <= 10 && Hive.restart_inc) || Hive.server_lock || Hive.local_ddos || Hive.hive_ddos){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards at the moment.");
                        pl.sendMessage(ChatColor.GRAY + "The servers are likely preparing to reboot or enter maintenance mode.");
                        deny = true;
                    }
                    if(!deny && DuelMechanics.duel_map.containsKey(pl.getName())){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a duel.");
                        deny = true;
                    }
                    if(!deny && InstanceMechanics.isInstance(pl.getWorld().getName())){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in an instance.");
                        deny = true;
                    }
                    if(!deny && !(pl.getWorld().getName().equalsIgnoreCase(main_world_name))){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a realm.");
                        deny = true;
                    }
                    if(!deny && MountMechanics.mount_map.containsKey(pl.getName())){
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while on a mount.");
                        deny = true;
                    }
                    if(!deny && !pl.isOp() && !DuelMechanics.isDamageDisabled(pl.getLocation()) && Hive.login_time.containsKey(pl.getName()) && (((System.currentTimeMillis() - Hive.login_time.get(pl.getName())) / 1000.0D) <= 300)){
                        int seconds_left = 300 - ((int)((System.currentTimeMillis() - Hive.login_time.get(pl.getName())) / 1000.0D));
                        pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a wilderness / chaotic zone for another " + ChatColor.UNDERLINE + seconds_left + ChatColor.BOLD + "s");
                        pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This delay is to prevent resource, monster, and treasure farming abuse.");
                        deny = true;
                    }

                    if(deny){
                    	Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                            public void run() {
                                pl.closeInventory();
                            }
                        }, 2L);
                        return;
                    }

                    if(Hive.restart_inc || Hive.server_lock || Hive.local_ddos || Hive.hive_ddos){
                        pl.sendMessage(ChatColor.RED + "This opperation is currently " + ChatColor.UNDERLINE + "unavailable.");
                        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                            public void run() {
                                pl.closeInventory();
                            }
                        }, 2L);
                        return;
                    }

                    if(vipServer(cur_item)){
                        String rank = PermissionMechanics.getRank(pl.getName());
                        if(rank.equalsIgnoreCase("default") && !(pl.isOp())){
                            // Don't let them in.
                            pl.sendMessage(ChatColor.RED + "You are " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " authorized to connect to subscriber only servers.");
                            pl.sendMessage(ChatColor.GRAY + "Subscribe at http://dungeonrealms.net/shop to gain instant access!");
                            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                public void run() {
                                    pl.closeInventory();
                                }
                            }, 2L);
                            return;
                        }
                    }

                    String i_name = ChatColor.stripColor(cur_item.getItemMeta().getDisplayName());
                    final String server_prefix = !i_name.contains("Minecade") ? i_name.substring(0, i_name.indexOf(" ")) : "lobby1";

                    if(cur_item.getType() != Material.SKULL_ITEM){
                        int online = Integer.parseInt(i_name.substring(i_name.lastIndexOf(" ") + 1, i_name.lastIndexOf("/")));
                        int max_online = Integer.parseInt(i_name.substring(i_name.lastIndexOf("/") + 1, i_name.length()));

                        // TODO: The stats on /shard are not in realtime, do we allow overflow or should we use ServerSwitchEvent to catch it?
                        if(online >= max_online && online != 0){
                            //int group = forum_usergroup.get(pl.getName());
                            String rank = PermissionMechanics.getRank(pl.getName());
                            if(!pl.isOp() && rank.equalsIgnoreCase("default")){
                                pl.sendMessage(ChatColor.RED + "This shard is currently " + ChatColor.UNDERLINE + "FULL.");
                                Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                    public void run() {
                                        pl.closeInventory();
                                    }
                                }, 2L);
                                return;
                            }
                        }
                    }
        
                    server_swap.put(pl.getName(), server_prefix);
                    server_swap_location.put(pl.getName(), pl.getLocation());
                    server_swap_pending.remove(pl.getName()); // Remove AFTER server_swap has been populated.

                    if(!(pl.getWorld().getName().equalsIgnoreCase(main_world_name))){
                        Location safe = null;
                        if(RealmMechanics.saved_locations.containsKey(pl.getName())){
                            safe = RealmMechanics.saved_locations.get(pl.getName());
                            if(RealmMechanics.inv_portal_map.containsKey(pl.getName())){
                                Location l = RealmMechanics.inv_portal_map.get(pl.getName());
                                RealmMechanics.inv_portal_map.remove(pl.getName());
                                RealmMechanics.portal_map.remove(l);
                                l.getBlock().setType(Material.AIR);
                                l.subtract(0, 1, 0).getBlock().setType(Material.AIR);
                            }
                        }
                        else if(InstanceMechanics.saved_location_instance.containsKey(pl.getName())){
                            safe = InstanceMechanics.saved_location_instance.get(pl.getName());
                        }
                        else{
                            safe = SpawnMechanics.getRandomSpawnPoint(pl.getName());
                        }

                        pl.teleport(safe);
                    }

                    //pl.saveData();
                    player_inventory.put(pl.getName(), pl.getInventory());
                    player_location.put(pl.getName(), pl.getLocation());
                    player_hp.put(pl.getName(), (double)pl.getHealth());
                    player_level.put(pl.getName(), HealthMechanics.getPlayerHP(pl.getName()));
                    player_food_level.put(pl.getName(), pl.getFoodLevel());
                    player_armor_contents.put(pl.getName(), pl.getInventory().getArmorContents());
                    // Update local data.

                    pl.sendMessage("");
                    pl.sendMessage(ChatColor.YELLOW + "                       Loading Shard - " + ChatColor.BOLD + server_prefix + ChatColor.YELLOW + " ... ");
                    pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Your current game session has been paused while your data is transferred.");
                    pl.sendMessage("");

                    Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
                        public void run() {
                            try {
                                ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 10);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /*pl.getWorld().spawnParticle(pl.getLocation().add(0.5, 0.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().add(0, 0.5, 0.5), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().add(0, 0.75, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().add(0.25, 0.75, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().add(0, 1, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().add(0.25, 1, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().add(0, 1.50, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().add(0.25, 1.50, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().add(0.25, 2, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().subtract(0.5, -0.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().subtract(0, -0.5, 0.5), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().subtract(0, -0.75, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().subtract(0.25, -0.75, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().subtract(0, -1, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().subtract(0.25, -1, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().subtract(0, -1.50, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().subtract(0.25, -1.50, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);

								pl.getWorld().spawnParticle(pl.getLocation().subtract(0, -2, 0.25), Particle.HAPPY_VILLAGER, 0.5F, 1);
								pl.getWorld().spawnParticle(pl.getLocation().subtract(0.25, -2, 0), Particle.HAPPY_VILLAGER, 0.5F, 1);*/
                        }
                    });

                    for(Player p_online : Bukkit.getServer().getOnlinePlayers()){
                        p_online.hidePlayer(pl);
                    }

                    // We should give them god mode so they can't die which would cause dupe issues.
                    //pl.setPlayerListName(""); // So server treats them like NPC, no trading, etc.
                    RealmMechanics.player_god_mode.put(pl.getName(), System.currentTimeMillis());

                    Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                        public void run() {
                            pl.closeInventory();
                        }
                    }, 2L);

                    new BukkitRunnable() {
						@Override
						public void run() {
							//UploadPlayerData.uploadData(pl.getName());
                            Thread t = new UploadPlayerData(pl.getName());
                            t.start();
						}
					}.runTaskLaterAsynchronously(Main.plugin, 4L);

                    Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
                        public void run() {
                            // 30 second timeout, if they're still online then d/c them.
                            if(Main.plugin.getServer().getPlayer(pl.getName()) != null && server_swap.containsKey(pl.getName())){
                                Player upl = Main.plugin.getServer().getPlayer(pl.getName());
                                upl.kickPlayer("Connection Timeout");
                            }
                        }
                    }, 10 * 20L);
                    // TODO: We might need to do some extra data transfer stuff, upload data first then move.
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e){
        Player pl = e.getPlayer();
        if(server_swap.containsKey(pl.getName())){
            e.setCancelled(true);
            e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
            e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAnimation(PlayerAnimationEvent e){
        Player pl = e.getPlayer();

        Player ply = TradeMechanics.getTarget(pl);
        /*
        if(ply != null && EcashMechanics.personal_clones.containsKey(pl.getName())){
            RemoteEntity npc = EcashMechanics.personal_clones.get(pl.getName());
            String pl_name = pl.getName();

            if(pl_name.length() > 14){
                pl_name = pl_name.substring(0, 14);
            }

            if(ChatColor.stripColor(ply.getName()).equalsIgnoreCase(pl_name)){
                List<Player> lpl = new ArrayList<Player>();
                for(Entity ent : npc.getBukkitEntity().getNearbyEntities(32, 32, 32)){
                    if(ent instanceof Player){
                        lpl.add((Player)ent);
                    }
                }

                npc.getBukkitEntity().remove();
                //ShopMechanics.updateEntity(npc.getBukkitEntity(), lpl); // TODO - Need to update to latest DR Api!
                //EcashMechanics.personal_clones.remove(pl.getName()); // TODO - Need to update to latest DR Api!
                EcashMechanics.personal_clones_msg.remove(pl.getName());
                try {
                    ParticleEffect.sendToLocation(ParticleEffect.CRIT, npc.getBukkitEntity().getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1, 10);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return;
            }
        } // TODO - Need to update to latest DR Api! */
        
        if(ply != null && Hive.player_to_npc.containsKey(ply.getName())){
            //			ply.damage(ply.getHealth());
            //			ply.playEffect(EntityEffect.DEATH);

            if(Hive.player_to_npc.containsKey(ply.getName())){  // It was an NPC that died!
                RemoteEntity n = Hive.player_to_npc.get(ply.getName());
                ply.playEffect(EntityEffect.DEATH);

                String align = null;
                if(Hive.player_to_npc_align.containsKey(ply.getName())){
                    align = Hive.player_to_npc_align.get(ply.getName());
                }

                boolean neutral_boots = false, neutral_legs = false, neutral_chest = false, neutral_helmet = false, neutral_weapon = false;

                /*if(align != null && align.equalsIgnoreCase("neutral")){
					align = "evil";  Temp. fix -- just drop everything if they combat log as neutrals.
				}*/

                if(align != null && align.equalsIgnoreCase("neutral")){
                    //50% of weapon dropping, 25% for every piece of equipped armor.
                    String lost_gear_s = "";
                    if(new Random().nextInt(100) <= 50){
                        neutral_weapon = true;
                        lost_gear_s += "0" + ",";
                    }

                    if(new Random().nextInt(100) <= 25){
                        int index = new Random().nextInt(4);
                        if(index == 0){
                            neutral_boots = true;
                            lost_gear_s += "1" + ",";
                        }
                        if(index == 1){
                            neutral_legs = true;
                            lost_gear_s += "2" + ",";
                        }
                        if(index == 2){
                            neutral_chest = true;
                            lost_gear_s += "3" + ",";
                        }
                        if(index == 3){
                            neutral_helmet = true;
                            lost_gear_s += "4" + ",";
                        }
                    }

                    if(lost_gear_s.length() > 0){
                        Hive.sql_query.add("INSERT INTO player_database(p_name, lost_gear) VALUES('" + ply.getName() + "', '" + lost_gear_s + "') ON DUPLICATE KEY UPDATE lost_gear='" + lost_gear_s + "'");
                        // Now when the player logs in, the server will know exactly which items to take away.
                    }
                }

                List<ItemStack> p_inv = Hive.npc_inventory.get(n);
                if(align != null && !align.equalsIgnoreCase("evil")){
                    if(!neutral_weapon && !ProfessionMechanics.isSkillItem(p_inv.get(0)) && p_inv.get(0) != null && !ItemMechanics.getDamageData(p_inv.get(0)).equalsIgnoreCase("no")){
                        try{
                            p_inv.remove(0);  // Remove first hand weapon drop.
                        } catch(IndexOutOfBoundsException ioobe){
                            // Why is this even thrown?
                        }
                    }
                }

                for(ItemStack is : p_inv){
                    if(align != null && !align.equalsIgnoreCase("evil")){
                        //They're not chaotic.
                        if(ProfessionMechanics.isSkillItem(is)){
                            // Do not drop pickaxes, fishingrods.
                            continue;
                        }
                    }
                    ply.getWorld().dropItemNaturally(ply.getLocation(), is);
                }

                if(align != null && (align.equalsIgnoreCase("evil") || align.equalsIgnoreCase("neutral"))){
                    //Drop armor as well if chaotic.
                    for(ItemStack is : Hive.npc_armor.get(n)){
                        if(is == null || is.getType() == Material.AIR){
                            continue;
                        }
                        if(align.equalsIgnoreCase("neutral")){
                            if(is.getType().name().toLowerCase().contains("helmet") && !neutral_helmet){
                                continue;  
                            }
                            if(is.getType().name().toLowerCase().contains("leggings") && !neutral_legs){
                                continue; 
                            }
                            if(is.getType().name().toLowerCase().contains("chestplate") && !neutral_chest){
                                continue;  
                            }
                            if(is.getType().name().toLowerCase().contains("boots") && !neutral_boots){
                                continue;  
                            }
                        }
                        ply.getWorld().dropItemNaturally(ply.getLocation(), is);
                    }
                }

                if(Hive.player_mule_inventory.containsKey(ply.getName())){
                    // They have a mule in their inventory whose items need to be dropped.
                    for(ItemStack is : Hive.player_mule_inventory.get(ply.getName())){
                        if(is == null || is.getType() == Material.AIR){
                            continue;
                        }
                        ply.getWorld().dropItemNaturally(ply.getLocation(), is);
                    }
                }

                Hive.npc_inventory.remove(n);
                Hive.npc_armor.remove(n);
                Hive.player_to_npc.remove(ply.getName());
                Hive.player_to_npc_align.remove(ply.getName());
                Hive.player_item_in_hand.remove(ply.getName());
                Hive.player_mule_inventory.remove(ply.getName());
                List<Player> lpl = new ArrayList<Player>();
                for(Entity ent : n.getBukkitEntity().getNearbyEntities(32, 32, 32)){
                    if(ent instanceof Player){
                        lpl.add((Player)ent);
                    }
                }

                n.getBukkitEntity().remove();
                //ShopMechanics.updateEntity(n.getBukkitEntity(), lpl); // TODO - Need to update to latest DR Api!
            }

        }
        if(server_swap.containsKey(pl.getName())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent e){
        Player pl = e.getPlayer();
        if(server_swap.containsKey(pl.getName()) || (System.currentTimeMillis() - login_time.get(pl.getName())) <= 5000){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreProcessPrevent(PlayerCommandPreprocessEvent e){
        Player pl = e.getPlayer();
        if(server_swap.containsKey(pl.getName())){
            log.info("Cancelled command due to server_swap!");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatEvent(AsyncPlayerChatEvent e){
        Player pl = e.getPlayer();
        if(server_swap_pending.containsKey(pl.getName()) || server_swap.containsKey(pl.getName())){
            e.setCancelled(true); 
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent e){
        Player pl = (Player)e.getPlayer();
        if(server_swap.containsKey(pl.getName())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickPrevent(InventoryClickEvent e){
        Player pl = (Player)e.getWhoClicked();
        if(server_swap.containsKey(pl.getName())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        final Player pl = (Player)e.getPlayer();
        if(server_swap_pending.containsKey(pl.getName())){
            // Remove in 2 ticks.
        	Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                public void run() {
                    server_swap_pending.remove(pl.getName());
                }
            }, 2L);
        }
    }

}
