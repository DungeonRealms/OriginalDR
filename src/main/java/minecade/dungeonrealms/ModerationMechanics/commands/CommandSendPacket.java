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
        Player p = (Player) sender;
        
        if (!Main.isMaster(p.getName()) || !(sender instanceof ConsoleCommandSender)) {
            return true;
        }
        
        if (args.length != 3) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax: /sendpacket [data] [servernumber] [allservers]");
            return true;
        }
        try {
            CommunityMechanics.sendPacketCrossServer(args[0], Integer.valueOf(args[1]), Boolean.valueOf(args[2]));
            p.sendMessage(ChatColor.GRAY + "Packet sent!");
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax");
        }
        return false;
    }
}
