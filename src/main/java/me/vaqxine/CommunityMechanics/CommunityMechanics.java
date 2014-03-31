package me.vaqxine.CommunityMechanics;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.commands.CommandAdd;
import me.vaqxine.CommunityMechanics.commands.CommandDebug;
import me.vaqxine.CommunityMechanics.commands.CommandDelete;
import me.vaqxine.CommunityMechanics.commands.CommandIgnore;
import me.vaqxine.CommunityMechanics.commands.CommandRoll;
import me.vaqxine.CommunityMechanics.commands.CommandTips;
import me.vaqxine.CommunityMechanics.commands.CommandToggleChaos;
import me.vaqxine.CommunityMechanics.commands.CommandToggleFF;
import me.vaqxine.CommunityMechanics.commands.CommandToggleFilter;
import me.vaqxine.CommunityMechanics.commands.CommandToggleGlobal;
import me.vaqxine.CommunityMechanics.commands.CommandToggleGuild;
import me.vaqxine.CommunityMechanics.commands.CommandToggleIndicator;
import me.vaqxine.CommunityMechanics.commands.CommandToggleParty;
import me.vaqxine.CommunityMechanics.commands.CommandToggleProfile;
import me.vaqxine.CommunityMechanics.commands.CommandTogglePvP;
import me.vaqxine.CommunityMechanics.commands.CommandToggleStarterPack;
import me.vaqxine.CommunityMechanics.commands.CommandToggleTells;
import me.vaqxine.CommunityMechanics.commands.CommandToggleTips;
import me.vaqxine.CommunityMechanics.commands.CommandToggleTradeChat;
import me.vaqxine.CommunityMechanics.commands.CommandToggles;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.FatigueMechanics.FatigueMechanics;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.database.ConnectionPool;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CommunityMechanics implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");

	public static final String encryption_key = "Hzixcwi3M58539PM";

	public static HashMap<Integer, String> server_list = new HashMap<Integer, String>();
	// Server #, Server IP

	public static HashMap<String, String> last_reply = new HashMap<String, String>();
	// Stores information for /r <msg>, PLAYER_NAME, LAST_MESSAGE_RECIEVED

	public static HashMap<String, Long> last_pm = new HashMap<String, Long>();
	// Last time a PM was sent by a player. Prevents PM spam cross server and such.

	public static HashMap<String, List<String>> buddy_list = new HashMap<String, List<String>>();
	// Player_name, List of all buddy names.

	public static HashMap<String, List<String>> ignore_list = new HashMap<String, List<String>>();
	// Player_name, List of all players on ignore_list.

	public static HashMap<String, List<String>> toggle_list = new HashMap<String, List<String>>();
	// All ticked togglable options.

	public static HashMap<String, Long> local_last_login = new HashMap<String, Long>();
	// Locally cached information for the last time a player logged in, used in community book for Last Online:

	public static HashMap<String, List<String>> local_confirmed_buddies = new HashMap<String, List<String>>();
	// Player_name, A list of all players who have been queried / tested to be buddies of the player.

	public static HashMap<String, List<String>> local_confirmed_ignores = new HashMap<String, List<String>>();
	// Player_name, A list of all players who have been queried / tested to be on ignore list of the player.

	public static ConcurrentHashMap<String, Long> last_book_click = new ConcurrentHashMap<String, Long>();
	// Determines if the community book should regenerate itself again when opened.

	public static HashMap<String, Long> roll_delay = new HashMap<String, Long>();
	// Delay between using /roll -- prevents spam and abuse, currently 1s.

	public static volatile HashMap<String, Integer> player_server_num = new HashMap<String, Integer>();
	// Stores online status of players on other servers, updated through sockets.

	public static HashMap<Integer, Socket> sock_list = new HashMap<Integer, Socket>();
	// Server num, Active socket

	public static volatile ConcurrentHashMap<String, String> async_pm = new ConcurrentHashMap<String, String>();
	// PLAYER_SENDER_NAME, Full message string (EX: @Vaquxine Hey dude!)

	public static volatile CopyOnWriteArrayList<String> socket_list = new CopyOnWriteArrayList<String>(); 
	// List of all sockets to be processed in ConnectProtocl.

	public static volatile ConcurrentHashMap<String, List<Object>> social_query_list = new ConcurrentHashMap<String, List<Object>>();
	// List of all sockets to be processed in ConnectProtocl.
	
	public static List<String> ip_whitelist = new ArrayList<String>();
	
	public static List<String> toggle_map = new ArrayList<String>();
	// A map used to cycle through when generating toggle menu of /toggle.
	
	Thread ConnectProtocol;
	// Controls socket handling.
	
	Thread CrossServerPacketThread;
	// SocialQueries

	public static Team green;
	public static Team dark_green;
	public static Team yellow;
	public static Team red;
	public static Team dark_red;
	public static Team purple;
	public static Team aqua;
	public static Team white;
	public static Scoreboard board;

	public Thread message_listener;
	public static CommunityMechanics instance;
	public TipMechanics TipMechanics = new TipMechanics(this);
	 
	public static Thread PMThread;

	public void onEnable() {
		instance = this;
		
		Main.plugin.getCommand("add").setExecutor(new CommandAdd());
		Main.plugin.getCommand("debug").setExecutor(new CommandDebug());
		Main.plugin.getCommand("delete").setExecutor(new CommandDelete());
		Main.plugin.getCommand("ignore").setExecutor(new CommandIgnore());
		Main.plugin.getCommand("roll").setExecutor(new CommandRoll());
		Main.plugin.getCommand("tips").setExecutor(new CommandTips());
		Main.plugin.getCommand("togglechaos").setExecutor(new CommandToggleChaos());
		Main.plugin.getCommand("toggleff").setExecutor(new CommandToggleFF());
		Main.plugin.getCommand("togglefilter").setExecutor(new CommandToggleFilter());
		Main.plugin.getCommand("toggleglobal").setExecutor(new CommandToggleGlobal());
		Main.plugin.getCommand("toggleguild").setExecutor(new CommandToggleGuild());
		Main.plugin.getCommand("toggleparty").setExecutor(new CommandToggleParty());
		Main.plugin.getCommand("toggleprofile").setExecutor(new CommandToggleProfile());
		Main.plugin.getCommand("togglepvp").setExecutor(new CommandTogglePvP());
		Main.plugin.getCommand("toggles").setExecutor(new CommandToggles());
		Main.plugin.getCommand("togglestarterpack").setExecutor(new CommandToggleStarterPack());
		Main.plugin.getCommand("toggletells").setExecutor(new CommandToggleTells());
		Main.plugin.getCommand("toggletips").setExecutor(new CommandToggleTips());
		Main.plugin.getCommand("toggletradechat").setExecutor(new CommandToggleTradeChat());
		Main.plugin.getCommand("toggleindicator").setExecutor(new CommandToggleIndicator());
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		TipMechanics.loadTips();
		
		board = Bukkit.getScoreboardManager().getMainScoreboard();
		
		green = board.getTeam("green");
		if(green == null) green = board.registerNewTeam("green");
		green.setPrefix(ChatColor.GREEN.toString());

		dark_green = board.getTeam("dark_green");
		if(dark_green == null) dark_green = board.registerNewTeam("dark_green");
		dark_green.setPrefix(ChatColor.DARK_GREEN.toString());

		yellow = board.getTeam("yellow");
		if(yellow == null) yellow = board.registerNewTeam("yellow");
		yellow.setPrefix(ChatColor.YELLOW.toString());

		red = board.getTeam("red");
		if(red == null) red = board.registerNewTeam("red");
		red.setPrefix(ChatColor.RED.toString());

		dark_red = board.getTeam("dark_red");
		if(dark_red == null) dark_red = board.registerNewTeam("dark_red");
		dark_red.setPrefix(ChatColor.DARK_RED.toString());

		purple = board.getTeam("purple");
		if(purple == null) purple = board.registerNewTeam("purple");
		purple.setPrefix(ChatColor.LIGHT_PURPLE.toString());

		white = board.getTeam("white");
		if(white == null) white = board.registerNewTeam("white");
		white.setPrefix(ChatColor.WHITE.toString());

		aqua = board.getTeam("aqua");
		if(aqua == null) aqua = board.registerNewTeam("aqua");
		aqua.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM" + ChatColor.AQUA.toString() + " ");
		
		new BukkitRunnable(){
			@Override
			public void run() {
				processPM();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 5 * 20L, 2L);
		
		// Tip batch sender.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				TipMechanics.displayRandomTip();
			}
		}, 400 * 20L, 300 * 20L);

		server_list.put(0, "72.8.157.66");
		server_list.put(1, "72.20.38.165"); // 74..63.245.13 US-1
		server_list.put(2, "72.20.38.166"); // 74..63.245.14 US-2
		server_list.put(3, "72.8.134.149"); // US-3
		server_list.put(4, "72.8.134.150"); // US-4
		//server_list.put(5, "72.8.157.57"); // US-5
		//server_list.put(6, "72.8.157.58"); // US-6
		//server_list.put(7, "69.197.61.57"); // US-7
		//server_list.put(8, "69.197.61.58"); // US-8
		server_list.put(9, "72.20.33.81"); // US-9 (VIP)
		server_list.put(10, "72.20.33.82"); // US-10 (VIP)
		server_list.put(11, "72.20.42.197"); // US-11 (RP)

		server_list.put(2001, "72.20.42.198"); // BR-1
		
		for(String s : server_list.values()){
			ip_whitelist.add(s);
		}

		ip_whitelist.add(Hive.Proxy_IP);
		ip_whitelist.add(Hive.Hive_IP);
		ip_whitelist.add(Hive.Site_IP);
		ip_whitelist.add("72.8.157.66"); // Donation Back-end Server AND US-0
		ip_whitelist.add("72.20.9.154"); 
		ip_whitelist.add("72.20.9.158");
		ip_whitelist.add("72.8.172.242");
		ip_whitelist.add("69.197.57.158");
		ip_whitelist.add("72.20.30.14");
		ip_whitelist.add("72.8.172.254");
		ip_whitelist.add("74.63.199.162");
		
		toggle_map.add("toggledebug");
		toggle_map.add("toggleff");
		toggle_map.add("toggletrade");
		toggle_map.add("toggleduel");
		toggle_map.add("toggletells");
		toggle_map.add("toggleglobal");
		toggle_map.add("togglefilter");
		toggle_map.add("toggleparty");
		toggle_map.add("toggletradechat");
		toggle_map.add("togglechaos");
		toggle_map.add("togglepvp");
		toggle_map.add("toggletips");
		toggle_map.add("toggleprofile");
		toggle_map.add("togglestarterpack");
		toggle_map.add("toggleindicator");
		
		CrossServerPacketThread = new CrossServerPacketThread();
		CrossServerPacketThread.start();
		
		message_listener = new ListenThread();
		message_listener.start();
		
		log.info("[CommunityMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[CommunityMechanics] has been disabled.");
	}
	
	public void processPM(){
		if(CommunityMechanics.async_pm.size() <= 0){
			return;
		}
		for(Entry<String, String> data : CommunityMechanics.async_pm.entrySet()){
			String p_name = data.getKey();
			String msg = data.getValue();

			try{
				Player sent_from = Bukkit.getServer().getPlayer(p_name);

				String sent_to_s = "";
				String sent_from_s = p_name;
				if(msg.contains(" ")){
					sent_to_s = msg.substring(1, msg.indexOf(" "));
				}
				else if(!(msg.contains(" "))){
					sent_to_s = msg.substring(1, msg.length());
				}

				if(CommunityMechanics.toggle_list.get(sent_from_s).contains("tells") && (!(CommunityMechanics.isPlayerOnBuddyList(sent_from, sent_to_s)))){ // They have tells disabled and the reciever is NOT a bud, so they can't reply.
					sent_from.sendMessage(ChatColor.RED + "You currently have non-BUD private messages " + ChatColor.BOLD + "DISABLED." + ChatColor.RED + " Type '/toggletells' to re-enable.");
					continue;
				}

				if(sent_to_s.equalsIgnoreCase("")){
					if(!CommunityMechanics.last_reply.containsKey(sent_from_s)){
						sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have no conversation to respond to!");
						continue;
					}
					sent_to_s = CommunityMechanics.last_reply.get(sent_from_s);
				}

				if(!PermissionMechanics.getRank(sent_from.getName()).equalsIgnoreCase("gm") && (CommunityMechanics.ignore_list.get(sent_from_s).contains(sent_to_s) || CommunityMechanics.socialQuery(sent_from_s, sent_to_s, "CHECK_FOE"))){
					sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " is OFFLINE.");
					continue;
				}

				int sent_to_server_data = CommunityMechanics.getPlayerServer(sent_to_s, true); // Update.

				if(sent_to_server_data < 0){
					if(sent_to_server_data == -1){
						sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " is OFFLINE.");
					}
					else if(sent_to_server_data == -2){
						sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " has " + ChatColor.UNDERLINE + "NEVER" + ChatColor.RED + " logged in to Dungeon Realms.");
					}
					continue;
				}

				if(!(CommunityMechanics.server_list.containsKey(sent_to_server_data))){
					sent_from.sendMessage(ChatColor.RED + "Your message could not be delivered because the server ID " + sent_to_server_data + " is not defined.");
					continue;
				}

				//int o_sent_to_server = sent_to_server_data; // TODO - UNUSED!
				String prefix = "US-";

				if(sent_to_server_data > 1000 && sent_to_server_data < 2000){
					sent_to_server_data -= 1000;
					prefix = "EU-";
				}

				if(sent_to_server_data > 2000 && sent_to_server_data < 3000){
					sent_to_server_data -= 2000;
					prefix = "BR-";
				}


				if(sent_to_server_data >= 3000){
					sent_to_server_data -= 3000;
					prefix = "US-YT";
				}

				String sent_to_server = prefix + sent_to_server_data;
				String local_server = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
				String message = "";

				if(msg.contains(" ")){
					message = msg.substring(msg.indexOf(" "), msg.length());
				}
				else if(!(msg.contains(" "))){
					message = "";
				}

				if(Bukkit.getPlayer(sent_to_s) != null){
					// Local msg'ing.
					Player sent_to_p = Bukkit.getPlayer(sent_to_s);
					sent_to_s = sent_to_p.getName();

					CommunityMechanics.last_pm.put(sent_from_s, System.currentTimeMillis());

					if(!sent_from.isOp() && (CommunityMechanics.toggle_list.get(sent_to_p.getName()).contains("tells") && !(CommunityMechanics.isPlayerOnBuddyList(sent_to_p, sent_from_s))) || (CommunityMechanics.toggle_list.get(sent_from_s).contains("tells")  && !(CommunityMechanics.isPlayerOnBuddyList(sent_from, sent_to_p.getName())))){
						if(CommunityMechanics.toggle_list.get(sent_to_p.getName()).contains("tells")){
							sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_p.getName() + ChatColor.RED + " currently has private messaging " + ChatColor.UNDERLINE + "DISABLED.");
							continue;
						}
						continue;
					}

					CommunityMechanics.log.info(sent_from_s + " --> " + sent_to_s + " " + msg);

					ChatColor st_c = ChatMechanics.getPlayerColor(sent_from, sent_to_p);
					ChatColor sf_c = ChatMechanics.getPlayerColor(sent_to_p, sent_from);

					String from_prefix = ChatMechanics.getPlayerPrefix(sent_from);
					String to_prefix = ChatMechanics.getPlayerPrefix(sent_to_p);

					String to_personal_msg = message;
					if(ChatMechanics.hasAdultFilter(sent_to_p.getName())){
						to_personal_msg = "";
						for(String s : message.split(" ")){
							for(String bad : ChatMechanics.bad_words){
								if(s.contains(bad)){
									s = s.replaceAll(bad, "****");
								}
							}
							to_personal_msg += s + " ";
						}
					}

					String from_personal_msg = message;
					if(ChatMechanics.hasAdultFilter(sent_from_s)){
						from_personal_msg = "";
						for(String s : message.split(" ")){
							for(String bad : ChatMechanics.bad_words){
								if(s.contains(bad)){
									s = s.replaceAll(bad, "****");
								}
							}
							from_personal_msg += s + " ";
						}
					}

					sent_to_p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FROM " + from_prefix + st_c + sent_from_s + ":" + ChatColor.WHITE + to_personal_msg);
					sent_from.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "TO " + to_prefix + sf_c + sent_to_p.getName() + ":" + ChatColor.WHITE + from_personal_msg);
					if(!(CommunityMechanics.last_reply.containsKey(sent_to_p.getName())) || !CommunityMechanics.last_reply.get(sent_to_p.getName()).equalsIgnoreCase(sent_from_s)){
						sent_to_p.playSound(sent_to_p.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 1.2F);
						CommunityMechanics.last_reply.put(sent_to_p.getName(), sent_from_s);
					}
				}
				else if(!(sent_to_server.equalsIgnoreCase(local_server))){
					List<Object> query = new ArrayList<Object>();
					query.add("^" + sent_to_s + "/" + GuildMechanics.getGuildPrefix(sent_from_s) + "@" + sent_from_s + ";" + local_server + ":" + message);
					query.add(sent_to_s);
					query.add(false);
					social_query_list.put(p_name, query);
					//log.info("d1 - " + (String)query.get(0));
					//CommunityMechanics.sendMessageCrossServer(message_to_send, CommunityMechanics.server_list.get(o_sent_to_server), o_sent_to_server);
				}

				CommunityMechanics.async_pm.remove(p_name);
			} catch(Exception err){
				err.printStackTrace();
				CommunityMechanics.async_pm.remove(p_name);
				continue;
			}
		}

		CommunityMechanics.async_pm.clear();
	}

	public static void setColor(Player pl, ChatColor c){
	    if(pl == null){
	        System.out.print("Player was null! chatcolor > " + c.toString());
	        return;
	    }
		if(GuildMechanics.inGuild(pl.getName())){
			String g_prefix = GuildMechanics.guild_handle_map.get(GuildMechanics.getGuild(pl.getName()));
			Team t = null;

			String fixed_gname = g_prefix;

			if((g_prefix + ".default").length() > 16){
				// Name is too long, let's cut off from g_name.
				// .default = 8
				fixed_gname = g_prefix.substring(0, 8);
			}

			if(c == ChatColor.WHITE){
				t = board.getTeam(fixed_gname + ".default");
				if(!(t.hasPlayer(pl))){
					t.addPlayer(pl);
				}
			}

			if(c == ChatColor.RED){
				t = board.getTeam(fixed_gname + ".chaotic");
				if(!(t.hasPlayer(pl))){
					t.addPlayer(pl);
				}
			}
			if(c == ChatColor.DARK_RED){
				if(!dark_red.hasPlayer(pl)){
					dark_red.addPlayer(pl);
				}
			}
			if(c == ChatColor.YELLOW){
				t = board.getTeam(fixed_gname + ".neutral");
				if(!(t.hasPlayer(pl))){
					t.addPlayer(pl);
				}
			}
			if(c == ChatColor.GREEN){
				if(!green.hasPlayer(pl)){
					green.addPlayer(pl);
				}
			}
			if(c == ChatColor.LIGHT_PURPLE){
				if(!purple.hasPlayer(pl)){
					purple.addPlayer(pl);
				}
			}
			if(c == ChatColor.AQUA){
				t = board.getTeam(fixed_gname + ".gm");

				if(!(t.hasPlayer(pl))){
					t.addPlayer(pl);
				}
			}
			return;
		}
		else if(!(GuildMechanics.inGuild(pl.getName()))){
			if(c == ChatColor.WHITE){
				if(!white.hasPlayer(pl)){
					white.addPlayer(pl);
				}
			}
			if(c == ChatColor.RED){
				if(!red.hasPlayer(pl)){
					red.addPlayer(pl);
				}
			}
			if(c == ChatColor.DARK_RED){
				if(!dark_red.hasPlayer(pl)){
					dark_red.addPlayer(pl);
				}
			}
			if(c == ChatColor.YELLOW){
				if(!yellow.hasPlayer(pl)){
					yellow.addPlayer(pl);
				}
			}
			if(c == ChatColor.GREEN){
				if(!green.hasPlayer(pl)){
					green.addPlayer(pl);
				}
			}
			if(c == ChatColor.LIGHT_PURPLE){
				if(!purple.hasPlayer(pl)){
					purple.addPlayer(pl);
				}
			}
			if(c == ChatColor.AQUA){
				if(!aqua.hasPlayer(pl)){
					aqua.addPlayer(pl);
				}
			}
		}
	}

	public static int getPlayerServer(String p_name, boolean refresh){
		p_name = StringEscapeUtils.escapeSql(p_name);
		// Prevents SQL injection.
		
		return Hive.getPlayerServer(p_name, refresh);
	}

	public static int getPlayerServer(String p_name){
		p_name = StringEscapeUtils.escapeSql(p_name);
		// Prevents SQL injection.

		return Hive.getPlayerServer(p_name, true);
	}

	public static boolean isPlayerOnline(String p_name){
		int server_num = getPlayerServer(p_name);
		if(server_num < 0){return false;}
		else{
			return true;
		}
	}

	public static boolean isPlayerOnline(String p_name, boolean refresh){
		int server_num = getPlayerServer(p_name, refresh);
		if(server_num < 0){return false;}
		else{
			return true;
		}
	}

	/*
	 * Only updates the first few pages of the character journal, the pages with realtime, non-SQL/socket based information.
	 * */
	@SuppressWarnings("deprecation")
	public static void updateCombatPage(Player p){
		List<String> new_pages = new ArrayList<String>();
		int slot = -1;
		if(p.getInventory().contains(Material.WRITTEN_BOOK)){
			for(Entry<Integer, ? extends ItemStack> data : p.getInventory().all(Material.WRITTEN_BOOK).entrySet()){
				ItemStack is = data.getValue();
				if(isSocialBook(is)){
					slot = data.getKey();
					break;
				}
			}
		}

		if(slot != -1){
			ItemStack journal = p.getInventory().getItem(slot);

			BookMeta bm = (BookMeta) journal.getItemMeta();
			List<String> pages = bm.getPages();
			String page1_string = "";
			String page2_string = "";
			String page3_string = "";
			String page4_string = "";
			String page5_string = "";
			
			String new_line = "\n" + ChatColor.WHITE.toString() + "`" + "\n";
			String pretty_align = KarmaMechanics.getAlignment(p.getName());
			String raw_align = KarmaMechanics.align_map.get(p.getName());
			String align_descrip = KarmaMechanics.getAlignmentDescription(raw_align);
			String align_expire_message = "";

			if(!KarmaMechanics.align_map.containsKey(p.getName())){
				raw_align = "good";
				align_expire_message = "";
			}

			if(raw_align.equalsIgnoreCase("neutral")){
				align_expire_message = "\n" + ChatColor.BLACK.toString() +  ChatColor.BOLD.toString() + "Lawful" + ChatColor.BLACK.toString() + " in " + KarmaMechanics.getSecondsUntilAlignmentChange(p.getName()) + "s";
			}

			if(raw_align.equalsIgnoreCase("evil")){
				align_expire_message = "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "Neutral" + ChatColor.BLACK.toString() + " in " + KarmaMechanics.getSecondsUntilAlignmentChange(p.getName()) + "s";
			}

			DecimalFormat df = new DecimalFormat("#.##");

			String money_space = "";
			if(align_expire_message.length() > 0){
				money_space = new_line;
			}
			else{
				money_space = new_line;
			}

			String gold_find = "1.00";
			if(ItemMechanics.gfind_data.containsKey(p.getName())){
				gold_find = (df.format((((double)ItemMechanics.gfind_data.get(p.getName())) / 100.0D) + 1.0D));
				if(gold_find.equalsIgnoreCase("1")){
					gold_find = "1.00";
				}

				String item_find = "1.00";
				if(ItemMechanics.ifind_data.containsKey(p.getName())){
					item_find = (df.format((((double)ItemMechanics.ifind_data.get(p.getName())) / 100.0D) + 1.0D));
				}
				if(item_find.equalsIgnoreCase("1")){
					item_find = "1.00";
				}

				page1_string = ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "  Your Character" + "   "
						+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "Alignment: " + pretty_align + align_expire_message
						+ "\n" + ChatColor.BLACK.toString() + align_descrip
						+ new_line+ ChatColor.BLACK.toString() + "   " + HealthMechanics.getPlayerHP(p.getName()) + " / " + HealthMechanics.health_data.get(p.getName()) + "" + ChatColor.BOLD.toString() + " HP"
						+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.armor_data.get(p.getName()).get(0) + " - " + ItemMechanics.armor_data.get(p.getName()).get(1) + "% " + ChatColor.BOLD.toString() + "Armor"
						+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.dmg_data.get(p.getName()).get(0) + " - " + ItemMechanics.dmg_data.get(p.getName()).get(1) + "% " + ChatColor.BOLD.toString() + "DPS"
						+ "\n" + ChatColor.BLACK.toString() + "   " + (HealthMechanics.health_regen_data.get((p.getName())) + 5) + " " + ChatColor.BOLD.toString() + "HP/s"
						+ "\n" + ChatColor.BLACK.toString() + "   " + df.format((((double)FatigueMechanics.energy_regen_data.get((p.getName()))) * 100.0D) + 90.0D) + "% " + ChatColor.BOLD.toString() + "Energy"
						+ "\n" + ChatColor.BLACK.toString() + "   " + gold_find + "x " + ChatColor.BOLD.toString() + "Gem Find"
						+ "\n" + ChatColor.BLACK.toString() + "   " + item_find + "x " + ChatColor.BOLD.toString() + "Item Find"
						+ money_space + ChatColor.BLACK.toString() + "" + Hive.player_ecash.get(p.getName()) + " " + ChatColor.BOLD.toString() + "E-CASH";


				page2_string = "\n" 
						+ ChatColor.BLACK.toString() + "   " + ItemMechanics.fire_res_data.get(p.getName()) + "% " + ChatColor.BOLD.toString() + "Fire Resist"
						+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.ice_res_data.get(p.getName()) + "% " + ChatColor.BOLD.toString() + "Ice Resist" 
						+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.poison_res_data.get(p.getName()) + "% " + ChatColor.BOLD.toString() + "Poison Resist"
						+ new_line + ChatColor.BLACK.toString() + "Monsters that deal" + "\n" + "elemental damage will" + "\n" + "ignore 80% of your" + "\n" + "ARMOR."
						+ new_line + ChatColor.BLACK.toString() + "Fire, Ice, and Poison" + "\n" + "resistances will take" + "\n" + "the place of your" + "\n" + "ARMOR vs. elements.";


				int str_val = ItemMechanics.str_data.get(p.getName());
				int dex_val = ItemMechanics.dex_data.get(p.getName());
				int vit_val = ItemMechanics.vit_data.get(p.getName());
				int int_val = ItemMechanics.int_data.get(p.getName());

				page3_string = 
						ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "+ " + str_val + " Strength"
								+ "\n" + ChatColor.BLACK.toString() + "   " + ChatColor.UNDERLINE.toString() + "'The Warrior'"
								+ new_line 
								+ ChatColor.BLACK.toString() + "+" + df.format(str_val * 0.01) + "% Armor"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(str_val * 0.015) + "% Axe DMG"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(str_val * 0.02) + "% Polearm DMG"
								+ "\n" 
								+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "+ " + dex_val + " Dexterity"
								+ "\n" + ChatColor.BLACK.toString() + "   " + ChatColor.UNDERLINE.toString() + "'The Archer'"
								+ new_line
								+ ChatColor.BLACK.toString() + "+" + df.format(dex_val * 0.03) + "% Dodge"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(dex_val * 0.015) + "% Bow DMG"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(dex_val * 0.005) + "% Critical Hit"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(dex_val * 0.009) + "% Armor Pen.";
				
				page4_string = 
						ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "+ " + vit_val + " Vitality"
						+ "\n" + ChatColor.BLACK.toString() + "   " + ChatColor.UNDERLINE.toString() + "'The Defender'"
						+ new_line
								+ ChatColor.BLACK.toString() + "+" + df.format(vit_val * 0.05) + "% Health"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(vit_val * 0.015) + "% Sword DMG"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(vit_val * 0.03) + "% Block"
								+ "\n"
								+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "+ " + int_val + " Intellect"
								+ "\n" + ChatColor.BLACK.toString() + "   " + ChatColor.UNDERLINE.toString() + "'The Mage'"
								+ new_line
								+ ChatColor.BLACK.toString() + "+" + df.format(int_val * 0.015) + "% Energy"
								+ "\n" + ChatColor.BLACK.toString() + "+" + df.format(int_val * 0.02) + "% Staff DMG";

				page5_string =
						ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "Portal Key Shards"
						+ "\n" + ChatColor.BLACK.toString() + ChatColor.ITALIC.toString() + "A sharded fragment from the great portal of Maltai that may be exchanged at the Dungeoneer for epic equipment."
						+ new_line + ChatColor.DARK_GRAY.toString() + "Portal Shards: " + ChatColor.BLACK + InstanceMechanics.getPortalShardCount(p.getName(), 1)
						+ "\n" + ChatColor.GREEN.toString() + "Portal Shards: " + ChatColor.BLACK + InstanceMechanics.getPortalShardCount(p.getName(), 2)
						+ "\n" + ChatColor.AQUA.toString() + "Portal Shards: "  + ChatColor.BLACK + InstanceMechanics.getPortalShardCount(p.getName(), 3)
						+ "\n" + ChatColor.LIGHT_PURPLE.toString() + "Portal Shards: " + ChatColor.BLACK + InstanceMechanics.getPortalShardCount(p.getName(), 4)
						+ "\n" + ChatColor.GOLD.toString() + "Portal Shards: " + ChatColor.BLACK + InstanceMechanics.getPortalShardCount(p.getName(), 5);
				
				new_pages.add(page1_string);
				new_pages.add(page2_string);
				new_pages.add(page3_string);
				new_pages.add(page4_string);
				new_pages.add(page5_string);
				
				for(String s : pages){
					if(!s.contains("Your Character") && !s.startsWith(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "+ ") && !(s.contains("Monsters that deal")) && !(s.contains("Portal Key Shards"))){
						new_pages.add(s);
					}
				}

				bm.setPages(new_pages);
				journal.setItemMeta(bm);
				p.getInventory().setItem(slot, journal);
				p.updateInventory();
			}

			if(slot == -1){ // They don't even have a book.
				if(p.getInventory().getItem(8) == null || p.getInventory().getItem(8).getType() == Material.AIR){
					p.getInventory().setItem(8, generateCommBook(p));
				}
				else{
					p.getInventory().setItem(p.getInventory().firstEmpty(), generateCommBook(p));
				}
			}
		}
	}

	/*
	 * Generates a full character journal. -- updateCombatPage() will run after this everytime the book opens to insert those pages.
	 * */
	public static ItemStack generateCommBook(final Player p){
		if(p == null || !p.isOnline()){
			return null;
		}
		String new_line = "\n" + ChatColor.WHITE.toString() + "`" + "\n";
		ItemStack i = new ItemStack(Material.WRITTEN_BOOK, 1);
		List<String> the_pages = new ArrayList<String>();
		List<String> lbuddy_list = buddy_list.get(p.getName());
		List<String> lignore_list = ignore_list.get(p.getName());

		int buddies_printed = 0;
		boolean first_buddy_page = true;

		String pretty_align = KarmaMechanics.getAlignment(p.getName());
		String raw_align = KarmaMechanics.align_map.get(p.getName());
		String align_descrip = KarmaMechanics.getAlignmentDescription(raw_align);
		String align_expire_message = "";

		if(!KarmaMechanics.align_map.containsKey(p.getName())){
			raw_align = "good";
			align_expire_message = "";
		}

		if(raw_align.equalsIgnoreCase("neutral")){
			align_expire_message = "\n" + ChatColor.BLACK.toString() +  ChatColor.BOLD.toString() + "Lawful" + ChatColor.BLACK.toString() + " in " + KarmaMechanics.getSecondsUntilAlignmentChange(p.getName()) + "s";
		}

		if(raw_align.equalsIgnoreCase("evil")){
			align_expire_message = "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "Neutral" + ChatColor.BLACK.toString() + " in " + KarmaMechanics.getSecondsUntilAlignmentChange(p.getName()) + "s";
		}

		DecimalFormat df = new DecimalFormat("#.##");

		String money_space = "";
		if(align_expire_message.length() > 0){
			money_space = new_line;
		}
		else{
			money_space = new_line;
		}

		String gold_find = "1.00";
		if(ItemMechanics.gfind_data.containsKey(p.getName())){
			gold_find = (df.format((((double)ItemMechanics.gfind_data.get(p.getName())) / 100.0D) + 1.0D));
			if(gold_find.equalsIgnoreCase("1")){
				gold_find = "1.00";
			}
		}

		String item_find = "1.00";
		if(ItemMechanics.ifind_data.containsKey(p.getName())){
			item_find = (df.format((((double)ItemMechanics.ifind_data.get(p.getName())) / 100.0D) + 1.0D));
		}
		if(item_find.equalsIgnoreCase("1")){
			item_find = "1.00";
		}

		try{
			the_pages.add(ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "  Your Character" + "   "
					+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "Alignment: " + pretty_align + align_expire_message
					+ "\n" + ChatColor.BLACK.toString() + align_descrip
					+ new_line+ ChatColor.BLACK.toString() + "   " + HealthMechanics.getPlayerHP(p.getName()) + " / " + HealthMechanics.health_data.get(p.getName()) + "" + ChatColor.BOLD.toString() + " HP"
					+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.armor_data.get(p.getName()).get(0) + " - " + ItemMechanics.armor_data.get(p.getName()).get(1) + "% " + ChatColor.BOLD.toString() + "Armor"
					+ "\n" + ChatColor.BLACK.toString() + "   " + ItemMechanics.dmg_data.get(p.getName()).get(0) + " - " + ItemMechanics.dmg_data.get(p.getName()).get(1) + "% " + ChatColor.BOLD.toString() + "DPS"
					+ "\n" + ChatColor.BLACK.toString() + "   " + (HealthMechanics.health_regen_data.get((p.getName())) + 5) + " " + ChatColor.BOLD.toString() + "HP/s"
					+ "\n" + ChatColor.BLACK.toString() + "   " + df.format((((double)FatigueMechanics.energy_regen_data.get((p.getName()))) * 100.0D) + 90.0D) + "% " + ChatColor.BOLD.toString() + "Energy"
					+ "\n" + ChatColor.BLACK.toString() + "   " + gold_find + "x " + ChatColor.BOLD.toString() + "Gem Find"
					+ "\n" + ChatColor.BLACK.toString() + "   " + item_find + "x " + ChatColor.BOLD.toString() + "Item Find"
					+ money_space + ChatColor.BLACK.toString() + "" + Hive.player_ecash.get(p.getName()) + " " + ChatColor.BOLD.toString() + "E-CASH");
			//+ RealmMechanics.getMoneyInInventory(p) + " / " + (MoneyMechanics.bank_map.get(p.getName()) + RealmMechanics.getMoneyInInventory(p)) + ChatColor.BOLD.toString() + "G");
		} catch(NullPointerException npe){
			log.info("[CommunityMechanics] Failed to give book to " + p.getName());
			npe.printStackTrace();
			return null;
		}

		if(lbuddy_list == null || lbuddy_list.size() == 0 || !(buddy_list.containsKey(p.getName()))){
			the_pages.add(ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "     Buddy List" + "      "
					+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "@<PLAYER> <MSG>"
					+ "\n" + ChatColor.BLACK.toString() + "Sends <MSG> to <PLAYER>."
					+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/add <PLAYER>"
					+ "\n" + ChatColor.BLACK.toString() + "Adds PLAYER to buddy list."
					+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/delete <PLAYER>"
					+ "\n" + ChatColor.BLACK.toString() + "Deletes PLAYER from all lists.");
		}

		if(!(lbuddy_list == null)){
			List<String> format_buddy_list = new ArrayList<String>();

			for(String s : lbuddy_list){
				s = ChatColor.stripColor(s);
				String online_mark = "";
				//Boolean is_buddy = socialQuery(p.getName(), s, "CHECK_BUD");
				boolean online = isPlayerOnline(s);

				if(online){ // is_buddy && 
					online_mark = ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + "O";
				}
				else if(!(online)){
					online_mark = ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "O";
				}

				int server_num = getPlayerServer(s); //TODO: See if this is causing SQL bottleneck.

				OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(s);
				
				if(server_num >= 0 && !op.isOp()){
					
					String prefix = "US-";

					if(server_num > 1000){
						server_num -= 1000;
						prefix = "EU-";
					}

					if(server_num > 2000){
						server_num -= 2000;
						prefix = "BR-";
					}

					if(server_num >= 3000){
						server_num -= 3000;
						prefix = "US-YT";
					}

					String remote_server = prefix + server_num;
					
					format_buddy_list.add(ChatColor.BLACK.toString() + "" + online_mark + ChatColor.BLACK.toString() + " " + ChatColor.BOLD.toString() + s + "\n" 
							+ ChatColor.BLACK.toString() + "Shard: " + remote_server + new_line);
				}
			}

			for(String s : lbuddy_list){
				s = ChatColor.stripColor(s);
				String online_mark = "";
				online_mark = ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "O";

				int server_num = getPlayerServer(s); //TODO: See if this is causing SQL bottleneck.

				OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(s);
				
				if(server_num == -1 || op.isOp()){
					Long last_login = getLastLogin(s, false); //TODO: Make this time save locally or have it DL database at start
					String release_s = ""; 

					if(last_login == -1L || op.isOp()){
						release_s = "NEVER";
					}
					else{
						Date startTime, endTime;
						endTime = new Date(System.currentTimeMillis());
						startTime = new Date(last_login);

						long sec;
						sec = (endTime.getTime() - startTime.getTime()) / 1000;
						int hour = (int) (sec / 3600);    
						sec = sec % 3600;
						int min = (int) (sec / 60);
						sec = sec % 60;

						if(min <= 0){
							release_s = sec + "s ago";
						}
						else if(release_s == "" && (min > 0 && min <= 60) && hour <= 0){
							release_s = min + "m ago";
						}
						else if(release_s == "" && (hour > 0 && hour <= 24)){
							release_s = hour + "h ago";
						}
						else if(release_s == ""){
							release_s = String.valueOf(Math.round((hour / 24))) + "d ago";
						}
					}

					format_buddy_list.add(ChatColor.BLACK.toString() + "" + online_mark + ChatColor.BLACK.toString() + " " + ChatColor.BOLD.toString() + s + "\n" 
							+ ChatColor.BLACK.toString() + "Last On: " + release_s + new_line);

					/*page_structure += ChatColor.BLACK.toString() + "" + online_mark + ChatColor.BLACK.toString() + " " + ChatColor.BOLD.toString() + s + "\n" 
							+ ChatColor.BLACK.toString() + "Last On: " + release_s + new_line;
					being_added_to_book.remove(s);*/
				}
			}

			for(String s : lbuddy_list){
				s = ChatColor.stripColor(s);
				String online_mark = "";
				online_mark = ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "O";

				int server_num = getPlayerServer(s); //TODO: See if this is causing SQL bottleneck.

				if(server_num == -2){
					format_buddy_list.add( ChatColor.BLACK.toString() + "" + online_mark + ChatColor.BLACK.toString() + " " + ChatColor.BOLD.toString() + s + "\n" 
							+ ChatColor.BLACK.toString() + "Last On: " + "NEVER" + new_line);

					/*page_structure += ChatColor.BLACK.toString() + "" + online_mark + ChatColor.BLACK.toString() + " " + ChatColor.BOLD.toString() + s + "\n" 
							+ ChatColor.BLACK.toString() + "Last On: " + "NEVER" + new_line;
					being_added_to_book.remove(s);*/
				}
			}

			int attempts = 50;
			while(format_buddy_list.size() > buddies_printed && attempts > 0){
				attempts--;
				int buddies_to_print = 5;

				if(first_buddy_page == true){
					buddies_to_print = 4;
				}

				if((buddies_printed + buddies_to_print) > lbuddy_list.size()){
					buddies_to_print =  lbuddy_list.size() - buddies_printed;
					// Less than 5 buddies left to add. So we just add one final page.
				}

				CopyOnWriteArrayList<String> being_added_to_book = new CopyOnWriteArrayList<String>(format_buddy_list.subList(buddies_printed, (buddies_printed + buddies_to_print)));
				String page_structure= "";

				if(first_buddy_page == true){
					page_structure += ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "     Buddy List" + "      "
							+ "\n" + ChatColor.BLACK.toString();
				}

				for(String s : being_added_to_book){
					page_structure += s;
				}

				the_pages.add(page_structure);
				buddies_printed += buddies_to_print;
				first_buddy_page = false;
			}
		}


		if(lignore_list == null || lignore_list.size() == 0 || !(ignore_list.containsKey(p.getName()))){
			the_pages.add(ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "    Ignore List" + "     "
					+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/ignore <PLAYER>"
					+ "\n" + ChatColor.BLACK.toString() + "Adds PLAYER to ignore list."
					+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/delete <PLAYER>"
					+ "\n" + ChatColor.BLACK.toString() + "Deletes PLAYER from all lists.");
		}

		if(lignore_list != null){	
			int ignores_printed = 0;
			boolean first_ignore_page = true;

			while(lignore_list.size() > ignores_printed){
				int ignores_to_print = 13;

				if(first_ignore_page == true){
					ignores_to_print = 11;
				}

				if((ignores_printed + ignores_to_print) > lignore_list.size()){
					ignores_to_print =  lignore_list.size() - ignores_printed;
				}
				// Less than 5 ignores left to add. So we just add one final page.
				List<String> being_added_to_book = lignore_list.subList(ignores_printed, (ignores_printed + ignores_to_print));
				String page_structure= "";

				if(first_ignore_page == true){
					page_structure += ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "    Ignore List" + "     "
							+ new_line + ChatColor.BLACK.toString();
					first_ignore_page = false;
				}

				for(String s : being_added_to_book){
					page_structure += ChatColor.BLACK.toString() + s + "\n";
				}

				the_pages.add(page_structure);
				ignores_printed += ignores_to_print;
			}
		}

		the_pages.add(ChatColor.BLACK.toString() + "" + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "   Command Guide  "
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "@<PLAYER> <MSG>"
				+ "\n" + ChatColor.BLACK.toString() + "Sends a PM."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/shard"
				+ "\n" + ChatColor.BLACK.toString() + "Opens game shard selection menu."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD + "Press TAB (CHAT)"
				+ "\n" + ChatColor.BLACK.toString() + "Sends MESSAGE typed to all players on the shard. ");


		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD + "/realm" + ChatColor.BLACK.toString() + " <TITLE>"
				+ "\n" + ChatColor.BLACK.toString() + "Sets your realm description to TITLE."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/add " + ChatColor.BLACK.toString() + "<PLAYER>"
				+ "\n" + ChatColor.BLACK.toString() + "Adds PLAYER to buddy list."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/ignore " + ChatColor.BLACK.toString() + "<PLAYER>"
				+ "\n" + ChatColor.BLACK.toString() + "Adds PLAYER to ignore list."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/delete " + ChatColor.BLACK.toString() + "<PLAYER>"
				+ "\n" + ChatColor.BLACK.toString() + "Deletes PLAYER from           all lists.");

		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/[p/g]invite"
				+ "\n" + ChatColor.BLACK.toString() + "Invite to p(arty) or g(uild)" //"               "
				+ new_line + ChatColor.BLACK + ChatColor.BOLD.toString() + "/[p/g]kick " 
				+ "\n" + ChatColor.BLACK.toString() + "Kick player from p(arty) or g(uild)"
				+ new_line + ChatColor.BLACK + ChatColor.BOLD.toString() + "/[p/g]quit " 
				+ "\n" + ChatColor.BLACK.toString() + "Leave your p(arty) or g(uild)"
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/[p/g]promote " 
				+ "\n" + ChatColor.BLACK.toString() + "Set to p(arty) leader         g(uild) officer"); // " + "            " + "party leader"

		/*the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggledebug"
				+ "\n" + ChatColor.BLACK.toString() + "Toggles debug messages." //"               "
				+ new_line + ChatColor.BLACK + ChatColor.BOLD.toString() + "/toggleff " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles friendly fire against buddies."
				+ new_line + ChatColor.BLACK + ChatColor.BOLD.toString() + "/toggletrade " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles accepting player trades."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggleduel " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles accepting" + "        " + "     duels");

		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggletells " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles Non-BUD private messages."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggleglobal " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles Global Chat messaging."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/togglefilter " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles adult chat filter."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggleparty " 
				+ "\n" + ChatColor.BLACK.toString() + "Toggles Non-BUD              party invites");*/

		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/toggles " 
				+ "\n" + ChatColor.BLACK.toString() + "Display Toggle Menu."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/roll " 
				+ "\n" + ChatColor.BLACK.toString() + "Rolls a random number."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/logout " 
				+ "\n" + ChatColor.BLACK.toString() + "Safetly logs out your character."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/report " 
				+ "\n" + ChatColor.BLACK.toString() + "Submit a ticket to the staff.");
		
		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/biography " 
				+ "\n" + ChatColor.BLACK.toString() + "Write your player bio."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/gbiography " 
				+ "\n" + ChatColor.BLACK.toString() + "Write your guild bio."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/gbanner " 
				+ "\n" + ChatColor.BLACK.toString() + "Upload your guild banner."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/suicide " 
				+ "\n" + ChatColor.BLACK.toString() + "KILLS your character.");

		the_pages.add(ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/reboot " 
				+ "\n" + ChatColor.BLACK.toString() + "Time until next scheduled reboot."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/profile " 
				+ "\n" + ChatColor.BLACK.toString() + "Displays link to player profile."
				+ new_line + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/ecash " 
				+ "\n" + ChatColor.BLACK.toString() + "Opens E-CASH Vendor."
				+ new_line + ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "      ! CAUTION !"
				+ "\n" + ChatColor.BLACK.toString() + ChatColor.BOLD.toString() + "/resetrealm " 
				+ "\n" + ChatColor.BLACK.toString() + "Resets your player owned realm. ");


		BookMeta bm = (BookMeta) i.getItemMeta();

		bm.setAuthor("");
		bm.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY.toString() + "A book that displays", ChatColor.GRAY.toString() + "your character's data", ChatColor.GREEN.toString() + "Left Click: " + ChatColor.GRAY + "Invite to Party", ChatColor.GREEN.toString() + "Sneak-Left Click:" + ChatColor.GRAY + " Setup Shop")));
		bm.setTitle(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Character Journal");
		bm.setPages(the_pages);

		i.setItemMeta(bm);

		return i;
	}

	public static long getLastLogin(String p_name, boolean set){
		if(local_last_login.containsKey(p_name)){
			return local_last_login.get(p_name);
		}

		Connection con = null;
		PreparedStatement pst = null;

		try {

			pst = ConnectionPool.getConnection().prepareStatement("SELECT last_login_time FROM player_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){
				return -1L;
			}
			long amount = rs.getLong("last_login_time");
			local_last_login.put(p_name, amount);
			return amount;

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

		return -1L;
	}


	public static boolean isSocialBook(ItemStack i) {
		try{
			if (i.getType() == Material.WRITTEN_BOOK && hasTitle(i)) {
				String fake_var = CraftItemStack.asNMSCopy(i).getTag().getString("title");
				if(fake_var.equalsIgnoreCase(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Character Journal")){
					return true;
				}
			}
		}catch (Exception e){
			return false;
		}

		return false;
	}

	public static boolean hasTitle(ItemStack i) {
		try {
			try {
				String fake_var = CraftItemStack.asNMSCopy(i).getTag().getString("title");
				if (fake_var != null && fake_var.length() > 0) {
					return true;
				}
			} catch (NullPointerException npe) {
				return false;
			}

		} catch (ClassCastException cce) {
			return false;
		}

		return false;
	}

	@SuppressWarnings("resource")
	public static void upload_social_lists(Player p){
		Connection con = null;
		PreparedStatement pst = null;

		if(!(buddy_list.containsKey(p.getName())) && !(ignore_list.containsKey(p.getName()))){
			return;
		}

		String buddy_list_string = "";
		String ignore_list_string = "";

		if(buddy_list.containsKey(p.getName())){
			List<String> lbuddy_list = buddy_list.get(p.getName());
			for(String s : lbuddy_list){
				buddy_list_string += s + ",";
			}
		}

		if(ignore_list.containsKey(p.getName())){
			List<String> lignore_list = ignore_list.get(p.getName());
			for(String s : lignore_list){
				ignore_list_string += s + ",";
			}

		}
		try {
			if(buddy_list.containsKey(p.getName())){
				pst = ConnectionPool.getConnection().prepareStatement( 
						"INSERT INTO player_database (p_name, buddy_list)"
								+ " VALUES"
								+ "('"+ p.getName() + "', '"+ StringEscapeUtils.escapeSql(buddy_list_string) +"') ON DUPLICATE KEY UPDATE buddy_list='" + StringEscapeUtils.escapeSql(buddy_list_string) + "'");

				pst.executeUpdate();
			}

			if(ignore_list.containsKey(p.getName())){
				pst = ConnectionPool.getConnection().prepareStatement( 
						"INSERT INTO player_database (p_name, ignore_list)"
								+ " VALUES"
								+ "('"+ p.getName() + "', '"+ StringEscapeUtils.escapeSql(ignore_list_string) +"') ON DUPLICATE KEY UPDATE ignore_list='" + StringEscapeUtils.escapeSql(ignore_list_string) + "'");

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
	}


	public static List<String> query_buddy_list(String p_name){
		Connection con = null;
		PreparedStatement pst = null;
		List<String> lbuddy_list = new ArrayList<String>();

		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			pst = ConnectionPool.getConnection().prepareStatement( 
					"SELECT buddy_List FROM player_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){
				//buddy_list.put(p.getName(), new ArrayList<String>());
				return null;
			}
			String result_set = rs.getString("friends");

			for(String s : result_set.split(",")){
				if(s.length() > 0){
					lbuddy_list.add(s);
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

		return lbuddy_list;
	}

	public static boolean isPlayerOnBuddyList(Player p, String p_to_check_name){
		if(!(buddy_list.containsKey(p.getName())) || buddy_list.get(p.getName()) == null){return false;}
		for(String s : buddy_list.get(p.getName())){
			if(s.equalsIgnoreCase(p_to_check_name)){
				return true;
			}
		}
		return false;
	}

	public static boolean isPlayerOnBuddyList(String p_name, String p_to_check_name){
		if(!(buddy_list.containsKey(p_name))){return false;}
		for(String s : buddy_list.get(p_name)){
			if(s.equalsIgnoreCase(p_to_check_name)){
				return true;
			}
		}
		return false;
	}

	public static boolean isPlayerOnIgnoreList(Player p, String p_to_check_name){
		if(!(ignore_list.containsKey(p.getName()))){return false;}
		for(String s : ignore_list.get(p.getName())){
			if(s.equalsIgnoreCase(p_to_check_name)){
				return true;
			}
		}
		return false;
	}

	public static boolean socialQuery(final String local_player, final String remote_player, final String meta_data){

		/*
		 * CHECK_BUD = Check if remote_player has local_player on their friends list. TRUE=yes, FALSE=no
		 * CHECK_FOE = Check if remote_player has local_player on their ignore list. TRUE=yes, FALSE=no
		 *
		 * ONLINE = Check if remote_player has local_player on their friends list AND if they do, tell them that local_player has logged in. TRUE=yes, FALSE=no
		 * OFFLINE = Check if remote_player has local_player on their friends list AND if they do, tell them that local_player has logged out. TRUE=yes, FALSE=no
		 * 
		 * */
		

		OfflinePlayer op = Bukkit.getOfflinePlayer(local_player);
		
		if(op.isOp() && meta_data.equalsIgnoreCase("CHECK_BUD")){
			return false;
		}
		
		if(local_confirmed_buddies.containsKey(local_player) && (meta_data.equalsIgnoreCase("CHECK_BUD"))){
			if(local_confirmed_buddies.get(local_player).contains(remote_player)){
				return true;
			}
		}

		if(local_confirmed_ignores.containsKey(local_player) && (meta_data.equalsIgnoreCase("CHECK_FOE"))){
			if(local_confirmed_ignores.get(local_player).contains(remote_player)){
				return true;
			}
		}

		if(remote_player != null && Bukkit.getPlayer(remote_player) != null){
			// They're on the same server.
			Player p_check = Bukkit.getPlayer(remote_player);
			if(p_check == null){
				return false;
			}

			if(meta_data.equalsIgnoreCase("CHECK_BUD")){
				if(!buddy_list.containsKey(p_check.getName())){return false;}
				for(String s : buddy_list.get(p_check.getName())){
					if(s.equalsIgnoreCase(local_player)){
						List<String> confirmed_buds = local_confirmed_buddies.get(local_player);
						confirmed_buds.add(p_check.getName());
						local_confirmed_buddies.put(local_player, confirmed_buds);
						return true;
					}
				}
			}


			if(meta_data.equalsIgnoreCase("CHECK_FOE")){
				if(!ignore_list.containsKey(p_check.getName())){return false;}
				if(ignore_list.get(p_check.getName()).contains(local_player)){
					for(String s : ignore_list.get(p_check.getName())){
						if(s.equalsIgnoreCase(local_player)){
							List<String> confirmed_foes = local_confirmed_ignores.get(local_player);
							confirmed_foes.add(p_check.getName());
							local_confirmed_ignores.put(local_player, confirmed_foes);
							return true;
						}
					}
				}
			}

			// If it's ONLINE/OFFLINE query, then nothing needs to happen and this can return false, because it would of already taken care of the request on PlayerJoin().
			return false;
		}

		if(!(isPlayerOnline(remote_player))){
			if(meta_data.equalsIgnoreCase("CHECK_BUD")){
				return true;
			}
			if(meta_data.equalsIgnoreCase("CHECK_FOE")){
				return false; // They don't need to be enemies if they're offline.
			}
		}


		/*
		 * TODO: Add cross-server support for bud/foe CHECKING
		 * */

		if(meta_data.equalsIgnoreCase("CHECK_BUD")){
			return true;
		}
		if(meta_data.equalsIgnoreCase("CHECK_FOE")){
			return false;
		}

		if(meta_data.equalsIgnoreCase("ONLINE")){
			List<Object> query = new ArrayList<Object>();
			query.add("[sq_online]" + local_player);
			query.add(remote_player);
			
			query.add(false);
			social_query_list.put(local_player, query);
			//sendPacketCrossServer("[sq_online]" + local_player, getPlayerServer(remote_player), false);
		}
		if(meta_data.equalsIgnoreCase("OFFLINE")){
			List<Object> query = new ArrayList<Object>();
			query.add("[sq_offline]" + local_player);
			query.add(remote_player);
			query.add(false);
			social_query_list.put(local_player, query);
			//sendPacketCrossServer("[sq_offline]" + local_player, getPlayerServer(remote_player), false);
		}

		/*Thread social_query = new Thread(new Runnable() {
			public void run() {
				if(meta_data.equalsIgnoreCase("ONLINE")){
					sendPacketCrossServer("[sq_online]" + local_player, getPlayerServer(remote_player), false);
				}
				if(meta_data.equalsIgnoreCase("OFFLINE")){
					sendPacketCrossServer("[sq_offline]" + local_player, getPlayerServer(remote_player), false);
				}
			}
		});

		social_query.start();*/
		return true;
	}


	public static void addBuddy(Player host, String new_friend_name){
		List<String> cur_list = new ArrayList<String>();
		if(buddy_list.containsKey(host.getName())){
			cur_list = buddy_list.get(host.getName());
		}

		cur_list.add(new_friend_name);
		buddy_list.put(host.getName(), cur_list);

		upload_social_lists(host);
	}

	public static void addIgnore(Player host, String new_friend_name){
		//boolean first_add = false;
		List<String> cur_list = new ArrayList<String>();
		if(ignore_list.containsKey(host.getName())){
			cur_list = ignore_list.get(host.getName());
		}

		cur_list.add(new_friend_name);
		ignore_list.put(host.getName(), cur_list);

		upload_social_lists(host);
	}

	public static void deleteFromAllLists(Player host, String to_remove){

		if(isPlayerOnBuddyList(host, to_remove)){
			List<String> cur_list = buddy_list.get(host.getName());
			String save_s = "";
			for(String s : cur_list){
				if(s.equalsIgnoreCase(to_remove)){
					save_s = s;
					break;
				}
			}

			if(save_s.length() > 0){
				cur_list.remove(save_s);
				buddy_list.put(host.getName(), cur_list);
				host.sendMessage(ChatColor.YELLOW + "" + save_s + ChatColor.YELLOW + " has been removed from your BUDDY list.");
				return; // We can return, if they were on buddy list they won't be on ignore.
			}
		}

		if(isPlayerOnIgnoreList(host, to_remove)){
			List<String> cur_list = ignore_list.get(host.getName());
			String save_s = "";
			for(String s : cur_list){
				if(s.equalsIgnoreCase(to_remove)){
					save_s = s;
					break;
				}
			}

			if(save_s.length() > 0){
				cur_list.remove(save_s);
				ignore_list.put(host.getName(), cur_list);
				host.sendMessage(ChatColor.YELLOW + "" + save_s + ChatColor.YELLOW + " has been removed from your IGNORE list.");
				return;
			}
		}

		host.sendMessage(ChatColor.YELLOW + "" + to_remove + ChatColor.YELLOW + " is not on any of your social lists.");
		updateCommBook(host);
	}

	public static int getBuddyListLength(String p_name){
		if(!(buddy_list.containsKey(p_name))){
			return 0;
		}
		return buddy_list.get(p_name).size();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageEvent e){
		if(e.getCause() == DamageCause.ENTITY_ATTACK && e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;

			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Player){
				Player attacker = (Player)edbee.getDamager();
				Player hurt = (Player)e.getEntity();

				if(isPlayerOnBuddyList(attacker, hurt.getName())){
					if(!toggle_list.get(attacker.getName()).contains("ff")){
						if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())){
							return;
						}
						e.setCancelled(true); // Friendly fire is OFF.
						e.setDamage(0);
					}
				}
			}
		}

		if(e.getCause() == DamageCause.PROJECTILE && e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;

			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Arrow){
				Arrow a = (Arrow)edbee.getDamager();
				if(a.getShooter() instanceof Player){
					Player shooter = (Player)a.getShooter();
					Player hurt = (Player)e.getEntity();

					if(isPlayerOnBuddyList(shooter, hurt.getName())){
						if(!toggle_list.get(shooter.getName()).contains("ff")){
							if(DuelMechanics.duel_map.containsKey(shooter.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())){
								return;
							}
							e.setCancelled(true); // Friendly fire is OFF.
							e.setDamage(0);
						}
					}
				}
			}
		}
	}
	
	public static String getToggleDescription(String toggle){
		String desc = ChatColor.GRAY.toString();
		if(toggle.equalsIgnoreCase("toggledebug")){
			desc += "Toggles displaying combat debug messages.";
		}
		if(toggle.equalsIgnoreCase("toggleff")){
			desc += "Toggles friendly-fire between buddies.";
		}
		if(toggle.equalsIgnoreCase("toggletrade")){
			desc += "Toggles trading requests.";
		}
		if(toggle.equalsIgnoreCase("toggleduel")){
			desc += "Toggles dueling requests.";
		}
		if(toggle.equalsIgnoreCase("toggletells")){
			desc += "Toggles recieving NON-BUD /tell.";
		}
		if(toggle.equalsIgnoreCase("toggleglobal")){
			desc += "Toggles recieving <G>lobal chat.";
		}
		if(toggle.equalsIgnoreCase("togglefilter")){
			desc += "Toggles the adult chat filter.";
		}
		if(toggle.equalsIgnoreCase("toggleparty")){
			desc += "Toggles recieving NON-BUD /pinvite.";
		}
		if(toggle.equalsIgnoreCase("toggletradechat")){
			desc += "Toggles recieving <T>rade chat.";
		}
		if(toggle.equalsIgnoreCase("togglechaos")){
			desc += "Toggles killing blows on lawful players (anti-chaotic).";
		}
		if(toggle.equalsIgnoreCase("togglepvp")){
			desc += "Toggles all outgoing PvP damage (anti-neutral).";
		}
		if(toggle.equalsIgnoreCase("toggletips")){
			desc += "Toggles displaying noob-friendly server tips.";
		}
		if(toggle.equalsIgnoreCase("toggleprofile")){
			desc += "Toggles displaying inventory and stats on website statistics.";
		}
		if(toggle.equalsIgnoreCase("togglestarterpack")){
			desc += "Toggles recieving starter bread and potions on respawn.";
		}
		if(toggle.equalsIgnoreCase("toggleindicator")){
		    desc += "Toggles damage indicators when in combat.";
		}
		return desc;
	}
	
	public static ItemStack generateToggleButton(String toggle, boolean on){
		ItemStack toggle_button = new ItemStack(Material.INK_SACK);
		ChatColor cc = null;
		
		if(on){
			toggle_button.setDurability((short)10);
			cc = ChatColor.GREEN;
		}
		else if(!on){
			toggle_button.setDurability((short)8);
			cc = ChatColor.RED;
		}
		
		ItemMeta im = toggle_button.getItemMeta();
		im.setDisplayName(cc + "/" + toggle);
		
		List<String> lore = new ArrayList<String>();
		lore.add(getToggleDescription(toggle));
		
		im.setLore(lore);
		toggle_button.setItemMeta(im);
		
		return toggle_button;
	}
	
	public boolean isToggleButton(ItemStack is){
		if(is != null && is.getType() == Material.INK_SACK && (is.getDurability() == (short)8 || is.getDurability() == (short)10) && is.hasItemMeta() && is.getItemMeta().hasDisplayName() 
				&& (is.getItemMeta().getDisplayName().startsWith(ChatColor.GREEN.toString() + "/toggle") || is.getItemMeta().getDisplayName().startsWith(ChatColor.RED.toString() + "/toggle"))){
			return true;
		} 
		return false;
	}

	public static Socket getSocket(int server_num){
		Socket return_socket = sock_list.get(server_num);
		int delay = 200;
		if(return_socket == null || return_socket.isClosed()){
			try {
				/*if(server_num > 1000 && server_num < 3000){
					delay = 3000;
				}*/

				Socket s = new Socket();
				//s.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
				s.connect(new InetSocketAddress(server_list.get(server_num), Hive.transfer_port), delay);
				return s;
			} catch (IOException e) {
				//e.printStackTrace(); Worthless spam for dead servers.
			}
		}
		return return_socket;
	}
	public void refreshSockets(){
		// DEPRECIATED.
	}  

	// @server_num@p_name:server_num
	public static void sendPacketCrossServer(String packet_data, int server_num, boolean all_servers){
		String local_ip = Hive.local_IP;

		Socket kkSocket = null;
		PrintWriter out = null;

		if(all_servers){
			for(int sn : CommunityMechanics.server_list.keySet()){
				String server_ip = CommunityMechanics.server_list.get(sn);
				if(server_ip.equalsIgnoreCase(local_ip)){
					continue; // Don't send to same server.
				}
				try {
						kkSocket = new Socket();
						//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
						kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 100);
						out = new PrintWriter(kkSocket.getOutputStream(), true);
				
						out.println(packet_data);

				} catch (IOException e) {
					if(out != null){
						out.close();
					}
					continue;
				}

				if(out != null){
					out.close();
				}
			}
		}
		else if(!all_servers){
			try {
				String server_ip = CommunityMechanics.server_list.get(server_num);

				kkSocket = new Socket();
				//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
				kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 100);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
				
				out.println(packet_data);

			} catch (IOException e) {

			} finally {
				if(out != null){
					out.close();
				}
			}

			if(out != null){
				out.close();
			}
		}
	}

	public static void sendPacketCrossServer(String packet_data, String ip){
		Socket kkSocket = null;
		PrintWriter out = null;

			try {
				kkSocket = new Socket();
				//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
				kkSocket.connect(new InetSocketAddress(ip, Hive.transfer_port), 100);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
				
				out.println(packet_data);
				kkSocket.close();
			} catch (IOException e) {
				
			} finally {
				if(out != null){
					out.close();
				}
			}

			if(out != null){
				out.close();
			}
	}

	/*public static void sendMessageCrossServer(String message, String server_ip, int server_num){
		// message = to/from@US-0: Message contents here.
		// server_ip = IP.

		String p_name = message.substring(0, message.indexOf("/"));
		Socket kkSocket = null;
		PrintWriter out = null;

		try {
			try {
				out = new PrintWriter(getSocket(server_num).getOutputStream(), true);
			} catch (SocketException e) {
				if(out != null){
					out.close();
				}
				kkSocket = new Socket();
				kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 100);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
				e.printStackTrace();
				log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[CommunityMechanics] FAILED to send message to " + p_name + " on server " + server_ip + ". --> REROUTING...");
			}

			out.println(message);

		} catch (IOException e) {
			if(out != null){
				out.close();
			}
			e.printStackTrace();
			log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[CommunityMechanics] FAILED to send message to " + p_name + " on server " + server_ip + ".");
			return;
		}

		if(out != null){
			out.close();
		}
	}*/

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e){
		String root_cmd = e.getMessage().split(" ")[0];
		Player p = e.getPlayer();
		if(root_cmd.equalsIgnoreCase("/r") || root_cmd.equalsIgnoreCase("/reply")){
			String message = "@ " + e.getMessage().replaceAll(root_cmd + " ", "");
			Set<Player> plist = new HashSet<Player>();
			plist.add(p);
			AsyncPlayerChatEvent asce = new AsyncPlayerChatEvent(false, p, message, plist);
			Bukkit.getPluginManager().callEvent(asce); // Call chat event.
			e.setCancelled(true);
		}

		if(root_cmd.equalsIgnoreCase("/whisper") || root_cmd.equalsIgnoreCase("/m") || root_cmd.equalsIgnoreCase("/msg") || root_cmd.equalsIgnoreCase("/tell") || root_cmd.equalsIgnoreCase("/t") || root_cmd.equalsIgnoreCase("/w") || root_cmd.equalsIgnoreCase("/to")  || root_cmd.equalsIgnoreCase("/message")){
			if(!(e.getMessage().contains(" "))){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect syntax. " + root_cmd + " <PLAYER> <MESSAGE>");
				e.setCancelled(true);
				return;
			}
			String to_player = e.getMessage().split(" ")[1];
			String message = "@" + to_player + " " + e.getMessage().replaceAll(root_cmd + " " + e.getMessage().split(" ")[1] + " ", "");
			Set<Player> plist = new HashSet<Player>();
			plist.add(p);
			AsyncPlayerChatEvent asce = new AsyncPlayerChatEvent(false, p, message, plist);
			Bukkit.getPluginManager().callEvent(asce); // Call chat event.
			e.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerAsyncChatEvent (final AsyncPlayerChatEvent e){
		if(!e.getMessage().startsWith("@")){return;}
		e.setCancelled(true);

		if(last_pm.containsKey(e.getPlayer().getName())){
			long last = last_pm.get(e.getPlayer().getName());
			if((System.currentTimeMillis() - last) <= 500){
				return; // Do nothing, been too short.
			}
		}

		async_pm.put(e.getPlayer().getName(), e.getMessage());

		// Depreciated, using Thread Pooling (processPMs())
		/*Player sent_from = e.getPlayer();
		String sent_to_s = "";
		if(e.getMessage().contains(" ")){
			sent_to_s = e.getMessage().substring(1, e.getMessage().indexOf(" "));
		}
		else if(!(e.getMessage().contains(" "))){
			sent_to_s = e.getMessage().substring(1, e.getMessage().length());
		}

		if(toggle_list.get(sent_from.getName()).contains("tells") && (!(isPlayerOnBuddyList(sent_from, sent_to_s)))){ // They have tells disabled and the reciever is NOT a bud, so they can't reply.
			sent_from.sendMessage(ChatColor.RED + "You currently have non-BUD private messages " + ChatColor.BOLD + "DISABLED." + ChatColor.RED + " Type '/toggletells' to re-enable.");
			return;
		}

		if(sent_to_s.equalsIgnoreCase("")){
			if(!last_reply.containsKey(sent_from.getName())){
				sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You have no conversation to respond to!");
				return;
			}
			sent_to_s = last_reply.get(sent_from.getName());
		}

		if(ignore_list.get(sent_from.getName()).contains(sent_to_s) || socialQuery(sent_from.getName(), sent_to_s, "CHECK_FOE")){
			sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " is OFFLINE.");
			return;
		}

		int sent_to_server_data = getPlayerServer(sent_to_s, true); // Update.

		if(sent_to_server_data < 0){
			if(sent_to_server_data == -1){
				sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " is OFFLINE.");
			}
			else if(sent_to_server_data == -2){
				sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_s + ChatColor.RED + " has " + ChatColor.UNDERLINE + "NEVER" + ChatColor.RED + " logged in to Dungeon Realms.");
			}
			return;
		}

		if(!(server_list.containsKey(sent_to_server_data))){
			sent_from.sendMessage(ChatColor.RED + "Your message could not be delivered because the server ID " + sent_to_server_data + " is not defined.");
			return;
		}

		int o_sent_to_server = sent_to_server_data;
		String prefix = "US-";

		if(sent_to_server_data > 1000 && sent_to_server_data < 2000){
			sent_to_server_data -= 1000;
			prefix = "EU-";
		}

		if(sent_to_server_data > 2000 && sent_to_server_data < 3000){
			sent_to_server_data -= 2000;
			prefix = "BR-";
		}


		if(sent_to_server_data >= 3000){
			sent_to_server_data -= 3000;
			prefix = "US-YT";
		}

		String sent_to_server = prefix + sent_to_server_data;
		String local_server = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		String message = "";

		if(e.getMessage().contains(" ")){
			message = e.getMessage().substring(e.getMessage().indexOf(" "), e.getMessage().length());
		}
		else if(!(e.getMessage().contains(" "))){
			message = "";
		}

		if(Bukkit.getPlayer(sent_to_s) != null){
			// Local msg'ing.
			Player sent_to_p = Bukkit.getPlayer(sent_to_s);
			sent_to_s = sent_to_p.getName();

			last_pm.put(e.getPlayer().getName(), System.currentTimeMillis());

			if((toggle_list.get(sent_to_p.getName()).contains("tells") && !(isPlayerOnBuddyList(sent_to_p, sent_from.getName()))) || (toggle_list.get(sent_from.getName()).contains("tells")  && !(isPlayerOnBuddyList(sent_from, sent_to_p.getName())))){
				if(toggle_list.get(sent_to_p.getName()).contains("tells")){
					sent_from.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + sent_to_p.getName() + ChatColor.RED + " currently has private messaging " + ChatColor.UNDERLINE + "DISABLED.");
					return;
				}
				return;
			}

			log.info(sent_from.getName() + " --> " + sent_to_s + " " + e.getMessage());

			ChatColor st_c = ChatMechanics.getPlayerColor(sent_from, sent_to_p);
			ChatColor sf_c = ChatMechanics.getPlayerColor(sent_to_p, sent_from);

			String from_prefix = ChatMechanics.getPlayerPrefix(sent_from);
			String to_prefix = ChatMechanics.getPlayerPrefix(sent_to_p);

			String to_personal_msg = message;
			if(ChatMechanics.hasAdultFilter(sent_to_p.getName())){
				to_personal_msg = "";
				for(String s : message.split(" ")){
					for(String bad : ChatMechanics.bad_words){
						if(s.contains(bad)){
							s = s.replaceAll(bad, "****");
						}
					}
					to_personal_msg += s + " ";
				}
			}

			String from_personal_msg = message;
			if(ChatMechanics.hasAdultFilter(sent_from.getName())){
				from_personal_msg = "";
				for(String s : message.split(" ")){
					for(String bad : ChatMechanics.bad_words){
						if(s.contains(bad)){
							s = s.replaceAll(bad, "****");
						}
					}
					from_personal_msg += s + " ";
				}
			}

			sent_to_p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FROM " + from_prefix + st_c + sent_from.getName() + ":" + ChatColor.WHITE + to_personal_msg);
			sent_from.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "TO " + to_prefix + sf_c + sent_to_p.getName() + ":" + ChatColor.WHITE + from_personal_msg);
			if(!(last_reply.containsKey(sent_to_p.getName())) || !last_reply.get(sent_to_p.getName()).equalsIgnoreCase(sent_from.getName())){
				sent_to_p.playSound(sent_to_p.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 1.2F);
				last_reply.put(sent_to_p.getName(), sent_from.getName());
			}
		}
		else if(!(sent_to_server.equalsIgnoreCase(local_server))){
			String message_to_send = "^" + sent_to_s + "/" + GuildMechanics.getGuildPrefix(sent_from.getName()) + "@" + sent_from.getName() + ";" + local_server + ":" + message;
			sendMessageCrossServer(message_to_send, server_list.get(o_sent_to_server), o_sent_to_server);
		}*/
	}




	// DEPRECIATED, Kept for backwards compatibility
	public static void setIgnoreColor(final Player p_viewer, final Player p_edited){}

	@SuppressWarnings("deprecation")
	public static void clearAllViewColor(final Player p_viewer, final Player p_edited){
		final String r_name = p_edited.getName();

		if(p_viewer.getName().equalsIgnoreCase(p_edited.getName())){
			return;
		}

		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				//ChatColor c = ChatColor.WHITE;

				//CraftPlayer test = null; TODO - UNUSED
				//EntityPlayer e_test = null; TODO - UNUSED

				//test = (CraftPlayer)e_test.getBukkitEntity();

				EntityPlayer ent_p_edited = ((CraftPlayer) p_edited).getHandle();
				net.minecraft.server.v1_7_R1.ItemStack boots = null, legs = null, chest = null, head = null;

				if(ent_p_edited.getEquipment(1) != null){
					boots = ent_p_edited.getEquipment(1);
				}
				if(ent_p_edited.getEquipment(2) != null){
					legs = ent_p_edited.getEquipment(2);
				}
				if(ent_p_edited.getEquipment(3) != null){
					chest = ent_p_edited.getEquipment(3);
				}
				if(ent_p_edited.getEquipment(4) != null){
					head = ent_p_edited.getEquipment(4);
				}

				ent_p_edited.displayName = ChatColor.stripColor(r_name);

				((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ent_p_edited));
				List<Packet> pack_list = new ArrayList<Packet>();
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

				for(Packet pa : pack_list){
					((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(pa);
				}

			}
		}, 2L); 
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		Player p = e.getPlayer();

		local_confirmed_buddies.put(p.getName(), new ArrayList<String>());
		local_confirmed_ignores.put(p.getName(), new ArrayList<String>());

		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {

				Player p = e.getPlayer();
				
				if(p == null){
					return;
				}

				if(toggle_list.containsKey(p.getName()) && toggle_list.get(p.getName()).size() > 0){ // They have something off/on.
					
					if(toggle_list.get(p.getName()).contains("tradechat")){
						List<String> toggles = toggle_list.get(p.getName());
						toggles.remove("tradechat");
						toggles.add("tchat");
						toggle_list.put(p.getName(), toggles);
					}
					
					p.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "     To manage your gameplay settings, use " + ChatColor.YELLOW + ChatColor.UNDERLINE + "/toggles");
					p.sendMessage("");
					
					//List<String> ltoggle_list = toggle_list.get(p.getName());
					//String toggle_message = "                                ";
					//String toggle_message = ChatColor.GRAY + "" + ChatColor.BOLD + "Toggles: ";
					/*String toggle_message = "";
					if(ltoggle_list.contains("debug")){
						toggle_message += ChatColor.GRAY + "DEBUG: " + ChatColor.GREEN + "O" + "  ";
					}
					else if(!ltoggle_list.contains("debug")){
						toggle_message += ChatColor.GRAY + "DEBUG: " + ChatColor.RED + "O" + "  ";
					}

					if(ltoggle_list.contains("trade")){
						toggle_message += ChatColor.GRAY + "TRADE: " + ChatColor.RED + "O" + "  ";
					}
					else if(!ltoggle_list.contains("trade")){
						toggle_message += ChatColor.GRAY + "TRADE: " + ChatColor.GREEN + "O" + "  ";
					}

					if(ltoggle_list.contains("duel")){
						toggle_message += ChatColor.GRAY + "DUEL: " + ChatColor.RED + "O" + "  ";
					}
					else if(!ltoggle_list.contains("duel")){
						toggle_message += ChatColor.GRAY + "DUEL: " + ChatColor.GREEN + "O" + "  ";
					}

					if(ltoggle_list.contains("tells")){
						toggle_message += ChatColor.GRAY + "PM: " + ChatColor.RED + "O" + "  ";
					}
					else if(!ltoggle_list.contains("tells")){
						toggle_message += ChatColor.GRAY + "PM: " + ChatColor.GREEN + "O" + "  ";
					}

					if(ltoggle_list.contains("ff")){
						toggle_message += ChatColor.GRAY + "FF: " + ChatColor.GREEN + "O" + "  ";
					}
					else if(!ltoggle_list.contains("ff")){
						toggle_message += ChatColor.GRAY + "FF: " + ChatColor.RED + "O" + "  ";
					}

					if(ltoggle_list.contains("global")){
						toggle_message += ChatColor.GRAY + "GLC: " + ChatColor.RED + "O" + " ";
					}
					else if(!ltoggle_list.contains("global")){
						toggle_message += ChatColor.GRAY + "GLC: " + ChatColor.GREEN + "O" + " ";
					}

					if(ltoggle_list.contains("filter")){
						toggle_message += ChatColor.GRAY + "FIL: " + ChatColor.RED + "O" + " ";
					}
					else if(!ltoggle_list.contains("filter")){
						toggle_message += ChatColor.GRAY + "FIL: " + ChatColor.GREEN + "O" + " ";
					}

					if(ltoggle_list.contains("party")){
						toggle_message += ChatColor.GRAY + "PTY: " + ChatColor.RED + "O";
					}
					else if(!ltoggle_list.contains("party")){
						toggle_message += ChatColor.GRAY + "PTY: " + ChatColor.GREEN + "O";
					}

					if(ltoggle_list.contains("pvp")){
						toggle_message += ChatColor.GRAY + "PVP: " + ChatColor.RED + "O";
					}
					else if(!ltoggle_list.contains("pvp")){
						toggle_message += ChatColor.GRAY + "PVP: " + ChatColor.GREEN + "O";
					}*/
					
				}
				else{
					toggle_list.put(p.getName(), new ArrayList<String>());
				}

				updateCommBook(p);

				if(buddy_list.containsKey(p.getName())){
					List<Object> data = new ArrayList<Object>();
					data.add("[sq_online]" + p.getName() + ":" + Bukkit.getServer().getMotd().substring(0, Bukkit.getServer().getMotd().indexOf(" ")));
					data.add(null);
					data.add(true);
					CommunityMechanics.social_query_list.put(p.getName(), data);
					
					//sendPacketCrossServer("[sq_online]" + p.getName() + ":" + getServer().getMotd().substring(0, getServer().getMotd().indexOf(" ")), 0, true); // Will tell all buddy's on all servers he's online.
					if(!(buddy_list.containsKey(p.getName()))){
						return; // They logged out. Delay from sendPacketCrossServer.
					}
					for(String s : buddy_list.get(p.getName())){
						if(Bukkit.getPlayer(s) != null){
							Player local_bud = Bukkit.getPlayer(s);
							if(socialQuery(p.getName(), local_bud.getName(), "CHECK_BUD")){
								local_bud.sendMessage(ChatColor.YELLOW + p.getName() + " has joined this server.");
								local_bud.playSound(p.getLocation(), Sound.ORB_PICKUP, 2F, 1.2F);
								updateCommBook(local_bud);
							}
						}
					}
				}

			}
		}, 2L);

	}

	@SuppressWarnings("deprecation")
	public static void updateCommBook(Player p){
		int slot = -1;
		boolean book = false;
		for(ItemStack is : p.getInventory().getContents()){
			slot++;
			is = p.getInventory().getItem(slot);
			if(is != null && isSocialBook(is)){
				book = true;
				break; // Slot is what it should be.
			}
		}

		if(book == true){
			p.getInventory().setItem(slot, generateCommBook(p));
		}

		if(book == false){
			if(p.getInventory().getItem(8) == null || p.getInventory().getItem(8).getType() == Material.AIR){
				p.getInventory().setItem(8, generateCommBook(p));
			}
			else{
				if(p.getInventory().firstEmpty() != -1){
					p.getInventory().setItem(p.getInventory().firstEmpty(), generateCommBook(p));
				}
			}
		}
		p.updateInventory();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		final Player p = e.getPlayer();
		if(buddy_list.containsKey(p.getName())){
			final List<String> lbuddy_list = buddy_list.get(p.getName());

			new BukkitRunnable(){
				@Override
				public void run() {
					for(String s : lbuddy_list){
						if(Bukkit.getPlayer(s) != null){
							final Player local_bud = Bukkit.getPlayer(s);
							if(socialQuery(p.getName(), local_bud.getName(), "CHECK_BUD")){
								local_bud.playSound(p.getLocation(), Sound.ORB_PICKUP, 2F, 0.5F);
								local_bud.sendMessage(ChatColor.YELLOW + p.getName() + " has logged out.");
							}
						}
					}

					List<Object> data = new ArrayList<Object>();
					data.add("[sq_offline]" + p.getName() + ":" + Bukkit.getServer().getMotd().substring(0, Bukkit.getServer().getMotd().indexOf(" ")));
					data.add(null);
					data.add(true);
					CommunityMechanics.social_query_list.put(p.getName(), data);
					//sendPacketCrossServer("[sq_offline]" + p.getName() + ":" + getServer().getMotd().substring(0, getServer().getMotd().indexOf(" ")), 0, true);
					
					for(String s : lbuddy_list){
						if(Bukkit.getPlayer(s) != null){
							final Player local_bud = Bukkit.getPlayer(s);
							if(socialQuery(p.getName(), local_bud.getName(), "CHECK_BUD")){
								updateCommBook(local_bud);
							}
						}
					}
				}
			}.runTaskLaterAsynchronously(Main.plugin, 5L);
			
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player damager = (Player)e.getDamager();
			if(isSocialBook(damager.getItemInHand())){
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent e){
		if(e.hasItem() && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			final Player p = e.getPlayer();
			if(isSocialBook(e.getItem())){
				if(!(last_book_click.containsKey(p.getName())) || (System.currentTimeMillis() - last_book_click.get(p.getName())) > (2 * 1000)){
					updateCombatPage(p);
					p.closeInventory();
					last_book_click.put(p.getName(), System.currentTimeMillis());
				}
				p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F);
				return;
			}
		}

		if(e.hasItem() && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR)){
			final Player p = e.getPlayer();
			if(isSocialBook(e.getItem()) && !(p.isSneaking())){
				final Player new_bud = TradeMechanics.getTarget(p);
				if(new_bud != null){

					if(new_bud.getPlayerListName().equalsIgnoreCase("") || new_bud.hasMetadata("NPC")){
						return; // Not a real player.
					}

					if(isPlayerOnIgnoreList(p, new_bud.getName())){
						p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +  new_bud.getName() + ChatColor.YELLOW + " is currently on your IGNORE LIST.");
						p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.BOLD + "/delete " +  new_bud.getName() + ChatColor.GRAY + " to remove them from your ignore list.");
						return;
					}

					if(CommunityMechanics.toggle_list.get(new_bud.getName()).contains("party")){
						if(!CommunityMechanics.isPlayerOnBuddyList(new_bud.getName(), p.getName())){
							// They're not buddies and this player doesn't want non-bud invites.
							p.sendMessage(ChatColor.RED + new_bud.getName() + " has Non-BUD party invites " + ChatColor.BOLD + "DISABLED");
							return;
						}
					}

					//PartyMechanics.inviteToParty(new_bud, p);
				}

				if(new_bud == null && !(p.isSneaking())){
					p.sendMessage(ChatColor.GREEN + "To invite a player to your party, use the " + ChatColor.BOLD + "/pinvite" + ChatColor.GREEN + " command OR " + ChatColor.UNDERLINE.toString() + "Left Click" + ChatColor.GREEN.toString() + " the player with your book in hand.");
					return; 
				}

			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();

		new BukkitRunnable(){
			@Override
			public void run() {
				if(!(Hive.server_swap.containsKey(p.getName())) && p != null){
					updateCommBook(p);
				}
			}
		}.runTaskLaterAsynchronously(Main.plugin, 1L);
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onToggleMenuClick(InventoryClickEvent e){
		if(e.getInventory().getTitle().equalsIgnoreCase("Toggle Menu")){
			e.setCancelled(true);
			// No item exchange should ever happen.
			
			Player pl = (Player)e.getWhoClicked();
			if(isToggleButton(e.getCurrentItem())){
				String toggle_cmd = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				pl.performCommand(toggle_cmd.substring(1, toggle_cmd.length()));
				
				boolean on; // The -new- status.
				if(e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.RED.toString())){
					on = true;
				}
				else{
					on = false;
				}
				
				e.setCurrentItem(generateToggleButton(toggle_cmd.substring(1, toggle_cmd.length()), on));
				pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 0.5F);
			}
			pl.updateInventory();
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if(e.getSlotType() == SlotType.OUTSIDE && e.getCursor() == null){return;}
		Player p = (Player)e.getWhoClicked();
		if(e.getCurrentItem() == null){return;}

		if(isSocialBook(e.getCurrentItem()) && !p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			e.setCancelled(true);
		}
		if(isSocialBook(e.getCursor()) && !p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			e.setCancelled(true);
		}		
	}

}
