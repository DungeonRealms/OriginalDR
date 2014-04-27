package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRLoad implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!(p.isOp())) { return true; }
		}
		SpawnMechanics.loadSpawnLocationTemplate();
		Bukkit.broadcastMessage(ChatColor.GREEN + "Loaded all data to filesystem.");
		//p.sendMessage(ChatColor.GREEN + "Saved all data to filesystem.");
		return true;
	}
	
}