package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLock implements CommandExecutor {
	
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
		msg = "{LOCK}";
		
		if(args[0].equalsIgnoreCase("*")) {
			for(String ip : CommunityMechanics.server_list.values()) {
				CommunityMechanics.sendPacketCrossServer(msg, ip);
				ModerationMechanics.log.info("[ModerationMechanics] Sent server LOCK request to " + ip);
			}
		} else {
			String ip = args[0];
			CommunityMechanics.sendPacketCrossServer(msg, ip);
			ModerationMechanics.log.info("[ModerationMechanics] Sent server LOCK request to " + ip);
		}
		
		return true;
	}
	
}
