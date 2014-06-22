package me.vilsol.betanpc.items.spawnmenu.rarity;

import me.vilsol.betanpc.enums.SpawnStage;
import me.vilsol.betanpc.workers.ItemSpawnWorker;
import me.vilsol.itemgenerator.ItemGenerator;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.enums.ItemRarity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Unique implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		ItemSpawnWorker m = (ItemSpawnWorker) DynamicMenuModel.getMenu(plr);
		plr.getInventory().addItem(new ItemGenerator().setTier(m.tier.getDRTier()).setType(m.type.getDRType()).setRarity(ItemRarity.UNIQUE).generateItem().getItem());
		m.stage = SpawnStage.TYPE_CHOICE;
		DynamicMenuModel.getMenu(plr).showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.GOLD_INGOT).setName(ChatColor.YELLOW + "Unique").getItem();
	}
	
}