package minecade.dungeonrealms.MountMechanics.commands;

import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class CommandMount implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		p.getInventory().addItem(ShopMechanics.removePrice(MountMechanics.t1_mule));
		p.getInventory().addItem(ShopMechanics.removePrice(MountMechanics.t2_mule));
		p.getInventory().addItem(ShopMechanics.removePrice(MountMechanics.t2_mule_upgrade));
		p.getInventory().addItem(ShopMechanics.removePrice(MountMechanics.t3_mule_upgrade));
		
		if(p.getName().equalsIgnoreCase("Vaquxine")) {
			for(Entity ent : p.getNearbyEntities(4, 4, 4)) {
				if(ent instanceof Horse) {
					ent.remove();
				}
			}
		}
		return true;
	}
	
}