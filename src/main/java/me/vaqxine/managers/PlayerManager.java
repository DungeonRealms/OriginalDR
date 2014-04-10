package me.vaqxine.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.vaqxine.models.PlayerModel;

public class PlayerManager {
	
	private static HashMap<String, PlayerModel> models = new HashMap<String, PlayerModel>();
	
	public static PlayerModel getPlayerModel(String playerName){
		if(!models.containsKey(playerName)) models.put(playerName, new PlayerModel(playerName));
		return models.get(playerName);
	}
	
	public static PlayerModel getPlayerModel(Player plr){
		return getPlayerModel(plr.getName());
	}
	
}
