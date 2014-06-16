package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGSetRank implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;
		if (pl == null) return true;

		if (!pl.isOp()) {
			pl.sendMessage(ChatColor.RED + "You lack the permissions to use this command.");
			return true;
		}

		if (args.length < 1) {
			pl.sendMessage(ChatColor.RED + "Invalid syntax. \u00A7n/gsetrank <1-4> \u00A7cor \u00A7n/gsetrank <PLAYER> <1-4>");
			return true;
		}
		
		if ((args.length == 2 && !isInt(args[1])) || (args.length == 1 && !isInt(args[0]))) {
			pl.sendMessage("Your rank parameter must be an integer!");
			return true;
		}

		if (args.length == 1) {
			if (Integer.parseInt(args[0]) > 4 || Integer.parseInt(args[0]) < 1) {
				pl.sendMessage(ChatColor.RED + "Please choose a rank between \u00A7n1 \u00A7cand \u00A7n4."); // \u00A7 = § - too lazy to use ChatColor, takes up less space as well.
				return true;
			}
			if (GuildMechanics.guild_map.containsKey(GuildMechanics.getGuild(pl.getName()))) {
				GuildMechanics.setGuildRank(pl.getName(), Integer.parseInt(args[0]));
				String rank = "";
				switch (Integer.parseInt(args[0])) {
				case 1:
					rank = "Member";
					break;
				case 2:
					rank = "Officer";
					break;
				case 3:
					rank = "Co-Owner";
					break;
				case 4:
					rank = "Owner";
					break;
				default: 
					break;
				}
				pl.sendMessage(ChatColor.GREEN + "You set your rank to " + ChatColor.UNDERLINE + rank);
				GuildMechanics.sendGuildMessageCrossServer("[gupdate]" + GuildMechanics.getGuild(args[0])); 
				return true;
			}
			pl.sendMessage(ChatColor.RED + "You aren't in an existant guild.");
		}

		if (args.length == 2) {
			if (Integer.parseInt(args[1]) > 4 || Integer.parseInt(args[1]) < 1) {
				pl.sendMessage(ChatColor.RED + "Please choose a rank between \u00A7n1 \u00A7cand \u00A7n4."); // \u00A7 = § - too lazy to use ChatColor, takes up less space as well.
				return true;
			}
			if (GuildMechanics.guild_map.containsKey(GuildMechanics.getGuild(args[0]))) {
				GuildMechanics.setGuildRank(args[0], Integer.parseInt(args[1]));
				GuildMechanics.updateGuildSQL(GuildMechanics.getGuild(args[0]));
				String rank = "";
				switch (Integer.parseInt(args[1])) {
				case 1:
					rank = "Member";
					break;
				case 2:
					rank = "Officer";
					break;
				case 3:
					rank = "Co-Owner";
					break;
				case 4:
					rank = "Owner";
					break;
				default: 
					break;
				}
				pl.sendMessage(ChatColor.GREEN + "You set " + ChatColor.UNDERLINE + args[0] + ChatColor.RED + " to the rank " + ChatColor.UNDERLINE + rank);
				GuildMechanics.sendGuildMessageCrossServer("[gupdate]" + GuildMechanics.getGuild(args[0])); 
				return true;
			}
			pl.sendMessage(ChatColor.RED + "The guild the user is doesn't exist.");
		}
		return true;
	}

	private boolean isInt(String integer) {
		try {
			Integer.parseInt(integer);
		} catch (NumberFormatException ex) { 
			return false;
		}
		return true;
	}
}