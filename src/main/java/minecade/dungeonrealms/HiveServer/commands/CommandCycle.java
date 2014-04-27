package minecade.dungeonrealms.HiveServer.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.HiveServer.HiveServer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCycle implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.RED + "You cannot issue this command from anywhere but the console window.");
			return true;
		}
		
		if(args.length != 1) {
			Main.log.info("Invalid Syntax. /cycle <IP/*>");
			return true;
		}
		
		String ip = args[0];
		
		if(HiveServer.isThisRootMachine()) {
			if(ip.equalsIgnoreCase("*")) {
				//send8008Packet("@restart@", null, true);
				CommunityMechanics.sendPacketCrossServer("@restart@", -1, true);
				return true;
			} else if(ip.equalsIgnoreCase("proxy")) {
				return true;
			} else {
				CommunityMechanics.sendPacketCrossServer("@rollout@", args[0]);
				//send8008Packet("@restart@", args[0], false);
				return true;
			}
		}
		return true;
	}
	
}