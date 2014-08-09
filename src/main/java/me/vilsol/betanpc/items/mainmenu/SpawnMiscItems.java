package me.vilsol.betanpc.items.mainmenu;

import java.util.Arrays;

import me.vilsol.betanpc.menus.MiscItemMenu;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.engine.MenuModel;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnMiscItems implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		MenuModel.menus.get(MiscItemMenu.class).getMenu().showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.DIAMOND).setName(ChatColor.GOLD + "Spawn Misc. Item(s)").setLore(Arrays.asList(ChatColor.GRAY + "Spawn items such as", ChatColor.GRAY + "Orbs, Enchant Scrolls, Food and Potions")).getItem();
	}
	
}