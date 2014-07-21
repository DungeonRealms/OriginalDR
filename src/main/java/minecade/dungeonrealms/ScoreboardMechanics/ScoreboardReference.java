package minecade.dungeonrealms.ScoreboardMechanics;

public class ScoreboardReference implements Comparable<ScoreboardReference> {

	private Object reference;
	private int priority = 0;
	
	public ScoreboardReference(Object reference){
		this.reference = reference;
	}
	
	public ScoreboardReference setPriority(int priority){
		this.priority = priority;
		return this;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public String getData(){
		return reference.toString();
	}

	@Override
	public int compareTo(ScoreboardReference o) {
		return o.getPriority() - this.getPriority();
	}
	
}
