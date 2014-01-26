package me.vaqxine.ModerationMechanics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
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
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MobEffect;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ModerationMechanics extends JavaPlugin implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	Thread port_listener;

	static HashMap<String, Integer> report_step = new HashMap<String, Integer>();
	static HashMap<String, Integer> particle_effects = new HashMap<String, Integer>();
	static ConcurrentHashMap<String, String> report_data = new ConcurrentHashMap<String, String>();
	static ConcurrentHashMap<String, Long> last_unstuck = new ConcurrentHashMap<String, Long>();

	static HashMap<String, Integer> mute_count = new HashMap<String, Integer>();
	static HashMap<String, Integer> kick_count = new HashMap<String, Integer>();
	static HashMap<String, Integer> ban_count = new HashMap<String, Integer>();

	public static List<String> used_stuck = new ArrayList<String>();
	public static CopyOnWriteArrayList<String> vanish_list = new CopyOnWriteArrayList<String>();

	public static List<String> report_types = new ArrayList<String>(Arrays.asList("0", "Bug", "Hacker", "Abuse", "Other"));

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				ConnectionPool.refresh = true;
			}
		}, 120 * 20L, 120 * 20L);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(String s : vanish_list){
					if(Bukkit.getPlayer(s) != null){
						Player pl = Bukkit.getPlayer(s);
						for(Player p : getServer().getOnlinePlayers()){
							if(p.getName().equalsIgnoreCase(pl.getName())){continue;}
							p.hidePlayer(pl);
						}
					}
				}
			}
		}, 5 * 20L, 1L);

		log.info("[ModerationMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[ModerationMechanics] has been disabled.");
	}

	public void doParticleEffects(){
		if(particle_effects.size() <= 0){
			return;
		}
		List<String> to_remove = new ArrayList<String>();
		for(Entry<String, Integer> data : particle_effects.entrySet()){
			String p_name = data.getKey();
			int count = data.getValue();

			if(count == 6){
				to_remove.add(p_name);
				continue;
			}

			count += 1;
			particle_effects.put(p_name, count);

			if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline()){
				Player pl = Bukkit.getPlayer(p_name);
				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(pl.getLocation().getX()), (int)Math.round(pl.getLocation().getY()), (int)Math.round(pl.getLocation().getZ()), 30, false);
				((CraftServer) getServer()).getServer().getPlayerList().sendPacketNearby(pl.getLocation().getX(), pl.getLocation().getY(), pl.getLocation().getZ(), 24, ((CraftWorld) pl.getWorld()).getHandle().dimension, particles);
			}

		}

		for(String s : to_remove){
			particle_effects.remove(s);
		}
	}

	public static int getBanCount(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT ban_count FROM ban_list WHERE pname = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return 0;}
			int ban_count = rs.getInt("ban_count");
			return ban_count;

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

		return 0;
	}

	public static void IPBanPlayer(String IP){
		Connection con = null;
		PreparedStatement pst = null;
		List<String> associated_players = new ArrayList<String>();

		if(!IP.contains(".")){
			// Probably a username.
			try {
				pst = ConnectionPool.getConneciton().prepareStatement( 
						"SELECT ip FROM player_database WHERE p_name='" + IP + "'");

				pst.execute();
				ResultSet rs = pst.getResultSet();
				if(!rs.next()){
					// We have no IP on file. Cannot ban?
					log.info("[ModerationMechanics] Cannot IPBAN -> Found NO IP for player: " + IP);
					return;
				}

				IP = rs.getString("ip");
				// Ok, resolved the IP.
				log.info("[ModerationMechanics] Resolved IP to: " + IP);

				if(IP == null){
					log.info("[ModerationMechanics] Yikes! Could not locate IP -- player hasn't logged in since we added tracking!");
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

		if(!(IP.contains(","))){
			IP += ","; // Add trailing , for compat.
		}
		
		try {
			for(String s_ip : IP.split(",")){
				if(s_ip.length() <= 1){
					continue; // For compat. trailing ','.
				}
				log.info("[ModerationMechanics] Processing IP: " + s_ip);
				
				pst = ConnectionPool.getConneciton().prepareStatement( 
						"SELECT p_name FROM player_database WHERE ip like '%" + s_ip + "%'");

				pst.execute();
				ResultSet rs = pst.getResultSet();
				if(!rs.next()){
					// Just input the IP.
					if(s_ip.contains(".")){
						Hive.sql_query.add("INSERT INTO ban_list (pname, ip)"
								+ " VALUES" + "('" + s_ip + "', '"+ s_ip + "') "  
								+ "ON DUPLICATE KEY UPDATE ip='" + s_ip + "'");

						log.info("Banned IP " + s_ip + ", but found no linked account data.");
					}
				}

				associated_players.add(rs.getString("p_name")); // Need this, or it skips first index.
				
				while(rs.next()){
					associated_players.add(rs.getString("p_name"));
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

		// TODO: Make this work for multiple IP's.
		if(associated_players.size() > 0){
			for(String s : associated_players){
				log.info("[ModerationMechanics] IP Ban issued to user " + s + ".");
				for(String s_ip : IP.split(",")){
					// This will only ban the first IP in the sequence.
					if(s_ip.length() <= 1){
						continue;
					}
					
				Hive.sql_query.add("INSERT INTO ban_list (pname, ip)"
						+ " VALUES" + "('" + s + "', '" + s_ip + "') "  
						+ "ON DUPLICATE KEY UPDATE ip='" + s_ip + "'");
				}
			}
		}
		else{
			log.info("[ModerationMechanics] Found no additional associated accounts.");
		}

		Socket kkSocket = null;
		PrintWriter out = null;
		try {

			kkSocket = new Socket();
			//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
			kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 500);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			out.println("[ipban]" + IP);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if(out != null){
			out.close();
		}
	}

	public static void BanPlayer(final String p_name, final long unban_time, final String reason, final String banner, final boolean perm){
		String rank = PermissionMechanics.getRank(p_name);

		long date = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dt = dateFormat.format(date);
		String unban_time_s = dateFormat.format(unban_time);
		int ban_count = getBanCount(p_name) + 1;

		String sperm = "0";

		if(perm == true){
			sperm = "1";
		}

		// TODO: We need to get if the player being banned is already perma'd, and if so then ignore a temp.
		
		Socket kkSocket = null;
		PrintWriter out = null;
		try {
			kkSocket = new Socket();
			//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
			
			kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 5000); // 5000ms timeout
			out = new PrintWriter(kkSocket.getOutputStream(), true);

			if(perm == true){
				out.println("[ban]" + p_name + "@" + "-1");
			}
			else{
				out.println("[ban]" + p_name + "@" + (unban_time - System.currentTimeMillis()));
			}
			kkSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		if(out != null){
			out.close();
		}

		Hive.sql_query.add("INSERT INTO ban_list (pname, unban_date, ban_reason, who_banned, ban_date, rank, ban_count, perm)"
				+ " VALUES"
				+ "('"+ p_name + "', '"+ unban_time_s + "', '"+ reason + "', '" + banner + "', '" + dt + "', '" + rank + "', '" + ban_count + "', '" + sperm + "') "  
				+ "ON DUPLICATE KEY UPDATE unban_date = '" + unban_time_s + "', ban_reason = '" + reason + "', who_banned = '" + banner 
				+ "', ban_date = '" + dt + "', rank = '" + rank + "', ban_count = '" + ban_count + "', unban_reason = '" + "" + "', who_unbanned = '" + "" + "', perm = '" + sperm + "'");

	}

	public static void unbanPlayer(final String p_name, final String reason, final String unbanner){

		long unban_time = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String futuredate = dateFormat.format(unban_time);


		Hive.sql_query.add("INSERT INTO ban_list (pname, unban_date, unban_reason, who_unbanned)"
				+ " VALUES"
				+ "('"+ p_name + "', '"+ futuredate + "', '" + reason + "', '" + unbanner + "') "  
				+ "ON DUPLICATE KEY UPDATE unban_date = '" + futuredate + "', unban_reason = '" + reason + "', who_unbanned = '" + unbanner + "'");

		Socket kkSocket = null;
		PrintWriter out = null;
		try {

			kkSocket = new Socket();
			//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
			kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 500);
			out = new PrintWriter(kkSocket.getOutputStream(), true);

			out.println("[unban]" + p_name);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if(out != null){
			out.close();
		}
	}

	public static String getUnbanReason(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
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

		return null;
	}

	public static long getUnbanDate(String p_name){
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

		return 0;
	}

	public void sendReport(final String lreport_data){
		String report_type = lreport_data.substring(0, lreport_data.indexOf("@"));
		String reporter_name = lreport_data.substring(lreport_data.indexOf("@") + 1, lreport_data.indexOf(":"));
		String report = "";
		String offender = "N/A";
		String server_name = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		try{
			report = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.lastIndexOf(":"));
			offender = lreport_data.substring(lreport_data.lastIndexOf(":") + 1, lreport_data.length());
		} catch(StringIndexOutOfBoundsException e){
			report = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.length());
		}

		report = report.replaceAll("'", "''");

		long current_date = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format_date = dateFormat.format(current_date);

		Hive.sql_query.add("INSERT INTO reports (type, reporter, offender, report, server, time)" + " VALUES"
				+ "('"+ report_type + "', '"+ reporter_name + "', '" + offender + "', '" + report + "', '" + server_name + "', '" + format_date + "') ");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		report_data.remove(p.getName());
		report_step.remove(p.getName());
		used_stuck.remove(p.getName());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncChatEvent(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(report_step.containsKey(p.getName())){ // bug@username:report:meta   hack@availer:I saw him fly hacking omg:vaquxine
			e.setCancelled(true);
			int step = report_step.get(p.getName());
			String msg = e.getMessage();

			if(msg.equalsIgnoreCase("cancel")){
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "                *** REPORT CANCELLED ***");
				p.sendMessage("");
				report_data.remove(p.getName());
				report_step.remove(p.getName());
				return;
			}

			if(step == 1){
				int report_type = 0;
				try{
					report_type = Integer.parseInt(msg);
				} catch(NumberFormatException nfe){
					p.sendMessage(ChatColor.DARK_RED + "Please enter the " + ChatColor.BOLD + "TOPIC # [1-4]" + ChatColor.DARK_RED + " of the subject of this report.");
					p.sendMessage(ChatColor.GRAY + "EX: 1 " + ChatColor.ITALIC + "(bug report)");
					return;
				}

				if(report_type != 1 && report_type != 2 && report_type != 3 && report_type != 4){
					p.sendMessage(ChatColor.DARK_RED + "Please enter the " + ChatColor.BOLD + "TOPIC # [1-4]" + ChatColor.DARK_RED + " of the subject of this report.");
					p.sendMessage(ChatColor.GRAY + "EX: 1 " + ChatColor.ITALIC + "(bug report)");
					return;
				}

				String report_type_s = report_types.get(report_type);
				String lreport_data = report_type_s + "@" + p.getName(); // bug@Vaquxine

				if(report_type == 2){ // Hacker!
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "FULL MINECRAFT NAME" + ChatColor.GRAY + " of the hacker.");
					report_step.put(p.getName(), 2);
					report_data.put(p.getName(), lreport_data);
				}
				if(report_type == 3){ // Abuse!
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "FULL MINECRAFT NAME" + ChatColor.GRAY + " of the abuser.");
					report_step.put(p.getName(), 2);
					report_data.put(p.getName(), lreport_data);
				}
				else if(report_type != 2 && report_type != 3){
					report_step.put(p.getName(), 3);
					lreport_data += ":";
					report_data.put(p.getName(), lreport_data);
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "DETAILS" 
							+ ChatColor.GRAY + " of your report; once you are satisfied with your report, type '" + ChatColor.DARK_RED + "submit" + ChatColor.GRAY + "' to SEND your report.");

					if(report_type == 1){ // BUG!
						p.sendMessage(ChatColor.GRAY + "Please include specific information about how to recreate this BUG in a clean environment.");
					}
					if(report_type == 4){ // OTHER!
						p.sendMessage(ChatColor.GRAY + "Please include relevant information about what you are reporting, where it occured, when it occured, and who was involved.");
					}
				}
			}

			if(step == 2){
				String hacker_name = msg;
				if(CommunityMechanics.getLastLogin(hacker_name, false) == -1L){
					p.sendMessage(ChatColor.DARK_RED + "The player " + ChatColor.BOLD + hacker_name + ChatColor.DARK_RED + " has NEVER logged in to Dungeon Realms before.");
					return;
				}


				String lreport_data = report_data.get(p.getName());
				String report_type = lreport_data.substring(0, lreport_data.indexOf("@"));
				lreport_data += "::" + hacker_name;
				report_data.put(p.getName(), lreport_data);
				report_step.put(p.getName(), 3);

				p.sendMessage(ChatColor.GRAY + hacker_name);
				p.sendMessage("");
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "DETAILS" 
						+ ChatColor.GRAY + " of your report; once you are satisfied with your report, type '" + ChatColor.DARK_RED + "submit" + ChatColor.GRAY + "' to SEND your report.");
				if(report_type.equalsIgnoreCase("hacker")){
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Please include specific information about what hacks you witnessed this player using, the exact time these hacks where used, and any additional witnesses or relevant information.");
				}
				if(report_type.equalsIgnoreCase("abuse")){
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Please include specific information about what abuse you witnessed this player engaging in, the approx. time this abuse took place, and any additional witnesses or relevant information.");
				}
				p.sendMessage("");
			}

			if(step == 3){
				String lreport_data = report_data.get(p.getName());

				if(msg.equalsIgnoreCase("submit")){
					//TODO: sendTicket(lreport_data); (multithread)
					sendReport(lreport_data);
					p.sendMessage(ChatColor.GREEN + "                 " + ChatColor.BOLD + "REPORT SUBMITTED, THANK YOU!");
					p.sendMessage("");
					p.playSound(p.getLocation(), Sound.COW_IDLE, 1F, 1F);
					report_step.remove(p.getName());
					report_data.remove(p.getName());
					return;
				}

				String report_type = lreport_data.substring(0, lreport_data.indexOf("@"));
				String current_details = "";

				if(report_type.equalsIgnoreCase("hacker") || report_type.equalsIgnoreCase("abuse")){
					String hacker_name = lreport_data.substring(lreport_data.lastIndexOf(":") + 1, lreport_data.length());
					log.info(lreport_data);
					current_details = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.lastIndexOf(":"));
					current_details += " " + msg;
					lreport_data = report_type + "@" + p.getName() + ":" + current_details + ":" + hacker_name;
				}
				else{
					current_details = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.length());
					current_details += " " + msg;
					lreport_data = report_type + "@" + p.getName() + ":" + current_details;
				}

				report_data.put(p.getName(), lreport_data);

				int word_count = current_details.length();
				if(word_count >= 512){
					p.sendMessage(ChatColor.DARK_RED + "Maximum character count reached [512], sending report...");
					sendReport(lreport_data);
					p.sendMessage(ChatColor.GREEN + "                 " + ChatColor.BOLD + "REPORT SUBMITTED, THANK YOU!");
					p.sendMessage("");
					report_step.remove(p.getName());
					report_data.remove(p.getName());
					return;
				}

				p.sendMessage(ChatColor.GRAY + "" + word_count + "/512 characters. Type '" + ChatColor.DARK_RED + "submit" + ChatColor.GRAY + "' to SEND your report.");

			}

		}
	}

	public static int getPlayerServer(String p_name){
		return Hive.getPlayerServer(p_name, false);
	}

	public static boolean isPlayerOnline(String p_name){
		int server_num = getPlayerServer(p_name);
		if(server_num == -1 || server_num == -2){return false;}
		else{
			return true;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.getGameMode() != GameMode.SURVIVAL && !(p.isOp())){
			p.setGameMode(GameMode.SURVIVAL);
		}

		if(!(mute_count.containsKey(p.getName()))){
			mute_count.put(p.getName(), 0);
		}
		if(!(kick_count.containsKey(p.getName()))){
			kick_count.put(p.getName(), 0);
		}
		if(!(ban_count.containsKey(p.getName()))){
			ban_count.put(p.getName(), 0);
		}
	}

	/*public static void sendPacketCrossServer(String packet_data, int server_num, boolean all_servers){
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
					kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 500);
					out = new PrintWriter(kkSocket.getOutputStream(), true);

					out.println(packet_data);

				} catch (Exception err) {
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
				try {
					out = new PrintWriter(CommunityMechanics.getSocket(server_num).getOutputStream(), true);
				} catch (Exception e) {
					if(out != null){
						out.close();
					}
					kkSocket = new Socket();
					//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
					kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 1000);
					out = new PrintWriter(kkSocket.getOutputStream(), true);
				}

				out.println(packet_data);

			} catch (IOException e) {
				if(out != null){
					out.close();
				}
				return;
			}

			if(out != null){
				out.close();
			}
		}
	}*/

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = null;
		if(sender instanceof Player){
			p = (Player)sender;
		}

		if(cmd.getName().equalsIgnoreCase("drtppos")){
			if(p != null && !p.isOp()){
				return true;
			}

			if(args.length != 3){
				p.sendMessage("/drtppos X Y Z");
				return true;
			}

			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			int z = Integer.parseInt(args[2]);

			p.teleport(new Location(p.getWorld(), x, y, z));
		}

		if(cmd.getName().equalsIgnoreCase("sayall")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			String msg = "";
			for(String s : args){
				msg += s + " ";
			}

			msg = "!!!" + msg;
			final String fmsg = msg;


			//Thread t = new Thread(new Runnable(){
			//	public void run(){
			for(final String ip : CommunityMechanics.server_list.values()){
				CommunityMechanics.sendPacketCrossServer(fmsg, ip);
			}
			//	}
			//});

			//t.start();
		}


		if(cmd.getName().equalsIgnoreCase("lock")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			String msg = "";
			msg = "{LOCK}";

			if(args[0].equalsIgnoreCase("*")){
				for(String ip : CommunityMechanics.server_list.values()){
					CommunityMechanics.sendPacketCrossServer(msg, ip);
					log.info("[ModerationMechanics] Sent server LOCK request to " + ip);
				}
			}
			else{
				String ip = args[0];
				CommunityMechanics.sendPacketCrossServer(msg, ip);
				log.info("[ModerationMechanics] Sent server LOCK request to " + ip);
			}
		}

		if(cmd.getName().equalsIgnoreCase("unlock")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			String msg = "";
			msg = "{UNLOCK}";

			if(args[0].equalsIgnoreCase("*")){
				for(String ip : CommunityMechanics.server_list.values()){
					CommunityMechanics.sendPacketCrossServer(msg, ip);
					log.info("[ModerationMechanics] Sent server UnLOCK request to " + ip);
				}
			}
			else{
				String ip = args[0];
				CommunityMechanics.sendPacketCrossServer(msg, ip);
				log.info("[ModerationMechanics] Sent server UnLOCK request to " + ip);
			}
		}

		if(cmd.getName().equalsIgnoreCase("stuck")){
			/*if(args.length != 0){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/stuck");
				}
				return true;
			}

			Location p_loc = p.getLocation();

			if(!p_loc.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
				p.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.BOLD + "/stuck" + ChatColor.RED + " in a player owned realm.");
				return true;
			}*/

			p.sendMessage(ChatColor.RED + "This command has been " + ChatColor.BOLD + "DISABLED" + ChatColor.RED + " due to abuse.");
			p.sendMessage(ChatColor.GRAY + "If you are still in need of assistance, please contact a GM ingame, on the forums or teamspeak; or, submit a /report and " + ChatColor.UNDERLINE + "be sure to include your coordinates.");
			return true;

			/*if(!HealthMechanics.in_combat.containsKey(p.getName())){
				if(used_stuck.contains(p.getName())){
					p.sendMessage(ChatColor.RED + "You have already used " + ChatColor.BOLD + "/stuck" + ChatColor.RED + " in this session.");
					p.sendMessage(ChatColor.GRAY + "If you are still in need of assistance, please contact a GM on the forums or teamspeak; or, submit a /report.");
					return true;
				}

				used_stuck.add(p.getName());

				if(last_unstuck.containsKey(p.getName())){
					long last_time = last_unstuck.get(p.getName());
					if((System.currentTimeMillis() - last_time) <= 360 * 1000){
						int difference = Math.round(((360 * 1000) - (System.currentTimeMillis() - last_time)) / 1000); 
						p.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.BOLD + "/stuck" + ChatColor.RED + ". You may use it again in " + ChatColor.BOLD + difference + "s...");
						return true;
					}
				}
				particle_effects.put(p.getName(), 0);
				p.setVelocity(new Vector(0,1.0F,0));
				last_unstuck.put(p.getName(), System.currentTimeMillis());
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "* UNSTUCK! *");
				return true;
				/*if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("evil")){
						p.teleport(new Location(Bukkit.getWorlds().get(0), -414, 62, 620));
						p.sendMessage(ChatColor.GREEN + "* UNSTUCK! *");
						return true;
					}
					p.teleport(SpawnMechanics.getRandomSpawnPoint());
					p.sendMessage(ChatColor.GREEN + "* UNSTUCK! *");
					return true;
			}*/

		}

		if(cmd.getName().equalsIgnoreCase("report")){
			if(args.length != 0){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/report");
				}
				return true;
			}
			if(report_step.containsKey(p.getName())){
				p.sendMessage(ChatColor.RED + "Please complete your pending REPORT before filing a new one. Type 'cancel' to void your pending report.");
				return true;
			}

			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_RED + "                " + ChatColor.BOLD + "*** NEW REPORT SUBMISSION ***");
			p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "TOPIC #" + ChatColor.GRAY + " of the report to submit.");
			p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + ChatColor.BOLD + "(1)" + ChatColor.GRAY + " Bug" + "   " + ChatColor.BOLD + "(2)" + ChatColor.GRAY + " Hacker" + "   " + ChatColor.BOLD + "(3)" + ChatColor.GRAY + " Abuse" + "   " + ChatColor.BOLD + "(4)" + ChatColor.GRAY + " Other");
			report_step.put(p.getName(), 1);
		}

		if(cmd.getName().equalsIgnoreCase("armorsee")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			if(args.length <= 0){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/armorsee <PLAYER>");
				}
				return true;
			}

			String p_name = args[0];
			Inventory inv = Bukkit.createInventory(null, 9, "ARMOR OF " + p_name);
			Inventory inv_clone = null;
			if(Bukkit.getPlayer(p_name) != null){
				Player victim = Bukkit.getPlayer(p_name);
				for(ItemStack is : victim.getInventory().getArmorContents()){
					inv.addItem(CraftItemStack.asCraftCopy(is));
				}
			}
			else{
				p.sendMessage(ChatColor.RED + "The player " + p_name + "'s armor data is not loaded, and therfore cannot be displayed.");
				p.sendMessage(ChatColor.GRAY + "In a later update, I will make it possible to view offline armor data.");
				return true;
			}

			if(inv != null){
				p.openInventory(inv);
				p.sendMessage(ChatColor.GREEN + "Displaying the current armor contents of " + p_name);
			}

		}

		if(cmd.getName().equalsIgnoreCase("banksee")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			if(args.length <= 0){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/banksee <PLAYER>");
				}
				return true;
			}

			String p_name = args[0];
			Inventory inv = Bukkit.createInventory(null, 54, "CLONE OF " + p_name);
			Inventory inv_clone = null;
			if(Bukkit.getPlayer(p_name) != null){
				p_name = Bukkit.getPlayer(p_name).getName();
			}
			if(MoneyMechanics.bank_contents.containsKey(p_name)){
				// Data is already locally downloaded, we're in luck.
				inv_clone = MoneyMechanics.bank_contents.get(p_name).get(0);
				for(ItemStack is : inv_clone){
					if(is == null || is.getType() == Material.AIR){
						continue;
					}
					inv.addItem(is);
				}
			}
			else{
				p.sendMessage(ChatColor.RED + "The player " + p_name + "'s bank data is not loaded, and therfore cannot be displayed.");
				p.sendMessage(ChatColor.GRAY + "In a later update, I will make it possible to view offline bank data.");
				return true;
			}

			if(inv != null){
				p.openInventory(inv);
				p.sendMessage(ChatColor.GREEN + "Displaying the current bank contents of " + p_name);
			}

		}

		if(cmd.getName().equalsIgnoreCase("realmclone")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			if(args.length <= 0){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/realmclone <PLAYER>");
				}
				return true;
			}

			final String p_name = args[0];
			final Player mod = p;

			if(!Bukkit.getMotd().contains("US-0")){
				mod.sendMessage("What you thinking? Trying to use /realmclone on a public server? Go to US-0.");
				return true;
			}

			getServer().unloadWorld(Bukkit.getWorld(p.getName()), false);
			File world_root = new File(RealmMechanics.rootDir + "/" + p.getName());
			RealmMechanics.deleteFolder(world_root);

			mod.sendMessage(ChatColor.RED + "CLONING REALM OF " + p_name + " ....");
			//mod.sendMessage(ChatColor.RED + "YOU WILL NEED TO MANUALLY PLACE PORTAL ONCE THE DOWNLOAD IS COMPLETE.");
			Location portal_location = p.getLocation().add(0, 1, 0);
			p.getWorld().playEffect(portal_location, Effect.ENDER_SIGNAL, 20, 5);
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 5F, 1.25F);
			RealmMechanics.has_portal.put(p.getName(), true);
			RealmMechanics.makePortal(p.getName(), portal_location.subtract(0, 2, 0), 60);

			Thread t = new Thread(new Runnable() {
				public void run() {
					RealmMechanics.realmHandler(mod, p_name);
				}
			});

			t.start();

		}


		if(cmd.getName().equalsIgnoreCase("unban")){
			String rank = "";
			if(p != null){
				rank = PermissionMechanics.getRank(p.getName());
				if(rank == null){
					return true;
				}

				if(!p.isOp() && !rank.equalsIgnoreCase("gm")){
					return true;
				}
			}

			if(args.length <= 1){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/unban <PLAYER> <REASON>");
				}
				return true;
			}

			String unbanner = "Console";
			if(p != null){
				unbanner = p.getName();
			}

			String p_name = args[0];
			String reason = "";

			for(int i = 1; i < args.length; i++){
				reason += args[i] + " ";
			}

			unbanPlayer(p_name, reason, unbanner);
			log.info("[ModerationMechanics] UNBANNED player " + p_name + " for " + reason + "by " + unbanner);

			if(p != null){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "UNBANNED" + ChatColor.RED + " player " + p_name + " because " + reason);
			}
		}

		if(cmd.getName().equalsIgnoreCase("ban")){
			String rank = "";
			boolean perm = false;
			if(p != null){
				rank = PermissionMechanics.getRank(p.getName());
				if(rank == null){
					return true;
				}

				if(!(p.isOp()) && !rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm")){
					return true;
				}
			}

			if(args.length <= 2){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/ban <PLAYER> <TIME(in hours)> <REASON>");
					p.sendMessage(ChatColor.GRAY + "Insert -1 for <TIME> to permentantly lock.");
				}
				return true;
			}

			String banner = "Console";
			if(p != null){
				banner = p.getName();
			}
			final String p_name = args[0];
			int hours = 24;
			try{
				hours = Integer.parseInt(args[1]);
			} catch(NumberFormatException nfe){
				if(p != null){
					p.sendMessage(ChatColor.RED + "Invalid time entired for hours of duration for the ban.");
					p.sendMessage(ChatColor.GRAY + "You entered: " + args[1] + ", which is not a numberic value.");
					return true;
				}
			}

			if(p != null){
				if(rank.equalsIgnoreCase("pmod") && ((hours > 24) || hours == -1)){
					p.sendMessage(ChatColor.RED + "As a PLAYER MODERATOR, you can only ban players for up to 24 hours.");
					return true;
				}
				int count = ban_count.get(p.getName());
				if(rank.equalsIgnoreCase("pmod") && count >= 10){
					p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " bans today.");
					return true;
				}
				count += 1;
				ban_count.put(p.getName(), count);
			}

			final long unban_date = (System.currentTimeMillis() + (1000 * (hours * 3600)));
			String reason = "";

			for(int i = 2; i < args.length; i++){
				reason += args[i] + " ";
			}

			if(hours == -1){
				perm = true;
			}

			final String f_reason = reason;
			final String f_banner = banner;
			final boolean f_perm = perm;

			if(PermissionMechanics.getRank(p_name).equalsIgnoreCase("gm") || (Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOp() && sender instanceof Player)){
				p.sendMessage(ChatColor.RED + "You cannot ban a Game Moderator unless you have console acesss.");
				return true;
			}

			//ShopMechanics.removeShop(p); TODO: This causes exceptions.
			
			
			Thread ban_player = new Thread(new Runnable(){
				public void run(){
					try{Thread.sleep(100);} catch(Exception err){} // Wait 100ms -- this should occur after function has returned.
					BanPlayer(p_name, unban_date, f_reason, f_banner, f_perm);
				}
			});
			ban_player.start();


			if(Bukkit.getPlayer(p_name) != null){
				Player banned = Bukkit.getPlayer(p_name);
				if(reason == ""){
					banned.kickPlayer(ChatColor.RED.toString() + "Your account has been TEMPORARILY locked due to suspisious activity." + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
				}
				else if(reason.length() > 0){
					banned.kickPlayer(ChatColor.RED.toString() + "Your account has been TEMPORARILY locked due to " + reason + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
				}
			}
			else{
				Thread t = new Thread(new Runnable(){
					public void run(){
						CommunityMechanics.sendPacketCrossServer("@ban@" + p_name + ":" + f_reason, -1, true);
					}
				});

				t.start();
			}

			if(p != null){
				p.sendMessage(ChatColor.AQUA + "You have banned the user '" + p_name + "' for " + hours + " hours.");
				p.sendMessage(ChatColor.GRAY + "Reason: " + reason);
			}

			log.info("[ModerationMechanics] BANNED player " + p_name + " for " + hours + " hours because " + reason);
		}

		if(cmd.getName().equalsIgnoreCase("ipban")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			if(args.length != 1){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/ipban <IP / PLAYER>");
					p.sendMessage(ChatColor.GRAY + "All IP bans are permanent.");
				}
				return true;
			}

			final String IP = args[0];
			// Check if they gave us an IP or a player.

			Thread t = new Thread(new Runnable(){
				public void run(){
					IPBanPlayer(IP);
				}
			});

			t.start();

			if(p != null){
				p.sendMessage("IP ban issued for " + IP + "...");
			}
			else{
				log.info("IP ban issued for " + IP + "...");
			}
		}

		if(cmd.getName().equalsIgnoreCase("playerclone")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
				
				if(args.length != 1 && args.length != 2){
					if(p != null){
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/playerclone <PLAYER>");
						p.sendMessage(ChatColor.GRAY + "Copies all player data of <PLAYER> to your account.");
					}
					return true;
				}
				
				String to_name = p.getName();
				
				if(args.length == 2){
					to_name = args[1];
				}
				final String p_name = args[0];
				
				Hive.sql_query.add("DELETE FROM player_database where p_name='" + to_name + "'");
				Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_playerdb SELECT * FROM player_database WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("UPDATE tmp_playerdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("INSERT INTO player_database SELECT * FROM tmp_playerdb WHERE p_name='" + to_name + "'");
				Hive.sql_query.add("DROP TABLE tmp_playerdb");
				
				Hive.sql_query.add("DELETE FROM bank_database where p_name='" + to_name + "'");
				Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_bankdb SELECT * FROM bank_database WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("UPDATE tmp_bankdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("INSERT INTO bank_database SELECT * FROM tmp_bankdb WHERE p_name='" + to_name + "'");
				Hive.sql_query.add("DROP TABLE tmp_bankdb");
				
				Hive.sql_query.add("DELETE FROM shop_database where p_name='" + to_name + "'");
				Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_shopdb SELECT * FROM shop_database WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("UPDATE tmp_shopdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
				Hive.sql_query.add("INSERT INTO shop_database SELECT * FROM tmp_shopdb WHERE p_name='" + to_name + "'");
				Hive.sql_query.add("DROP TABLE tmp_shopdb");
				
				Hive.no_upload.add(to_name);
				
				final String f_to_name = to_name;
				
				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						if(Bukkit.getPlayer(f_to_name) != null){
							Bukkit.getPlayer(f_to_name).kickPlayer(p_name + "'s data is copying, please wait 5-10 seconds and login.");
						}
					}
				}, 2L);
				
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("kick")){
			if(p != null){
				String rank = PermissionMechanics.getRank(p.getName());
				if(rank == null){
					return true;
				}

				if(!(p.isOp()) && !rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm")){
					return true;
				}
			}

			if(args.length <= 1){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/kick <PLAYER> <REASON>");
				}
				return true;
			}

			if(p != null){
				int count = kick_count.get(p.getName());
				if(count >= 50){
					p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " kicks today.");
					return true;
				}
				count += 1;
				kick_count.put(p.getName(), count);
			}


			String p_name_2kick = args[0];
			String reason = "";

			for(String s : args){
				if(s.equalsIgnoreCase(p_name_2kick)){
					continue; //args[0]
				}
				reason += s + " ";
			}

			if(Bukkit.getPlayer(p_name_2kick) != null && Bukkit.getPlayer(p_name_2kick).isOnline()){
				p_name_2kick = Bukkit.getPlayer(p_name_2kick).getName();
			}

			if(p != null){
				p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "KICKED" + ChatColor.AQUA + " the user " + ChatColor.BOLD + p_name_2kick + ChatColor.AQUA + " from all servers.");
				p.sendMessage(ChatColor.GRAY + "REASON: " + reason);
			}

			if(Bukkit.getPlayer(p_name_2kick) != null && Bukkit.getPlayer(p_name_2kick).isOnline()){
				Player kicked = Bukkit.getPlayer(p_name_2kick);
				kicked.kickPlayer(reason);
			}
			else if(isPlayerOnline(p_name_2kick)){ // @kick@notch:reason
				int server_num = getPlayerServer(p_name_2kick);
				CommunityMechanics.sendPacketCrossServer("@kick@" + p_name_2kick + ":" + reason, server_num, false);
			}

		}

		if(cmd.getName().equalsIgnoreCase("drvanish")){
			if(!(p.isOp())){
				return true;
			}

			if(vanish_list.contains(p.getName())){
				vanish_list.remove(p.getName());
				for(Player pl : getServer().getOnlinePlayers()){
					if(pl.getName().equalsIgnoreCase(p.getName())){
						continue;
					}
					pl.showPlayer(p);
				}
				p.sendMessage(ChatColor.RED + "You are now " + ChatColor.BOLD + "visible.");
			}
			else{
				vanish_list.add(p.getName());
				p.sendMessage(ChatColor.GREEN + "You are now " + ChatColor.BOLD + "invisible.");
			}
		}

		if(cmd.getName().equalsIgnoreCase("mute")){
			String rank = "";
			if(p != null){
				rank = PermissionMechanics.getRank(p.getName());
				if(rank == null){
					return true;
				}

				if(!rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm") && !(p.isOp())){
					return true;
				}
			}

			if(args.length != 2){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/mute <PLAYER> <TIME(in minutes)>");
				}
				return true;
			}

			String p_name_2mute = args[0];
			int minutes_to_mute = 0;
			try{
				minutes_to_mute = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Non-Numeric Time. " + ChatColor.RED + "/mute <PLAYER> <TIME(in minutes)>");
				return true;
			}

			if(p != null){
				if(rank.equalsIgnoreCase("pmod") && (minutes_to_mute > 1440)){
					p.sendMessage(ChatColor.RED + "As a PLAYER MODERATOR, you can only mute players for up to 24 hours. (1440 minutes)");
					return true;
				}

				int count = mute_count.get(p.getName());
				if(count >= 20){
					p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " mutes today.");
					return true;
				}

				count += 1;
				mute_count.put(p.getName(), count);
			}


			//long unmute_time = (System.currentTimeMillis() + ((minutes_to_mute * 60) * 1000));

			if(Bukkit.getPlayer(p_name_2mute) != null && Bukkit.getPlayer(p_name_2mute).isOnline()){
				p_name_2mute = Bukkit.getPlayer(p_name_2mute).getName();
				if(PermissionMechanics.getRank(p_name_2mute).equalsIgnoreCase("gm")){
					p.sendMessage(ChatColor.RED + "You cannot mute a Game Moderator.");
					return true;
				}
			}

			ChatMechanics.mute_list.put(p_name_2mute, (long)minutes_to_mute);
			ChatMechanics.setMuteStateSQL(p_name_2mute);

			if(p != null){
				p.sendMessage(ChatColor.AQUA + "You have issued a " + minutes_to_mute + " minute " + ChatColor.BOLD + "MUTE" + ChatColor.AQUA + " on the user " + ChatColor.BOLD + p_name_2mute);
				p.sendMessage(ChatColor.GRAY + "If this was made in error, type '/unmute " + p_name_2mute + "'");
			}
			else if(p == null){
				log.info("[ModerationMechanics] Muted player " + p_name_2mute + " for " + minutes_to_mute + " minute(s).");
			}

			String banner = "SYSTEM";
			if(p != null){
				banner = p.getName();
			}

			if(Bukkit.getPlayer(p_name_2mute) != null && Bukkit.getPlayer(p_name_2mute).isOnline()){
				Player muted = Bukkit.getPlayer(p_name_2mute);
				muted.sendMessage("");
				muted.sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + " by " + ChatColor.BOLD + banner + ChatColor.RED + " for " + minutes_to_mute + " minute(s).");
				muted.sendMessage("");
			} else if (isPlayerOnline(p_name_2mute)){
				int server_num = getPlayerServer(p_name_2mute);
				CommunityMechanics.sendPacketCrossServer("@mute@" + p.getName() + "/" + p_name_2mute + ":" + minutes_to_mute, server_num, false);
				//ConnectProtocol.sendResultCrossServer(CommunityMechanics.server_list.get(server_num), "@mute@" + p.getName() + "/" + p_name_2mute + ":" + unmute_time);
			}
		}
		if(cmd.getName().equalsIgnoreCase("unmute")){
			if(p != null){
				String rank = PermissionMechanics.getRank(p.getName());
				if(rank == null){
					return true;
				}

				if(!rank.equalsIgnoreCase("gm") && !p.isOp()){
					return true;
				}
			}

			if(args.length != 1){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/unmute <PLAYER>");
				}
				return true;
			}

			String p_name_2unmute = args[0];
			if(Bukkit.getPlayer(p_name_2unmute) != null){
				p_name_2unmute = Bukkit.getPlayer(p_name_2unmute).getName(); // Fixes capitalization issues.
			}

			ChatMechanics.mute_list.remove(p_name_2unmute);
			ChatMechanics.setMuteStateSQL(p_name_2unmute);

			if(p != null){
				p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "UNMUTED " + ChatColor.AQUA + p_name_2unmute);
			}
			else if(p == null){
				log.info("[ModerationMechanics] Unmuted player " + p_name_2unmute + ".");
			}

			if(Bukkit.getPlayer(p_name_2unmute) != null && Bukkit.getPlayer(p_name_2unmute).isOnline()){
				Player p_2unmute = Bukkit.getPlayer(p_name_2unmute);
				p_2unmute.sendMessage("");
				p_2unmute.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has been removed.");
				p_2unmute.sendMessage("");
			} else if(isPlayerOnline(p_name_2unmute)){
				int server_num = getPlayerServer(p_name_2unmute);
				CommunityMechanics.sendPacketCrossServer("@unmute@" + p_name_2unmute, server_num, false);
				//ConnectProtocol.sendResultCrossServer(CommunityMechanics.server_list.get(server_num), "@unmute@" + p_name_2unmute);
			}

		}
		return true;
	}

}
