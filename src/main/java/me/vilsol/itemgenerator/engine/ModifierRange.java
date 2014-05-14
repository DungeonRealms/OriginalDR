package me.vilsol.itemgenerator.engine;

import java.util.Random;

public class ModifierRange {

	private ModifierType type;
	private int low;
	private int high;
	
	public ModifierRange(ModifierType type, int low, int high){
		this.type = type;
		this.low = low;
		this.high = high;
	}
	
	public String generateRandom(){
		String random = "";
		Random r = new Random();
		
		int first = r.nextInt(high - low) + low;
		int second = high;
		
		if(type == ModifierType.RANGE){
			
			if(high - first > 0){
				second = r.nextInt(high - first) + first;
			}
			
			random += String.valueOf(first);
			random += " - ";
			random += String.valueOf(second);
		}else{
			random += String.valueOf(first);
		}
		
		return random;
	}
	
}
