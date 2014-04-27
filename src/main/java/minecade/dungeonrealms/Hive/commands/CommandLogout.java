package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLogout implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		double seconds_left = 3;
		if(DuelMechanics.isDamageDisabled(p.getLocation())) {
			// They're in a safe zone, no need for this.
			p.kickPlayer(ChatColor.GREEN.toString() + "You have safely logged out." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
			return true;
		}
		
		if(HealthMechanics.in_combat.containsKey(p.getName())) {
			long dif = ((HealthMechanics.HealthRegenCombatDelay * 1000) + HealthMechanics.in_combat.get(p.getName())) - System.currentTimeMillis();
			seconds_left = (dif / 1000.0D) + 0.5D;
			seconds_left = Math.round(seconds_left);
		}
		
		if(seconds_left < 3) {
			seconds_left = 3;
		}
		
		p.sendMessage(ChatColor.RED + "You will be " + ChatColor.BOLD + "LOGGED OUT" + ChatColor.RED + " of the game world shortly.");
		Hive.safe_logout.put(p.getName(), (int) seconds_left);
		Hive.safe_logout_location.put(p.getName(), p.getLocation());
		
		//p.kickPlayer(ChatColor.GREEN.toString() + "You have safely logged out." + "\n\n" + ChatColor.GRAY.toString() + "Goodbye!");
		return true;
	}
	
}