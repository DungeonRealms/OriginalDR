package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnban implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		String rank = "";
		if(p != null) {
			rank = PermissionMechanics.getRank(p.getName());
			if(rank == null) { return true; }
			
			if(!p.isOp() && !rank.equalsIgnoreCase("gm")) { return true; }
		}
		
		if(args.length <= 1) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/unban <PLAYER> <REASON>");
			}
			return true;
		}
		
		String unbanner = "Console";
		if(p != null) {
			unbanner = p.getName();
		}
		
		String p_name = args[0];
		String reason = "";
		
		for(int i = 1; i < args.length; i++) {
			reason += args[i] + " ";
		}
		
		ModerationMechanics.unbanPlayer(p_name, reason, unbanner);
		ModerationMechanics.log.info("[ModerationMechanics] UNBANNED player " + p_name + " for " + reason + "by " + unbanner);
		
		if(p != null) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "UNBANNED" + ChatColor.RED + " player " + p_name + " because " + reason);
		}
		
		return true;
	}
	
}
