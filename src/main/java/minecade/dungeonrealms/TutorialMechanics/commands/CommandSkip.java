package minecade.dungeonrealms.TutorialMechanics.commands;

import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSkip implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(TutorialMechanics.onTutorialIsland(p))) {
		
		return true; }
		if(!(TutorialMechanics.skip_confirm.contains(p.getName()))) {
			TutorialMechanics.skip_confirm.add(p.getName());
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "WARNING: " + ChatColor.RED + "If you skip this tutorial you will not recieve " + ChatColor.UNDERLINE + "ANY" + ChatColor.RED + " of the item rewards for completing it.");
			p.sendMessage(ChatColor.GRAY + "If you're sure you still want to skip it, type '" + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + "' to finish the tutorial. Otherwise, just type '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to continue with the tutorial.");
			return true;
		}
		return true;
	}
	
}