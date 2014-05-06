package me.vilsol.betanpc.menus;

import org.bukkit.ChatColor;

import me.vilsol.betanpc.items.tiermenu.TierFive;
import me.vilsol.betanpc.items.tiermenu.TierFour;
import me.vilsol.betanpc.items.tiermenu.TierOne;
import me.vilsol.betanpc.items.tiermenu.TierThree;
import me.vilsol.betanpc.items.tiermenu.TierTwo;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

public class TierMenu extends MenuModel {

	public TierMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Tier Menu");
		getMenu().addItem(TierOne.class, 0);
		getMenu().addItem(TierTwo.class, 1);
		getMenu().addItem(TierThree.class, 2);
		getMenu().addItem(TierFour.class, 3);
		getMenu().addItem(TierFive.class, 4);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
