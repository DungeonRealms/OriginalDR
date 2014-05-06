package me.vilsol.betanpc.menus;

import me.vilsol.betanpc.items.potionmenu.SpawnTierFivePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierFourPotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierOnePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierThreePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierTwoPotion;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

import org.bukkit.ChatColor;

public class PotionMenu extends MenuModel {

	public PotionMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Potions");
		getMenu().addItem(SpawnTierOnePotion.class, 0);
		getMenu().addItem(SpawnTierTwoPotion.class, 1);
		getMenu().addItem(SpawnTierThreePotion.class, 2);
		getMenu().addItem(SpawnTierFourPotion.class, 3);
		getMenu().addItem(SpawnTierFivePotion.class, 4);
		
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
