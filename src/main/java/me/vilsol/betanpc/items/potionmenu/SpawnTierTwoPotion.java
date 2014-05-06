package me.vilsol.betanpc.items.potionmenu;

import java.util.Arrays;

import me.vilsol.betanpc.utils.Utils;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class SpawnTierTwoPotion implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		// TODO Spawn Potion
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new Builder(Material.POTION).setName(ChatColor.GREEN + "Spawn " + ChatColor.BOLD + "Tier 2 " + ChatColor.GREEN + "Potion").setLore(Arrays.asList(ChatColor.GRAY + "Spawn a potion that heals " + ChatColor.BOLD + "75hp")).getItem();
		new Potion(PotionType.INSTANT_HEAL).apply(item);
		return Utils.removePotionLore(item);
	}

}