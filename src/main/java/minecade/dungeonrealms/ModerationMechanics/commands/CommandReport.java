package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReport implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(args.length != 0) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/report");
			}
			return true;
		}
		if(ModerationMechanics.report_step.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "Please complete your pending REPORT before filing a new one. Type 'cancel' to void your pending report.");
			return true;
		}
		
		p.sendMessage("");
		p.sendMessage(ChatColor.DARK_RED + "                " + ChatColor.BOLD + "*** NEW REPORT SUBMISSION ***");
		p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + "Enter the " + ChatColor.BOLD + "TOPIC #" + ChatColor.GRAY + " of the report to submit.");
		p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.GRAY + ChatColor.BOLD + "(1)" + ChatColor.GRAY + " Bug" + "   " + ChatColor.BOLD + "(2)" + ChatColor.GRAY + " Hacker" + "   " + ChatColor.BOLD + "(3)" + ChatColor.GRAY + " Abuse" + "   " + ChatColor.BOLD + "(4)" + ChatColor.GRAY + " Other");
		ModerationMechanics.report_step.put(p.getName(), 1);
		
		return true;
	}
	
}
