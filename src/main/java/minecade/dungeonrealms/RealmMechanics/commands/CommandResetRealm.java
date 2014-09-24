package minecade.dungeonrealms.RealmMechanics.commands;

import minecade.dungeonrealms.RealmMechanics.RealmMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandResetRealm implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(RealmMechanics.realm_loaded_status.containsKey(p.getName()) && RealmMechanics.realm_loaded_status.get(p.getName()) == true) {
			p.sendMessage(ChatColor.RED + "Your realm is still LOADED on another server.");
			p.sendMessage(ChatColor.GRAY + "Wait 2 minute(s) and try again, or rejoin the other server.");
			RealmMechanics.async_realm_status.add(p.getName());
			return true;
		}
		if(RealmMechanics.realm_reset_cd.containsKey(p.getName())) {
			if((System.currentTimeMillis() - RealmMechanics.realm_reset_cd.get(p.getName())) <= (3600 * 1000)) { // 1 hour.
				p.sendMessage(ChatColor.RED + "You may only reset your realm " + ChatColor.UNDERLINE + "ONCE" + ChatColor.RED + " per hour.");
				return true;
			}
		}
		p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "REALM RESET RUNNING..." + ChatColor.RED + " 0%");
		RealmMechanics.resetRealm(p);
		RealmMechanics.realm_tier.put(p.getName(), 1);
		int slot = -1;
		if(p.getInventory().contains(Material.NETHER_STAR)) {
			slot = p.getInventory().first(Material.NETHER_STAR);
		}
		
		if(slot == -1) {
			if(p.getInventory().getItem(7) == null && p.getInventory().getItem(7).getType() == Material.AIR) {
				p.getInventory().setItem(7, RealmMechanics.makeTeleportRune(p));
			} else {
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeTeleportRune(p));
			}
		}
		p.updateInventory();
		RealmMechanics.realm_reset_cd.put(p.getName(), System.currentTimeMillis());
		return true;
	}
	
}