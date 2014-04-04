package me.vaqxine.DonationMechanics.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import me.vaqxine.Main;
import me.vaqxine.DonationMechanics.DonationMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAcceptAll implements CommandExecutor {
	
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
			if(args.length != 0) {
				ps.sendMessage(ChatColor.RED + "Incorrect Syntax. " + "/acceptall");
				return true;
			}
			if(!(ps.isOp())) {
				Main.log.info("[DonationMechanics] User " + ps.getName() + " tried to illegally grant access to ALL");
				return true;
			}
		}
		
		File f = new File("accept.txt");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(f));
			
			String line = "";
			int count = 0;
			while((line = reader.readLine()) != null) {
				count++;
				String p_name = line;
				int user_id = DonationMechanics.getForumUserID(p_name);
				if(user_id == -1) {
					Main.log.info("[DonationMechanics] Granted user " + p_name + " ACCEPTED APPLICANT (BETA ACCESS), however they didn't have a forum account!");
					if(ps != null) {
						ps.sendMessage(ChatColor.RED + "The user " + p_name + " does not have a forum account yet. Cannot grant access.");
					}
					continue;
				}
				
				DonationMechanics.setAsNormalBetaTester(user_id);
			}
			reader.close();
			
			Main.log.info("[DonationMechanics] Granted ALL users in accept.txt beta access.");
			if(ps != null) {
				ps.sendMessage(ChatColor.GREEN + "Accepted ALL users in accept.txt to closed beta.");
				ps.sendMessage(ChatColor.GRAY + "TOTAL ACCEPTS: " + count);
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
}