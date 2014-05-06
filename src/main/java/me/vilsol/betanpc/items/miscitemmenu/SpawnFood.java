package me.vilsol.betanpc.items.miscitemmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnFood implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		// TODO Add 64x T5 Fish
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.COOKED_FISH).setName(ChatColor.YELLOW + "Spawn 64x T5 Fish").setLore(Arrays.asList(ChatColor.GRAY + "Will give you " + ChatColor.BOLD + "64x " + ChatColor.YELLOW + "T5 Fish")).getItem();
	}

}