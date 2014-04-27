package minecade.dungeonrealms.PetMechanics.commands;

import minecade.dungeonrealms.PetMechanics.PetMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class CommandPet implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(p.isOp())) { return true; }
		p.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.BAT, ""));
		p.sendMessage("[DEBUG] Field 0x0000c2 -> null");
		
		for(Entity ent : p.getNearbyEntities(32, 32, 32)) {
			if(ent instanceof Horse) {
				ent.remove();
			}
		}
		return true;
	}
	
}