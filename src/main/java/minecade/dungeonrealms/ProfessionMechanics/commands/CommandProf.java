package minecade.dungeonrealms.ProfessionMechanics.commands;

import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProf implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		if(args.length == 1 && args[0].equalsIgnoreCase("fish")) {
			p.getInventory().addItem(ProfessionMechanics.t1_fishing);
			p.getInventory().addItem(ProfessionMechanics.t3_fishing);
			p.getInventory().addItem(ProfessionMechanics.getFishDrop(1));
			p.getInventory().addItem(ProfessionMechanics.getFishDrop(2));
			p.getInventory().addItem(ProfessionMechanics.getFishDrop(3));
			p.getInventory().addItem(ProfessionMechanics.getFishDrop(4));
			p.getInventory().addItem(ProfessionMechanics.getFishDrop(5));
			return true;
		}
		if(args.length == 1 && args[0].equalsIgnoreCase("addexp")) {
			ProfessionMechanics.addEXP(p, p.getItemInHand(), 5000, "fishing");
			return true;
		}
		if(args.length == 1) {
			p.getInventory().addItem(MerchantMechanics.t1_s_pot);
			p.getInventory().addItem(MerchantMechanics.t2_s_pot);
			p.getInventory().addItem(MerchantMechanics.t3_s_pot);
			p.getInventory().addItem(MerchantMechanics.t4_s_pot);
			p.getInventory().addItem(MerchantMechanics.t5_s_pot);
		}
		// TODO: Add Level 1 pickaxe
		//p.setLevel(Integer.parseInt(args[0]));
		p.getInventory().addItem(ProfessionMechanics.t1_pickaxe);
		p.getInventory().addItem(ProfessionMechanics.t2_pickaxe);
		p.getInventory().addItem(ProfessionMechanics.t3_pickaxe);
		p.getInventory().addItem(ProfessionMechanics.t4_pickaxe);
		p.getInventory().addItem(ProfessionMechanics.t5_pickaxe);
		
		p.getInventory().addItem(ProfessionMechanics.coal_ore);
		p.getInventory().addItem(ProfessionMechanics.emerald_ore);
		p.getInventory().addItem(ProfessionMechanics.iron_ore);
		p.getInventory().addItem(ProfessionMechanics.diamond_ore);
		p.getInventory().addItem(ProfessionMechanics.gold_ore);
		//p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
		return true;
	}
	
}