package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGQuit implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gquit.");
			return true;
		}
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/gquit");
			return true;
		}
		
		String g_name = GuildMechanics.getGuild(p.getName());
		p.sendMessage(ChatColor.GRAY + "Are you sure you want to QUIT the guild '" + ChatColor.DARK_AQUA + g_name + ChatColor.GRAY + "' - This cannot be undone. " + "(" + ChatColor.GREEN.toString() + ChatColor.BOLD + "Y" + ChatColor.GRAY + " / " + ChatColor.RED.toString() + ChatColor.BOLD + "N" + ChatColor.GRAY + ")");
		if(GuildMechanics.isGuildLeader(p.getName())) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "WARNING: " + ChatColor.GRAY + "You are the " + ChatColor.UNDERLINE + "GUILD LEADER" + ChatColor.GRAY + ", if you leave this guild it will be " + ChatColor.BOLD + "PERMENANTLY DELETED" + ChatColor.GRAY + ". All members will be kicked, and you will lose your 5,000g deposit.");
		}
		
		GuildMechanics.guild_quit_confirm.add(p.getName());
		return true;
	}
	
}