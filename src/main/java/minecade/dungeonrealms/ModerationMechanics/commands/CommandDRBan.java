package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRBan implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		String rank = "";
		boolean perm = false;
		if(p != null) {
			rank = PermissionMechanics.getRank(p.getName());
			if(rank == null) { return true; }
			
			if(!(p.isOp()) && !rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm")) { return true; }
		}
		
		if(args.length <= 2) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/drban <PLAYER> <TIME(in hours)> <REASON>");
				p.sendMessage(ChatColor.GRAY + "Insert -1 for <TIME> to permentantly lock.");
			}
			return true;
		}
		
		String banner = "Console";
		if(p != null) {
			banner = p.getName();
		}
		final String p_name = args[0];
		int hours = 24;
		try {
			hours = Integer.parseInt(args[1]);
		} catch(NumberFormatException nfe) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "Invalid time entired for hours of duration for the ban.");
				p.sendMessage(ChatColor.GRAY + "You entered: " + args[1] + ", which is not a numberic value.");
				return true;
			}
		}
		
		if(p != null) {
			if(rank.equalsIgnoreCase("pmod") && ((hours > 24) || hours == -1)) {
				p.sendMessage(ChatColor.RED + "As a PLAYER MODERATOR, you can only ban players for up to 24 hours.");
				return true;
			}
			int count = ModerationMechanics.ban_count.get(p.getName());
			if(rank.equalsIgnoreCase("pmod") && count >= 10) {
				p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " bans today.");
				return true;
			}
			count += 1;
			ModerationMechanics.ban_count.put(p.getName(), count);
		}
		
		final long unban_date = (System.currentTimeMillis() + (1000 * (hours * 3600)));
		String reason = "";
		
		for(int i = 2; i < args.length; i++) {
			reason += args[i] + " ";
		}
		
		if(hours == -1) {
			perm = true;
		}
		
		final String f_reason = reason;
		final String f_banner = banner;
		final boolean f_perm = perm;
		
		if(PermissionMechanics.getRank(p_name).equalsIgnoreCase("gm") || (Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOp() && sender instanceof Player)) {
			p.sendMessage(ChatColor.RED + "You cannot ban a Game Moderator unless you have console acesss.");
			return true;
		}
		
		Thread ban_player = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
					ModerationMechanics.BanPlayer(p_name, unban_date, f_reason, f_banner, f_perm);
				} catch(Exception err) {} // Wait 100ms -- this should occur after function has returned.
				//ModerationMechanics.BanPlayer(p_name, unban_date, f_reason, f_banner, f_perm); wtf?
			}
		});
		ban_player.start();
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player banned = Bukkit.getPlayer(p_name);
			if(reason == "") {
				banned.kickPlayer(ChatColor.RED.toString() + "Your account has been TEMPORARILY locked due to suspisious activity." + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
			} else if(reason.length() > 0) {
				banned.kickPlayer(ChatColor.RED.toString() + "Your account has been TEMPORARILY locked due to " + reason + "\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
			}
		} else {
			Thread t = new Thread(new Runnable() {
				public void run() {
					CommunityMechanics.sendPacketCrossServer("@ban@" + p_name + ":" + f_reason, -1, true);
				}
			});
			
			t.start();
		}
		
		if(p != null) {
			p.sendMessage(ChatColor.AQUA + "You have banned the user '" + p_name + "' for " + hours + " hours.");
			p.sendMessage(ChatColor.GRAY + "Reason: " + reason);
		}
		
		ModerationMechanics.log.info("[ModerationMechanics] BANNED player " + p_name + " for " + hours + " hours because " + reason);
		
		return true;
	}
	
}
