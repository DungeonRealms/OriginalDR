package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRKick implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			String rank = PermissionMechanics.getRank(p.getName());
			if(rank == null) { return true; }
			
			if(!(p.isOp()) && !rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm")) { return true; }
		}
		
		if(args.length <= 1) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/drkick <PLAYER> <REASON>");
			}
			return true;
		}
		
		if(p != null) {
			int count = ModerationMechanics.kick_count.get(p.getName());
			if(count >= 50) {
				p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " kicks today.");
				return true;
			}
			count += 1;
			ModerationMechanics.kick_count.put(p.getName(), count);
		}
		
		String p_name_2kick = args[0];
		String reason = "";
		
		for(String s : args) {
			if(s.equalsIgnoreCase(p_name_2kick)) {
				continue; //args[0]
			}
			reason += s + " ";
		}
		
		if(Bukkit.getPlayer(p_name_2kick) != null && Bukkit.getPlayer(p_name_2kick).isOnline()) {
			p_name_2kick = Bukkit.getPlayer(p_name_2kick).getName();
		}
		
		if(p != null) {
			p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "KICKED" + ChatColor.AQUA + " the user " + ChatColor.BOLD + p_name_2kick + ChatColor.AQUA + " from all servers.");
			p.sendMessage(ChatColor.GRAY + "REASON: " + reason);
		}
		
		if(Bukkit.getPlayer(p_name_2kick) != null && Bukkit.getPlayer(p_name_2kick).isOnline()) {
			Player kicked = Bukkit.getPlayer(p_name_2kick);
			kicked.kickPlayer(reason);
		} else if(ModerationMechanics.isPlayerOnline(p_name_2kick)) { // @kick@notch:reason
			int server_num = ModerationMechanics.getPlayerServer(p_name_2kick);
			CommunityMechanics.sendPacketCrossServer("@kick@" + p_name_2kick + ":" + reason, server_num, false);
		}
		
		return true;
	}
	
}
