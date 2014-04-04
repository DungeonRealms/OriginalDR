package me.vaqxine.HearthstoneMechanics.commands;

import me.vaqxine.HearthstoneMechanics.HearthstoneMechanics;

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
        if(args.length != 1){
            p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "INVALID SYNTAX: " + ChatColor.RED + "/addheartstone <name>");
            p.sendMessage(ChatColor.GRAY + "Please do not use spaces in the name. Use '_' instead. It will convert later.");
            return true;
        }
        Location l = p.getLocation();
        if(!l.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
            p.sendMessage(ChatColor.RED + "You must be in Andulicia to do this.");
            return true;
        }
        if(HearthstoneMechanics.spawn_map.values().contains(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))){
            p.sendMessage(ChatColor.RED + "This location is already set!");
        }
        p.sendMessage(ChatColor.RED + "New Hearthstone Location: " + ChatColor.AQUA + args[0].replace("_", " "));
        p.sendMessage("Location set to: " + ChatColor.RED + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
        HearthstoneMechanics.spawn_map.put(args[0].replace("_", " "), l);
        HearthstoneMechanics.reloadSpawn();
        return false;
    }

}
