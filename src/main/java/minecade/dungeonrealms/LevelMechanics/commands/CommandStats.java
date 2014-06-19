package minecade.dungeonrealms.LevelMechanics.commands;

import me.vilsol.menuengine.engine.DynamicMenuModel;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StatsGUI;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StatsGUIWorker;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStats implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("You must be a player to use this command.");
    		return true;
    	}
        Player p = (Player) sender;
        p.sendMessage(String.valueOf(PlayerManager.getPlayerModel(p).getPlayerLevel().getStrPoints()));
        DynamicMenuModel.cleanInventories(p, p.getInventory());
        StatsGUIWorker gui = (StatsGUIWorker) DynamicMenuModel.createMenu(p, StatsGUI.class);
        gui.showToPlayer(p);
        return true;
    }

}
