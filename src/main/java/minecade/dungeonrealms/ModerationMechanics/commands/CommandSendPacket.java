package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandSendPacket implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
    	Player p = null;
        if (!(sender instanceof ConsoleCommandSender)) {
        	p = (Player) sender;
        }
        
        if (p != null && !Main.isMaster(p.getName())) {
        	return true;
        }
        
        if (args.length != 3) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax: /sendpacket [servernumber] [allservers] [data]");
            return true;
        }
        try {
        	StringBuilder sb = new StringBuilder("");
        	
        	for (int i = 2; i < args.length; i++)
        		sb.append(args[i]).append(" ");
        	
        	String data = sb.toString();
        	
        	if (data.endsWith(" ")) 
        		data = data.substring(0, data.length() -1);
        	
            CommunityMechanics.sendPacketCrossServer(data, Integer.valueOf(args[0]), Boolean.valueOf(args[1]));
            p.sendMessage(ChatColor.GRAY + "Packet sent!");
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax");
        }
        return false;
    }
}
