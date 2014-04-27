package minecade.dungeonrealms.HiveServer.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.HiveServer.HiveServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClassicRollout implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.RED + "You cannot issue this command from anywhere but the console window.");
			return true;
		}
		
		if(args.length != 1) {
			Main.log.info("Invalid Syntax. /rollout <IP/*>");
			return true;
		}
		
		String ip = args[0];
		
		if(HiveServer.isThisRootMachine()) {
			if(ip.equalsIgnoreCase("*")) {
				HiveServer.send8008Packet("@rollout@", null, true);
				//CommunityMechanics.sendPacketCrossServer("@rollout@", -1, true);
			} else {
				//CommunityMechanics.sendPacketCrossServer("@rollout@", args[0]);
				HiveServer.send8008Packet("@rollout@", args[0], false);
			}
		}
		
		if(HiveServer.isThisRootMachine()) {
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.saveData();
				p.kickPlayer("Launching a Content Patch to ALL #DungeonRealms Servers...");
			}
			
			World w = Bukkit.getWorlds().get(0);
			Bukkit.unloadWorld(w, true);
			
			Bukkit.shutdown();
			return true;
		}
		return true;
	}
	
}