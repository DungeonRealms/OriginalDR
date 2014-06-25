package minecade.dungeonrealms.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PlayerManager implements Listener {

	private static HashMap<String, PlayerModel> models = new HashMap<String, PlayerModel>();

	public static PlayerModel getPlayerModel(String playerName){
		if(!models.containsKey(playerName)) models.put(playerName, new PlayerModel(playerName));
		return models.get(playerName);
	}

	public static PlayerModel getPlayerModel(Player plr){
		return getPlayerModel(plr.getName());
	}
	
	public static List<PlayerModel> getPlayerModels() {
		return new ArrayList<PlayerModel>(models.values());
	}

}
