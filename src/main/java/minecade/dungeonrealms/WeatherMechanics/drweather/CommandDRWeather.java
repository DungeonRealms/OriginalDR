package minecade.dungeonrealms.WeatherMechanics.drweather;

import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRWeather implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(p.isOp())) { return true; }
		
		if(args[0].equalsIgnoreCase("rain")) {
			p.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			p.setPlayerWeather(WeatherType.CLEAR);
		}
		return true;
	}
	
}