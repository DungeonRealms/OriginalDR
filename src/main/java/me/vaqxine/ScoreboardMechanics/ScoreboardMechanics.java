package me.vaqxine.ScoreboardMechanics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardMechanics {
	
	private static HashMap<Player, Scoreboard> boards = new HashMap<Player, Scoreboard>();
	
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
		if(board.getTeam(name) == null) return board.registerNewTeam(name);
		return board.getTeam(name);
	}
	
}
