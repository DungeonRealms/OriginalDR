package me.vaqxine.ChatMechanics.commands;

import java.util.ArrayList;
import java.util.List;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandL implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player)sender;
		String msg = "";
		for(String s : args){
			msg += s + " ";
		}
		if(msg.endsWith(" ")){
			msg = msg.substring(0, (msg.length() - 1));
		}

		if(ChatMechanics.sending_message.contains(p.getName())){
			ChatMechanics.sending_message.remove(p.getName());
			return true;
		}

		ChatMechanics.sending_message.add(p.getName());

		if(ChatMechanics.mute_list.containsKey(p.getName())){
			long time_left = ChatMechanics.mute_list.get(p.getName());
			p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + time_left + " minute(s).");
			return true;
		}

		if(TutorialMechanics.onTutorialIsland(p) && !(p.isOp())){
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " chat while on tutorial island.");
			p.sendMessage(ChatColor.GRAY + "Either finish the tutorial or type /skip to enable chat.");
			return true;
		}
		
		List<Player> to_send = new ArrayList<Player>();
		List<Player> secret_send = new ArrayList<Player>();
		
		for(Player pl : Bukkit.getServer().getOnlinePlayers()){
			if(pl.getName().equalsIgnoreCase(p.getName())){
				continue;
			}
			if(!pl.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
				continue;
			}
			if(pl.getLocation().distanceSquared(p.getLocation()) > 16384){
				continue;
			}
			if(CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())){
				continue; // Either sender has the sendie ignored or vise versa, no need for them to be able to see each other's messages.
			}
			if(ModerationMechanics.vanish_list.contains(pl.getName())){
				secret_send.add(pl);
			}
			else{
				to_send.add(pl);
			}
			continue;
		}

		if(to_send.size() <= 0){
			ChatColor p_color = ChatMechanics.getPlayerColor(p, p);
			String prefix = ChatMechanics.getPlayerPrefix(p);

			String personal_msg = msg;
			if(ChatMechanics.hasAdultFilter(p.getName())){
				personal_msg = ChatMechanics.censorMessage(msg);
			}

			personal_msg = ChatMechanics.fixCapsLock(personal_msg);

			if(personal_msg.endsWith(" ")){
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}

			p.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No one heard you.");
			new BukkitRunnable(){
				@Override
				public void run() {
					ChatMechanics.sending_message.remove(p.getName());
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
		}

		for(Player pl : to_send){
			ChatColor p_color = ChatMechanics.getPlayerColor(p, pl);
			String prefix = ChatMechanics.getPlayerPrefix(p);
			String personal_msg = msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())){
				personal_msg = ChatMechanics.censorMessage(msg);
			}

			personal_msg = ChatMechanics.fixCapsLock(personal_msg);

			if(personal_msg.endsWith(" ")){
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}

			pl.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
		}
		
		for(Player pl : secret_send){
			ChatColor p_color = ChatMechanics.getPlayerColor(p, pl);
			String prefix = ChatMechanics.getPlayerPrefix(p);
			String personal_msg = msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())){
				personal_msg = ChatMechanics.censorMessage(msg);
			}

			personal_msg = ChatMechanics.fixCapsLock(personal_msg);

			if(personal_msg.endsWith(" ")){
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}

			pl.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
		}

		if(to_send.size() <= 0){
			return true; // Don't show debug.
		}
		
		ChatColor p_color = ChatMechanics.getPlayerColor(p, p);
		String prefix = ChatMechanics.getPlayerPrefix(p);

		String personal_msg = msg;
		if(ChatMechanics.hasAdultFilter(p.getName())){
			personal_msg = ChatMechanics.censorMessage(msg);
		}

		personal_msg = ChatMechanics.fixCapsLock(personal_msg);

		if(personal_msg.endsWith(" ")){
			personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
		}

		p.sendMessage(prefix + p_color + p.getName() + ": " + ChatColor.WHITE + personal_msg);
		Main.log.info(ChatColor.stripColor("" + p.getName() + ": " + msg));
		new BukkitRunnable(){
			@Override
			public void run() {
				ChatMechanics.sending_message.remove(p.getName());
			}
		}.runTaskLaterAsynchronously(Main.plugin, 2L);
		return true;
	}
	
}