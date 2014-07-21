package minecade.dungeonrealms.ScoreboardMechanics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardModule implements Comparable<ScoreboardModule> {
	
	private List<ScoreboardReference> references = new ArrayList<ScoreboardReference>();
	private int priority = 0;
	
	public ScoreboardModule() {
		
	}
	
	public ScoreboardModule setPriority(int priority){
		this.priority = priority;
		return this;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public ScoreboardModule addReference(ScoreboardReference r){
		if(!this.references.contains(r)) references.add(r);
		return this;
	}

	@SuppressWarnings("deprecation")
	public Scoreboard fillScoreboard(Scoreboard s, int highestPoint){
		Objective o = s.getObjective(DisplaySlot.SIDEBAR);
		if(o == null) return s;
		Collections.sort(references);
		int current = highestPoint;
		for(ScoreboardReference r : references) {
			Score c = o.getScore(Bukkit.getOfflinePlayer(r.toString()));
			c.setScore(current);
			current--;
		}
		return s;
	}
	
	public int getDataCount(){
		return references.size();
	}

	@Override
	public int compareTo(ScoreboardModule o) {
		return o.getPriority() - this.getPriority();
	}
	
}
