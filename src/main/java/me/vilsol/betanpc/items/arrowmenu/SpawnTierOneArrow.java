package me.vilsol.betanpc.items.arrowmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnTierOneArrow implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		ItemStack arrow = ItemMechanics.t1_arrow.clone();
		arrow.setAmount(64);
        plr.getInventory().addItem(arrow);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.ARROW).setName(ChatColor.WHITE + "Spawn Tier 1 Arrow").setLore(Arrays.asList(ChatColor.GRAY + "Spawn in " + ChatColor.BOLD + "64x " + ChatColor.WHITE + "Tier 1 Arrows.")).getItem();
	}
	
}
