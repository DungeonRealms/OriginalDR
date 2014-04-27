package minecade.dungeonrealms.HearthstoneMechanics.commands;

import java.util.Map.Entry;

import minecade.dungeonrealms.HearthstoneMechanics.HearthstoneMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListHearthstone implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!p.isOp())
            return true;
        for (Entry<String, Location> s : HearthstoneMechanics.spawn_map.entrySet()) {
            p.sendMessage(ChatColor.RED + "Name: " + s.getKey() + " Location: " + s.getValue().getBlockX() + ", " + s.getValue().getBlockY() + ", "
                    + s.getValue().getBlockZ() + ChatColor.GRAY + " Price: " + HearthstoneMechanics.hearthstone_price.get(s.getKey()));
        }
        return false;
    }

}
