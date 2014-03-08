package me.vaqxine.DonationMechanics.commands;

import me.vaqxine.Main;
import me.vaqxine.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGiveSub implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player ps = null;
		if(sender instanceof Player){
			ps = (Player)sender;
			if(!(ps.isOp())){
				return true;
			}
		}
		String p_name = args[0];
		int user_id = DonationMechanics.getForumUserID(p_name);
		if(user_id == -1){
			Main.log.info("[DonationMechanics] Granted user " + p_name + " SUBSCRIBER STATUS, however they didn't have a forum account!");
			if(ps != null){
				ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant subscriber status.");
			}
			return true;
		}

		String current_rank = DonationMechanics.getRank(p_name);
		boolean plus = false;

		DonationMechanics.setAsSubscriber(user_id, plus);
		
		if(!current_rank.equalsIgnoreCase("default")){
			// Don't let them overwrite pmod, sub+, sub++ ... just give them additional forum group.
			return true;
		}
		
		DonationMechanics.setRank(p_name, "sub");
		DonationMechanics.addSubscriberDays(p_name, 30, false);
		DonationMechanics.sendPacketCrossServer("[forum_group]" + p_name + ":" + 75, -1, true);
		DonationMechanics.sendPacketCrossServer("[rank_map]" + p_name + ":" + "sub", -1, true);
		if(ps != null){
			ps.sendMessage(ChatColor.GREEN + "Set " + p_name + " to SUBSCRIBER.");
			ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
		}
		Main.log.info("[DonationMechanics] Set user " + p_name + " to SUBSCRIBER. user_id = " + user_id);
		return true;
	}
	
}