package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnBankSee implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) { return true; }
		Player p = (Player) sender;
		if(!p.isOp()) return true;
		if(!ModerationMechanics.looking_into_offline_bank.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "You have not loaded anyones bank into memory.");
			return true;
		}
		String p_name = ModerationMechanics.looking_into_offline_bank.get(p.getName());
		if(Bukkit.getPlayer(p_name) != null) {
			//They logged on and dont need to be unloaded.
			p.sendMessage(ChatColor.RED + p_name + " has logged in and cannot have their bank unloaded.");
			ModerationMechanics.looking_into_offline_bank.remove(p.getName());
			return true;
		}
		MoneyMechanics.bank_contents.remove(p_name);
		MoneyMechanics.bank_map.remove(p_name);
		MoneyMechanics.bank_level.remove(p_name);
		ModerationMechanics.looking_into_offline_bank.remove(p.getName());
		p.sendMessage(ChatColor.GRAY + p_name + "'s bank has been unloaded.");
		return true;
	}
	
}
