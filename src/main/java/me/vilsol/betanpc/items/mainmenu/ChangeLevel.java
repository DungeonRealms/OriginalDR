package me.vilsol.betanpc.items.mainmenu;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import me.vilsol.betanpc.utils.Utils;
import me.vilsol.menuengine.engine.ChatCallback;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.engine.MenuModel;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;

public class ChangeLevel implements MenuItem, ChatCallback {

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		plr.sendMessage(Utils.NPC + "Enter your desired level! " + ChatColor.GRAY + "(1 - 100)");
		ChatCallback.locked_players.put(plr, this);
		plr.closeInventory();
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.EXP_BOTTLE).setName(ChatColor.YELLOW + "Change Level").setLore(Arrays.asList(ChatColor.GRAY + "Set a custom level for yourself.")).getItem();
	}

	@Override
	public void onChatMessage(AsyncPlayerChatEvent e) {
		if(Utils.isInteger(e.getMessage())){
			int level = Integer.parseInt(e.getMessage());
			if(level > 100 || level < 1){
				e.getPlayer().sendMessage(Utils.NPC + "Level change cancelled because number is not between 1 - 100!");
			}else{
				LevelMechanics.getPlayerData(e.getPlayer()).setXP(0);
				LevelMechanics.getPlayerData(e.getPlayer()).setLevel(level);
			}
		}else{
			e.getPlayer().sendMessage(Utils.NPC + "Level change cancelled because number is not between 1 - 100!");
		}
		ChatCallback.locked_players.remove(e.getPlayer());
		MenuModel.openLastMenu(e.getPlayer());
	}
	
}
