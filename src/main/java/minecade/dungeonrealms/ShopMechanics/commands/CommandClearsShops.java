package minecade.dungeonrealms.ShopMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandClearsShops implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player pl = (Player)sender;
		if (pl == null) return true;

		if (!pl.isOp() || !PermissionMechanics.isGM(pl.getName())) {
			pl.sendMessage(ChatColor.RED + "You don't have the required permission for this command.");
			return true;
		}
		
		if (args.length > 1) return true;

		if (!ShopMechanics.inverse_shop_owners.isEmpty()) {
			ShopMechanics.saveOpenShopsToCollBin();
			if (args[0].equalsIgnoreCase("stdb")) { // SaveToDatabase
				new BukkitRunnable() {
					public void run() {
						ShopMechanics.uploadAllCollectionBinData(); // Lets save it to database as well. 
						// (Note you wont be able to actually grab it from the bin directly after without downloading data from db.)
					}
				}.runTaskLater(Main.plugin, 20L * 2);
			}
			pl.sendMessage(ChatColor.GREEN + "Check console for more information.");
		}
		return true;
	}

}
