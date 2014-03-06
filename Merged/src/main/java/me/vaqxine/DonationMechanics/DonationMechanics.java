package me.vaqxine.DonationMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class DonationMechanics implements Listener {

	static Logger log = Logger.getLogger("Minecraft");

	public final static String site_sql_url = "jdbc:mysql://192.169.82.62:9108/vbforum";
	public final static String site_sql_user = "forum_G31FS2";
	public final static String site_sql_password = "9UEAXHK90GmFBwjL";

	public final static String sql_url = "jdbc:mysql://72.20.40.38:7447/dungeonrealms";
	public final static String sql_user = "slave_3XNZvi";
	public final static String sql_password = "SgUmxYSJSFmOdro3";

	public final static String Proxy_IP = "69.197.31.34";
	public final static int transfer_port = 6427;
	
	public final static String Site_IP = "192.169.82.62";
	public static String Hive_IP = "72.20.40.38";

	public static String local_IP = "";
	
	public static HashMap<Integer, String> server_list = new HashMap<Integer, String>();
	// Server #, Server IP
	
	public static HashMap<String, Integer> rank_forumgroup = new HashMap<String, Integer>();
	// Rank name, Forum group ID.

	public static HashMap<Integer, String> forumgroup_name = new HashMap<Integer, String>();
	// FG ID, Group Name
	
	public static volatile ConcurrentHashMap<String, String> async_set_rank = new ConcurrentHashMap<String, String>();
	// Controls the async thread pool for setRank().
	
	public static HashMap<String, String> rank_map = new HashMap<String, String>();
	
	Thread RankThread;
	
	// TODO: Fix sub++ expiring when sub/sub+ expires, prevent sub++ from showing days left in sub.
	
	public void onEnable() {
		local_IP = Bukkit.getIp();
		
		RankThread = new RankThread();
		RankThread.start();
		
		rank_forumgroup.put("pmod", 11);
		rank_forumgroup.put("sub", 75);
		rank_forumgroup.put("sub+", 76);
		rank_forumgroup.put("sub++", 79);
		rank_forumgroup.put("gm", 7);
		rank_forumgroup.put("wd", 72);
		
		forumgroup_name.put(11, "Player Moderator");
		forumgroup_name.put(75, "Subscriber");
		forumgroup_name.put(76, "Subscriber+");
		forumgroup_name.put(79, "Subscriber++");
		forumgroup_name.put(7, "Moderator");
		forumgroup_name.put(72, "Developer");
		
		server_list.put(0, "72.8.157.66");
		server_list.put(1, "72.20.38.165"); // 74..63.245.13 US-1
		server_list.put(2, "72.20.38.166"); // 74..63.245.14 US-2
		server_list.put(3, "72.8.134.149"); // US-3
		server_list.put(4, "72.8.134.150"); // US-4
		/*server_list.put(5, "72.8.157.57"); // US-5
		server_list.put(6, "72.8.157.58"); // US-6
		server_list.put(7, "69.197.61.57"); // US-7
		server_list.put(8, "69.197.61.58"); // US-8*/
		server_list.put(9, "72.20.33.81"); // US-9 (VIP)
		server_list.put(10, "72.20.33.82"); // US-10 (VIP)
		server_list.put(11, "72.20.42.197"); // US-11 (RP)
		
		server_list.put(2001, "72.20.42.198"); // BR-1
		log.info("" + Bukkit.getOnlinePlayers().length);
		
		new BukkitRunnable(){
			@Override
			public void run() {
				tickSubscriberDays();
				log.info("[DonationMechanics] Ticked all user's subscriber days forward by ONE.");
				
				tickFreeEcash();
				log.info("[DonationMechanics] Reset all 'Free E-cash' users, login for more e-cash!");
			}
		}.runTaskTimerAsynchronously(Main.plugin, 24 * 3600 * 20L, 24 * 3600 * 20L);
		
		new BukkitRunnable(){
			@Override
			public void run() {
				tickLifetimeSubEcash();
				log.info("[DonationMechanics] 999 E-CASH has been given to all sub++ users.");
			}
		}.runTaskTimerAsynchronously(Main.plugin, 30 * 24 * 3600 * 20L, 30 * 24 * 3600 * 20L);
		
		if(new File("plugins/Votifier.jar").exists()){
			log.info("[DonationMechanics] Votifier.jar detected, registering listener.");
			Bukkit.getServer().getPluginManager().registerEvents(new CustomEventListener(this), Main.plugin);
		}
		
	}

	public static String getRank(String p_name){
		p_name = p_name.replaceAll("'", "''");

		//if(!(rank_map.containsKey(p_name))){
			Connection con = null;
			PreparedStatement pst = null;

			try {
				con = DriverManager.getConnection(sql_url, sql_user, sql_password);
				pst = con.prepareStatement( 
						"SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");

				pst.execute();
				ResultSet rs = pst.getResultSet();
				
				if(!rs.next()){
					return "default";
				}
				
				final String rank = rs.getString("rank");
				//final String fp_name = p_name; // TODO Unused
				
				if(rank == null || rank.equalsIgnoreCase("null")){
					return "default";
				}
				
				return rank;

			} catch (SQLException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
				return "default";

			} finally {
				try {
					if (pst != null) {
						pst.close();
					}

				} catch (SQLException ex) {
					log.log(Level.WARNING, ex.getMessage(), ex);
					return "default";
				}
			}
		//}

		//return rank_map.get(p_name);
	}
	
	public static void tickSubscriberDays(){
		/*UPDATE player_database SET player_database.sdays_left=player_database.sdays_left-1 WHERE player_database.sdays_left IS NOT NULL;*/
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement( 
					"UPDATE player_database SET player_database.sdays_left=player_database.sdays_left-1 WHERE player_database.sdays_left IS NOT NULL");

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
	}
	
	public static void tickLifetimeSubEcash(){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement( 
					"UPDATE player_database SET player_database.ecash=player_database.ecash+999 WHERE player_database.rank='sub++'");

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
	}
	
	public static void tickFreeEcash(){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement( 
					"UPDATE player_database SET player_database.online_today=0 WHERE player_database.online_today=1");

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
	}
	
	public static void addPetToPlayer(String p_name, String pet){
		List<String> pet_list = downloadPetData(p_name);
		String pet_string = "";

		for(String s : pet_list){
			pet_string = pet_string + s + ",";
		}

		if(pet_list.contains(pet)){
			return; // Already in list.
		}

		pet_string = pet_string + pet + ",";
		if(pet_string.endsWith(",")){
			pet_string = pet_string.substring(0, pet_string.length() - 1);
		}
		pet_string = pet_string.replaceAll(",,", ",");

		try {
			Connection con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			PreparedStatement pst = con.prepareStatement( 
					"INSERT INTO player_database (p_name, pets)"
							+ " VALUES"
							+ "('" + p_name + "', '" + pet_string +"') ON DUPLICATE KEY UPDATE pets = '" + pet_string + "'");

			pst.executeUpdate();

			if (pst != null) {
				pst.close();
			}

			if (con != null) {
				pst.close();
			}

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public static int getSubscriberDays(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement(
					"SELECT sdays_left FROM player_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return 0;}
			int days_left = rs.getInt("sdays_left");
			return days_left;

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
	
	public static void addSubscriberDays(String p_name, int days_to_add, boolean set){
		Connection con = null;
		PreparedStatement pst = null;
		
		if(!(set)){
			int current_days = getSubscriberDays(p_name);
			days_to_add += current_days;
		}
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement( 
					"INSERT INTO player_database (p_name, sdays_left)"
							+ " VALUES"
							+ "('"+ p_name + "', '"+ days_to_add +"') ON DUPLICATE KEY UPDATE sdays_left ='" + days_to_add + "'");

			pst.executeUpdate();
			log.info("[DonationMechanics] Set " + p_name + "'s REMAINING SUBSCRIBER DAYS to " + days_to_add);

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

	public static List<String> downloadPetData(String pname){
		Connection con = null;
		PreparedStatement pst = null;
		List<String> pet_data = new ArrayList<String>();

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement(
					"SELECT pets FROM player_database WHERE p_name = '" + pname + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return pet_data;}
			String pet_list = rs.getString("pets");
			if(pet_list != null && pet_list.contains(",")){
				for(String s : pet_list.split(",")){
					pet_data.add(s);
				}
			}
			else if(pet_list != null){
				if(pet_list.length() > 0){
					pet_data.add(pet_list);
				}
			}
			return pet_data;

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

		return pet_data;
	}

	public static void setRank(String p_name, String rank){
		async_set_rank.put(p_name, rank);
	}

	public int downloadECASH(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement(
					"SELECT ecash FROM player_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return 0;}
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

	public static void setECASH_SQL(String p_name, int amount){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement( 
					"INSERT INTO player_database (p_name, ecash)"
							+ " VALUES"
							+ "('"+ p_name + "', '"+ amount +"') ON DUPLICATE KEY UPDATE ecash ='" + amount + "'");

			pst.executeUpdate();
			log.info("[DonationMechanics] Set " + p_name + "'s ECASH to " + amount);

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

	public static void sendMessageToProxy(String packet_data){
		Socket kkSocket = null;
		PrintWriter out = null;
		try {

			kkSocket = new Socket();
			//kkSocket.bind(new InetSocketAddress(local_IP, transfer_port+1));
			kkSocket.connect(new InetSocketAddress(Proxy_IP, transfer_port), 250);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			out.println(packet_data);
			log.info("[DonationMechanics] Sent payload to proxy: " + packet_data);
			kkSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(out != null){
			out.close();
		}

	}
	
	public static void sendPacketCrossServer(String packet_data, int server_num, boolean all_servers){
		//String local_ip = Bukkit.getIp(); // TODO - UNUSED

		Socket kkSocket = null;
		PrintWriter out = null;

		if(all_servers){
			for(int sn : server_list.keySet()){
				String server_ip = server_list.get(sn);
				// Send it anyway, fix for not getting insta-ecash.
				/*if(server_ip.equalsIgnoreCase(local_ip)){
					continue; // Don't send to same server.
				}*/
				try {
					kkSocket = new Socket();
					//kkSocket.bind(new InetSocketAddress(local_IP, transfer_port+1));
					kkSocket.connect(new InetSocketAddress(server_ip, transfer_port), 250);
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
				String server_ip = server_list.get(server_num);

				kkSocket = new Socket();
				//kkSocket.bind(new InetSocketAddress(local_IP, transfer_port+1));
				kkSocket.connect(new InetSocketAddress(server_ip, transfer_port), 250);
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

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

		if(cmd.getName().equalsIgnoreCase("rewardsublife")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			
			tickLifetimeSubEcash();
			log.info("[DonationMechanics] 999 E-CASH has been given to all sub++ users.");
		}
		
		if(cmd.getName().equalsIgnoreCase("tickdays")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			
			tickSubscriberDays();
			log.info("[DonationMechanics] Ticked all user's subscriber days forward by ONE.");
			
			tickFreeEcash();
			log.info("[DonationMechanics] Reset all 'Free E-cash' users, login for more e-cash!");
		}
		
		if(cmd.getName().equalsIgnoreCase("addec")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			final String p_name = args[0];
			final int amount = Integer.parseInt(args[1]);

			// Add <amount> E-CASH to player. (player_database->ecash)

			int current = downloadECASH(p_name);
			log.info("[DonationMechanics] Adding " + amount + " ECASH to " + p_name + "'s stash of " + current + " EC!");
			
			current += amount;
			setECASH_SQL(p_name, current);
			sendPacketCrossServer("[ecash]" + p_name + ":" + current, -1, true);
			
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.sendMessage(ChatColor.GOLD + "  +" + amount + ChatColor.BOLD + " E-CASH");
				pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			}
		}

		if(cmd.getName().equalsIgnoreCase("removesubplus")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			String current_rank = getRank(p_name);
			
			if(!current_rank.equalsIgnoreCase("sub+")){
				boolean plus = true;
				removeSubscriber(user_id, plus);
				return true;
			}
			
			sendPacketCrossServer("[rank_map]" + p_name + ":" + "default", -1, true);
			
			if(user_id == -1){
				log.info("[DonationMechanics] Set user " + p_name + " to DEFAULT, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot set DEFAULT status.");
				}
				return true;
			}

			boolean plus = true;
			removeSubscriber(user_id, plus);
			addSubscriberDays(p_name, 0, true);
			setRank(p_name, "default");
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to DEFAULT.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Set user " + p_name + " to DEFAULT. user_id = " + user_id);
		}

		if(cmd.getName().equalsIgnoreCase("removesub")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			
			log.info("d1");
			
			String current_rank = getRank(p_name);
			
			if(!current_rank.equalsIgnoreCase("sub")){
				boolean plus = false;
				removeSubscriber(user_id, plus);
				return true;
			}
			
			log.info("d2");
			
			if(user_id == -1){
				log.info("[DonationMechanics] Set user " + p_name + " to DEFAULT, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot set DEFAULT status.");
				}
				return true;
			}
			
			log.info("d3");

			boolean plus = false;
			removeSubscriber(user_id, plus);
			setRank(p_name, "default");
			
			log.info("d4");
			
			addSubscriberDays(p_name, 0, true);
			sendPacketCrossServer("[rank_map]" + p_name + ":" + "default", -1, true);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to DEFAULT.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Set user " + p_name + " to DEFAULT. user_id = " + user_id);
		}

		if(cmd.getName().equalsIgnoreCase("givesub")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			if(user_id == -1){
				log.info("[DonationMechanics] Granted user " + p_name + " SUBSCRIBER STATUS, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant subscriber status.");
				}
				return true;
			}

			String current_rank = getRank(p_name);
			boolean plus = false;
	
			setAsSubscriber(user_id, plus);
			
			if(!current_rank.equalsIgnoreCase("default")){
				// Don't let them overwrite pmod, sub+, sub++ ... just give them additional forum group.
				return true;
			}
			
			setRank(p_name, "sub");
			addSubscriberDays(p_name, 30, false);
			sendPacketCrossServer("[forum_group]" + p_name + ":" + 75, -1, true);
			sendPacketCrossServer("[rank_map]" + p_name + ":" + "sub", -1, true);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to SUBSCRIBER.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Set user " + p_name + " to SUBSCRIBER. user_id = " + user_id);
		}

		if(cmd.getName().equalsIgnoreCase("givesublife")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			if(user_id == -1){
				log.info("[DonationMechanics] Granted user " + p_name + " SUBSCRIBER++ (LIFETIME) STATUS, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant subscriber++ (LIFETIME) status.");
				}
				return true;
			}
			
			boolean plus = true;
			setAsSubscriber(user_id, plus);
			setRank(p_name, "sub++");
			addSubscriberDays(p_name, 9999, false);
			//addSubscriberDays(p_name, 30, false); Never bother to expire.
			sendPacketCrossServer("[forum_group]" + p_name + ":" + 79, -1, true);
			sendPacketCrossServer("[rank_map]" + p_name + ":" + "sub++", -1, true);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to LIFETIME SUBSCRIBER (SUB++).");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Set user " + p_name + " to LIFETIME SUBSCRIBER (SUB++). user_id = " + user_id);
		}
		
		if(cmd.getName().equalsIgnoreCase("givesubplus")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			if(user_id == -1){
				log.info("[DonationMechanics] Granted user " + p_name + " SUBSCRIBER+ STATUS, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant subscriber+ status.");
				}
				return true;
			}
			
			String current_rank = getRank(p_name);
			boolean plus = true;
			
			setAsSubscriber(user_id, plus);
			
			if(!current_rank.equalsIgnoreCase("default") && !(current_rank.equalsIgnoreCase("sub"))){
				log.info("[DonationMechanics] Not overwriting " + p_name + "'s rank, because they're currently a " + current_rank);
				// Don't let them overwrite pmod, sub+, sub++ ...
				return true;
			}

			setRank(p_name, "sub+");
			addSubscriberDays(p_name, 30, false);
			sendPacketCrossServer("[forum_group]" + p_name + ":" + 76, -1, true);
			sendPacketCrossServer("[rank_map]" + p_name + ":" + "sub+", -1, true);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to SUBSCRIBER+.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Set user " + p_name + " to SUBSCRIBER+. user_id = " + user_id);
		}

		if(cmd.getName().equalsIgnoreCase("givebeta")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(!(ps.isOp())){
					return true;
				}
			}
			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			if(user_id == -1){
				log.info("[DonationMechanics] Granted user " + p_name + " BETA ACCESS, however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant premium access.");
				}
				return true;
			}
			setAsBetaTester(user_id);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Added PREMIUM user " + p_name + " into the Dungeon Realms CLOSED BETA.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
			log.info("[DonationMechanics] Granted user " + p_name + " BETA ACCESS. user_id = " + user_id);
		}

		if(cmd.getName().equalsIgnoreCase("accept")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(args.length <= 0){
					ps.sendMessage(ChatColor.RED + "Incorrect Syntax. " + "/accept " + "<MINECRAFT IGN>");
					return true;
				}
				if(!(ps.isOp())){
					log.info("[DonationMechanics] User " + ps.getName() + " tried to illegally grant access to " + args[0]);
					return true;
				}
			}

			String p_name = args[0];
			int user_id = getForumUserID(p_name);
			if(user_id == -1){
				log.info("[DonationMechanics] Granted user " + p_name + " ACCEPTED APPLICANT (BETA ACCESS), however they didn't have a forum account!");
				if(ps != null){
					ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant access.");
				}
				return true;
			}
			setAsNormalBetaTester(user_id);
			log.info("[DonationMechanics] Granted user " + p_name + " ACCEPTED APPLICANT (BETA ACCESS) user_id = " + user_id);
			if(ps != null){
				ps.sendMessage(ChatColor.GREEN + "Accepted user " + p_name + " into the Dungeon Realms CLOSED BETA.");
				ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
			}
		}

		if(cmd.getName().equalsIgnoreCase("acceptall")){
			Player ps = null;
			if(sender instanceof Player){
				ps = (Player)sender;
				if(args.length != 0){
					ps.sendMessage(ChatColor.RED + "Incorrect Syntax. " + "/acceptall");
					return true;
				}
				if(!(ps.isOp())){
					log.info("[DonationMechanics] User " + ps.getName() + " tried to illegally grant access to ALL");
					return true;
				}
			}

			File f = new File("accept.txt");
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(f));

				String line = "";
				int count = 0;
				while((line = reader.readLine()) != null){
					count++;
					String p_name = line;
					int user_id = getForumUserID(p_name);
					if(user_id == -1){
						log.info("[DonationMechanics] Granted user " + p_name + " ACCEPTED APPLICANT (BETA ACCESS), however they didn't have a forum account!");
						if(ps != null){
							ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant access.");
						}
						continue;
					}

					setAsNormalBetaTester(user_id);
				}
				reader.close();

				log.info("[DonationMechanics] Granted ALL users in accept.txt beta access.");
				if(ps != null){
					ps.sendMessage(ChatColor.GREEN + "Accepted ALL users in accept.txt to closed beta.");
					ps.sendMessage(ChatColor.GRAY + "TOTAL ACCEPTS: " + count);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if(cmd.getName().equalsIgnoreCase("addpet")){
			Player p = null;
			if(sender instanceof Player){
				p = (Player)sender;
				if(!(p.isOp())){return true;}
			}
			if(args.length != 2){
				if(p != null){
					p.sendMessage("Incorrect Syntax. " + "/addpet <player> <pet>");
					return true;
				}
				log.info("[PetMechanics] Incorrect syntax. /addpet <player> <pet>");
			}

			String player = args[0];
			String pet = args[1];

			addPetToPlayer(player, pet);
			sendPacketCrossServer("[addpet]" + player + ":" + pet, -1, true);
			
			log.info("[PetMechanics] Added pet '" + pet + "' to player " + player + ".");
			if(p != null){
				p.sendMessage("Added pet '" + pet + "' to player " + player + ".");
			}
		}
		return true;
	}

	public static int getForumUserID(String mc_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement( 
					"SELECT userid FROM userfield WHERE field5 = '" + mc_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!(rs.next())){
				return -1;
			}

			int userid = rs.getInt("userid");
			return userid;

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

		return -1;
	}

	@SuppressWarnings("resource")
	public void setAsNormalBetaTester(int user_id){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement("SELECT usergroupid, membergroupids FROM user WHERE userid = '" + user_id + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			int primary_rank = -1;
			String all_groups = "";
			if(rs.next()){
				primary_rank = rs.getInt("usergroupid");
			}

			if(primary_rank != 12){ // 9 == Beta Tester
				all_groups = rs.getString("membergroupids");
				if(all_groups.contains("12")){
					all_groups.replaceAll("12,", "");
					all_groups.replaceAll("12", "");
				}
			}

			pst = con.prepareStatement( 
					"INSERT INTO user (userid, usergroupid, membergroupids, usertitle) VALUES('" + user_id + "', '12', '" + all_groups + "', 'Accepted Applicant') ON DUPLICATE KEY UPDATE usertitle = 'Accepted Applicant', usergroupid = '12', membergroupids = '" + all_groups + "'");
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

	}

	@SuppressWarnings("resource")
	public void setAsBetaTester(int user_id){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement("SELECT usergroupid, membergroupids FROM user WHERE userid = '" + user_id + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			int primary_rank = -1;
			String all_groups = "";
			if(rs.next()){
				primary_rank = rs.getInt("usergroupid");
			}

			if(primary_rank != 9){ // 9 == Beta Tester
				all_groups = rs.getString("membergroupids");
				if(all_groups.contains("9")){
					all_groups.replaceAll("9,", "");
					all_groups.replaceAll("9", "");
				}
			}

			pst = con.prepareStatement( 
					"INSERT INTO user (userid, usergroupid, membergroupids, usertitle) VALUES('" + user_id + "', '9', '" + all_groups + "', 'Beta Tester') ON DUPLICATE KEY UPDATE usertitle = 'Beta Tester', usergroupid = '9', membergroupids = '" + all_groups + "'");
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
	}

	@SuppressWarnings("resource")
	public static void addForumGroup(int user_id, int group_id){
		Connection con = null;
		PreparedStatement pst = null;

		String group_name = forumgroup_name.get(group_id);
		
		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement("SELECT usergroupid, membergroupids FROM user WHERE userid = '" + user_id + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			int primary_rank = -1;
			String all_groups = "";
			if(rs.next()){
				primary_rank = rs.getInt("usergroupid");
			}

			if(primary_rank != group_id){
				all_groups = rs.getString("membergroupids");
				if(all_groups.contains("" + group_id)){
					all_groups.replaceAll("" + group_id + ",", "");
					all_groups.replaceAll("" + group_id, "");
				}
				// Ok, but if the primary_rank is not subscriber, we should make sure the primary rank is stored in membergroupids...
				if(primary_rank != group_id && !(all_groups.contains(String.valueOf(primary_rank)))){ // If it's sub -> sub+, just remove the sub group.
					if(all_groups.endsWith(",")){
						all_groups += primary_rank + ",";
					}
					else{
						all_groups += "," + primary_rank + ",";
					}
				}
			}

			all_groups.replaceAll(",,", ",");
			String f_all_groups = "";
			for(String s : all_groups.split(",")){
				if(!(f_all_groups.contains(s))){
					f_all_groups += s + ",";
				}
			}
			if(f_all_groups.endsWith(",")){
				f_all_groups = f_all_groups.substring(0, f_all_groups.length() - 1);
			}

			all_groups = f_all_groups;

			pst = con.prepareStatement( 
					"INSERT INTO user (userid, usergroupid, membergroupids, usertitle) VALUES('" + user_id + "', '" + group_id + "', '" + all_groups + "', '" + group_name + "') ON DUPLICATE KEY UPDATE usertitle = '" + group_name + "', usergroupid = '" + group_id + "', membergroupids = '" + all_groups + "'");
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
	}
	
	@SuppressWarnings("resource")
	public void setAsSubscriber(int user_id, boolean sub_plus){
		Connection con = null;
		PreparedStatement pst = null;

		int group_id = 75;
		String group_name = "Subscriber";

		if(sub_plus){
			group_id = 76;
			group_name = "Subscriber+";
		}

		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement("SELECT usergroupid, membergroupids FROM user WHERE userid = '" + user_id + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			int primary_rank = -1;
			String all_groups = "";
			if(rs.next()){
				primary_rank = rs.getInt("usergroupid");
			}

			if(primary_rank != group_id){
				all_groups = rs.getString("membergroupids");
				if(all_groups.contains("" + group_id)){
					all_groups.replaceAll("" + group_id + ",", "");
					all_groups.replaceAll("" + group_id, "");
				}
				// Ok, but if the primary_rank is not subscriber, we should make sure the primary rank is stored in membergroupids...
				if(!(sub_plus && primary_rank == 75) && !(all_groups.contains(String.valueOf(primary_rank)))){ // If it's sub -> sub+, just remove the sub group.
					if(all_groups.endsWith(",")){
						all_groups += primary_rank + ",";
					}
					else{
						all_groups += "," + primary_rank + ",";
					}
				}
			}

			all_groups.replaceAll(",,", ",");
			String f_all_groups = "";
			for(String s : all_groups.split(",")){
				if(!(f_all_groups.contains(s))){
					f_all_groups += s + ",";
				}
			}
			if(f_all_groups.endsWith(",")){
				f_all_groups = f_all_groups.substring(0, f_all_groups.length() - 1);
			}

			all_groups = f_all_groups;

			pst = con.prepareStatement( 
					"INSERT INTO user (userid, usergroupid, membergroupids, usertitle) VALUES('" + user_id + "', '" + group_id + "', '" + all_groups + "', '" + group_name + "') ON DUPLICATE KEY UPDATE usertitle = '" + group_name + "', usergroupid = '" + group_id + "', membergroupids = '" + all_groups + "'");
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
	}

	@SuppressWarnings("resource")
	public void removeSubscriber(int user_id, boolean sub_plus){
		Connection con = null;
		PreparedStatement pst = null;

		int group_id = 75;
		String group_name = "User";

		if(sub_plus){
			group_id = 76;
		}

		try {
			con = DriverManager.getConnection(site_sql_url, site_sql_user, site_sql_password);
			pst = con.prepareStatement("SELECT usergroupid, membergroupids FROM user WHERE userid = '" + user_id + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			int primary_rank = -1;
			String all_groups = "";
			if(rs.next()){
				primary_rank = rs.getInt("usergroupid");
			}

			if(primary_rank == group_id){ // They're a sub, kill them!
				all_groups = rs.getString("membergroupids");
				if(all_groups.contains("" + group_id)){
					all_groups.replaceAll("" + group_id + ",", "");
					all_groups.replaceAll("" + group_id, "");
				}

				// Remove from all groups.
				if(all_groups.contains(String.valueOf(primary_rank))){
					all_groups = all_groups.replaceAll(primary_rank + ",", "");
					all_groups = all_groups.replaceAll(primary_rank + "", "");
				}
			}

			// Prevent any corrupt user grouppings from mistakes, extra ,, duplicate groups etc.
			all_groups.replaceAll(",,", ",");
			String f_all_groups = "";
			for(String s : all_groups.split(",")){
				if(!(f_all_groups.contains(s))){
					f_all_groups += s + ",";
				}
			}
			if(f_all_groups.endsWith(",")){
				f_all_groups = f_all_groups.substring(0, f_all_groups.length() - 1);
			}

			all_groups = f_all_groups;

			pst = con.prepareStatement( 
					"INSERT INTO user (userid, usergroupid, membergroupids, usertitle) VALUES('" + user_id + "', '" + 2 + "', '" + all_groups + "', '" + group_name + "') ON DUPLICATE KEY UPDATE usertitle = '" + group_name + "', usergroupid = '" + 2 + "', membergroupids = '" + all_groups + "'");
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
	}
}
