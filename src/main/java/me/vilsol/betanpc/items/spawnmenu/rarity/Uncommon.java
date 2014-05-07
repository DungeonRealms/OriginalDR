package me.vilsol.betanpc.items.spawnmenu.rarity;

import me.vilsol.betanpc.enums.SpawnStage;
import me.vilsol.betanpc.workers.ItemSpawnWorker;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Uncommon implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		ItemSpawnWorker m = (ItemSpawnWorker) DynamicMenuModel.getMenu(plr);
		if(ItemType.isArmor(m.type.getDRType())){
			plr.getInventory().addItem(m.type.getDRType().generateArmor(m.tier.getDRTier(), null, -1, ItemRarity.UNCOMMON));
		}else{
			plr.getInventory().addItem(m.type.getDRType().generateWeapon(m.tier.getDRTier(), null, ItemRarity.UNCOMMON));
		}
		m.stage = SpawnStage.TYPE_CHOICE;
		DynamicMenuModel.getMenu(plr).showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EMERALD).setName(ChatColor.GREEN + "Uncommon").getItem();
	}
	
}