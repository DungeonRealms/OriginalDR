package minecade.dungeonrealms.models;

import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.enums.LogType;

public class LogModel {
	
	public LogType type;
	public String data;
	public String player;
	public long time;
	
	public LogModel(LogType type, String player){
		new LogModel(type, player, null);
	}
	
	public LogModel(LogType type, String player, Object data){
		new LogModel(type, player, data, Utils.getTime());
	}
	
	public LogModel(LogType type, String player, Object data, long time){
		this.type = type;
		this.player = player;
		this.data = data.toString();
		this.time = time;
		Hive.logs.add(this);
	}
	
}
