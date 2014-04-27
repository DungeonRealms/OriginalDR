package minecade.dungeonrealms.RealmMechanics.commands;

import minecade.dungeonrealms.RealmMechanics.RealmMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRealm implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		String msg = "";
		for(String s : args) {
			if(!(s.contains("/realm"))) {
				msg += s + " ";
			}
		}
		
		RealmMechanics.realm_title.put(p.getName(), msg);
		p.sendMessage("");
		p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "                       " + "* REALM TITLE SET *");
		p.sendMessage(ChatColor.GRAY + "\"" + msg + "\"");
		p.sendMessage("");
		return true;
	}
	
}