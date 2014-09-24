package minecade.dungeonrealms.LevelMechanics.commands;

import minecade.dungeonrealms.managers.PlayerManager;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetLevel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.isOp()) {
            return true;
        }
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax: /setlevel <name> <level>");
            return true;
        }

        if (!StringUtils.isNumeric(args[1])) {
            p.sendMessage(ChatColor.RED + "Level must be a number.");
            return true;
        }
        int level = Integer.parseInt(args[1]);
        if (level < 1 || level > 100) {
            p.sendMessage(ChatColor.RED + "Number must be between 1 and 100");
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "This player is not online");
            return true;
        }

        Player to_set = Bukkit.getPlayer(args[0]);

        p.sendMessage(ChatColor.AQUA + "You have set " + to_set.getName() + "'s level from " + PlayerManager.getPlayerModel(to_set).getPlayerLevel().getLevel()
                + " to " + level);
        p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 1, 1.3F);
        PlayerManager.getPlayerModel(to_set).getPlayerLevel().setLevel(level);
        PlayerManager.getPlayerModel(to_set).getPlayerLevel().updateScoreboardLevel();
        return true;
    }

}
