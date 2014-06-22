package me.vilsol.betanpc.items.mainmenu;

import java.util.Arrays;

import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnGems implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		MoneyMechanics.addMoneyCert(plr, 1000000, true);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EMERALD).setName(ChatColor.GREEN + "Spawn Gems").setLore(Arrays.asList(ChatColor.GRAY + "Spawns a bank note worth " + ChatColor.BOLD + "1,000,000g")).getItem();
	}

}
