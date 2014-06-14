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

		if (args.length < 1  && !pl.isOp() && !PermissionMechanics.isGM(pl.getName())) {
			pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player! /gsetleader <PLAYER>");
			return true;
		} else if (args.length <= 1 && PermissionMechanics.isGM(pl.getName()) || pl.isOp()) {
			pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player and/or guild! /gsetleader <PLAYER> [GUILD]");
			return true;
		}

		if (GuildMechanics.inGuild(pl.getName()) || PermissionMechanics.isGM(pl.getName()) || pl.isOp()) {
			if (GuildMechanics.isGuildLeader(pl.getName()) || PermissionMechanics.isGM(pl.getName()) || pl.isOp()) {
				if (args.length != 1) {
					String g_name = "";
					for(String s : args) g_name += s + " ";
					g_name = g_name.substring(args[0].length(), g_name.length() - 1);
					if (g_name.endsWith(" ")) g_name = g_name.substring(1, g_name.length() - 1);
					if (!GuildMechanics.guild_map.containsKey(g_name)) {
						pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "No guild exists by that name!");
						return true;
					}
					GuildMechanics.setGuildRank(GuildMechanics.getGuildOwner(g_name), 3);
					GuildMechanics.setGuildRank(args[0], 1);
					
					pl.sendMessage(ChatColor.GREEN + "You've set " + ChatColor.UNDERLINE + "" + args[0] + ChatColor.GREEN + " as guild leader of " + ChatColor.UNDERLINE + g_name);
					GuildMechanics.sendMessageToGuild(pl, "I've set " + ChatColor.BOLD + args[0] + ChatColor.WHITE + " as leader of your guild");
				} else {
					GuildMechanics.setGuildRank(GuildMechanics.getGuildOwner(GuildMechanics.getGuild(pl.getName())), 3);
					GuildMechanics.setGuildRank(args[0], 1);
					
					pl.sendMessage(ChatColor.GREEN + "You've set " + ChatColor.UNDERLINE + "" + args[0] + ChatColor.GREEN + " as guild leader of " + ChatColor.UNDERLINE + GuildMechanics.getGuild(pl.getName()));
					GuildMechanics.sendMessageToGuild(pl, "I've set " + ChatColor.BOLD + args[0] + ChatColor.WHITE + " as leader of our guild");
				}
			}
		}
		return true;
	}

}
