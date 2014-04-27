package minecade.dungeonrealms.DonationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRewardSubLife implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		
		DonationMechanics.tickLifetimeSubEcash();
		Main.log.info("[DonationMechanics] 999 E-CASH has been given to all sub++ users.");
		return true;
	}
	
}