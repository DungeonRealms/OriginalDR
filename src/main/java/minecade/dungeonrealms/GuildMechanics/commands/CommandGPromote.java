package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGPromote implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gpromote <player>");
			return true;
		}

		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gpromote.");
			return true;
		}

		if(!(GuildMechanics.isGuildLeader(p.getName()) || GuildMechanics.isGuildCoOwner(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD OWNER" + ChatColor.RED + " or " + ChatColor.BOLD + "GUILD CO-OWNER" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gpromote.");
			return true;
		}

		String p_name_2promote = args[0];
		if(Bukkit.getPlayer(p_name_2promote) != null) {
			p_name_2promote = Bukkit.getPlayer(p_name_2promote).getName();
		} else {
			p_name_2promote = args[0];
		}

		if (GuildMechanics.isGuildLeader(p_name_2promote)) {
			p.sendMessage(ChatColor.RED + "You can't promote the owner of a guild.");
			return true;
		}

		if (p_name_2promote.equalsIgnoreCase(p.getName())) {
			p.sendMessage(ChatColor.RED + "You cannot promote yourself in your own guild.");
			return true;
		}

		if (!(GuildMechanics.areGuildies(p.getName(), p_name_2promote))) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + p_name_2promote + ChatColor.RED + " is not in YOUR guild. But in " + ChatColor.UNDERLINE + GuildMechanics.getGuild(p_name_2promote));
			return true;
		}

		if (!GuildMechanics.isGuildOfficer(p_name_2promote) && !GuildMechanics.isGuildCoOwner(p_name_2promote)) {
			if (GuildMechanics.getRankNum(p.getName()) >= 3) {
				GuildMechanics.promoteToOfficer(p_name_2promote, p);
				return true;
			}
			p.sendMessage(ChatColor.RED + "You can't promote the guild owner.");
			return true;
		}

		if (GuildMechanics.isGuildCoOwner(p.getName()) && GuildMechanics.isGuildOfficer(p_name_2promote)) {
			p.sendMessage(ChatColor.RED + "You aren't allowed to promote others to the rank of " + ChatColor.BOLD + "CO-OWNER");
			return true;
		}

		if (GuildMechanics.getTotalCoOwnersCount(GuildMechanics.getGuild(p.getName())) == 2 && GuildMechanics.isGuildLeader(p.getName())) {
			p.sendMessage(ChatColor.RED + "You've already set 2 guild co-owners, demote one of them to set another!");
			return true;
		}

		if (GuildMechanics.isGuildLeader(p.getName()) && GuildMechanics.isGuildOfficer(p_name_2promote)) {
			GuildMechanics.promoteToCoOwner(p_name_2promote, p);
		}
		return true;
	}
}