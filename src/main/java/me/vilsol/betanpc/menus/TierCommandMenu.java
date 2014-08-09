package me.vilsol.betanpc.menus;

import me.vilsol.betanpc.items.tiermenu.TierFive;
import me.vilsol.betanpc.items.tiermenu.TierFour;
import me.vilsol.betanpc.items.tiermenu.TierOne;
import me.vilsol.betanpc.items.tiermenu.TierThree;
import me.vilsol.betanpc.items.tiermenu.TierTwo;
import me.vilsol.menuengine.engine.MenuModel;

import org.bukkit.ChatColor;

/**
 * Same as TierMenu except functions as main menu for /addweaponnewnew's GUI
 * @author Alan
 *
 */
public class TierCommandMenu extends MenuModel {

	public TierCommandMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Tier Menu");
		getMenu().addItem(TierOne.class, 0);
		getMenu().addItem(TierTwo.class, 1);
		getMenu().addItem(TierThree.class, 2);
		getMenu().addItem(TierFour.class, 3);
		getMenu().addItem(TierFive.class, 4);
	}
	
}
