package minecade.dungeonrealms.LevelMechanics.commands;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNotice implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("You must be a player to use this command.");
    		return true;
    	}
        Player p = (Player) sender;
        PlayerManager.getPlayerModel(p).getPlayerLevel().sendStatNoticeToPlayer(p);
        return true;
    }

}
