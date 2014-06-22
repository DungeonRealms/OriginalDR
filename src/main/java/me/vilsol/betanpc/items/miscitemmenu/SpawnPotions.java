package me.vilsol.betanpc.items.miscitemmenu;

import java.util.Arrays;

import me.vilsol.betanpc.menus.PotionMenu;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.engine.MenuModel;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class SpawnPotions implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		MenuModel.menus.get(PotionMenu.class).getMenu().showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new Builder(Material.POTION).setName(ChatColor.GREEN + "Spawn Potions").setLore(Arrays.asList(ChatColor.GRAY + "Spawn Health Potions of any Tier")).getItem();
		new Potion(PotionType.WATER).apply(item);
		return item;
	}

}
