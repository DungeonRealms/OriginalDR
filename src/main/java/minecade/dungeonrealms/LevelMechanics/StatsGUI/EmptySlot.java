package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EmptySlot implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		return;
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.PISTON_EXTENSION).setName(" ").getItem();
	}

}