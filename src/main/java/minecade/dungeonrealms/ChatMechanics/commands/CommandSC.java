package minecade.dungeonrealms.ChatMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSC implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = (Player)sender;
		if (pl == null) return true;
		
		if (args.length <= 0) {
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect syntax. You must supply a message! " + ChatColor.GOLD + "/sc <MESSAGE>");
			return true;
		}
		
		String prefix = ChatMechanics.getPlayerPrefix(pl);
		String msg = "";
		
		for (String s : args) msg += s + " ";
		msg = ChatColor.WHITE + msg;
		if (msg.endsWith(" ")) msg = msg.substring(0, msg.length() -1);
		if (PermissionMechanics.isStaff(pl)) {
			ChatMechanics.sendAllStaffMessage(pl, msg);
		} else return true;
		
		Main.log.info(ChatColor.stripColor("<" + "Staff" + ">" + " " + prefix + pl.getName() + ": "+ ChatColor.WHITE + msg));
 		return true;
	}

}
