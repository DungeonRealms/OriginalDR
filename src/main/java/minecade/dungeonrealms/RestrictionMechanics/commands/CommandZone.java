package minecade.dungeonrealms.RestrictionMechanics.commands;

import minecade.dungeonrealms.RestrictionMechanics.RestrictionMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandZone implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		RestrictionMechanics.zone_type.remove(p.getName());
		return true;
	}
	
}