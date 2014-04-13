package me.vaqxine.ModerationMechanics.commands;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSendPacket implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		if(!(sender instanceof Player)) { return true; }
		Player p = (Player) sender;
		//if(!p.getName().equalsIgnoreCase("iFamasssxD")) { return true; }
		if(args.length != 3) {
			p.sendMessage(ChatColor.RED + "Invalid Syntax: /sendpacket [data] [servernumber] [allservers]");
			return true;
		}
		try {
			CommunityMechanics.sendPacketCrossServer(args[0], Integer.valueOf(args[1]), Boolean.valueOf(args[2]));
			p.sendMessage(ChatColor.GRAY + "Packet sent!");
		} catch(Exception e) {
			p.sendMessage(ChatColor.RED + "Invalid Syntax");
		}
		return false;
	}
}
