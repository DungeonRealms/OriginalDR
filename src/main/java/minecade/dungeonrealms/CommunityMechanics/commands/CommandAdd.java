package minecade.dungeonrealms.CommunityMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class CommandAdd implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("crypt")) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
			}
			
			return true;
		}
		
		if(!(args.length == 1)) {
			p.sendMessage(ChatColor.RED + "Incorrect syntax - " + ChatColor.BOLD + "/add <PLAYER>");
			return true;
		}
		
		final String to_add = args[0];
		
		String rank = PermissionMechanics.getRank(p.getName());
		int max_buds = 50;
		
		if(rank.equalsIgnoreCase("sub")) {
			max_buds = 100;
		}
		if(rank.equalsIgnoreCase("sub+") || rank.equalsIgnoreCase("sub++")) {
			max_buds = 150;
		}
		
		if(CommunityMechanics.getBuddyListLength(p.getName()) >= max_buds) {
			p.sendMessage(ChatColor.RED + "Max. Buddy Limit of " + ChatColor.BOLD + max_buds + ChatColor.RED + " Reached.");
			p.sendMessage(ChatColor.GRAY + "You can " + ChatColor.UNDERLINE + "subscribe" + ChatColor.GRAY + " at store.dungeonrealms.net to increase your buddy limit");
			return true;
		}
		
		if(CommunityMechanics.isPlayerOnBuddyList(p, to_add)) {
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + to_add + ChatColor.YELLOW + " is already on your BUDDY LIST.");
			return true;
		}
		
		if(CommunityMechanics.isPlayerOnIgnoreList(p, to_add)) {
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + to_add + ChatColor.YELLOW + " is currently on your IGNORE LIST.");
			p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.BOLD + "/delete " + to_add + ChatColor.GRAY + " to remove them from your ignore list.");
			return true;
		}
		
		if(to_add.equalsIgnoreCase(p.getName())) {
			p.sendMessage(ChatColor.YELLOW + "You can't add yourself to your buddy list!");
			p.sendMessage(ChatColor.GRAY + "Go make some friends :).");
			return true;
		}
		
		if(to_add.length() > 16) {
			p.sendMessage(ChatColor.YELLOW + "Player name exceeds max. length of 16 characters.");
			return true;
		}
		
		final OfflinePlayer op = Bukkit.getOfflinePlayer(to_add);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				CommunityMechanics.addBuddy(p, to_add);
				CommunityMechanics.updateCommBook(p);
				p.sendMessage(ChatColor.GREEN + "You've added " + ChatColor.BOLD + to_add + ChatColor.GREEN + " to your BUDDY list.");
				
				if(op.isOp()) { return; }
				
				int bud_server = CommunityMechanics.getPlayerServer(to_add);
				
				if(bud_server >= 0) {
					String prefix = "US-";
					
					if(bud_server > 1000) {
						bud_server -= 1000;
						prefix = "EU-";
					}
					
					if(bud_server > 2000) {
						bud_server -= 2000;
						prefix = "BR-";
					}
					
					if(bud_server >= 3000) {
						bud_server -= 3000;
						prefix = "US-YT";
					}
					
					String remote_server = prefix + bud_server;
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2F, 1.2F);
					p.sendMessage(ChatColor.YELLOW + to_add + " has joined " + remote_server + ".");
				} else if(bud_server <= -1) { return; // They're not online.
				}
			}
		}.runTaskLaterAsynchronously(Main.plugin, 1L);
		
		return true;
	}
	
}
