package minecade.dungeonrealms.CommunityMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandIgnore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("crypt")) {
            if (p != null) {
                if (!(p.isOp())) {
                    return true;
                }
            }

            return true;
        }

        if (!(args.length == 1)) {
            p.sendMessage(ChatColor.RED + "Incorrect syntax - " + ChatColor.BOLD + "/ignore <PLAYER>");
            return true;
        }

        final String to_add = args[0];
        if (to_add.equalsIgnoreCase(p.getName())) {
            p.sendMessage(ChatColor.RED + "Why would you want to ignore yourself silly?");
            return true;
        }
        if (CommunityMechanics.isPlayerOnIgnoreList(p, to_add)) {
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + to_add + ChatColor.YELLOW + " is already on your IGNORE LIST.");
            p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.BOLD + "/delete " + to_add + ChatColor.GRAY + " to remove them from your ignore list.");
            return true;
        }

        if (CommunityMechanics.isPlayerOnBuddyList(p, to_add)) {
            CommunityMechanics.deleteFromAllLists(p, to_add);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                CommunityMechanics.addIgnore(p, to_add);
                CommunityMechanics.updateCommBook(p);
                p.sendMessage(ChatColor.RED + "You've added " + ChatColor.BOLD + to_add + ChatColor.RED + " to your IGNORE list.");
            }
        }.runTaskLaterAsynchronously(Main.plugin, 1L);
        return true;
    }

}
