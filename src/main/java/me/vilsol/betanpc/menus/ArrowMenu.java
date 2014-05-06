package me.vilsol.betanpc.menus;

import me.vilsol.betanpc.items.arrowmenu.SpawnTierFiveArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierFourArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierOneArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierThreeArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierTwoArrow;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

import org.bukkit.ChatColor;

public class ArrowMenu extends MenuModel {

	public ArrowMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Arrows");
		getMenu().addItem(SpawnTierOneArrow.class, 0);
		getMenu().addItem(SpawnTierTwoArrow.class, 1);
		getMenu().addItem(SpawnTierThreeArrow.class, 2);
		getMenu().addItem(SpawnTierFourArrow.class, 3);
		getMenu().addItem(SpawnTierFiveArrow.class, 4);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
