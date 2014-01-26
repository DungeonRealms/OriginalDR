package me.vaqxine.ProxyMechanics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import me.vaqxine.query.MCQuery;
import me.vaqxine.query.QueryResponse;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;


public class ProxyMechanics extends Plugin implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	List<String> us_public_servers = new ArrayList<String>(Arrays.asList("US-1", "US-2", "US-3", "US-4", "US-5", "US-6", "US-7", "US-8", "US-11"));
	List<String> us_private_servers = new ArrayList<String>(Arrays.asList("US-9", "US-10"));
	List<String> br_public_servers = new ArrayList<String>(Arrays.asList("BR-1"));

	public static final String Proxy_IP = "69.197.31.34";
	public static final String Site_IP = "192.169.82.62"; // Website IP for SQL.
	public static final String Hive_IP = "72.20.40.38"; // Player database backend IP (used for SQL). 

	public static final int transfer_port = 6427;

	public static HashMap<Integer, String> server_list = new HashMap<Integer, String>();
	// Server #, Server IP

	public static volatile HashMap<Integer, List<Integer>> server_population = new HashMap<Integer, List<Integer>>();
	// Contains min/max players for every server, used for shard menu.
	// US-1, Array(10,150)

	public static volatile ConcurrentHashMap<Integer, Long> last_ping = new ConcurrentHashMap<Integer, Long>();
	// Last time each server_num sent information to the proxy. If >20 seconds, server is offline.

	public static volatile HashMap<Integer, String> server_motd = new HashMap<Integer, String>();
	// Server_num, MOTD (use to check for Lock, etc?)

	public static volatile HashMap<String, String> last_server = new HashMap<String, String>();
	// Last server a player was on.

	public static HashMap<String, Long> last_connection = new HashMap<String, Long>();
	// PLAYER_NAME, last time they connected.

	public static HashMap<String, String> user_rank = new HashMap<String, String>();
	// User ranks, used to determine VIP servers.

	public static volatile HashSet<String> crashed_servers = new HashSet<String>();
	// List of all crashed servers, they will be ignored when getRandomServer() is fired.
	// Also used to skip getLastServer() logins if that server is reported as crashed.
	// Servers will be removed from this list when the server boots, it will send an unlock packet.

	Thread ThreadPool;
	// Controls all new Thread() SQL queries.

	public static HashSet<String> banned_players = new HashSet<String>();
	// Locally banned players, prevents extra queries.

	public static HashMap<String, Long> ban_database = new HashMap<String, Long>();
	// BANNED_PLAYER_NAME, unban_date

	public static HashMap<String, String> ipban_database = new HashMap<String, String>();
	// Account, IP.

	public static HashSet<String> banned_IP = new HashSet<String>();
	// List of all banned IP's. Never unbanned.

	public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<String>();
	// All SQL queries to run on ThreadPool.

	public static HashSet<String> online_players = new HashSet<String>();
	// Online players.

	public static boolean reboot_soon = false;
	// When true, lock everyone out.

	public static volatile ConcurrentHashMap<String, String> socket_pool = new ConcurrentHashMap<String, String>();
	// Packet data, address to send to
	
	public static List<String> ip_whitelist = new ArrayList<String>();

	ProxyMechanics plugin = null;

	public void onEnable() {
		plugin = this;
		this.getProxy().getPluginManager().registerListener(this, this);

		downloadBanDatabase();
		downloadIPBanDatabase();
		downloadRankDatabase();
		downloadLastServers();

		ListenThread listener = new ListenThread();
		listener.start(); // Start listening!

		ThreadPool = new ThreadPool();
		ThreadPool.start();
		// Handles all unbanPlayer() sql multithreaded queries.

		SocketPool socketpool = new SocketPool();
		socketpool.start();
		// Sends packets to other servers.
		
		server_list.put(1, "72.20.38.165"); //  US-1
		server_list.put(2, "72.20.38.166"); // US-2
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

		ip_whitelist.add(Proxy_IP);
		ip_whitelist.add(Hive_IP);
		ip_whitelist.add(Site_IP);
		ip_whitelist.add("72.8.157.66"); // Donation Back-end Server AND US-0
		ip_whitelist.add("72.20.9.154"); 
		ip_whitelist.add("72.20.9.158");
		ip_whitelist.add("72.8.172.242");
		ip_whitelist.add("69.197.57.158");
		ip_whitelist.add("72.20.30.14");
		ip_whitelist.add("72.8.172.254");
		ip_whitelist.add("74.63.199.162");

		// Handles refreshing socketpool for SQL transactions.
		this.getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				ConnectionPool.refresh = true;
			}
		}, 180, 180, TimeUnit.SECONDS);

		this.getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				for(Entry<Integer, Long> data : last_ping.entrySet()){
					long time = data.getValue();
					int server_num = data.getKey();

					if((System.currentTimeMillis() - time) > (15 * 1000)){
						String server_prefix = getServerPrefixFromNum(server_num);
						if(!(crashed_servers.contains(server_prefix))){
							crashed_servers.add(server_prefix);
						}
						server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(0, 0)));
						last_ping.remove(server_num);
					}
				}
			}
		}, 4, 4, TimeUnit.SECONDS);

		this.getProxy().getInstance().getPluginManager().registerCommand(this, new Command("reloadbans"){
			public void execute(CommandSender sender, String[] args) {
				downloadBanDatabase();
				downloadIPBanDatabase();
				System.out.println(ChatColor.RED + "Ban database reloaded.");
			}
		});
		
		this.getProxy().getInstance().getPluginManager().registerCommand(this, new Command("synctime"){
			public void execute(CommandSender sender, String[] args) {
				syncTimes();
				System.out.println(ChatColor.RED + "All server times have been synced to HIVE's epoch time.");
			}
		});
		
		
		this.getProxy().getInstance().getPluginManager().registerCommand(this, new Command("bancheck"){
			public void execute(CommandSender sender, String[] args) {
				String p_name = args[0];
				if(ban_database.containsKey(p_name.toLowerCase())){
					System.out.println(ChatColor.AQUA + p_name + " is in ban_database -> " + ban_database.get(p_name));
				}
				if(ipban_database.containsKey(p_name.toLowerCase())){
					System.out.println(ChatColor.AQUA + p_name + " is in ipban_database -> " + ipban_database.get(p_name));
				}
			}
		});
		
		
		
		System.out.println("[DungeonProxy] has been enabled.");
	}

	public void onDisable() {
		System.out.println("[DungeonProxy] has been disabled.");
	}

	public void downloadLastServers(){
		PreparedStatement pst = null;
		int count = 0;
		try {
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT p_name, last_server FROM player_database");

			pst.execute();
			ResultSet rs = pst.getResultSet();

			while(rs.next()){
				count++;
				String p_name = rs.getString("p_name");
				String l_last_server = rs.getString("last_server");

				last_server.put(p_name, l_last_server);
			}

		} catch(Exception err){
			err.printStackTrace();
			last_server.clear();
			downloadLastServers();
			return;
		}

		System.out.println("[ProxyMechanics] Loaded " + count + " LAST SERVER PLAYER DATA into memory.");
	}

	public void downloadBanDatabase(){
		ban_database.clear();

		PreparedStatement pst = null;
		int count = 0;
		try {
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT * FROM ban_list WHERE (unban_reason IS NULL || unban_reason='') && IP IS NULL && unban_date IS NOT NULL");

			pst.execute();
			ResultSet rs = pst.getResultSet();

			while(rs.next()){
				count++;
				long unban_time = -1L;
				String p_name = rs.getString("pname").toLowerCase();
				String perm = rs.getString("perm");
				if(perm != null && perm.equalsIgnoreCase("1")){
					unban_time = -1L;
					ban_database.put(p_name.toLowerCase(), -1L);
					continue;
				}
				else if(perm == null || !perm.equalsIgnoreCase("1")){
					unban_time = rs.getDate("unban_date").getTime();
				}

				ban_database.put(p_name.toLowerCase(), unban_time);
			}

		} catch(Exception err){
			err.printStackTrace();
			ban_database.clear();
			downloadBanDatabase();
			return;
		}

		System.out.println("[ProxyMechanics] Loaded " + count + " BANNED PLAYERS into memory.");
	} 
	
	public static void syncTimes(){
		long time = System.currentTimeMillis() + 50; // Forward 50ms for ping delay.
		socket_pool.put("@date_update@" + time, "*");
	}

	public void downloadIPBanDatabase(){
		ipban_database.clear();
		banned_IP.clear();

		PreparedStatement pst = null;
		int count = 0;
		try {
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT pname, ip, unban_reason FROM ban_list WHERE ip IS NOT NULL");

			pst.execute();
			ResultSet rs = pst.getResultSet();

			while(rs.next()){
				String ip = rs.getString("ip");
				String p_name = rs.getString("pname").toLowerCase();
				String unban_reason = rs.getString("unban_reason");

				if(unban_reason != null && unban_reason.length() > 0){
					// The local user is unbanned, but we want to keep that IP on the no-no list.
					count++;
					banned_IP.add(ip);
					continue;
				}

				// If we reach this point, the user is not locally unbanned, so ban both the IP AND the user.
				if(ip != null && ip.length() > 0){
					count++;
					banned_IP.add(ip);
					ipban_database.put(p_name, ip);
				}
			}

		} catch(Exception err){
			err.printStackTrace();
			ipban_database.clear();
			banned_IP.clear();
			downloadBanDatabase();
			return;
		}

		System.out.println("[ProxyMechanics] Loaded " + count + " BANNED IP's into memory.");
	} 

	public void downloadRankDatabase(){
		PreparedStatement pst = null;
		int count = 0;
		try {
			// con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT p_name, rank FROM player_database WHERE rank IS NOT NULL");

			pst.execute();
			ResultSet rs = pst.getResultSet();

			while(rs.next()){
				count++;
				String p_name = rs.getString("p_name");
				String rank = rs.getString("rank");

				if(!rank.equalsIgnoreCase("default")){ // Default doesn't matter.
					user_rank.put(p_name, rank);
				}
			}
		} catch(Exception err){
			err.printStackTrace();
			user_rank.clear();
			downloadRankDatabase();
			return;
		}

		System.out.println("[ProxyMechanics] Loaded " + count + " PLAYER RANKS into memory.");
	}

	@EventHandler
	public void onLoginEvent(LoginEvent e){
		// Ok, we need to determine if there are available servers.
		if(reboot_soon == true){
			e.setCancelled(true);
			e.setCancelReason(ChatColor.RED.toString() + "No login servers available."
					+ "\n" + ChatColor.GRAY + "The game servers are about to reboot."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}

		if(server_population.size() <= 0){
			e.setCancelled(true);
			e.setCancelReason(ChatColor.RED.toString() + "No game servers online."
					+ "\n" + ChatColor.GRAY + "Please try again later."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}

		if(crashed_servers.size() == server_list.size()){
			// They're all offline.
			e.setCancelled(true);
			e.setCancelReason(ChatColor.RED.toString() + "No game servers online."
					+ "\n" + ChatColor.GRAY + "Please try again later."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}

		final String p_name = e.getConnection().getName();

		// Check their ban status.
		String ip = e.getConnection().getAddress().getAddress().getHostAddress();
		if(ip == null){
			e.setCancelled(true);
			e.setCancelReason(ChatColor.RED.toString() + "Invalid game session ID."
					+ "\n" + ChatColor.GRAY + "Please connect again."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}

		if(banned_IP.contains(ip) || ipban_database.containsKey(p_name.toLowerCase())){
			e.setCancelled(true);
			e.setCancelReason(ChatColor.RED.toString() + "Your IP (" + ip + ")" + " has been " + ChatColor.UNDERLINE + "PERMANENTLY" + ChatColor.RED.toString() + " disabled." + "\n\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
			return;
		}

		if(ban_database.containsKey(p_name.toLowerCase())){
			long unban_date = ban_database.get(p_name.toLowerCase());

			if(unban_date != -1L && unban_date != -1 && unban_date > 1){
				unban_date += (43200000 - 3600000);
			}

			long cur_date = System.currentTimeMillis();

			if(unban_date == -1L || unban_date == -1 || unban_date == 1 || unban_date == 0){
				e.setCancelled(true);
				e.setCancelReason(ChatColor.RED.toString() + "Your account has been " + ChatColor.UNDERLINE + "PERMANENTLY" + ChatColor.RED.toString() + " disabled." + "\n\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
				return;
			}

			if((unban_date - cur_date) >= 0){ // still banned.
				e.setCancelled(true);
				e.setCancelReason(ChatColor.RED.toString() + "Your account has been " + ChatColor.UNDERLINE + "TEMPORARILY" + ChatColor.RED.toString() + " locked due to suspisious activity." + "\n\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
				return;
			}

			// They're no longer banned if it reaches this point.
			if((unban_date - cur_date) < 0){
				Thread t = new Thread(new Runnable(){
					public void run(){
						unbanPlayer(p_name.toLowerCase(), "AUTO TIMEOUT", "Console");
					}
				});

				t.start();
			}
			else{
				e.setCancelled(true);
				e.setCancelReason(ChatColor.RED.toString() + "Invalid game session ID."
					+ "\n" + ChatColor.GRAY + "Please connect again."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
				return;
			}
		}

	}

	public static void unbanPlayer(final String p_name, final String reason, final String unbanner){

		long unban_time = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String futuredate = dateFormat.format(unban_time);


		sql_query.add("INSERT INTO ban_list (pname, unban_date, unban_reason, who_unbanned)"
				+ " VALUES"
				+ "('"+ p_name + "', '"+ futuredate + "', '" + reason + "', '" + unbanner + "') "  
				+ "ON DUPLICATE KEY UPDATE unban_date = '" + futuredate + "', unban_reason = '" + reason + "', who_unbanned = '" + unbanner + "'");

	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e){
		String server_name = e.getPlayer().getServer().getInfo().getName();
		last_server.put(e.getPlayer().getName(), server_name);
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent e){
		if(e.getServer().getInfo().getName().equalsIgnoreCase("cloud")){
			ProxiedPlayer pp = e.getPlayer();

			pp.disconnect(ChatColor.RED.toString() + "Invalid game session ID."
					+ "\n" + ChatColor.GRAY + "Please connect again."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}
	}

	@EventHandler
	public void onServerKickEvent(ServerKickEvent e){
		// Disconnect them from the proxy for the kick reason if they're kicked off a server.
		ProxiedPlayer pp = e.getPlayer();
		pp.disconnect(e.getKickReason());
	}

	@EventHandler
	public void onServerDisconnectEvent(PlayerDisconnectEvent e){
		String p_name = e.getPlayer().getName();
		online_players.remove(p_name);
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent e){
		if(!e.getTarget().getName().equalsIgnoreCase("cloud")){
			// They're connecting to a game server, we don't care.
			return; // Do nothing.
		}

		ProxiedPlayer pp = e.getPlayer();
		//last_connection.put(pp.getName(), System.currentTimeMillis());

		String p_name = pp.getName();
		String IP = pp.getAddress().getAddress().getHostAddress();

		if(online_players.contains(p_name)){
			return; // This is probably a bug -- the lag spikes due to false rerouting?
		}

		// Determine what their last server was, and then move them there.
		// If no last server can be found or their's is offline / full, pick a random one.
		String last_server = getLastServer(p_name); //getLastServer(p_name, IP);

		if(last_server == null){
			e.setCancelled(true);
			pp.disconnect(ChatColor.RED.toString() + "No game servers online."
					+ "\n" + ChatColor.GRAY + "Please try again later."
					+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			return;
		}

		if(last_server.equalsIgnoreCase("US-0")){
			last_server = getRandomServer(p_name);
		}

		int server_num = getServerNumFromPrefix(last_server);

		if(server_population.containsKey(server_num)){
			int online = server_population.get(server_num).get(0);
			int max_online = server_population.get(server_num).get(1);

			if((((online) >= (max_online)) || max_online == 0)){
				if(online >= max_online){
					if(p_name.equalsIgnoreCase("Vaquxine") || p_name.equalsIgnoreCase("availer")){
						// Now we have their last server, so we need to push them there.
						System.out.println("[DungeonProxy] Accepted connection from " + p_name + "(" + IP + ") -> " + last_server);
						e.setTarget(BungeeCord.getInstance().getServerInfo(last_server));
					}
				}
				boolean found_a_server = false;
				// Server is full / offline, find a new one.
				for(Entry<Integer, List<Integer>> data : server_population.entrySet()){
					int lserver_num = data.getKey();
					List<Integer> population = data.getValue();
					if(population != null){
						if(population.get(0) < (population.get(1))){
							// This one looks good, let's use it!
							String lmotd = server_motd.get(lserver_num);
							server_num = lserver_num;
							last_server = lmotd.substring(0, lmotd.indexOf(" "));
							found_a_server = true;
							break;
						}
					}
				}
				if(!(found_a_server)){
					// TODO: Check if they're subscribers, if so, put them in VIP server, or just ignore the population and throw them in last server.
					String rank = getRank(p_name);
					if(!(rank.equalsIgnoreCase("sub")) && !(rank.equalsIgnoreCase("sub+")) && !(rank.equalsIgnoreCase("sub++")) && !(rank.equalsIgnoreCase("pmod")) && !(rank.equalsIgnoreCase("gm")) && !(rank.equalsIgnoreCase("wd"))){
						//if(group != 75 && group != 76 && group != 6 && group != 7 && group != 5 && group != 10 && group != 11 && group != 72){
						e.setCancelled(true);
						pp.disconnect(ChatColor.RED.toString() + "No game sessions available."
								+ "\n" + ChatColor.GRAY + "Please try again later."
								+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
						return;
					}
					// If they're in one of the above groups, ignore full servers like a boss.
				}
			}
		}

		// Now we have their last server, so we need to push them there.
		System.out.println("[DungeonProxy] Accepted connection from " + p_name + "(" + IP + ") -> " + last_server);
		e.setTarget(BungeeCord.getInstance().getServerInfo(last_server));
		online_players.add(p_name);
	}

	public static String getRank(String p_name){
		if(user_rank.containsKey(p_name)){
			return user_rank.get(p_name);
		}
		else{
			return "default";
		}
	}

	/*public static int getForumGroup(String p_name){
		if(forum_usergroup.containsKey(p_name)){
			return forum_usergroup.get(p_name);
		}

		Connection con = null;
		PreparedStatement pst = null;
		int rank_num = 0;
		try {
			// con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT userid FROM userfield WHERE field5 = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!(rs.next())){
				return -1;
				//return "Unregistered";
				// Player is not registered on forums.
			}

			int userid = rs.getInt("userid");
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT usergroupid, membergroupids FROM user WHERE userid = '" + userid + "'");

			pst.execute();
			rs = pst.getResultSet();
			if(!(rs.next())){
				return -1;
				//return "nominecraftname";
				// Player is not registered on forums.
			}
			int primary_rank = rs.getInt("usergroupid");
			if(primary_rank == 9 || primary_rank == 12){ // TODO: Not 14.
				rank_num = primary_rank;
			} else { // 9 == Beta Tester
				String all_groups = rs.getString("membergroupids");
				if(all_groups.contains("9")){
					rank_num = 9;
				}
				if(all_groups.contains("12")){
					rank_num = 12;
				}
			}

			return rank_num;

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

		return -2;
	}*/

	public static int getServerNumFromPrefix(String prefix){
		int server_num = -1;
		if(prefix.contains("-")){
			server_num = Integer.parseInt(prefix.substring(prefix.indexOf("-") + 1, prefix.length()));
		}
		if(prefix.contains("US")){
			server_num = server_num;
		}
		else if(prefix.contains("EU")){
			server_num += 1000;
		}
		else if(prefix.contains("BR")){
			server_num += 2000;
		}

		return server_num;
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

	public String getLastServer(String p_name){
		// The name of the last server the player was on.
		PreparedStatement pst = null;

		try {
			String last_server = ProxyMechanics.last_server.get(p_name);
			if(last_server == null || !server_population.containsKey(getServerNumFromPrefix(last_server)) || crashed_servers.contains(last_server)){
				return getRandomServer(p_name);
			}
			return last_server;

		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			return getRandomServer(p_name);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
				return getRandomServer(p_name);
			}
		}
	}

	/*public static String getUnbanReason(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(ConnectionPool.sql_url, ConnectionPool.sql_user, ConnectionPool.sql_password);
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT unban_reason FROM ban_list WHERE pname = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return "";}
			String unban_reason = rs.getString("unban_reason");
			if(unban_reason == null || unban_reason.equalsIgnoreCase("")){
				return null;
			}
			return unban_reason;

		} catch (SQLException ex) {
			ex.printStackTrace(); 

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}*/

	/*public static long getUnbanDate(String p_name){
		// 0 = Not banned.
		// -1 = Never.
		// ### = Then.
		Connection con = null;
		PreparedStatement pst = null;

		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT unban_date, perm FROM ban_list WHERE pname = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return 0;}

			Date unban_date = rs.getDate("unban_date");
			String perm = rs.getString("perm");
			if(perm != null && perm.equalsIgnoreCase("1")){
				return -1;
			}
			return unban_date.getTime();


		} catch (SQLException ex) {
			ex.printStackTrace();  

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return 0;
	}*/

	public String getRandomServer(String p_name){
		String rank = getRank(p_name);

		String lowest_pop_server = "";
		String empty_server = "";
		int lowest_pop = -1;

		for(Entry<Integer, List<Integer>> data : server_population.entrySet()){
			String server = getServerPrefixFromNum(data.getKey());
			int server_num = getServerNumFromPrefix(server);
			if(crashed_servers.contains(server)){
				continue;
			}
			if(!last_ping.containsKey(server_num)){
				continue;
			}
			if(server.contains("BR-")){
				continue; // No random BR plz.
			}
			if(!rank.equalsIgnoreCase("sub++") && !rank.equalsIgnoreCase("sub+") && !rank.equalsIgnoreCase("sub") && (server.contains("US-9") || server.contains("US-10"))){
				continue; // VIP server.
			}
			if(server.contains("US-11")){
				continue; // RP server, don't randomly put people in here lol.
			}

			int online = data.getValue().get(0);
			int max = data.getValue().get(1);

			if(online == max){
				continue; // Sorry, full server.
			}
			if(max <= 1){
				continue; // Skip, server may be unreachable or offline.
			}
			if(online == 0){
				if(empty_server.equalsIgnoreCase("")){
					empty_server = server;
				}
				continue;
			}
			if(lowest_pop == -1){
				lowest_pop_server = server;
				lowest_pop = online;
				continue;
			}
			if(online < lowest_pop){
				lowest_pop_server = server;
				lowest_pop = online;
			}
		}

		if(!(lowest_pop_server.equalsIgnoreCase(""))){
			return lowest_pop_server;
		}
		else{
			if(!(empty_server.equalsIgnoreCase(""))){
				return empty_server; // Return an empty server.
			}

			return null;
		}
	}

}
