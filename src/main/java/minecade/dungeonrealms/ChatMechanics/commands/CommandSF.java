package minecade.dungeonrealms.ChatMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.jsonlib.JSONMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSF implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = (Player)sender;
		if (pl == null) return true;
		
		if (args.length <= 0) {
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect syntax. You must supply a message! " + ChatColor.GOLD + "/sf <MESSAGE>");
			return true;
		}
		
		String rank = PermissionMechanics.getRank(pl.getName());
		String prefix = ChatMechanics.getPlayerPrefix(pl);
		String msg = "";
		
		
		for (String s : args) msg += s + " ";
		
		if (PermissionMechanics.isStaff(pl)) {
			String aprefix = pl.getName() + ": " + ChatColor.WHITE;
			JSONMessage filter = null;
	        JSONMessage normal = null;
			
			if (msg.contains("@i@") && pl.getItemInHand().getType() != Material.AIR) {
				String[] split = msg.split("@i@");
				String after = "";
				String before = "";
				if (split.length > 0) before = split[0];
				if (split.length > 1) after = split[1];
				
				normal = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
				normal.addText(before + " ");
				normal.addItem(pl.getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
				normal.addText(after);
				
				filter = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
				filter.addText(ChatMechanics.censorMessage(before) + " ");
				filter.addItem(pl.getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
				filter.addText(ChatMechanics.censorMessage(after));
			}
			 
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				String pRank = PermissionMechanics.getRank(p.getName());
				if (PermissionMechanics.isStaff(p)) {	
					if (normal != null) {
						JSONMessage toSend = normal;
						if (ChatMechanics.hasAdultFilter(p.getName())) {
							toSend = filter;
						}
						ChatColor pColor = ChatMechanics.getPlayerColor(pl, p);
						toSend.setText(ChatColor.GOLD + "<" + ChatColor.BOLD + "Staff" + ChatColor.GOLD + ">" + " " + prefix + pColor + aprefix);
						toSend.sendToPlayer(p);
					} else {
						ChatColor pColor = ChatMechanics.getPlayerColor(pl, p);
						p.sendMessage(ChatColor.GOLD + "<" + ChatColor.BOLD + "Staff" + ChatColor.GOLD + ">" + " " + prefix + pColor + aprefix + msg);
					}
				}
			}
		} else return true;
		
		Main.log.info(ChatColor.stripColor("" + "<" + "Staff" + ">" + " " + prefix + pl.getName() + ": " + msg));
 		return true;
	}

}
