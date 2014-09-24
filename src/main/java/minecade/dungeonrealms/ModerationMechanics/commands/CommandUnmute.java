package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnmute implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			String rank = PermissionMechanics.getRank(p.getName());
			if(rank == null) { return true; }
			
			if(!rank.equalsIgnoreCase("gm") && !p.isOp()) { return true; }
		}
		
		if(args.length != 1) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/unmute <PLAYER>");
			}
			return true;
		}
		
		String p_name_2unmute = args[0];
		if(Bukkit.getPlayer(p_name_2unmute) != null) {
			p_name_2unmute = Bukkit.getPlayer(p_name_2unmute).getName(); // Fixes capitalization issues.
		}
		
		ChatMechanics.mute_list.remove(p_name_2unmute);
		ChatMechanics.setMuteStateSQL(p_name_2unmute);
		if(p != null) {
			p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "UNMUTED " + ChatColor.AQUA + p_name_2unmute);
		} else if(p == null) {
			ModerationMechanics.log.info("[ModerationMechanics] Unmuted player " + p_name_2unmute + ".");
		}
		
		if(Bukkit.getPlayer(p_name_2unmute) != null && Bukkit.getPlayer(p_name_2unmute).isOnline()) {
			Player p_2unmute = Bukkit.getPlayer(p_name_2unmute);
			p_2unmute.sendMessage("");
			p_2unmute.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has been removed.");
			p_2unmute.sendMessage("");
		} else if(ModerationMechanics.isPlayerOnline(p_name_2unmute)) {
			int server_num = ModerationMechanics.getPlayerServer(p_name_2unmute);
			CommunityMechanics.sendPacketCrossServer("@unmute@" + p_name_2unmute, server_num, false);
			//ConnectProtocol.sendResultCrossServer(CommunityMechanics.server_list.get(server_num), "@unmute@" + p_name_2unmute);
		}
		
		return true;
	}
	
}
