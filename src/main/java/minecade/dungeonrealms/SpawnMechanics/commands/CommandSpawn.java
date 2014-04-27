package minecade.dungeonrealms.SpawnMechanics.commands;

import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(p.isOp())) { return true; }
		Location respawn_location = SpawnMechanics.getRandomSpawnPoint(p.getName());
		p.teleport(respawn_location);
		return true;
	}
	
}