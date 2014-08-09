package minecade.dungeonrealms.Hive.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSync implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("Temporarily Disabled");
		return true;
		/*final Player p = (Player) sender;
		p.updateInventory();
		p.teleport(p);
		
		if((System.currentTimeMillis() - Hive.login_time.get(p.getName())) <= 5000) {
			int seconds_left = 5 - (int) ((System.currentTimeMillis() - Hive.login_time.get(p.getName())) / 1000.0D);
			p.sendMessage(ChatColor.RED + "You cannot /sync for another " + seconds_left + ChatColor.BOLD + "s");
			return true;
		}
		
		if(Hive.last_sync.containsKey(p.getName()) && (((System.currentTimeMillis() - Hive.last_sync.get(p.getName())) <= 10000))) {
			p.sendMessage(ChatColor.RED + "You already have a recent sync request -- please wait a few seconds.");
			return true;
		}
		Hive.last_sync.put(p.getName(), System.currentTimeMillis());
		
		//sync_queue.add(p.getName()); - Disabled lol
		p.sendMessage(ChatColor.GREEN + "Synced player data to " + ChatColor.UNDERLINE + "HIVE" + ChatColor.GREEN + " server.");
		
		return true;*/
	}
	
}