package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuildSetLeader implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;

		if (pl == null) return true;

		if (args.length < 1) {
			pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player! /gsetleader <PLAYER>");
			return true;
		}
		
		if (args.length == 1 && GuildMechanics.inGuild(pl.getName())) {
			pl.sendMessage(ChatColor.RED + "You are " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " in a guild. Please specify a guild name! /gsetleader <PLAYER> <GUILD>");
		}

		if (GuildMechanics.inGuild(pl.getName()) || PermissionMechanics.isGM(pl.getName()) || pl.isOp()) {
			if (GuildMechanics.isGuildLeader(pl.getName()) || PermissionMechanics.isGM(pl.getName()) || pl.isOp()) {
				if (args.length > 1) {
					String g_name = "";
					for(String s : args) g_name += s + " ";
					g_name = g_name.substring(args[0].length(), g_name.length() - 1);
					if (g_name.endsWith(" ")) g_name = g_name.substring(1, g_name.length() - 1);
					if (!GuildMechanics.guild_map.containsKey(g_name)) {
						pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "No guild exists by that name!");
						return true;
					}
					if (GuildMechanics.inGuild(args[0]) && !GuildMechanics.getGuild(args[0]).equals(g_name)) {
						pl.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + args[0] + ChatColor.RED + " is already in a different guild; " + ChatColor.UNDERLINE + g_name);
						return true;
					}
					
					GuildMechanics.setGuildRank(GuildMechanics.getGuildOwner(g_name), 1);
					GuildMechanics.setGuildRank(args[0], 3);
					
					pl.sendMessage(ChatColor.GREEN + "You've set " + ChatColor.UNDERLINE + "" + args[0] + ChatColor.GREEN + " as guild leader of " + ChatColor.UNDERLINE + g_name);
					GuildMechanics.sendGuildMessageCrossServer(ChatColor.AQUA + "" + ChatColor.UNDERLINE + pl.getName() + ChatColor.GRAY + " has set " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + args[0] + " as " + ChatColor.BOLD + "LEADER" + ChatColor.GRAY + " of your guild.");
				} else {
					if (GuildMechanics.inGuild(args[0]) && !GuildMechanics.getGuild(args[0]).equals(GuildMechanics.getGuild(pl.getName()))) {
						pl.sendMessage(ChatColor.RED + "That user isn't part of your guild.");
						return true;
					}
					GuildMechanics.setGuildRank(GuildMechanics.getGuildOwner(GuildMechanics.getGuild(pl.getName())), 1);
					GuildMechanics.setGuildRank(args[0], 3);
					
					pl.sendMessage(ChatColor.GREEN + "You've set " + ChatColor.UNDERLINE + args[0] + ChatColor.GREEN + " as guild leader of " + ChatColor.UNDERLINE + GuildMechanics.getGuild(pl.getName()));
					GuildMechanics.sendGuildMessageCrossServer(ChatColor.AQUA + "" + ChatColor.UNDERLINE + pl.getName() + ChatColor.GRAY + " has set " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + args[0] + " as " + ChatColor.BOLD + "LEADER" + ChatColor.GRAY + " of your guild.");
				}
			}
		}
		return true;
	}

}
