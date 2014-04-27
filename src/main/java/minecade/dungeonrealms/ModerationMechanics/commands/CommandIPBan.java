package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIPBan implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
		}
		
		if(args.length != 1) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/ipban <IP / PLAYER>");
				p.sendMessage(ChatColor.GRAY + "All IP bans are permanent.");
			}
			return true;
		}
		
		final String IP = args[0];
		// Check if they gave us an IP or a player.
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				ModerationMechanics.IPBanPlayer(IP);
			}
		});
		
		t.start();
		
		if(p != null) {
			p.sendMessage("IP ban issued for " + IP + "...");
		} else {
			ModerationMechanics.log.info("IP ban issued for " + IP + "...");
		}
		
		return true;
	}
	
}
