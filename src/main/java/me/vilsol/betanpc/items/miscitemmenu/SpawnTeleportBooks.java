package me.vilsol.betanpc.items.miscitemmenu;

import java.util.Arrays;

import me.vilsol.betanpc.menus.TeleportMenu;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.engine.MenuModel;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnTeleportBooks implements MenuItem {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		MenuModel.menus.get(TeleportMenu.class).getMenu().showToPlayer(plr);
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.BOOK).setName(ChatColor.DARK_AQUA + "Spawn Teleport Books").setLore(Arrays.asList(ChatColor.GRAY + "Spawn in Teleportation books to any location.")).getItem();
	}

}
