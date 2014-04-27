package minecade.dungeonrealms.DonationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddPet implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
			if(!(p.isOp())) { return true; }
		}
		if(args.length != 2) {
			if(p != null) {
				p.sendMessage("Incorrect Syntax. " + "/addpet <player> <pet>");
				return true;
			}
			Main.log.info("[PetMechanics] Incorrect syntax. /addpet <player> <pet>");
		}
		
		String player = args[0];
		String pet = args[1];
		
		DonationMechanics.addPetToPlayer(player, pet);
		DonationMechanics.sendPacketCrossServer("[addpet]" + player + ":" + pet, -1, true);
		
		Main.log.info("[PetMechanics] Added pet '" + pet + "' to player " + player + ".");
		if(p != null) {
			p.sendMessage("Added pet '" + pet + "' to player " + player + ".");
		}
		return true;
	}
	
}