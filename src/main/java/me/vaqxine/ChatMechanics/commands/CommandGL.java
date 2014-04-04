package me.vaqxine.ChatMechanics.commands;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGL implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length <= 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + "/gl <MESSAGE>");
			return true;
		}
		
		if(TutorialMechanics.onTutorialIsland(p) && !(p.isOp())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " chat while on tutorial island.");
			p.sendMessage(ChatColor.GRAY + "Either finish the tutorial or type /skip to enable chat.");
			return true;
		}
		
		if(ChatMechanics.mute_list.containsKey(p.getName())) {
			long time_left = ChatMechanics.mute_list.get(p.getName());
			p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + time_left + " minute(s).");
			return true;
		}
		
		String msg = "";
		
		for(String s : args) {
			msg += s + " ";
		}
		
		String rank = PermissionMechanics.getRank(p.getName());
		
		if(ChatMechanics.global_chat_delay.containsKey(p.getName())) {
			long old_time = ChatMechanics.global_chat_delay.get(p.getName());
			long cur_time = System.currentTimeMillis();
			
			int personal_delay = ChatMechanics.GChat_Delay;
			ItemStack global_amp = EcashMechanics.tickGlobalAmplifier(p);
			if(global_amp != null) {
				// They have one!
				personal_delay *= 0.50D;
				
				// EcashMechanics.setMessagesLeftOnGlobalAmplifier(global_amp,
				// -1, true);
				// It will subtract from the item in getGlobalAmplifier.
			}
			
			if((cur_time - old_time) < (personal_delay * 1000) && !(p.isOp()) && !(rank.equalsIgnoreCase("GM")) && !(rank.equalsIgnoreCase("PMOD") && !(rank.equalsIgnoreCase("WD")))) {
				int s_delay_left = personal_delay - (int) ((cur_time - old_time) / 1000);
				p.sendMessage(ChatColor.RED + "You can send another GLOBAL MESSAGE in " + s_delay_left + ChatColor.BOLD + "s");
				return true;
			}
		}
		
		ChatMechanics.global_chat_delay.put(p.getName(), System.currentTimeMillis());
		
		boolean trade = false;
		if(ChatMechanics.hasTradeKeyword(msg)) {
			trade = true;
		}
		
		for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if(CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
				continue; // Either sender has the sendie ignored or vise versa,
							// no need for them to be able to see each other's
							// messages.
			}
			if(trade == false && CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("global")) {
				continue; // They have global off, and only want to hear from
							// their buds.
			}
			if(trade == true && CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("tchat")) {
				continue; // They have global off, and only want to hear from
							// their buds.
			}
			if(TutorialMechanics.onTutorialIsland(pl)) {
				continue; // Don't send global chat to players on tutorial
							// island.
			}
			
			ChatColor p_color = ChatMechanics.getPlayerColor(p, pl);
			String prefix = ChatMechanics.getPlayerPrefix(p);
			
			String personal_msg = msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())) {
				personal_msg = ChatMechanics.censorMessage(msg);
			}
			
			personal_msg = ChatMechanics.fixCapsLock(personal_msg);
			
			if(personal_msg.endsWith(" ")) {
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}
			
			if(trade == false) {
				pl.sendMessage(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + ">" + " " + prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
			}
			if(trade == true) {
				pl.sendMessage(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + ">" + " " + prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
			}
		}
		
		String prefix = ChatMechanics.getPlayerPrefix(p);
		Main.log.info(ChatColor.stripColor("" + "<" + "G" + ">" + " " + prefix + p.getName() + ": " + msg));
		return true;
	}
	
}
