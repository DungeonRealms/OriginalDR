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

@SuppressWarnings("deprecation")
public class CommandAddXP implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.isOp()) {
            return true;
        }
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED + "Invalid Syntax: /addXP <name> <XP>");
            return true;
        }

        if (!StringUtils.isNumeric(args[1])) {
            p.sendMessage(ChatColor.RED + "XP must be a number.");
            return true;
        }
        int level = Integer.parseInt(args[1]);
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "This player is not online");
            return true;
        }

        Player to_set = Bukkit.getPlayer(args[0]);

        p.sendMessage(ChatColor.AQUA + "You have added " + to_set.getName() + "'s XP from " + PlayerManager.getPlayerModel(to_set).getPlayerLevel().getXP()
                + " to " + level);
        p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 1, 1.3F);
        PlayerManager.getPlayerModel(to_set).getPlayerLevel().addXP(level);
        PlayerManager.getPlayerModel(to_set).getPlayerLevel().updateScoreboardLevel();
        return true;
    }

}
