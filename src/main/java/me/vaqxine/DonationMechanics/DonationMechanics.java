package me.vaqxine.DonationMechanics;

import java.io.File;
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
import me.vaqxine.DonationMechanics.commands.CommandAccept;
import me.vaqxine.DonationMechanics.commands.CommandAcceptAll;
import me.vaqxine.DonationMechanics.commands.CommandAddEC;
import me.vaqxine.DonationMechanics.commands.CommandAddPet;
import me.vaqxine.DonationMechanics.commands.CommandGiveBeta;
import me.vaqxine.DonationMechanics.commands.CommandGiveSub;
import me.vaqxine.DonationMechanics.commands.CommandGiveSubLife;
import me.vaqxine.DonationMechanics.commands.CommandGiveSubPlus;
import me.vaqxine.DonationMechanics.commands.CommandRemoveSub;
import me.vaqxine.DonationMechanics.commands.CommandRemoveSubPlus;
import me.vaqxine.DonationMechanics.commands.CommandRewardSubLife;
import me.vaqxine.DonationMechanics.commands.CommandTickDays;

import org.bukkit.Bukkit;
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
		
		Main.plugin.getCommand("accept").setExecutor(new CommandAccept());
		Main.plugin.getCommand("acceptall").setExecutor(new CommandAcceptAll());
		Main.plugin.getCommand("addec").setExecutor(new CommandAddEC());
		Main.plugin.getCommand("addpet").setExecutor(new CommandAddPet());
		Main.plugin.getCommand("givebeta").setExecutor(new CommandGiveBeta());
		Main.plugin.getCommand("givesub").setExecutor(new CommandGiveSub());
		Main.plugin.getCommand("givesublife").setExecutor(new CommandGiveSubLife());
		Main.plugin.getCommand("givesubplus").setExecutor(new CommandGiveSubPlus());
		Main.plugin.getCommand("removesub").setExecutor(new CommandRemoveSub());
		Main.plugin.getCommand("removesubplus").setExecutor(new CommandRemoveSubPlus());
		Main.plugin.getCommand("rewardsublife").setExecutor(new CommandRewardSubLife());
		Main.plugin.getCommand("tickdays").setExecutor(new CommandTickDays());
		
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

	public static int downloadECASH(String p_name){
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
	public static void setAsNormalBetaTester(int user_id){
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
	public static void setAsBetaTester(int user_id){
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
	public static void setAsSubscriber(int user_id, boolean sub_plus){
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
	public static void removeSubscriber(int user_id, boolean sub_plus){
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
