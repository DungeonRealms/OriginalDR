package minecade.dungeonrealms.DonationMechanics;

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

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DonationMechanics.commands.CommandAddEC;
import minecade.dungeonrealms.DonationMechanics.commands.CommandAddPet;
import minecade.dungeonrealms.DonationMechanics.commands.CommandAddSubDaysToAll;
import minecade.dungeonrealms.DonationMechanics.commands.CommandGiveSub;
import minecade.dungeonrealms.DonationMechanics.commands.CommandGiveSubLife;
import minecade.dungeonrealms.DonationMechanics.commands.CommandGiveSubPlus;
import minecade.dungeonrealms.DonationMechanics.commands.CommandRemoveSub;
import minecade.dungeonrealms.DonationMechanics.commands.CommandRemoveSubPlus;
import minecade.dungeonrealms.DonationMechanics.commands.CommandRewardSubLife;
import minecade.dungeonrealms.config.Config;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class DonationMechanics implements Listener {
	
	static Logger log = Logger.getLogger("Minecraft");
	
	public final static String sql_url = "jdbc:mysql://72.20.40.38:7447/dungeonrealms";
	public final static String sql_user = "slave_3XNZvi";
	public final static String sql_password = "SgUmxYSJSFmOdro3";
	
	public final static int transfer_port = 6427;
	
	public static String Hive_IP = "72.20.40.38";
	
	public static String local_IP = "";
	
	public static HashMap<Integer, String> server_list = new HashMap<Integer, String>();
	// Server #, Server IP
	
	public static volatile ConcurrentHashMap<String, String> async_set_rank = new ConcurrentHashMap<String, String>();
	// Controls the async thread pool for setRank().
	
	Thread RankThread;
	
	// TODO: Fix sub++ expiring when sub/sub+ expires, prevent sub++ from showing days left in sub.
	
	public void onEnable() {
		local_IP = Bukkit.getIp();
		
		Main.plugin.getCommand("addec").setExecutor(new CommandAddEC());
		Main.plugin.getCommand("addpet").setExecutor(new CommandAddPet());
		Main.plugin.getCommand("givesub").setExecutor(new CommandGiveSub());
		Main.plugin.getCommand("givesublife").setExecutor(new CommandGiveSubLife());
		Main.plugin.getCommand("givesubplus").setExecutor(new CommandGiveSubPlus());
		Main.plugin.getCommand("removesub").setExecutor(new CommandRemoveSub());
		Main.plugin.getCommand("removesubplus").setExecutor(new CommandRemoveSubPlus());
		Main.plugin.getCommand("rewardsublife").setExecutor(new CommandRewardSubLife());
		Main.plugin.getCommand("addsubdaystoall").setExecutor(new CommandAddSubDaysToAll());
		
		RankThread = new RankThread();
		RankThread.start();
		
		server_list.put(0, "72.8.157.66:32777");
		server_list.put(1, "72.20.37.210:10001");
		server_list.put(2, "72.20.37.210:10002");
		server_list.put(3, "72.20.37.210:10003");
		server_list.put(4, "72.20.37.210:10004"); 
		server_list.put(5, "72.20.37.210:10005");
		server_list.put(100, "72.20.55.80:25151");
		
		log.info("" + Bukkit.getOnlinePlayers().length);
		
		/* Do NOT uncomment!  The timer is now handled by a separate donation-dedicated server!
		if (Bukkit.getMotd().contains("US-0")) {
			Calendar nextUpdate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			nextUpdate.set(nextUpdate.get(Calendar.YEAR), nextUpdate.get(Calendar.MONTH), nextUpdate.get(Calendar.DATE) + 1, 0, 0);
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			Long msToUp = nextUpdate.getTimeInMillis() - now.getTimeInMillis();
			Long secToUp = TimeUnit.MILLISECONDS.toSeconds(msToUp);
			log.info("[DonationMechanics] Scheduled daily donation duties to run in "
					+ String.format(
							"%d hours, %d minutes, %d seconds",
							TimeUnit.MILLISECONDS.toHours(msToUp),
							TimeUnit.MILLISECONDS.toMinutes(msToUp)
									- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(msToUp)), secToUp
									- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(msToUp))));

			new BukkitRunnable() {

				@Override
				public void run() {
					log.info("[DonationMechanics] Time is 12:00:00 AM ES/DT.  Running daily donation duties.");
					tickSubscriberDays();
					log.info("[DonationMechanics] Ticked all user's subscriber days forward by ONE.");
					
					tickFreeEcash();
					log.info("[DonationMechanics] Reset all 'Free E-cash' users, login for more e-cash!");
					
					tickLifetimeSubEcash();
					log.info("[DonationMechanics] 999 E-CASH has been given to all sub++ users.");
				}
				
			}.runTaskLaterAsynchronously(Main.plugin, secToUp * 20L);
			
		}
		*/
			
		if(new File("plugins/Votifier.jar").exists()) {
			log.info("[DonationMechanics] Votifier.jar detected, registering listener.");
			Bukkit.getServer().getPluginManager().registerEvents(new CustomEventListener(this), Main.plugin);
		}
		
	}
	
	public static String getRank(String p_name) {
		p_name = p_name.replaceAll("'", "''");
		
		//if(!(rank_map.containsKey(p_name))){
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			
			if(!rs.next()) { return "default"; }
			
			final String rank = rs.getString("rank");
			
			if(rank == null || rank.equalsIgnoreCase("null")) { return "default"; }
			
			return rank;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			return "default";
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
				return "default";
			}
		}
		//}
		
		//return rank_map.get(p_name);
	}
	
	public static void addSubscriberDaysToAll(int number) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("UPDATE player_database SET player_database.sdays_left = player_database.sdays_left + ? WHERE player_database.sdays_left IS NOT NULL");
			pst.setInt(1, number);
			pst.executeUpdate();
			
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
	
	public static void tickSubscriberDays() {
		/*UPDATE player_database SET player_database.sdays_left=player_database.sdays_left-1 WHERE player_database.sdays_left IS NOT NULL;*/
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("UPDATE player_database SET player_database.sdays_left=player_database.sdays_left-1 WHERE player_database.sdays_left IS NOT NULL");
			
			pst.executeUpdate();
			
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
	
	public static void tickLifetimeSubEcash() {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("UPDATE player_database SET player_database.ecash=player_database.ecash+999 WHERE player_database.rank='sub++'");
			
			pst.executeUpdate();
			
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
	
	public static void tickFreeEcash() {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("UPDATE player_database SET player_database.online_today=0 WHERE player_database.online_today=1");
			
			pst.executeUpdate();
			
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
	
	public static void addPetToPlayer(String p_name, String pet) {
		List<String> pet_list = downloadPetData(p_name);
		String pet_string = "";
		
		for(String s : pet_list) {
			pet_string = pet_string + s + ",";
		}
		
		if(pet_list.contains(pet)) { return; // Already in list.
		}
		
		pet_string = pet_string + pet + ",";
		if(pet_string.endsWith(",")) {
			pet_string = pet_string.substring(0, pet_string.length() - 1);
		}
		pet_string = pet_string.replaceAll(",,", ",");
		
		try {
			Connection con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			PreparedStatement pst = con.prepareStatement("INSERT INTO player_database (p_name, pets)" + " VALUES" + "('" + p_name + "', '" + pet_string + "') ON DUPLICATE KEY UPDATE pets = '" + pet_string + "'");
			
			pst.executeUpdate();
			
			if(pst != null) {
				pst.close();
			}
			
			if(con != null) {
				pst.close();
			}
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public static int getSubscriberDays(String p_name) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("SELECT sdays_left FROM player_database WHERE p_name = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return 0; }
			int days_left = rs.getInt("sdays_left");
			return days_left;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return 0;
	}
	
	public static void addSubscriberDays(String p_name, int days_to_add, boolean set) {
		Connection con = null;
		PreparedStatement pst = null;
		
		if(!(set)) {
			int current_days = getSubscriberDays(p_name);
			days_to_add += current_days;
		}
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("INSERT INTO player_database (p_name, sdays_left)" + " VALUES" + "('" + p_name + "', '" + days_to_add + "') ON DUPLICATE KEY UPDATE sdays_left ='" + days_to_add + "'");
			
			pst.executeUpdate();
			log.info("[DonationMechanics] Set " + p_name + "'s REMAINING SUBSCRIBER DAYS to " + days_to_add);
			
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
	
	public static List<String> downloadPetData(String pname) {
		Connection con = null;
		PreparedStatement pst = null;
		List<String> pet_data = new ArrayList<String>();
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("SELECT pets FROM player_database WHERE p_name = '" + pname + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return pet_data; }
			String pet_list = rs.getString("pets");
			if(pet_list != null && pet_list.contains(",")) {
				for(String s : pet_list.split(",")) {
					pet_data.add(s);
				}
			} else if(pet_list != null) {
				if(pet_list.length() > 0) {
					pet_data.add(pet_list);
				}
			}
			return pet_data;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return pet_data;
	}
	
	public static void setRank(String p_name, String rank) {
		async_set_rank.put(p_name, rank);
	}
	
	public static int downloadECASH(String p_name) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("SELECT ecash FROM player_database WHERE p_name = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) { return 0; }
			int amount = rs.getInt("ecash");
			return amount;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return 0;
	}
	
	public static void setECASH_SQL(String p_name, int amount) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = DriverManager.getConnection(sql_url, sql_user, sql_password);
			pst = con.prepareStatement("INSERT INTO player_database (p_name, ecash)" + " VALUES" + "('" + p_name + "', '" + amount + "') ON DUPLICATE KEY UPDATE ecash ='" + amount + "'");
			
			pst.executeUpdate();
			log.info("[DonationMechanics] Set " + p_name + "'s ECASH to " + amount);
			
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
	
	public static void sendPacketCrossServer(String packet_data, int server_num, boolean all_servers) {
		Socket kkSocket = null;
		PrintWriter out = null;
		
		if(all_servers) {
			for(int sn : server_list.keySet()) {
			    String ipAndPort = CommunityMechanics.server_list.get(sn);
                String ipNoPort = ipAndPort.contains(":") ? CommunityMechanics.server_list.get(sn).split(":")[0]
                        : ipAndPort;
                int port = ipAndPort.contains(":") && StringUtils.isNumeric(ipAndPort.split(":")[1]) ? Integer.parseInt(ipAndPort
                        .split(":")[1]) : Config.transfer_port;
				// Send it anyway, fix for not getting insta-ecash.
				/*if(server_ip.equalsIgnoreCase(local_ip)){
					continue; // Don't send to same server.
				}*/
				try {
					kkSocket = new Socket();
					//kkSocket.bind(new InetSocketAddress(local_IP, transfer_port+1));
					kkSocket.connect(new InetSocketAddress(ipNoPort, port), 250);
					out = new PrintWriter(kkSocket.getOutputStream(), true);
					
					out.println(packet_data);
					
				} catch(IOException e) {
					if(out != null) {
						out.close();
					}
					continue;
				}
				
				if(out != null) {
					out.close();
				}
			}
		} else if(!all_servers) {
			try {
			    String ipAndPort = CommunityMechanics.server_list.get(server_num);
                String ipNoPort = ipAndPort.contains(":") ? CommunityMechanics.server_list.get(server_num).split(":")[0]
                        : ipAndPort;
                int port = ipAndPort.contains(":") && StringUtils.isNumeric(ipAndPort.split(":")[1]) ? Integer.parseInt(ipAndPort
                        .split(":")[1]) : Config.transfer_port;
				
				kkSocket = new Socket();
				//kkSocket.bind(new InetSocketAddress(local_IP, transfer_port+1));
				kkSocket.connect(new InetSocketAddress(ipNoPort, port), 250);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
				
				out.println(packet_data);
				
			} catch(IOException e) {
				
			} finally {
				if(out != null) {
					out.close();
				}
			}
			
			if(out != null) {
				out.close();
			}
		}
		
	}
	
}