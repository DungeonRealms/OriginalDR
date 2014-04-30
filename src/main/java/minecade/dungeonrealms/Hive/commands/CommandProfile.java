package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.jsonlib.JSONMessage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProfile implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		//p.sendMessage(ChatColor.RED + "This feature is temporarily disabled due to host transfer.");
		if(!p.isOp()) return true;
		String code = Hive.generateProfileCode(p.getName());
		String url = "http://72.8.157.66:11687/drinv/?name=" + p.getName() + "&code=" + code;
		JSONMessage msg = new JSONMessage("Click ", ChatColor.AQUA);
		msg.addURL(ChatColor.UNDERLINE + "HERE", ChatColor.GREEN, url);
		msg.addText(" to open your profile!", ChatColor.AQUA);
		msg.sendToPlayer(p);
		return true;
	}
	
}