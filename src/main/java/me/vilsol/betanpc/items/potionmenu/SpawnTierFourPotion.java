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

public class SpawnTierFourPotion implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
        ItemStack pot = MerchantMechanics.t4_pot.clone();
        pot.setAmount(64);
        plr.getInventory().addItem(pot);
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = new Builder(Material.POTION).setName(ChatColor.LIGHT_PURPLE + "Spawn " + ChatColor.BOLD + "Tier 4 " + ChatColor.LIGHT_PURPLE + "Potion").setLore(Arrays.asList(ChatColor.GRAY + "Spawn a potion that heals " + ChatColor.BOLD + "750hp")).getItem();
		new Potion(PotionType.INSTANT_DAMAGE).apply(item);
		return Utils.removePotionLore(item);
	}

}