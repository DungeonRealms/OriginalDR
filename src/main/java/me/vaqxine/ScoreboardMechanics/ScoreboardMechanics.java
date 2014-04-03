package me.vaqxine.ScoreboardMechanics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardMechanics implements Listener {
	
	private static HashMap<Player, Scoreboard> boards = new HashMap<Player, Scoreboard>();
	private static Scoreboard main;
	
	public ScoreboardMechanics(){
		main = Bukkit.getScoreboardManager().getNewScoreboard();
		
		Objective objective = main.registerNewObjective("hpdisplay", "dummy");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("§c" + "❤");
		
		Team green = main.registerNewTeam("green");
		green.setPrefix(ChatColor.GREEN.toString());

		Team dark_green = main.registerNewTeam("dark_green");
		dark_green.setPrefix(ChatColor.DARK_GREEN.toString());

		Team yellow = main.registerNewTeam("yellow");
		yellow.setPrefix(ChatColor.YELLOW.toString());

		Team red = main.registerNewTeam("red");
		red.setPrefix(ChatColor.RED.toString());

		Team dark_red = main.registerNewTeam("dark_red");
		dark_red.setPrefix(ChatColor.DARK_RED.toString());

		Team purple = main.registerNewTeam("purple");
		purple.setPrefix(ChatColor.LIGHT_PURPLE.toString());

		Team white = main.registerNewTeam("white");
		white.setPrefix(ChatColor.WHITE.toString());

		Team aqua = main.registerNewTeam("aqua");
		aqua.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM" + ChatColor.AQUA.toString() + " ");
		
		Team TI = main.registerNewTeam("TI");
		TI.setCanSeeFriendlyInvisibles(true);
		TI.setDisplayName("TI");
	}
	
	public static Scoreboard getBoard(Player plr){
		if(!boards.containsKey(plr)){
			Scoreboard b = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective objective = b.registerNewObjective("hpdisplay", "dummy");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName("§c" + "❤");
			
			Team green = b.registerNewTeam("green");
			green.setPrefix(ChatColor.GREEN.toString());

			Team dark_green = b.registerNewTeam("dark_green");
			dark_green.setPrefix(ChatColor.DARK_GREEN.toString());

			Team yellow = b.registerNewTeam("yellow");
			yellow.setPrefix(ChatColor.YELLOW.toString());

			Team red = b.registerNewTeam("red");
			red.setPrefix(ChatColor.RED.toString());

			Team dark_red = b.registerNewTeam("dark_red");
			dark_red.setPrefix(ChatColor.DARK_RED.toString());

			Team purple = b.registerNewTeam("purple");
			purple.setPrefix(ChatColor.LIGHT_PURPLE.toString());

			Team white = b.registerNewTeam("white");
			white.setPrefix(ChatColor.WHITE.toString());

			Team aqua = b.registerNewTeam("aqua");
			aqua.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM" + ChatColor.AQUA.toString() + " ");
			
			Team TI = b.registerNewTeam("TI");
			TI.setCanSeeFriendlyInvisibles(true);
			TI.setDisplayName("TI");
			
			boards.put(plr, b);
			plr.setScoreboard(b);
		}
		return boards.get(plr);
	}
	
	public static Team getTeam(Scoreboard board, String name){
		if(main.getTeam(name) == null) main.registerNewTeam(name);
		if(board.getTeam(name) == null) return board.registerNewTeam(name);
		return board.getTeam(name);
	}
	
	public static void cloneScoreboard(Player forp){
		Scoreboard board = getBoard(forp);
		for(Team t : main.getTeams()){
			Team x = getTeam(board, t.getName());
			x.setAllowFriendlyFire(t.allowFriendlyFire());
			x.setCanSeeFriendlyInvisibles(t.canSeeFriendlyInvisibles());
			x.setDisplayName(t.getDisplayName());
			x.setPrefix(t.getPrefix());
			x.setSuffix(t.getPrefix());
			for(OfflinePlayer o : t.getPlayers()) x.addPlayer(o);
		}
	}
	
	private void removeBoard(Player p){
		if(boards.containsKey(p)) boards.remove(p);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		removeBoard(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		cloneScoreboard(e.getPlayer());
	}
	
	public static void incrementViewCount(OfflinePlayer shop_tag){
        for(Player p : Bukkit.getOnlinePlayers()){
            Score c = ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.BELOW_NAME).getScore(shop_tag);
            c.setScore(c.getScore() + 1);
        }
        Score c = main.getObjective(DisplaySlot.BELOW_NAME).getScore(shop_tag);
        c.setScore(c.getScore() + 1);
    }
	
	public static void removePlayerFromTeam(String team, OfflinePlayer player){
		for(Player p : Bukkit.getOnlinePlayers()){
			ScoreboardMechanics.getTeam(ScoreboardMechanics.getBoard(p), team).removePlayer(player);
		}
		main.getTeam(team).removePlayer(player);
	}
	
	public static void addPlayerToTeam(String team, OfflinePlayer player){
		for(Player p : Bukkit.getOnlinePlayers()){
			ScoreboardMechanics.getTeam(ScoreboardMechanics.getBoard(p), team).addPlayer(player);
		}
		main.getTeam(team).addPlayer(player);
	}
	
	public static void setOverheadHP(Player pl, int hp){
		for(Player p : Bukkit.getOnlinePlayers()){
			ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(hp);
		}
		main.getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(hp);
	}
	
	public static void setStockCount(Player shop_tag, int stock){
		for(Player p : Bukkit.getOnlinePlayers()){
            ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.BELOW_NAME).getScore(shop_tag).setScore(stock);
        }
		main.getObjective(DisplaySlot.BELOW_NAME).getScore(shop_tag).setScore(stock);
	}
	
	public static void setupGuildTeam(String fixed_gname, String handle){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(ScoreboardMechanics.getBoard(p).getTeam(fixed_gname + ".default") != null) continue;
			
			Scoreboard board = ScoreboardMechanics.getBoard(p);
			Team default_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".default");
			Team neutral_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".neutral");
			Team chaotic_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".chaotic");
			Team gm_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".gm");

			String g_handle = "[" + handle + "]";
	
			default_g.setPrefix(g_handle + ChatColor.RESET.toString() + " ");
			default_g.setDisplayName(fixed_gname + ".default");
			neutral_g.setPrefix(ChatColor.YELLOW.toString() + g_handle + ChatColor.YELLOW.toString() + " ");
			neutral_g.setDisplayName(fixed_gname + ".neutral");
			chaotic_g.setPrefix(ChatColor.RED.toString() + g_handle + ChatColor.RED.toString() + " ");
			chaotic_g.setDisplayName(fixed_gname + ".chaotic");
			gm_g.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM " + ChatColor.AQUA.toString() + g_handle + " ");
			gm_g.setDisplayName(fixed_gname + ".gm");
		}
		
		if(main.getTeam(fixed_gname + ".default") != null) return;
		
		Scoreboard board = main;
		Team default_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".default");
		Team neutral_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".neutral");
		Team chaotic_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".chaotic");
		Team gm_g = ScoreboardMechanics.getTeam(board, fixed_gname + ".gm");

		String g_handle = "[" + handle + "]";

		default_g.setPrefix(g_handle + ChatColor.RESET.toString() + " ");
		default_g.setDisplayName(fixed_gname + ".default");
		neutral_g.setPrefix(ChatColor.YELLOW.toString() + g_handle + ChatColor.YELLOW.toString() + " ");
		neutral_g.setDisplayName(fixed_gname + ".neutral");
		chaotic_g.setPrefix(ChatColor.RED.toString() + g_handle + ChatColor.RED.toString() + " ");
		chaotic_g.setDisplayName(fixed_gname + ".chaotic");
		gm_g.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM " + ChatColor.AQUA.toString() + g_handle + " ");
		gm_g.setDisplayName(fixed_gname + ".gm");
	}
	
}
