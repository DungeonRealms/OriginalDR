package minecade.dungeonrealms.ModerationMechanics;

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

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.ModerationMechanics.commands.*;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.config.Config;
import minecade.dungeonrealms.database.ConnectionPool;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class ModerationMechanics implements Listener {
	
	public static Logger log = Logger.getLogger("Minecraft");
	Thread port_listener;
	public static HashMap<String, String> looking_into_offline_bank = new HashMap<String, String>();
	public static HashMap<String, Integer> report_step = new HashMap<String, Integer>();
	public static HashMap<String, Integer> particle_effects = new HashMap<String, Integer>();
	public static ConcurrentHashMap<String, String> report_data = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, Long> last_unstuck = new ConcurrentHashMap<String, Long>();
	
	public static HashMap<String, Integer> mute_count = new HashMap<String, Integer>();
	public static HashMap<String, Integer> kick_count = new HashMap<String, Integer>();
	public static HashMap<String, Integer> ban_count = new HashMap<String, Integer>();

    public static List<String> allowsFight = new ArrayList<>();
	public static List<String> used_stuck = new ArrayList<String>();
	public static CopyOnWriteArrayList<String> vanish_list = new CopyOnWriteArrayList<String>();
	
	public static List<String> report_types = new ArrayList<String>(Arrays.asList("0", "Bug", "Hacker", "Abuse", "Other"));
	
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for(String s : vanish_list) {
					if(Bukkit.getPlayer(s) != null) {
						Player pl = Bukkit.getPlayer(s);
						for(Player p : Main.plugin.getServer().getOnlinePlayers()) {
							if(p.getName().equalsIgnoreCase(pl.getName())) {
								continue;
							}
							if(PermissionMechanics.isGM(p.getName()) || p.isOp()) {
								p.showPlayer(pl);
								continue;
							}
							p.hidePlayer(pl);
						}
					}
				}
			}
		}.runTaskTimer(Main.plugin, 5 * 20L, 1L);
		
		initializeCommands();
		
		log.info("[ModerationMechanics] has been enabled.");
	}
	
	private void initializeCommands() {
		Main.plugin.getCommand("mute").setExecutor(new CommandMute());
		Main.plugin.getCommand("unmute").setExecutor(new CommandUnmute());
		Main.plugin.getCommand("drkick").setExecutor(new CommandDRKick());
		Main.plugin.getCommand("ipban").setExecutor(new CommandIPBan());
		Main.plugin.getCommand("drban").setExecutor(new CommandDRBan());
		Main.plugin.getCommand("unban").setExecutor(new CommandUnban());
		Main.plugin.getCommand("report").setExecutor(new CommandReport());
		Main.plugin.getCommand("check").setExecutor(new CommandCheck());
		Main.plugin.getCommand("stuck").setExecutor(new CommandStuck());
		Main.plugin.getCommand("sayall").setExecutor(new CommandSayAll());
		Main.plugin.getCommand("lock").setExecutor(new CommandLock());
		Main.plugin.getCommand("unlock").setExecutor(new CommandUnlock());
		Main.plugin.getCommand("banksee").setExecutor(new CommandBankSee());
		Main.plugin.getCommand("drtppos").setExecutor(new CommandDRTPPos());
		Main.plugin.getCommand("armorsee").setExecutor(new CommandArmorSee());
		Main.plugin.getCommand("realmclone").setExecutor(new CommandRealmClone());
		Main.plugin.getCommand("playerclone").setExecutor(new CommandPlayerClone());
		Main.plugin.getCommand("drvanish").setExecutor(new CommandDRVanish());
		Main.plugin.getCommand("unbanksee").setExecutor(new CommandUnBankSee());
		Main.plugin.getCommand("sendpacket").setExecutor(new CommandSendPacket());
		Main.plugin.getCommand("setalignment").setExecutor(new CommandSetAlignment());
        Main.plugin.getCommand("allowfight").setExecutor(new CommandAllowFight());
	}
	
	public void onDisable() {
		log.info("[ModerationMechanics] has been disabled.");
	}
	
	public void doParticleEffects() {
		if(particle_effects.size() <= 0) { return; }
		List<String> to_remove = new ArrayList<String>();
		for(Entry<String, Integer> data : particle_effects.entrySet()) {
			String p_name = data.getKey();
			int count = data.getValue();
			
			if(count == 6) {
				to_remove.add(p_name);
				continue;
			}
			
			count += 1;
			particle_effects.put(p_name, count);
			
			if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline()) {
				Player pl = Bukkit.getPlayer(p_name);
				Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(pl.getLocation().getX()), (int) Math.round(pl.getLocation().getY()), (int) Math.round(pl.getLocation().getZ()), 30, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(pl.getLocation().getX(), pl.getLocation().getY(), pl.getLocation().getZ(), 24, ((CraftWorld) pl.getWorld()).getHandle().dimension, particles);
			}
			
		}
		
		for(String s : to_remove) {
			particle_effects.remove(s);
		}
	}
	
	public static int getBanCount(String p_name) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			pst = ConnectionPool.getConnection().prepareStatement("SELECT ban_count FROM ban_list WHERE pname = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return 0; }
			int ban_count = rs.getInt("ban_count");
			return ban_count;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				if(con != null) {
					con.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return 0;
	}
	
	public static void IPBanPlayer(String IP) {
		Connection con = null;
		PreparedStatement pst = null;
		List<String> associated_players = new ArrayList<String>();
		
		if(!IP.contains(".")) {
			// Probably a username.
			try {
				pst = ConnectionPool.getConnection().prepareStatement("SELECT ip FROM player_database WHERE p_name='" + IP + "'");
				
				pst.execute();
				ResultSet rs = pst.getResultSet();
				if(!rs.next()) {
					// We have no IP on file. Cannot ban?
					log.info("[ModerationMechanics] Cannot IPBAN -> Found NO IP for player: " + IP);
					return;
				}
				
				IP = rs.getString("ip");
				// Ok, resolved the IP.
				log.info("[ModerationMechanics] Resolved IP to: " + IP);
				
				if(IP == null) {
					log.info("[ModerationMechanics] Yikes! Could not locate IP -- player hasn't logged in since we added tracking!");
				}
				
			} catch(SQLException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
				
			} finally {
				try {
					if(pst != null) {
						pst.close();
					}
					if(con != null) {
						con.close();
					}
					
				} catch(SQLException ex) {
					log.log(Level.WARNING, ex.getMessage(), ex);
				}
			}
		}
		
		if(!(IP.contains(","))) {
			IP += ","; // Add trailing , for compat.
		}
		
		try {
			for(String s_ip : IP.split(",")) {
				if(s_ip.length() <= 1) {
					continue; // For compat. trailing ','.
				}
				log.info("[ModerationMechanics] Processing IP: " + s_ip);
				
				pst = ConnectionPool.getConnection().prepareStatement("SELECT p_name FROM player_database WHERE ip like '%" + s_ip + "%'");
				
				pst.execute();
				ResultSet rs = pst.getResultSet();
				if(!rs.next()) {
					// Just input the IP.
					if(s_ip.contains(".")) {
						Hive.sql_query.add("INSERT INTO ban_list (pname, ip)" + " VALUES" + "('" + s_ip + "', '" + s_ip + "') " + "ON DUPLICATE KEY UPDATE ip='" + s_ip + "'");
						
						log.info("Banned IP " + s_ip + ", but found no linked account data.");
					}
				}
				
				associated_players.add(rs.getString("p_name")); // Need this, or it skips first index.
				
				while(rs.next()) {
					associated_players.add(rs.getString("p_name"));
				}
			}
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				if(con != null) {
					con.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		// TODO: Make this work for multiple IP's.
		if(associated_players.size() > 0) {
			for(String s : associated_players) {
				log.info("[ModerationMechanics] IP Ban issued to user " + s + ".");
				for(String s_ip : IP.split(",")) {
					// This will only ban the first IP in the sequence.
					if(s_ip.length() <= 1) {
						continue;
					}
					
					Hive.sql_query.add("INSERT INTO ban_list (pname, ip)" + " VALUES" + "('" + s + "', '" + s_ip + "') " + "ON DUPLICATE KEY UPDATE ip='" + s_ip + "'");
				}
			}
		} else {
			log.info("[ModerationMechanics] Found no additional associated accounts.");
		}
	}
	
	public static void BanPlayer(final String p_name, final long unban_time, final String reason, final String banner, final boolean perm) {
		String rank = PermissionMechanics.getRank(p_name);
		
		long date = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dt = dateFormat.format(date);
		String unban_time_s = dateFormat.format(unban_time);
		int ban_count = getBanCount(p_name) + 1;
		
		String sperm = "0";
		
		if(perm == true) {
			sperm = "1";
		}
		
		// TODO: We need to get if the player being banned is already perma'd, and if so then ignore a temp.
		
		Hive.sql_query.add("INSERT INTO ban_list (pname, unban_date, ban_reason, who_banned, ban_date, rank, ban_count, perm)" + " VALUES" + "('" + p_name + "', '" + unban_time_s + "', '" + reason + "', '" + banner + "', '" + dt + "', '" + rank + "', '" + ban_count + "', '" + sperm + "') " + "ON DUPLICATE KEY UPDATE unban_date = '" + unban_time_s + "', ban_reason = '" + reason + "', who_banned = '" + banner + "', ban_date = '" + dt + "', rank = '" + rank + "', ban_count = '" + ban_count + "', unban_reason = '" + "" + "', who_unbanned = '" + "" + "', perm = '" + sperm + "'");
		
	}
	
	public static void unbanPlayer(final String p_name, final String reason, final String unbanner) {
		
		long unban_time = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String futuredate = dateFormat.format(unban_time);
		
		Hive.sql_query.add("INSERT INTO ban_list (pname, unban_date, unban_reason, who_unbanned)" + " VALUES" + "('" + p_name + "', '" + futuredate + "', '" + reason + "', '" + unbanner + "') " + "ON DUPLICATE KEY UPDATE unban_date = '" + futuredate + "', unban_reason = '" + reason + "', who_unbanned = '" + unbanner + "'");
		
	}
	
	public static String getUnbanReason(String p_name) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(Config.sql_url, Config.sql_user, Config.sql_password);
			pst = ConnectionPool.getConnection().prepareStatement("SELECT unban_reason FROM ban_list WHERE pname = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return ""; }
			String unban_reason = rs.getString("unban_reason");
			if(unban_reason == null || unban_reason.equalsIgnoreCase("")) { return null; }
			return unban_reason;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				if(con != null) {
					con.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return null;
	}
	
	public static long getUnbanDate(String p_name) {
		// 0 = Not banned.
		// -1 = Never.
		// ### = Then.
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			pst = ConnectionPool.getConnection().prepareStatement("SELECT unban_date, perm FROM ban_list WHERE pname = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return 0; }
			
			Date unban_date = rs.getDate("unban_date");
			String perm = rs.getString("perm");
			if(perm != null && perm.equalsIgnoreCase("1")) { return -1; }
			return unban_date.getTime();
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				if(con != null) {
					con.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return 0;
	}
	
	public void sendReport(final String lreport_data) {
		String report_type = lreport_data.substring(0, lreport_data.indexOf("@"));
		String reporter_name = lreport_data.substring(lreport_data.indexOf("@") + 1, lreport_data.indexOf(":"));
		String report = "";
		String offender = "N/A";
		String server_name = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		try {
			report = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.lastIndexOf(":"));
			offender = lreport_data.substring(lreport_data.lastIndexOf(":") + 1, lreport_data.length());
		} catch(StringIndexOutOfBoundsException e) {
			report = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.length());
		}
		
		report = report.replaceAll("'", "''");
		
		long current_date = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format_date = dateFormat.format(current_date);
		
		Hive.sql_query.add("INSERT INTO reports (type, reporter, offender, report, server, time)" + " VALUES" + "('" + report_type + "', '" + reporter_name + "', '" + offender + "', '" + report + "', '" + server_name + "', '" + format_date + "') ");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		report_data.remove(p.getName());
		report_step.remove(p.getName());
		used_stuck.remove(p.getName());
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncChatEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(report_step.containsKey(p.getName())) { // bug@username:report:meta   hack@availer:I saw him fly hacking omg:vaquxine
			e.setCancelled(true);
			int step = report_step.get(p.getName());
			String msg = e.getMessage();
			
			if(msg.equalsIgnoreCase("cancel")) {
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "                *** REPORT CANCELLED ***");
				p.sendMessage("");
				report_data.remove(p.getName());
				report_step.remove(p.getName());
				return;
			}
			
			if(step == 1) {
				int report_type = 0;
				try {
					report_type = Integer.parseInt(msg);
				} catch(NumberFormatException nfe) {
					p.sendMessage(ChatColor.DARK_RED + "Please enter the " + ChatColor.BOLD + "TOPIC # [1-4]" + ChatColor.DARK_RED + " of the subject of this report.");
					p.sendMessage(ChatColor.GRAY + "EX: 1 " + ChatColor.ITALIC + "(bug report)");
					return;
				}
				
				if(report_type != 1 && report_type != 2 && report_type != 3 && report_type != 4) {
					p.sendMessage(ChatColor.DARK_RED + "Please enter the " + ChatColor.BOLD + "TOPIC # [1-4]" + ChatColor.DARK_RED + " of the subject of this report.");
					p.sendMessage(ChatColor.GRAY + "EX: 1 " + ChatColor.ITALIC + "(bug report)");
					return;
				}
				
				String report_type_s = report_types.get(report_type);
				String lreport_data = report_type_s + "@" + p.getName(); // bug@Vaquxine
				
				if(report_type == 2) { // Hacker!
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "FULL MINECRAFT NAME" + ChatColor.GRAY + " of the hacker.");
					report_step.put(p.getName(), 2);
					report_data.put(p.getName(), lreport_data);
				}
				if(report_type == 3) { // Abuse!
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "FULL MINECRAFT NAME" + ChatColor.GRAY + " of the abuser.");
					report_step.put(p.getName(), 2);
					report_data.put(p.getName(), lreport_data);
				} else if(report_type != 2 && report_type != 3) {
					report_step.put(p.getName(), 3);
					lreport_data += ":";
					report_data.put(p.getName(), lreport_data);
					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "DETAILS" + ChatColor.GRAY + " of your report; once you are satisfied with your report, type '" + ChatColor.DARK_RED + "submit" + ChatColor.GRAY + "' to SEND your report.");
					
					if(report_type == 1) { // BUG!
						p.sendMessage(ChatColor.GRAY + "Please include specific information about how to recreate this BUG in a clean environment.");
					}
					if(report_type == 4) { // OTHER!
						p.sendMessage(ChatColor.GRAY + "Please include relevant information about what you are reporting, where it occured, when it occured, and who was involved.");
					}
				}
			}
			
			if(step == 2) {
				String hacker_name = msg;
				if(CommunityMechanics.getLastLogin(hacker_name, false) == -1L) {
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
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "DETAILS" + ChatColor.GRAY + " of your report; once you are satisfied with your report, type '" + ChatColor.DARK_RED + "submit" + ChatColor.GRAY + "' to SEND your report.");
				if(report_type.equalsIgnoreCase("hacker")) {
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Please include specific information about what hacks you witnessed this player using, the exact time these hacks where used, and any additional witnesses or relevant information.");
				}
				if(report_type.equalsIgnoreCase("abuse")) {
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Please include specific information about what abuse you witnessed this player engaging in, the approx. time this abuse took place, and any additional witnesses or relevant information.");
				}
				p.sendMessage("");
			}
			
			if(step == 3) {
				String lreport_data = report_data.get(p.getName());
				
				if(msg.equalsIgnoreCase("submit")) {
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
				
				if(report_type.equalsIgnoreCase("hacker") || report_type.equalsIgnoreCase("abuse")) {
					String hacker_name = lreport_data.substring(lreport_data.lastIndexOf(":") + 1, lreport_data.length());
					log.info(lreport_data);
					current_details = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.lastIndexOf(":"));
					current_details += " " + msg;
					lreport_data = report_type + "@" + p.getName() + ":" + current_details + ":" + hacker_name;
				} else {
					current_details = lreport_data.substring(lreport_data.indexOf(":") + 1, lreport_data.length());
					current_details += " " + msg;
					lreport_data = report_type + "@" + p.getName() + ":" + current_details;
				}
				
				report_data.put(p.getName(), lreport_data);
				
				int word_count = current_details.length();
				if(word_count >= 512) {
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
	
	public static int getPlayerServer(String p_name) {
		return Hive.getPlayerServer(p_name, false);
	}
	
	public static boolean isPlayerOnline(String p_name) {
		int server_num = getPlayerServer(p_name);
		if(server_num == -1 || server_num == -2) {
			return false;
		} else {
			return true;
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.getGameMode() != GameMode.SURVIVAL && !(p.isOp())) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		
		if(!(mute_count.containsKey(p.getName()))) {
			mute_count.put(p.getName(), 0);
		}
		if(!(kick_count.containsKey(p.getName()))) {
			kick_count.put(p.getName(), 0);
		}
		if(!(ban_count.containsKey(p.getName()))) {
			ban_count.put(p.getName(), 0);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory().getTitle().contains("CLONE OF")) {
			Player p = (Player) e.getPlayer();
			//They must just be looking into an online players bank.
			if(!looking_into_offline_bank.containsKey(p.getName())) return;
			p.sendMessage(ChatColor.RED + looking_into_offline_bank.get(p.getName()) + "'s bank is still loaded into memory.");
			p.sendMessage(ChatColor.GRAY + "To unload it use /unbanksee");
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
	
}
