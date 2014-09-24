package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGKick implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gkick.");
			return true;
		}
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gkick <player>");
			return true;
		}
		
		int g_rank = GuildMechanics.getRankNum(p.getName());
		String g_name = GuildMechanics.getGuild(p.getName());
		
		if(g_rank < 2) { // 2 = officer, 3 = co-owner, 4 = owner, -> 1 is just a member.
			p.sendMessage(ChatColor.RED + "You must be at least a guild " + ChatColor.BOLD + "OFFICER" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gkick");
			return true;
		}
		
		String p_to_kick = args[0];
		
		if(!(GuildMechanics.inSpecificGuild(p_to_kick, g_name))) {
			p.sendMessage(ChatColor.RED + p_to_kick + " is not in your guild.");
			return true;
		}
		
		if(GuildMechanics.getGuildRank(p_to_kick, g_name) == 2 && !(GuildMechanics.isGuildLeader(p.getName()) || GuildMechanics.isGuildCoOwner(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You cannot kick a fellow guild officer. Only the guild leader or co-owner can do that.");
			return true;
		}
		
		if(GuildMechanics.getGuildRank(p_to_kick, g_name) >= 3) {
			p.sendMessage(ChatColor.RED + "You cannot kick someone who has equal or greater power than you.");
			return true;
		}
		
		GuildMechanics.leaveGuild(p_to_kick, g_name, true); // Kick them from local server.
		if(Bukkit.getPlayer(p_to_kick) != null) {
			Player pl = Bukkit.getPlayer(p_to_kick);
			pl.sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + "KICKED" + ChatColor.RED + " out of your guild.");
			pl.sendMessage(ChatColor.GRAY + "Kicked by: " + p.getName());
		}
		
		String message_to_send = "[gkick]" + p_to_kick + "," + g_name + ":" + p.getName();
		GuildMechanics.sendGuildMessageCrossServer(message_to_send);
		// Kick them from all servers to ensure no SQL data issues.
		
		for(String s : GuildMechanics.getOnlineGuildMembers(g_name)) {
			Player pl = Bukkit.getPlayer(ChatColor.stripColor(s));
			if(pl != null) {
				pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA + p_to_kick + " has been " + ChatColor.UNDERLINE + "kicked" + ChatColor.DARK_AQUA + " by " + p.getName() + ".");
			}
		}
		return true;
	}
	
}