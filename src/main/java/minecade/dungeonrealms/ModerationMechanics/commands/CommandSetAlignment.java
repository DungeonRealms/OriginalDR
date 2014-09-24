package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetAlignment implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if(!p.isOp()) return true;
		if(args.length != 2) {
			p.sendMessage(ChatColor.GRAY + "Use /setalignment [name] [alignment]");
			return true;
		}
		if(Bukkit.getPlayer(args[0]) == null) {
			p.sendMessage(ChatColor.RED + "This player is not online!");
			return true;
		}
		String alignment = args[1];
		if(!(alignment.contains("evil") || alignment.contains("chaotic") || alignment.contains("neutral") || alignment.contains("lawful") || alignment.contains("good"))) {
			p.sendMessage(ChatColor.RED + "Invalid alignment!");
			p.sendMessage(ChatColor.GRAY + "Valid Alignments: Chaotic, neutral, lawful");
			return true;
		}
		if(alignment.equalsIgnoreCase("chaotic")) {
			alignment = "evil";
		}
		if(alignment.equalsIgnoreCase("lawful")) {
			alignment = "good";
		}
		KarmaMechanics.setAlignment(args[0], alignment, 2);
		p.sendMessage(ChatColor.AQUA + "You have set " + args[0] + "'s alignment to: " + (alignment.equalsIgnoreCase("evil") ? ChatColor.RED : alignment.equalsIgnoreCase("neutral") ? ChatColor.YELLOW : ChatColor.GREEN) + alignment);
		Bukkit.getPlayer(args[0]).sendMessage(ChatColor.AQUA + p.getName() + " has set your alignment to: " + (alignment.equalsIgnoreCase("evil") ? ChatColor.RED : alignment.equalsIgnoreCase("neutral") ? ChatColor.YELLOW : ChatColor.GREEN) + alignment);
		return true;
	}
	
}
