package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

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
			if (!pl.isOp()) {
				pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player! /gsetleader <PLAYER>");
			} else {
				pl.sendMessage(ChatColor.RED + "Invalid syntax. You must supply a player and/or guild! /gsetleader <PLAYER> [GUILD]");
			}
			return true;
		}
		
		if (args.length > 1 && pl.isOp()) {
			String g_name = "";
			for(String s : args) g_name += s + " ";
			g_name = g_name.substring(args[0].length(), g_name.length() - 1);
			if (g_name.endsWith(" ")) g_name = g_name.substring(1, g_name.length() - 1);
			GuildMechanics.promoteToOwnerInSpecificGuild(pl, args[0], g_name);
		} else if (args.length == 1 && GuildMechanics.inGuild(pl.getName()) && GuildMechanics.isGuildLeader(pl.getName())) {
			GuildMechanics.promoteToOwnerInOwnGuild(pl, args[0]);
		}
		return true;
	}

}
