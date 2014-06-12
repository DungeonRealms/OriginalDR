package minecade.dungeonrealms.RecordMechanics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.database.ConnectionPool;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RecordMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	static HashMap<String, String> statistics = new HashMap<String, String>();

	public static HashMap<String, Integer> player_deaths = new HashMap<String, Integer>();
	public static HashMap<String, List<Integer>> player_kills = new HashMap<String, List<Integer>>(); // unlawful,lawful
	// Money can be obtained from MoneyMechanics.bank_map
	public static HashMap<String, Integer> mob_kills = new HashMap<String, Integer>();

	public static HashMap<String, List<Integer>> duel_statistics = new HashMap<String, List<Integer>>();

	// Player Name, List<new_loss_count, new_win_count>

	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		log.info("[RecordMechanics] has been ENABLED. LOGGING DATA!");
	}

	public void onDisable() {
		log.info("[RecordMechanics] has been disabled.");
	}

	@SuppressWarnings("resource")
	public static void updateStatisticData(String pname, int nmoney, int npdeaths, int nunlawful_kills, int nlawful_kills, int nmob_kills, int nduel_wins, int nduel_lose) {
		Connection con = null;
		PreparedStatement pst = null;

		//int tmoney = 0;
		//int tlawful_kills = 0;
		//int tunlawful_kills = 0;
		//int tmob_kills = 0;
		//int tdeaths = 0;
		//int tduel_wins = 0;
		//int tduel_lose = 0;

		int money = 0;
		int lawful_kills = 0;
		int unlawful_kills = 0;
		int mob_kills = 0;
		int deaths = 0;
		int duel_wins = 0;
		int duel_lose = 0;

		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT lawful_kills, unlawful_kills, deaths, mob_kills, money, duel_wins, duel_lose FROM statistics WHERE pname = '" + pname + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();

			if(rs.next()) {
				money = rs.getInt("money");
				lawful_kills = rs.getInt("lawful_kills");
				unlawful_kills = rs.getInt("unlawful_kills");
				mob_kills = rs.getInt("mob_kills");
				deaths = rs.getInt("deaths");
				duel_wins = rs.getInt("duel_wins");
				duel_lose = rs.getInt("duel_lose");
			}

			money = nmoney;
			deaths += npdeaths;
			lawful_kills += nunlawful_kills;
			unlawful_kills += nlawful_kills;
			mob_kills += nmob_kills;
			duel_wins += nduel_wins;
			duel_lose += nduel_lose;

			pst = ConnectionPool.getConnection().prepareStatement("SELECT lawful_kills, unlawful_kills, deaths, mob_kills, money, duel_wins, duel_lose FROM statistics WHERE pname = '" + pname + "'");

			pst.execute();
			rs = pst.getResultSet();

			if(rs.next()) {
				//tmoney = rs.getInt("money");
				//tlawful_kills = rs.getInt("lawful_kills");
				//tunlawful_kills = rs.getInt("unlawful_kills");
				//tmob_kills = rs.getInt("mob_kills");
				//tdeaths = rs.getInt("deaths");
				//tduel_wins = rs.getInt("duel_wins");
				//tduel_lose = rs.getInt("duel_lose");
			}

			pst = ConnectionPool.getConnection().prepareStatement("INSERT INTO statistics (pname, lawful_kills, unlawful_kills, deaths, mob_kills, money, duel_wins, duel_lose)" + " VALUES" + "('" + pname + "', '" + lawful_kills + "', '" + unlawful_kills + "', '" + deaths + "', '" + mob_kills + "', '" + money + "', '" + duel_wins + "', '" + duel_lose + "') ON DUPLICATE KEY UPDATE lawful_kills = '" + lawful_kills + "', unlawful_kills = '" + unlawful_kills + "', deaths = '" + deaths + "', mob_kills = '" + mob_kills + "', money = '" + money + "', duel_wins='" + duel_wins + "', duel_lose='" + duel_lose + "'");

			pst.executeUpdate();

			/*pst = ConnectionPool.getConneciton().prepareStatement(
				   "INSERT INTO perm_statistics (pname, lawful_kills, unlawful_kills, deaths, mob_kills, money)"
			         + " VALUES"
			         + "('"+ pname + "', '"+ tlawful_kills +"', '" + tunlawful_kills + "', '" + tdeaths + "', '" + tmob_kills + "', '" + tmoney + "') ON DUPLICATE KEY UPDATE lawful_kills = '" + tlawful_kills + "', unlawful_kills = '"
			         + tunlawful_kills + "', deaths = '" + tdeaths + "', mob_kills = '" + tmob_kills + "', money = '" + tmoney + "'");

			pst.executeUpdate();*/

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

	public static void incrementDuelStats(String p_name, boolean win) {
		List<Integer> duel_stats = duel_statistics.get(p_name);

		if(win) {
			int wins = duel_stats.get(1);
			if(wins >= 1) {
				AchievementMechanics.addAchievement(p_name, "Duelist I");
				if(wins >= 10) {
					AchievementMechanics.addAchievement(p_name, "Duelist II");
				}
				if(wins >= 25) {
					AchievementMechanics.addAchievement(p_name, "Duelist III");
				}
				if(wins >= 50) {
					AchievementMechanics.addAchievement(p_name, "Duelist IV");
				}
				if(wins >= 100) {
					AchievementMechanics.addAchievement(p_name, "Duelist V");
				}
				if(wins >= 200) {
					AchievementMechanics.addAchievement(p_name, "Duelist VI");
				}
			}
			duel_stats.set(1, wins + 1);
		} else if(!win) {
			int loses = duel_stats.get(0);
			duel_stats.set(0, loses + 1);
		}

		duel_statistics.put(p_name, duel_stats);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		player_deaths.put(p.getName(), 0);
		player_kills.put(p.getName(), new ArrayList<Integer>(Arrays.asList(0, 0)));
		mob_kills.put(p.getName(), 0);
		duel_statistics.put(p.getName(), new ArrayList<Integer>(Arrays.asList(0, 0)));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			int cur_deaths = 0;
			if(player_deaths.containsKey(p.getName())) {
				cur_deaths = player_deaths.get(p.getName());
			}
			player_deaths.put(p.getName(), cur_deaths + 1);
		}
	}

}
