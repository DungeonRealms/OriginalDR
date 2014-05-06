package me.vilsol.betanpc.menus;

import me.vilsol.betanpc.items.scrollmenu.SpawnTierFiveScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierFourScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierOneScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierThreeScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierTwoScrolls;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

import org.bukkit.ChatColor;

public class ScrollMenu extends MenuModel {

	public ScrollMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Scrolls");
		getMenu().addItem(SpawnTierOneScrolls.class, 0);
		getMenu().addItem(SpawnTierTwoScrolls.class, 1);
		getMenu().addItem(SpawnTierThreeScrolls.class, 2);
		getMenu().addItem(SpawnTierFourScrolls.class, 3);
		getMenu().addItem(SpawnTierFiveScrolls.class, 4);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
