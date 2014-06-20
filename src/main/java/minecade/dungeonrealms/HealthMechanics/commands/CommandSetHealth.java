package minecade.dungeonrealms.HealthMechanics.commands;

import minecade.dungeonrealms.HealthMechanics.HealthMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetHealth implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;
		
		if (!pl.isOp()) return true;
		
		if (args.length == 0 || args.length > 2) {
			pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player and health value. " + ChatColor.UNDERLINE + "/sethealth <PLAYER> <HEALTH>");
			pl.sendMessage(ChatColor.RED + "Or to set your own health, simply use " + ChatColor.UNDERLINE + "/sethealth <HEALTH>");
			return true;
		}
		
		if (args.length == 2) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[0] + " is " + ChatColor.RED + "" + ChatColor.UNDERLINE + "offline.");
				return true;
			}
			if (!isInt(args[1])) {
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[1] + ChatColor.RED + " is not a number!");
				return true;
			}
			HealthMechanics.health_data.put(target.getName(), Integer.parseInt(args[1]));
			HealthMechanics.setPlayerHP(target.getName(), Integer.parseInt(args[1]));
			pl.sendMessage(ChatColor.GREEN + "You changed " + ChatColor.BOLD + target.getName() + ChatColor.GREEN + "'s health to " + ChatColor.UNDERLINE + args[1]);
		} else if (args.length == 1) {
			if (!isInt(args[0])) {
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[0] + ChatColor.RED + " is not a number!");
				return true;
			}
			HealthMechanics.health_data.put(pl.getName(), Integer.parseInt(args[0]));
			HealthMechanics.setPlayerHP(pl.getName(), Integer.parseInt(args[0]));
			pl.sendMessage(ChatColor.GREEN + "You set your HP to " + ChatColor.BOLD + args[0]);
		}
		return true;
	}

	
	private boolean isInt(String integer) {
		try {
			Integer.parseInt(integer);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}
