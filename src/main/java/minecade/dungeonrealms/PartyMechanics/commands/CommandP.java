package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandP implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length == 0) {
			/*p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/p <MSG>");
			return true;*/
			
			// Toggle party-only chat.
			if(!(PartyMechanics.party_map.containsKey(p.getName()))) {
				p.sendMessage(ChatColor.RED + "You are not in a party.");
				return true;
			}
			
			if(!(PartyMechanics.party_only.contains(p.getName()))) {
				PartyMechanics.party_only.add(p.getName());
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Messages will now be default sent to <P>. Type " + ChatColor.UNDERLINE + "/l <msg>" + ChatColor.LIGHT_PURPLE + " to speak in local.");
				p.sendMessage(ChatColor.GRAY + "To change this back, type " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " again.");
			} else if(PartyMechanics.party_only.contains(p.getName())) {
				PartyMechanics.party_only.remove(p.getName());
				p.sendMessage(ChatColor.GRAY + "Messages will now be default sent to local chat.");
			}
			return true;
		}
		
		String msg = "";
		
		for(String s : args) {
			msg += s + " ";
		}
		
		if(msg.endsWith(" ")) {
			msg = msg.substring(0, (msg.length() - 1));
		}
		
		PartyMechanics.sendMessageToParty(p, msg);
		return true;
	}
	
}