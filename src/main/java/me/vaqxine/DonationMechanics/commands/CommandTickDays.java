package me.vaqxine.DonationMechanics.commands;

import me.vaqxine.Main;
import me.vaqxine.DonationMechanics.DonationMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTickDays implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player ps = null;
		if(sender instanceof Player){
			ps = (Player)sender;
			if(!(ps.isOp())){
				return true;
			}
		}
		
		DonationMechanics.tickSubscriberDays();
		Main.log.info("[DonationMechanics] Ticked all user's subscriber days forward by ONE.");
		
		DonationMechanics.tickFreeEcash();
		Main.log.info("[DonationMechanics] Reset all 'Free E-cash' users, login for more e-cash!");
		return true;
	}
	
}
