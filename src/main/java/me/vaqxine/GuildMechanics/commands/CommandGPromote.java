package me.vaqxine.GuildMechanics.commands;

import me.vaqxine.GuildMechanics.GuildMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGPromote implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player)sender;
		if(args.length != 1){
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gpromote <player>");
			return true;
		}

		if(!(GuildMechanics.inGuild(p.getName()))){
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gpromote.");
			return true;
		}

		if(!(GuildMechanics.isGuildLeader(p.getName()))){
			p.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD OWNER" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gpromote.");
			return true;
		}

		String p_name_2promote = args[0];
		if(Bukkit.getPlayer(p_name_2promote) != null){
			p_name_2promote = Bukkit.getPlayer(p_name_2promote).getName();
		}

		if(p_name_2promote.equalsIgnoreCase(p.getName())){
			p.sendMessage(ChatColor.RED + "You cannot promote yourself in your own guild.");
			return true;
		}

		if(!(GuildMechanics.areGuildies(p.getName(), p_name_2promote))){
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_2promote + ChatColor.RED + " is not in YOUR guild.");
			return true;
		}

		if(GuildMechanics.isGuildOfficer(p_name_2promote)){
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_2promote + ChatColor.RED + " is already a " + ChatColor.UNDERLINE + "guild officer");
			p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.RED + "/gdemote " + p_name_2promote + ChatColor.GRAY + " to take away their rank.");
			return true;
		}

		GuildMechanics.promoteToOfficer(p_name_2promote, p);
		return true;
	}
	
}