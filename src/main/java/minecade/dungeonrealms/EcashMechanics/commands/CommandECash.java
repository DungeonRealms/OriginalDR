package minecade.dungeonrealms.EcashMechanics.commands;

import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandECash implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = (Player) sender;
		
        String m = Bukkit.getMotd();
        if(m.contains("US-100") || m.contains("US-101") || m.contains("US-102") || m.contains("US-103") || m.contains("US-104") || m.contains("US-105") || m.contains("US-106") || m.contains("US-107") || m.contains("US-108") || m.contains("US-108") || m.contains("US-109") || m.contains("US-110")){
        	pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " do this on the " + ChatColor.UNDERLINE + "Beta Servers" + ChatColor.RED + "!");
        	return true;
        }
		
		pl.playSound(pl.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
		
		MerchantMechanics.in_npc_shop.add(pl.getName());
		pl.openInventory(MerchantMechanics.eCashVendor);
		return true;
	}
	
}