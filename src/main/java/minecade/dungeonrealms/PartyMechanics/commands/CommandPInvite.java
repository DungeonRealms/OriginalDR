package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPInvite implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pinvite <player>");
			p.sendMessage(ChatColor.GRAY + "You can also " + ChatColor.UNDERLINE + "LEFT CLICK" + ChatColor.GRAY + " players with your " + ChatColor.ITALIC + "Character Journal" + ChatColor.GRAY + " to invite them.");
			return true;
		}
		
		String p_name = args[0];
		
		if(p_name.equalsIgnoreCase(p.getName())) {
			p.sendMessage(ChatColor.RED + "You cannot invite yourself to your own party.");
			return true;
		}
		
		if(Bukkit.getPlayer(p_name) == null) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p_name + ChatColor.RED + " is OFFLINE");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p_name).getToggleList() != null && PlayerManager.getPlayerModel(p_name).getToggleList().contains("party")){
			if(!CommunityMechanics.isPlayerOnBuddyList(p_name, p.getName())) {
				// They're not buddies and this player doesn't want non-bud invites.
				p.sendMessage(ChatColor.RED + p_name + " has Non-BUD party invites " + ChatColor.BOLD + "DISABLED");
				return true;
			}
		}
		
		Player to_invite = Bukkit.getPlayer(p_name);
		if(CommunityMechanics.isPlayerOnIgnoreList(to_invite, p.getName()) || ModerationMechanics.isPlayerVanished(p_name)) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p_name + ChatColor.RED + " is OFFLINE.");
			return true;
		}
		PartyMechanics.inviteToParty(to_invite, p);
		return true;
	}
	
}