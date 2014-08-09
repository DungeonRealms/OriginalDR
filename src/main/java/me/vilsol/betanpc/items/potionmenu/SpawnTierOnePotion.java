package me.vilsol.betanpc.items.potionmenu;

import java.util.Arrays;

import me.vilsol.betanpc.utils.Utils;
import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class SpawnTierOnePotion implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
        ItemStack pot = MerchantMechanics.t1_pot.clone();
        pot.setAmount(64);
        plr.getInventory().addItem(pot);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new Builder(Material.POTION).setName(ChatColor.WHITE + "Spawn " + ChatColor.BOLD + "Tier 1 " + ChatColor.WHITE + "Potion").setLore(Arrays.asList(ChatColor.GRAY + "Spawn a potion that heals " + ChatColor.BOLD + "15hp")).getItem();
		new Potion(PotionType.REGEN).apply(item);
		return Utils.removePotionLore(item);
	}

}