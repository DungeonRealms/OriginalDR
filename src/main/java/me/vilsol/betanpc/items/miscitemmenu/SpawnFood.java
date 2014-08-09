package me.vilsol.betanpc.items.miscitemmenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnFood implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		ItemStack fish = new ItemStack(Material.COOKED_FISH);
		ItemMeta m = fish.getItemMeta();
        m.setDisplayName(ChatColor.YELLOW.toString() + "Gigantic Shark");
        List<String> fish_lore = new ArrayList<String>();
        fish_lore.add(ChatColor.RED + "-50% HUNGER " + ChatColor.GRAY.toString() + "(instant)");
        fish_lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A terrifying and massive predator.");
        m.setLore(fish_lore);
        fish.setItemMeta(m);
        fish.setAmount(64);
        plr.getInventory().addItem(fish);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.COOKED_FISH).setName(ChatColor.YELLOW + "Spawn 64x T5 Fish").setLore(Arrays.asList(ChatColor.GRAY + "Will give you " + ChatColor.BOLD + "64x " + ChatColor.YELLOW + "T5 Fish")).getItem();
	}

}