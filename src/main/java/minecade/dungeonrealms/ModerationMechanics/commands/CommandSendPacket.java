package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSendPacket implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
    	Player p = null;
        if (sender instanceof Player) {
        	p = (Player) sender;
        }
        
        if (p != null && !Main.isMaster(p.getName())) {
        	return true;
        }
        
        if (args.length < 3) {
        	if (sender instanceof Player) {
        		p.sendMessage(ChatColor.RED + "Invalid Syntax: /sendpacket [servernumber] [allservers] [data]");
        	} else  {
        		Main.log.warning("Invalid Syntax: /sendpacket [servernumber] [allservers] [data]");
        	}
            return true;
        }
        try {
        	StringBuilder sb = new StringBuilder("");
        	String data = "";
        	@SuppressWarnings("unused")
            int myServerId = Hive.getServerNumFromPrefix(Bukkit.getMotd());
        	
        	for (int i = 2; i < args.length; i++) {
        		sb.append(args[i]).append(" ");
        	}
        	
        	data = sb.toString();
        	
        	if (data.endsWith(" ")) {
        		data = data.substring(0, data.length() - 1);
        	}
        	
        	Main.log.info(data);
            CommunityMechanics.sendPacketCrossServer(data, Integer.parseInt(args[0]), Boolean.parseBoolean(args[1]));
     
            if (sender instanceof Player) {
            	p.sendMessage(ChatColor.GRAY + "Packet sent!");
            } else {
            	Main.log.warning("Packet sent!");
            }
        } catch (Exception e) {
        	if (sender instanceof Player) {
        		p.sendMessage(ChatColor.RED + "Invalid Syntax");
        	} else {
        		Main.log.warning("Invalid Syntax");
        	}
        }
        return true;
    }
}
