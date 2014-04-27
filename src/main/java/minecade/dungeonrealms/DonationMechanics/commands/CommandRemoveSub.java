package minecade.dungeonrealms.DonationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemoveSub implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		String p_name = args[0];
		
		Main.log.info("d1");
		
		String current_rank = DonationMechanics.getRank(p_name);
		
		if(!current_rank.equalsIgnoreCase("sub")) {
			return true;
		}
		
		Main.log.info("d2");
		
		Main.log.info("d3");
		
		DonationMechanics.setRank(p_name, "default");
		
		Main.log.info("d4");
		
		DonationMechanics.addSubscriberDays(p_name, 0, true);
		DonationMechanics.sendPacketCrossServer("[rank_map]" + p_name + ":" + "default", -1, true);
		if(ps != null) {
			ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to DEFAULT.");
		}
		Main.log.info("[DonationMechanics] Set user " + p_name + " to DEFAULT.");
		return true;
	}
	
}