package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRVanish implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(!(p.isOp())) { return true; }
		
		if(ModerationMechanics.isPlayerVanished(p.getName())) {
			ModerationMechanics.unvanishPlayer(p.getName());
			for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
				if(pl.getName().equalsIgnoreCase(p.getName())) {
					continue;
				}
				pl.showPlayer(p);
			}
			p.sendMessage(ChatColor.RED + "You are now " + ChatColor.BOLD + "visible.");
		} else {
			ModerationMechanics.vanishPlayer(p.getName());
			p.sendMessage(ChatColor.GREEN + "You are now " + ChatColor.BOLD + "invisible.");
		}
		
		return true;
	}
	
}
