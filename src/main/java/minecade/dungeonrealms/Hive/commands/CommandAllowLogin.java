package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAllowLogin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid Syntax: /allowlogin <name>");
            return true;
        }
        if (!sender.isOp()) {
            return true;
        }
        String name = args[0];
        Hive.setPlayerCanJoin(name, true);
        sender.sendMessage(ChatColor.AQUA + name + " join status set to " + ChatColor.GREEN + ChatColor.BOLD + "TRUE");
        return false;
    }

}
