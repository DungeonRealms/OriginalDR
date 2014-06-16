package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuildSetLeader implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;

		if (pl == null) return true;

		if (!GuildMechanics.inGuild(pl.getName()) && !(pl.isOp() || PermissionMechanics.isGM(pl.getName()))) {
			pl.sendMessage(ChatColor.RED + "You must be part of a guild to do this.");
			return true;
		}

		if (args.length < 1) {
			if (!pl.isOp()) {
				pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player! /gsetleader <PLAYER> - <PLAYER> must be online!");
			} else {
				pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player and/or guild! /gsetleader <PLAYER> [GUILD]");
			}
			return true;
		}

		if (args.length > 1) {
			if (pl.isOp() || PermissionMechanics.isGM(pl.getName())) {
				Player targ = Bukkit.getPlayer(args[0]);
				String g_name = "";
				String p_name = targ != null ? targ.getName() : args[0];
				for(String s : args) g_name += s + " ";
				g_name = g_name.substring(args[0].length(), g_name.length() - 1);
				g_name = g_name.substring(1, g_name.length());
				if (GuildMechanics.guild_map.containsKey(g_name)) {
					GuildMechanics.promoteToOwnerInSpecificGuild(pl, p_name, g_name);
				} else {
					pl.sendMessage(ChatColor.RED + "No guild exists by the name of " + ChatColor.UNDERLINE + g_name);
				}
				return true;
			}
			pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "can't" + ChatColor.RED + " change other guilds' leaders!");
			return true;
		}

		if (GuildMechanics.isGuildLeader(pl.getName())) {
			Player to_promote = Bukkit.getPlayer(args[0]);
			String to_pro_name = to_promote != null ? to_promote.getName() : args[0];
			GuildMechanics.promoteToOwnerInOwnGuild(pl, to_pro_name);
			return true;
		}
		pl.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD LEADER" + ChatColor.RED + " to change your guild's owner.");
		return true;
	}

}
