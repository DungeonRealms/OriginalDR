package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGBanner implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(!(GuildMechanics.inGuild(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be in a " + ChatColor.BOLD + "GUILD" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gbanner.");
			return true;
		}
		
		String g_name = GuildMechanics.getGuild(p.getName());
		
		if(!(GuildMechanics.isGuildLeader(p.getName()) || GuildMechanics.isGuildCoOwner(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You must be the " + ChatColor.BOLD + "GUILD LEADER" + ChatColor.RED + " to use " + ChatColor.BOLD + "/gbanner.");
			return true;
		}
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Syntax.");
			p.sendMessage(ChatColor.GRAY + "Usage: /gbanner <imgur direct link to banner>");
			p.sendMessage(ChatColor.GRAY + "Upload your guild banner to imgur.com, then paste the 'direct link' as the command argument.");
			p.sendMessage(ChatColor.RED + "Max File Size: 512KB");
			return true;
		}
		
		if(args.length == 1) {
			// First, we check if it's an imgur link.
			String url = args[0];
			if(!(url.contains("/")) || !(url.contains("imgur.com"))) {
				p.sendMessage(ChatColor.RED + "Invalid URL. Type /gbanner for assistance.");
			}
			
			url = url.replaceAll("http://", "");
			url = url.replaceAll("www.", "");
			
			String img_code = url.substring(url.lastIndexOf("/") + 1, url.length());
			if(img_code.contains(".")) {
				img_code = img_code.substring(0, img_code.lastIndexOf(".")); // Gets rid of the file extension if there is one.
			}
			
			String better_url = "i.imgur.com/" + img_code + ".png";
			Main.log.info("[GuildMechanics] Setting banner of " + g_name + " to: " + better_url);
			GuildMechanics.setGuildBannerSQL(g_name, better_url);
			
			p.sendMessage(ChatColor.GREEN + "Your Guild Banner has been SET. (" + ChatColor.UNDERLINE + better_url + ChatColor.GREEN + ")");
			p.sendMessage(ChatColor.GRAY + "Click the link to confirm it's viewable.");
			return true;
		}
		return true;
	}
	
}