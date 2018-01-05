package minecade.dungeonrealms.models;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.enums.LogType;

public class LogModel {
	
	public LogType type;
	public JsonObject data;
	public String player;
	public long time;
	
	public LogModel(LogType type, String player, JsonObject data){
		new LogModel(type, player, data, Utils.getTime());
	}
	
	public LogModel(LogType type, String player, JsonObject data, long time){
		this.type = type;
		this.player = player;
		this.data = data;
		this.time = time;
		Hive.logs.add(this);
	}
	
}
