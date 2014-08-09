package me.vilsol.betanpc.items.mainmenu;

import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExitMenu implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		plr.closeInventory();
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.ARROW).setName(ChatColor.RED + "Exit Menu").getItem();
	}
	
}
