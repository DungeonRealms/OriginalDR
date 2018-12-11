package minecade.dungeonrealms.ScoreboardMechanics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {
	
	private static HashMap<Player, Scoreboard> boards = new HashMap<Player, Scoreboard>();
	private static Scoreboard main;
	
	public ScoreboardManager() {
		main = initiateScoreobard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	public static Scoreboard initiateScoreobard(Scoreboard s){
		Objective objective = s.registerNewObjective("hpdisplay", "dummy");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("§c" + "❤");
		
		Team green = s.registerNewTeam("green");
		green.setPrefix(ChatColor.GREEN.toString());
		
		Team dark_green = s.registerNewTeam("dark_green");
		dark_green.setPrefix(ChatColor.DARK_GREEN.toString());
		
		Team yellow = s.registerNewTeam("yellow");
		yellow.setPrefix(ChatColor.YELLOW.toString());
		
		Team red = s.registerNewTeam("red");
		red.setPrefix(ChatColor.RED.toString());
		
		Team dark_red = s.registerNewTeam("dark_red");
		dark_red.setPrefix(ChatColor.DARK_RED.toString());
		
		Team purple = s.registerNewTeam("purple");
		purple.setPrefix(ChatColor.LIGHT_PURPLE.toString());
		
		Team white = s.registerNewTeam("white");
		white.setPrefix(ChatColor.WHITE.toString());
		
		Team aqua = s.registerNewTeam("aqua");
		aqua.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "GM" + ChatColor.AQUA.toString() + " ");
		
		Team TI = s.registerNewTeam("TI");
		TI.setCanSeeFriendlyInvisibles(true);
		TI.setDisplayName("TI");
		return s;
	}
	
	public static Scoreboard getBoard(final Player plr) {
		if(!boards.containsKey(plr)) {
			Scoreboard b = initiateScoreobard(Bukkit.getScoreboardManager().getNewScoreboard());
			boards.put(plr, b);
			plr.setScoreboard(b); 
		}
		return boards.get(plr);
	}
	
	public static Team getTeam(Scoreboard board, String name) {
		if(main.getTeam(name) == null) main.registerNewTeam(name);
		if(board.getTeam(name) == null) return board.registerNewTeam(name);
		return board.getTeam(name);
	}
	
	public static Scoreboard cloneScoreboard(Player forp) {
		Scoreboard board = getBoard(forp);
		for(Team t : main.getTeams()) {
			Team x = getTeam(board, t.getName());
			x.setAllowFriendlyFire(t.allowFriendlyFire());
			x.setCanSeeFriendlyInvisibles(t.canSeeFriendlyInvisibles());
			x.setDisplayName(t.getDisplayName());
			x.setPrefix(t.getPrefix());
			for(OfflinePlayer o : t.getPlayers())
				x.addPlayer(o);
		}
		return board;
	}
	
	private void removeBoard(Player p) {
		if(boards.containsKey(p)) boards.remove(p);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		removeBoard(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		cloneScoreboard(e.getPlayer());
	}
	
    public static void setOverheadHP(Player pl, int hp) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(hp);
		}
		main.getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(hp);
	}
	
    private static Team getTeam(Scoreboard sb, OfflinePlayer target){
		if(sb.getTeam(target.getName()) == null) return sb.registerNewTeam(target.getName());
		return sb.getTeam(target.getName());
	}
	
	public static void setPlayerColor(ChatColor color, OfflinePlayer pl){
		for(Player p : Bukkit.getOnlinePlayers()) {
			Team t = new TeamBuild(getTeam(ScoreboardMechanics.getBoard(p), pl)).setColor(color).getTeam();
			if(!t.hasPlayer(pl)) t.addPlayer(pl);
		}
		Team t = new TeamBuild(getTeam(main, pl)).setColor(color).getTeam();
		if(!t.hasPlayer(pl)) t.addPlayer(pl);
	}
	
	public static void setPlayerLevel(int level, OfflinePlayer pl){
		for(Player p : Bukkit.getOnlinePlayers()) {
			Team t = new TeamBuild(getTeam(ScoreboardMechanics.getBoard(p), pl)).setLevel(level).getTeam();
			if(!t.hasPlayer(pl)) t.addPlayer(pl);
		}
		Team t = new TeamBuild(getTeam(main, pl)).setLevel(level).getTeam();
		if(!t.hasPlayer(pl)) t.addPlayer(pl);
	}
	
}
