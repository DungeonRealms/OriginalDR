package me.vilsol.betanpc.items.tiermenu;

import java.util.Arrays;

import me.vilsol.betanpc.enums.ItemTier;
import me.vilsol.betanpc.menus.ItemSpawnMenu;
import me.vilsol.betanpc.workers.ItemSpawnWorker;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TierOne implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		ItemSpawnWorker m = (ItemSpawnWorker) DynamicMenuModel.createMenu(plr, ItemSpawnMenu.class);
		m.tier = ItemTier.T1;
		m.showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.LEATHER_CHESTPLATE).setName(ChatColor.WHITE + "Tier One").setLore(Arrays.asList(ChatColor.GRAY + "Spawn " + ChatColor.WHITE.toString() + ChatColor.BOLD + "Tier One " + ChatColor.GRAY + " Armor and Weapons")).getItem();
	}

}