package me.vaqxine.managers;

import java.util.HashMap;

import me.vaqxine.models.PlayerModel;

import org.bukkit.entity.Player;

public class PlayerManager {
	
	private static HashMap<String, PlayerModel> models = new HashMap<String, PlayerModel>();
	
	public static PlayerModel getPlayerModel(Player plr){
		if(!models.containsKey(plr.getName())) models.put(plr.getName(), new PlayerModel(plr.getName()));
		return models.get(plr.getName());
	}
	
}
