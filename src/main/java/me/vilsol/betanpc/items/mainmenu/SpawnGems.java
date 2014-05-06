package me.vilsol.betanpc.items.mainmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnGems implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		// TODO Add 1m gem note!
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EMERALD).setName(ChatColor.GREEN + "Spawn Gems").setLore(Arrays.asList(ChatColor.GRAY + "Spawns a bank note worth " + ChatColor.BOLD + "1,000,000g")).getItem();
	}

}
