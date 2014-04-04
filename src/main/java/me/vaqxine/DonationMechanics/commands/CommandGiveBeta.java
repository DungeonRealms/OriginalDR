package me.vaqxine.DonationMechanics.commands;

import me.vaqxine.Main;
import me.vaqxine.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGiveBeta implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//
		if(sender != null) {
			sender.sendMessage("Donation Mechanics currently disabled!");
			return true;
		}
		//
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		String p_name = args[0];
		int user_id = DonationMechanics.getForumUserID(p_name);
		if(user_id == -1) {
			Main.log.info("[DonationMechanics] Granted user " + p_name + " BETA ACCESS, however they didn't have a forum account!");
			if(ps != null) {
				ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant premium access.");
			}
			return true;
		}
		DonationMechanics.setAsBetaTester(user_id);
		if(ps != null) {
			ps.sendMessage(ChatColor.GREEN + "Added PREMIUM user " + p_name + " into the Dungeon Realms CLOSED BETA.");
			ps.sendMessage(ChatColor.GRAY + "FORUM USER_ID: " + user_id);
		}
		Main.log.info("[DonationMechanics] Granted user " + p_name + " BETA ACCESS. user_id = " + user_id);
		return true;
	}
	
}