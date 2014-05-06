package me.vilsol.betanpc.menus;

import org.bukkit.ChatColor;

import me.vilsol.betanpc.items.mainmenu.ChangeLevel;
import me.vilsol.betanpc.items.mainmenu.ExitMenu;
import me.vilsol.betanpc.items.mainmenu.SpawnGems;
import me.vilsol.betanpc.items.mainmenu.SpawnItem;
import me.vilsol.betanpc.items.mainmenu.SpawnMiscItems;
import me.vilsol.betanpc.items.mainmenu.SpawnProfessionItem;
import me.vilsol.menuengine.engine.MenuModel;

public class MainMenu extends MenuModel {

	public MainMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Beta Vendor");
		getMenu().addItem(ChangeLevel.class, 0);
		getMenu().addItem(SpawnItem.class, 1);
		getMenu().addItem(SpawnGems.class, 2);
		getMenu().addItem(SpawnMiscItems.class, 3);
		getMenu().addItem(SpawnProfessionItem.class, 4);
		
		getMenu().addItem(ExitMenu.class, 8);
	}
	
}
