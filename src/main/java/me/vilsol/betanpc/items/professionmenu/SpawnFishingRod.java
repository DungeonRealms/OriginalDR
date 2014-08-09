package me.vilsol.betanpc.items.professionmenu;

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
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;

public class SpawnFishingRod implements MenuItem, ChatCallback {

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
		return new Builder(Material.FISHING_ROD).setName(ChatColor.YELLOW + "Spawn Fishing Rod").setLore(Arrays.asList(ChatColor.GRAY + "Spawn a Fishing Rod with Random Enchants.")).getItem();
	}

	@Override
	public void onChatMessage(AsyncPlayerChatEvent e) {
		if(Utils.isInteger(e.getMessage())){
			int level = Integer.parseInt(e.getMessage());
			if(level > 100 || level < 1){
				e.getPlayer().sendMessage(Utils.NPC + "Fishing Rod spawning cancelled because number is not between 1 - 100!");
			}else{
				ItemStack base = ProfessionMechanics.t1_fishing.clone();
				if(level >= 20) base = ProfessionMechanics.t2_fishing.clone();
				if(level >= 40) base = ProfessionMechanics.t3_fishing.clone();
				if(level >= 60) base = ProfessionMechanics.t4_fishing.clone();
				if(level >= 80) base = ProfessionMechanics.t5_fishing.clone();
				
				while(ProfessionMechanics.getItemLevel(base) < level){
					ProfessionMechanics.addEXP(e.getPlayer(), base, ProfessionMechanics.getEXPNeeded(ProfessionMechanics.getItemLevel(base), "fishing"), "fishing");
				}
				
				e.getPlayer().getInventory().addItem(base);
			}
		}else{
			e.getPlayer().sendMessage(Utils.NPC + "Fishing Rod spawning cancelled because number is not between 1 - 100!");
		}
		ChatCallback.locked_players.remove(e.getPlayer());
		MenuModel.openLastMenu(e.getPlayer());
	}
	
}
