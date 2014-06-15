package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGBio implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to view " + ChatColor.BOLD + "/gbio.");
			return true;
		}
		
		if(args.length == 0) {
			String g_name = GuildMechanics.getGuild(p.getName());
			
			if(!(GuildMechanics.isGuildLeader(p.getName()) || GuildMechanics.isGuildCoOwner(p.getName()))) {
				// TODO: Display the biography if it exists.
				if((!(GuildMechanics.guild_bio.containsKey(GuildMechanics.getGuild(p.getName())))) || GuildMechanics.guild_bio.get(GuildMechanics.getGuild(p.getName())) == null) {
					// NO MOTD.
					p.sendMessage(ChatColor.RED + "No " + ChatColor.BOLD + "GUILD BIOGRAPHY" + ChatColor.RED + " currently set.");
					p.sendMessage(ChatColor.GRAY + "Your guild leader/co-owner should use /gbio to write one!");
					return true;
				}
				if(GuildMechanics.guild_bio.containsKey(g_name) && GuildMechanics.guild_bio.get(g_name) != null) {
					String bio = GuildMechanics.guild_bio.get(g_name);
					p.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.BOLD + "Guild Biography: " + ChatColor.DARK_AQUA + bio);
				}
				return true;
			}
			
			if(GuildMechanics.guild_bio_dynamic.containsKey(p.getName())) {
				p.sendMessage(ChatColor.RED + "You already have a pending guild biography. Type 'cancel' to void that one before starting another.");
				return true;
			}
			
			// Ok so they're the leader, let's let them start writing.
			GuildMechanics.guild_bio_dynamic.put(p.getName(), "");
			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_AQUA + "Start typing your guild biography just like you would chat.");
			p.sendMessage(ChatColor.GRAY + "Send your typed message at any time as a line break. Type '" + ChatColor.GREEN.toString() + "confirm" + ChatColor.GRAY + "' when you're done OR '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to void this process.");
			p.sendMessage("");
			p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "0/512 Characters");
			return true;
		}
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Syntax.");
			p.sendMessage(ChatColor.GRAY + "Usage: /gbio");
			return true;
		}
		return true;
	}
	
}