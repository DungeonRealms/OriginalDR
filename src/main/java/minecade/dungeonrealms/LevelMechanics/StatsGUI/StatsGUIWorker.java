package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatsGUIWorker extends DynamicMenu {
	
	public StatsGUIWorker(int size, DynamicMenuModel parent, Player owner) {
		super(size, parent, owner);
	}
	
    public static void setCustomStatAllocationSlot(int slot, Player plr, ItemStack item) {
        PlayerManager.getPlayerModel(plr).getPlayerLevel().setAllocateSlot(slot);
        plr.sendMessage(ChatColor.GRAY
                + "Type the "
                + ChatColor.BOLD.toString()
                + ChatColor.GREEN
                + "number"
                + ChatColor.GRAY
                + " of stat points you wish to allocate to your "
                + ChatColor.UNDERLINE.toString()
                + ChatColor.GREEN
                + ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase())
                + ChatColor.GRAY
                + " stat.  If you wish to unallocate points out of points you have allocated this session, you may type a negative number.  Type "
                + ChatColor.RED + "cancel" + ChatColor.GRAY + " to exit.");
    }

}
