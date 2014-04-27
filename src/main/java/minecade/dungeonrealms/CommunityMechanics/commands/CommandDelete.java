package minecade.dungeonrealms.CommunityMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandDelete implements CommandExecutor {
	
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
			p.sendMessage(ChatColor.RED + "Incorrect syntax - " + ChatColor.BOLD + "/delete <PLAYER>");
			return true;
		}
		
		final String to_remove = args[0];
		
		new BukkitRunnable() {
			@Override
			public void run() {
				CommunityMechanics.deleteFromAllLists(p, to_remove);
				CommunityMechanics.updateCommBook(p);
			}
		}.runTaskLaterAsynchronously(Main.plugin, 1L);
		
		// TODO: Send "X has logged out" to person who was deleted.
		return true;
	}
	
}
