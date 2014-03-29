package me.vaqxine.ScoreboardMechanics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardMechanics {
	
	private static HashMap<Player, Scoreboard> boards = new HashMap<Player, Scoreboard>();
	
	public static Scoreboard getBoard(Player plr){
		if(!boards.containsKey(plr)){
			Scoreboard b = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective objective = b.registerNewObjective("hpdisplay", "dummy");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName("§c" + "❤");
			boards.put(plr, b);
			plr.setScoreboard(b);
		}
		return boards.get(plr);
	}
	
	
	
}
