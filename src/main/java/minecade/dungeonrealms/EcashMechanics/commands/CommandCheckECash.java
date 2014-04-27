package minecade.dungeonrealms.EcashMechanics.commands;

import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckECash implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		
		if(ps != null) {
			if(args.length != 1) {
				ps.sendMessage("Syntax: /checkecash <player>");
				return true;
			}
			String p_name = args[0];
			if(Hive.player_ecash.containsKey(p_name)) {
				int ecash_balance = Hive.player_ecash.get(p_name);
				ps.sendMessage(p_name + "'s E-CASH: " + ChatColor.GREEN + ecash_balance);
			} else {
				ps.sendMessage(ChatColor.RED + "Player '" + p_name + "' is not online your local server.");
			}
		}
		return true;
	}
	
}