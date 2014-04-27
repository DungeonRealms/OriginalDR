package minecade.dungeonrealms.TradeMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleTrade implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(args.length == 0)) {
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggletrade");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables recieving trade requests.");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getToggleList().contains("trade")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.remove("trade");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Trade - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		if(!PlayerManager.getPlayerModel(p).getToggleList().contains("trade")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.add("trade");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.RED + "Trade - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}