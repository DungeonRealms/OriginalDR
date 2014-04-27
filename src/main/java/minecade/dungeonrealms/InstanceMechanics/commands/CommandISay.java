package minecade.dungeonrealms.InstanceMechanics.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandISay implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
		}
		
		if(args.length == 0) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
				p.sendMessage(ChatColor.RED + "Invalid Syntax. Please use /isay <msg> to send a local world messsage.");
				return true;
			}
		}
		
		String msg = "";
		for(String s : args) {
			msg += s + " ";
		}
		msg = msg.substring(0, msg.lastIndexOf(" "));
		
		msg = msg.replaceAll("&0", ChatColor.BLACK.toString());
		msg = msg.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		msg = msg.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		msg = msg.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		msg = msg.replaceAll("&4", ChatColor.DARK_RED.toString());
		msg = msg.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		msg = msg.replaceAll("&6", ChatColor.GOLD.toString());
		msg = msg.replaceAll("&7", ChatColor.GRAY.toString());
		msg = msg.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		msg = msg.replaceAll("&9", ChatColor.BLUE.toString());
		msg = msg.replaceAll("&a", ChatColor.GREEN.toString());
		msg = msg.replaceAll("&b", ChatColor.AQUA.toString());
		msg = msg.replaceAll("&c", ChatColor.RED.toString());
		msg = msg.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		msg = msg.replaceAll("&e", ChatColor.YELLOW.toString());
		msg = msg.replaceAll("&f", ChatColor.WHITE.toString());
		
		msg = msg.replaceAll("&u", ChatColor.UNDERLINE.toString());
		msg = msg.replaceAll("&s", ChatColor.BOLD.toString());
		msg = msg.replaceAll("&i", ChatColor.ITALIC.toString());
		msg = msg.replaceAll("&m", ChatColor.MAGIC.toString());
		
		if(sender instanceof BlockCommandSender) {
			BlockCommandSender cb = (BlockCommandSender) sender;
			for(Player pl : cb.getBlock().getWorld().getPlayers()) {
				pl.sendMessage(msg);
			}
		} else if(sender instanceof Player) {
			for(Player pl : p.getWorld().getPlayers()) {
				pl.sendMessage(msg);
			}
		}
		return true;
	}
	
}