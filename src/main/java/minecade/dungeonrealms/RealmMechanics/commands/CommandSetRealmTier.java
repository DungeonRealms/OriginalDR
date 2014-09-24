package minecade.dungeonrealms.RealmMechanics.commands;

import minecade.dungeonrealms.RealmMechanics.RealmMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetRealmTier implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = null;
		if(sender instanceof Player) {
			pl = (Player) sender;
			if(!(pl.isOp())) { return true; }
		}
		
		if(args.length != 2 && pl != null) {
			pl.sendMessage("/setrealmtier <player> <tier>");
			return true;
		}
		
		String p_name = args[0];
		int tier = Integer.parseInt(args[1]);
		
		if(Bukkit.getPlayer(p_name) == null) {
			pl.sendMessage(ChatColor.RED + "The player '" + p_name + "' is not online.");
			return true;
		}
		
		RealmMechanics.realm_tier.put(p_name, tier);
		pl.sendMessage(ChatColor.GREEN + "Set player " + p_name + "'s realm tier to " + tier);
		return true;
	}
	
}