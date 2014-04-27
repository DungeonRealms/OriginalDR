package minecade.dungeonrealms.ScoreboardMechanics;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class TeamBuild {
	
	private Team t;
	private ChatColor c = ChatColor.WHITE;
	private int level = 1;
	
	public TeamBuild(Team t){
		this.t = t;
	}
	
	public Team getTeam(){
		t.setPrefix(ChatColor.LIGHT_PURPLE + "[" + level + "] " + c);
		return t;
	}
	
	public TeamBuild setLevel(int level){
		this.level = level;
		return this;
	}
	
	public TeamBuild setColor(ChatColor c){
		this.c = c;
		return this;
	}
	
}
