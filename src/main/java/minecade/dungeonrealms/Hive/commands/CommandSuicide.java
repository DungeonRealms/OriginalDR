package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSuicide implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(Hive.server_swap.containsKey(p.getName()) || Hive.server_swap_pending.containsKey(p.getName())) {
					p.sendMessage(ChatColor.GRAY + "You cannot do that right now.");
					return;
				}
				
				Hive.killing_self.add(p.getName());
				p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "WARNING: " + ChatColor.GRAY + "This command will KILL you, you will LOSE every thing you are carrying. If you are sure, type '" + ChatColor.GREEN.toString() + ChatColor.BOLD + "Y" + ChatColor.GRAY + "', if not, type '" + ChatColor.RED.toString() + "cancel" + ChatColor.RED + "'.");
				
			}
		}, 4L);
		return true;
	}
	
}