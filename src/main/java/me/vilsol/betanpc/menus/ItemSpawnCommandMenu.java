package me.vilsol.betanpc.menus;

import me.vilsol.betanpc.enums.SpawnStage;
import me.vilsol.betanpc.items.spawnmenu.Boots;
import me.vilsol.betanpc.items.spawnmenu.Chestplate;
import me.vilsol.betanpc.items.spawnmenu.Helmet;
import me.vilsol.betanpc.items.spawnmenu.Leggings;
import me.vilsol.betanpc.items.spawnmenu.Weapon;
import me.vilsol.betanpc.items.spawnmenu.rarity.Common;
import me.vilsol.betanpc.items.spawnmenu.rarity.Rare;
import me.vilsol.betanpc.items.spawnmenu.rarity.Uncommon;
import me.vilsol.betanpc.items.spawnmenu.rarity.Unique;
import me.vilsol.betanpc.items.spawnmenu.weapons.Axe;
import me.vilsol.betanpc.items.spawnmenu.weapons.Bow;
import me.vilsol.betanpc.items.spawnmenu.weapons.Polearm;
import me.vilsol.betanpc.items.spawnmenu.weapons.Return;
import me.vilsol.betanpc.items.spawnmenu.weapons.Staff;
import me.vilsol.betanpc.items.spawnmenu.weapons.Sword;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.betanpc.workers.ItemSpawnWorker;
import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.enums.InventorySize;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemSpawnCommandMenu extends DynamicMenuModel {

	public ItemSpawnCommandMenu() {
		super(ItemSpawnWorker.class);
	}
	
	@Override
	public void addItems(DynamicMenu i, Player plr) {
		ItemSpawnWorker w = (ItemSpawnWorker) i;
		if(w.stage == SpawnStage.TYPE_CHOICE){
			i.setName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Choose Type");
			i.addItemDynamic(Helmet.class, 0, w.tier);
			i.addItemDynamic(Chestplate.class, 1, w.tier);
			i.addItemDynamic(Leggings.class, 2, w.tier);
			i.addItemDynamic(Boots.class, 3, w.tier);
			i.addItemDynamic(Weapon.class, 4, w.tier);
		}else if(w.stage == SpawnStage.WEAPON_CHOICE){
			i.setName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Choose Weapon");
			i.addItemDynamic(Sword.class, 0, w.tier);
			i.addItemDynamic(Axe.class, 1, w.tier);
			i.addItemDynamic(Polearm.class, 2, w.tier);
			i.addItemDynamic(Staff.class, 3, w.tier);
			i.addItemDynamic(Bow.class, 4, w.tier);
			
			i.addItemDynamic(Return.class, 6, w.tier);
			
			i.addItemDynamic(BackToMainMenu.class, 8);
		}else if(w.stage == SpawnStage.RARITY_CHOICE){
			i.setName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Choose Rarity");
			i.addItemDynamic(Common.class, 0);
			i.addItemDynamic(Uncommon.class, 1);
			i.addItemDynamic(Rare.class, 2);
			i.addItemDynamic(Unique.class, 3);

			i.addItemDynamic(BackToMainMenu.class, 8, true);
		}
		
		i.addItem(BackToMainMenu.class, 8, true);
	}

	@Override
	public InventorySize getSize(Player plr) {
		return InventorySize.S_9;
	}

	@Override
	public boolean canPlaceItem(DynamicMenu i, Player plr, int slot, ItemStack item) {
		return false;
	}

	@Override
	public void onPickupItem(DynamicMenu i, ItemStack item, int slot) {
		return;
	}

	@Override
	public void onPlaceItem(DynamicMenu i, ItemStack item, int slot) {
		return;
	}
	
}
