package minecade.dungeonrealms.CommunityMechanics.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRules implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player p = (Player) sender;
        
        p.sendMessage(ChatColor.GREEN + "DungeonRealms Rules - http://goo.gl/0pye9Q");
        return true;
    }

}