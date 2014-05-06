package me.vilsol.betanpc.menus;

import org.bukkit.ChatColor;

import me.vilsol.betanpc.items.professionmenu.SpawnFishingRod;
import me.vilsol.betanpc.items.professionmenu.SpawnPickaxe;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

public class ProfessionMenu extends MenuModel {

	public ProfessionMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Profession Items");
		getMenu().addItem(SpawnPickaxe.class, 0);
		getMenu().addItem(SpawnFishingRod.class, 1);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
