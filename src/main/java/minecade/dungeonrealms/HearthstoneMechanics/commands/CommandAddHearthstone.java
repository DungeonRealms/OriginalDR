package minecade.dungeonrealms.HearthstoneMechanics.commands;

import minecade.dungeonrealms.HearthstoneMechanics.HearthstoneMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddHearthstone implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!p.isOp())
            return true;
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "INVALID SYNTAX: " + ChatColor.RED + "/addheartstone <name> <cost>");
            p.sendMessage(ChatColor.GRAY + "Please do not use spaces in the name. Use '_' instead. It will convert later.");
            return true;
        }
        Location l = p.getLocation();
        if (!l.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
            p.sendMessage(ChatColor.RED + "You must be in Andalucia to do this.");
            return true;
        }
        if (HearthstoneMechanics.spawn_map.values().contains(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))) {
            p.sendMessage(ChatColor.RED + "This location is already set!");
        }
        try {
            // If it goes through its safe to set the data.
            Integer.parseInt(args[1]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "INVALID SYNTAX: " + ChatColor.RED + "/addheartstone <name> <cost>");
            return true;
        }
        p.sendMessage(ChatColor.RED + "New Hearthstone Location: " + ChatColor.AQUA + args[0].replace("_", " ") + " Price: " + ChatColor.GREEN + args[1]);
        p.sendMessage("Location set to: " + ChatColor.RED + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
        HearthstoneMechanics.spawn_map.put(args[0].replace("_", " "), l);
        HearthstoneMechanics.hearthstone_price.put(args[0].replace("_", " "), Integer.parseInt(args[1]));
        return false;
    }

}
