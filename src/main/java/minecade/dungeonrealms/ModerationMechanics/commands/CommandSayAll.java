package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSayAll implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
		}
		
		String msg = "";
		for(String s : args) {
			msg += s + " ";
		}
		
		msg = "!!!" + msg;
		final String fmsg = msg;
		
		//Thread t = new Thread(new Runnable(){
		//	public void run(){
		for(final String ip : CommunityMechanics.server_list.values()) {
			CommunityMechanics.sendPacketCrossServer(fmsg, ip);
		}
		//	}
		//});
		
		//t.start();
		
		return true;
	}
	
}
