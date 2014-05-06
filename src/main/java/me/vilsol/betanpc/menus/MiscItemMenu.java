package me.vilsol.betanpc.menus;

import org.bukkit.ChatColor;

import me.vilsol.betanpc.items.miscitemmenu.SpawnArrows;
import me.vilsol.betanpc.items.miscitemmenu.SpawnFood;
import me.vilsol.betanpc.items.miscitemmenu.SpawnOrbsOfAlteration;
import me.vilsol.betanpc.items.miscitemmenu.SpawnPotions;
import me.vilsol.betanpc.items.miscitemmenu.SpawnScrolls;
import me.vilsol.betanpc.items.miscitemmenu.SpawnTeleportBooks;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

public class MiscItemMenu extends MenuModel {

	public MiscItemMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Misc. Item(s)");
		getMenu().addItem(SpawnOrbsOfAlteration.class, 0);
		getMenu().addItem(SpawnTeleportBooks.class, 1);
		getMenu().addItem(SpawnArrows.class, 2);
		getMenu().addItem(SpawnScrolls.class, 3);
		getMenu().addItem(SpawnFood.class, 4);
		getMenu().addItem(SpawnPotions.class, 5);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
