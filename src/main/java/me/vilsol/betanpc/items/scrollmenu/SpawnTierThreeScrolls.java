package me.vilsol.betanpc.items.scrollmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnTierThreeScrolls implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		// TODO Spawn Scroll
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EMPTY_MAP).setName(ChatColor.AQUA + "Spawn " + ChatColor.BOLD + "Tier 3 " + ChatColor.AQUA + "Scrolls").setLore(Arrays.asList(ChatColor.GRAY + "Adds a stack of Armor, Weapon and Protection Scrolls")).getItem();
	}

}