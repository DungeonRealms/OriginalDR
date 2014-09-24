package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGAccept implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gaccept");
			return true;
		}

		if(!(GuildMechanics.guild_invite.containsKey(p.getName()))) {
			p.sendMessage(ChatColor.RED + "No pending guilds invites.");
			return true;
		}

		String guild_name = GuildMechanics.guild_invite.get(p.getName());
		String inviter = GuildMechanics.guild_inviter.get(p.getName());

		if(!GuildMechanics.guild_map.containsKey(guild_name)) { // Data is gone.
			p.sendMessage(ChatColor.RED + "This guild invite is no longer available.");
			GuildMechanics.guild_invite.remove(p.getName());
			GuildMechanics.guild_inviter.remove(p.getName());
			GuildMechanics.guild_invite_time.remove(p.getName());
			GuildMechanics.guild_inviter.remove(p.getName());
			return true;
		}

		for(String s : GuildMechanics.getGuildMembers(guild_name)) {
			if(Bukkit.getPlayer(s) != null) {
				Player pty_mem = Bukkit.getPlayer(s);
				pty_mem.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(guild_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA.toString() + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.UNDERLINE + "joined" + ChatColor.GRAY + " your guild. [INVITE: " + ChatColor.ITALIC + inviter + ChatColor.GRAY + "]");
			}
		}

		// They will always be local, just need to tell others.
		GuildMechanics.addPlayerToGuild(p.getName(), guild_name);

		String message_to_send = "[gadd]" + p.getName() + "," + guild_name + ":" + inviter;
		GuildMechanics.sendGuildMessageCrossServer(message_to_send);

		GuildMechanics.setPlayerGuildSQL(p.getName(), guild_name, false);
		GuildMechanics.updateGuildSQL(guild_name);

		AchievementMechanics.addAchievement(p.getName(), "Guildmember");

		p.sendMessage(ChatColor.DARK_AQUA + "You have joined '" + ChatColor.BOLD + guild_name + "'" + ChatColor.DARK_AQUA + ".");
		p.sendMessage(ChatColor.GRAY + "To chat with your new guild, use " + ChatColor.BOLD + "/g" + ChatColor.GRAY + " OR " + ChatColor.BOLD + " /g <message>");

		return true;
	}

}
