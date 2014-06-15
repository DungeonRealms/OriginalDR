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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;

		if (pl == null) return true;

		if (!GuildMechanics.inGuild(pl.getName())) {
			pl.sendMessage(ChatColor.RED + "You must be part of a guild first.");
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
				Player to_promote = Bukkit.getPlayer(args[0]);
				String g_name = "";
				String p_name = "";
				if (to_promote != null) {
					p_name = to_promote.getName();
				} else{
					pl.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + args[0] + ChatColor.RED + " is not online.");
					return true;
				}
				for(String s : args) g_name += s + " ";
				g_name = g_name.substring(p_name.length(), g_name.length() - 1);
				g_name = g_name.substring(1, g_name.length());
				GuildMechanics.promoteToOwnerInSpecificGuild(pl, p_name, g_name);
				return true;
			}
			pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "can't" + ChatColor.RED + " change other guilds' leaders!");
			return true;
		}
		if (GuildMechanics.isGuildLeader(pl.getName())) {
			Player to_promote = Bukkit.getPlayer(args[0]);
			if (to_promote != null) {
				GuildMechanics.promoteToOwnerInOwnGuild(pl, to_promote.getName());
				return true;
			}
			pl.sendMessage(ChatColor.RED + "The user you're trying to set as leader is currently " + ChatColor.UNDERLINE + "offline" + ChatColor.RED + ".");
			return true;
		}
		pl.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD LEADER" + ChatColor.RED + " to change your guild's owner.");
		return true;
	}

}
