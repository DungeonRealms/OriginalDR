package minecade.dungeonrealms.DonationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGiveSubLife implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		String p_name = args[0];
		
		DonationMechanics.setRank(p_name, "sub++");
		DonationMechanics.addSubscriberDays(p_name, 9999, false);
		//addSubscriberDays(p_name, 30, false); Never bother to expire.
		DonationMechanics.sendPacketCrossServer("[forum_group]" + p_name + ":" + 79, -1, true);
		DonationMechanics.sendPacketCrossServer("[rank_map]" + p_name + ":" + "sub++", -1, true);
		if(ps != null) {
			ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to LIFETIME SUBSCRIBER (SUB++).");
		}
		Main.log.info("[DonationMechanics] Set user " + p_name + " to LIFETIME SUBSCRIBER (SUB++).");
		return true;
	}
	
}