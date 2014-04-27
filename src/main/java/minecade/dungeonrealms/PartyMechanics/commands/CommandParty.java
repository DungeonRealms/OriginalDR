package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;

		if(PartyMechanics.isInParty(p.getName())){
			p.sendMessage(ChatColor.RED + "You are already in a party!");
			return true;
		}
		
		PartyMechanics.createParty(p.getName(), p, null);
		p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Party created.");
		p.sendMessage(ChatColor.GRAY.toString() + "To invite more people to join your party, " + ChatColor.UNDERLINE + "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use " + ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " + ChatColor.BOLD + "/pkick" + ChatColor.GRAY + ". To chat with party, use " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " To change the loot profile, use " + ChatColor.BOLD + "/ploot");
		return true;
	}
	
}