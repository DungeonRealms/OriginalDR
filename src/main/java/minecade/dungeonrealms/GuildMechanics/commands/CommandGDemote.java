package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGDemote implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gdemote <player>");
			return true;
		}
		
		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gdemote.");
			return true;
		}
		
		if(!(GuildMechanics.isGuildLeader(p.getName()) || GuildMechanics.isGuildCoOwner(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD OWNER" + ChatColor.RED + " or " + ChatColor.BOLD + "CO-OWNER" + ChatColor.RED +  " to use " + ChatColor.BOLD + "/gdemote.");
			return true;
		}
		
		String p_name_2demote = args[0];
		
		if(Bukkit.getPlayer(p_name_2demote) != null) {
			p_name_2demote = Bukkit.getPlayer(p_name_2demote).getName();
		}
		
		if(p_name_2demote.equalsIgnoreCase(p.getName())) {
			p.sendMessage(ChatColor.RED + "You cannot demote yourself in your own guild.");
			return true;
		}
		
		if(!(GuildMechanics.areGuildies(p.getName(), p_name_2demote))) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_2demote + ChatColor.RED + " is not in YOUR guild.");
			return true;
		}
		
		if(!GuildMechanics.isGuildOfficer(p_name_2demote) && !GuildMechanics.isGuildCoOwner(p_name_2demote)) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_2demote + ChatColor.RED + " is not yet a " + ChatColor.UNDERLINE + "guild officer");
			p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.RED + "/gpromote " + p_name_2demote + ChatColor.GRAY + " to make them a guild officer.");
			return true;
		} else if (GuildMechanics.isGuildCoOwner(p_name_2demote)) {
			if (GuildMechanics.isGuildLeader(p.getName())) {
				GuildMechanics.demoteCoOwner(p_name_2demote, p);
				return true;
			}
			p.sendMessage(ChatColor.RED + "Only the " + ChatColor.BOLD + "GUILD OWNER" + ChatColor.RED + " is able to demote co-owners.");
			return true;
		}
		GuildMechanics.demoteOfficer(p_name_2demote, p);
		return true;
	}
	
}