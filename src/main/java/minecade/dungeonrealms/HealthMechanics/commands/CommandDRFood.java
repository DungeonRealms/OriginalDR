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
		
		pl.getInventory().addItem(HealthMechanics.t1_common_food);
		pl.getInventory().addItem(HealthMechanics.t1_unique_food);
		pl.getInventory().addItem(HealthMechanics.t2_common_food);
		pl.getInventory().addItem(HealthMechanics.t2_unique_food);
		pl.getInventory().addItem(HealthMechanics.t3_common_food);
		pl.getInventory().addItem(HealthMechanics.t3_unique_food);
		pl.getInventory().addItem(HealthMechanics.t4_common_food);
		pl.getInventory().addItem(HealthMechanics.t4_unique_food);
		pl.getInventory().addItem(HealthMechanics.t5_common_food);
		pl.getInventory().addItem(HealthMechanics.t5_unique_food);
		
		pl.sendMessage(ChatColor.GREEN + "Added 1 of each HP regen food to your inventory.");
		
		return true;
	}
	
}
