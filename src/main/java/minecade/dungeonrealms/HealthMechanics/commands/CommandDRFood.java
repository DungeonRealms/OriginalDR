package minecade.dungeonrealms.HealthMechanics.commands;

import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRFood implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;
		
		if (!pl.isOp() && !Utils.isBeta()) return true;   // enable for testing on beta shard
		
		for (int i = 0; i < HealthMechanics.HP_regen_food.length; i++) {
		    pl.getInventory().addItem(HealthMechanics.HP_regen_food[i]);
		}
		
		pl.sendMessage(ChatColor.GREEN + "Added 1 of each HP regen food to your inventory.");
		
		return true;
	}
	
}
