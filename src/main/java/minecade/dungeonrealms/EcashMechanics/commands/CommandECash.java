package minecade.dungeonrealms.EcashMechanics.commands;

import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandECash implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = (Player) sender;
		
		pl.playSound(pl.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
		
		MerchantMechanics.in_npc_shop.add(pl.getName());
		pl.openInventory(MerchantMechanics.eCashVendor);
		return true;
	}
	
}