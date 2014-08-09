package minecade.dungeonrealms.ScoreboardMechanics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerScoreboard {
	
	private Scoreboard s;
	private Player owner;
	private Team t;
	private ChatColor c = ChatColor.WHITE;
	private int level = 1;
	private List<ScoreboardModule> modules = new ArrayList<ScoreboardModule>();
	
	public PlayerScoreboard(Scoreboard s, Player owner) {
		this.s = s;
		this.owner = owner;
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public Scoreboard getScoreboard(){
		return s;
	}
	
	public void refreshScoreboard(){
		Collections.sort(modules);
		int highestPoint = getTotalModuleCount();
		for(ScoreboardModule m : modules) {
			m.fillScoreboard(s, highestPoint);
			highestPoint -= m.getDataCount();
		}
		owner.setScoreboard(s);
	}
	
	private int getTotalModuleCount(){
		int total = 0;
		for(ScoreboardModule m : modules) {
			total += m.getDataCount();
		}
		return total;
	}
	
	public Team getOverhead(){
		t.setPrefix(ChatColor.LIGHT_PURPLE + "[" + level + "] " + c);
		return t;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void setColor(ChatColor c){
		this.c = c;
	}
	
}
