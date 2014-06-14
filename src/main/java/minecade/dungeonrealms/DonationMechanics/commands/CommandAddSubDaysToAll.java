package minecade.dungeonrealms.DonationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddSubDaysToAll implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		if (args.length != 1) {
			ps.sendMessage(ChatColor.RED + "Incorrect Syntax.  Usage: /addsubdaystoall <days>");
			return true;
		}
		
		try {
			DonationMechanics.addSubscriberDaysToAll(Integer.valueOf(args[0]));
		}
		catch (NumberFormatException ex) {
			ps.sendMessage(ChatColor.RED + "Error: argument must be a number.");
			return true;
		}
		
		Main.log.info("[DonationMechanics] " + args[0] + " days have been added to all subs and sub+'s.");
		return true;
	}
	
}