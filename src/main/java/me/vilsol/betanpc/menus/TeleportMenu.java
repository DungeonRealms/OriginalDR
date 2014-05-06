package me.vilsol.betanpc.menus;

import org.bukkit.ChatColor;

import me.vilsol.betanpc.items.teleportmenu.TeleportToCrestguard;
import me.vilsol.betanpc.items.teleportmenu.TeleportToCyrennica;
import me.vilsol.betanpc.items.teleportmenu.TeleportToDarkOakTavern;
import me.vilsol.betanpc.items.teleportmenu.TeleportToDeadPeaks;
import me.vilsol.betanpc.items.teleportmenu.TeleportToGloomyHallows;
import me.vilsol.betanpc.items.teleportmenu.TeleportToHarrisonsFields;
import me.vilsol.betanpc.items.teleportmenu.TeleportToTripoli;
import me.vilsol.betanpc.items.teleportmenu.TeleportToTrollsbane;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.menuengine.engine.MenuModel;

public class TeleportMenu extends MenuModel {

	public TeleportMenu() {
		super(9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Spawn Teleport Books");
		getMenu().addItem(TeleportToCyrennica.class, 0);
		getMenu().addItem(TeleportToHarrisonsFields.class, 1);
		getMenu().addItem(TeleportToDarkOakTavern.class, 2);
		getMenu().addItem(TeleportToTrollsbane.class, 3);
		getMenu().addItem(TeleportToDeadPeaks.class, 4);
		getMenu().addItem(TeleportToGloomyHallows.class, 5);
		getMenu().addItem(TeleportToTripoli.class, 6);
		getMenu().addItem(TeleportToCrestguard.class, 7);
		getMenu().addItem(BackToMainMenu.class, 8);
	}
	
}
