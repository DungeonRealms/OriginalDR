package minecade.dungeonrealms.models;

import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.enums.LogType;

public class LogModel {
	
	public LogType type;
	public String data;
	public String player;
	public long time;
	
	public LogModel(LogType type, String player){
		new LogModel(type, player, null);
	}
	
	public LogModel(LogType type, String player, String data){
		new LogModel(type, player, data, Utils.getTime());
	}
	
	public LogModel(LogType type, String player, String data, long time){
		this.type = type;
		this.player = player;
		this.data = data;
		this.time = time;
	}
	
}
