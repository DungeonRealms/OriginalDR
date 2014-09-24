package me.vilsol.betanpc.items.spawnmenu;

import java.util.Arrays;

import me.vilsol.betanpc.enums.ItemTier;
import me.vilsol.betanpc.enums.ItemType;
import me.vilsol.betanpc.enums.SpawnStage;
import me.vilsol.betanpc.workers.ItemSpawnWorker;
import me.vilsol.menuengine.engine.BonusItem;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Weapon implements MenuItem, BonusItem<ItemTier> {

	private ItemTier tier;
	
	@Override
	public void setBonusData(ItemTier t) {
		tier = t;
	}

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		((ItemSpawnWorker) DynamicMenuModel.getMenu(plr)).stage = SpawnStage.WEAPON_CHOICE;
		DynamicMenuModel.getMenu(plr).showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(tier.getMaterialFromType(ItemType.SWORD)).setName(tier.getTierColor() + "Spawn " + tier.name() + " Weapons").setLore(Arrays.asList(ChatColor.GRAY + "Open " + tier.name() + " Weaponry Menu")).getItem();
	}
	
}
