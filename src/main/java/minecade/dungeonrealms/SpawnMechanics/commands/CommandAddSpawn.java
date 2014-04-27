package minecade.dungeonrealms.SpawnMechanics.commands;

import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddSpawn implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(p.isOp())) { return true; }
		Location new_spawn_loc = p.getLocation();
		SpawnMechanics.spawn_map.add(new_spawn_loc);
		p.sendMessage(ChatColor.YELLOW + "New spawn location registered and loaded.");
		p.sendMessage(ChatColor.GRAY + "" + new_spawn_loc.getBlockX() + ", " + new_spawn_loc.getBlockY() + ", " + new_spawn_loc.getBlockZ());
		return true;
	}
	
}