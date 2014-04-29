package minecade.dungeonrealms.EnchantMechanics.commands;

import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDREnchant implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!p.isOp()) { return true; }
		p.getInventory().addItem(EnchantMechanics.t1_wep_scroll);
		p.getInventory().addItem(EnchantMechanics.t2_wep_scroll);
		p.getInventory().addItem(EnchantMechanics.t3_wep_scroll);
		p.getInventory().addItem(EnchantMechanics.t4_wep_scroll);
		p.getInventory().addItem(EnchantMechanics.t5_wep_scroll);
		
		p.getInventory().addItem(EnchantMechanics.t1_armor_scroll);
		p.getInventory().addItem(EnchantMechanics.t2_armor_scroll);
		p.getInventory().addItem(EnchantMechanics.t3_armor_scroll);
		p.getInventory().addItem(EnchantMechanics.t4_armor_scroll);
		p.getInventory().addItem(EnchantMechanics.t5_armor_scroll);
		
		p.getInventory().addItem(EnchantMechanics.t1_white_scroll);
		p.getInventory().addItem(EnchantMechanics.t2_white_scroll);
		p.getInventory().addItem(EnchantMechanics.t3_white_scroll);
		p.getInventory().addItem(EnchantMechanics.t4_white_scroll);
		p.getInventory().addItem(EnchantMechanics.t5_white_scroll);
		return true;
	}
	
}