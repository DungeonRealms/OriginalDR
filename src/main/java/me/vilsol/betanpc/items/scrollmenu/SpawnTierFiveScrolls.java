package me.vilsol.betanpc.items.scrollmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnTierFiveScrolls implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
        ItemStack i1 = EnchantMechanics.t5_white_scroll.clone();
        ItemStack i2 = EnchantMechanics.t5_armor_scroll.clone();
        ItemStack i3 = EnchantMechanics.t5_wep_scroll.clone();

        i1.setAmount(64);
        i2.setAmount(64);
        i3.setAmount(64);

        plr.getInventory().addItem(i1);
        plr.getInventory().addItem(i2);
        plr.getInventory().addItem(i3);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EMPTY_MAP).setName(ChatColor.YELLOW + "Spawn " + ChatColor.BOLD + "Tier 5 " + ChatColor.YELLOW + "Scrolls").setLore(Arrays.asList(ChatColor.GRAY + "Adds a stack of Armor, Weapon and Protection Scrolls")).getItem();
	}

}