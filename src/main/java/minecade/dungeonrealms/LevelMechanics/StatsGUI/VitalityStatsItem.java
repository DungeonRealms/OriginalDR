package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map.Entry;

import me.vilsol.menuengine.engine.BonusItem;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import org.bukkit.event.inventory.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.LevelMechanics.PlayerLevel;
import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VitalityStatsItem implements MenuItem, BonusItem {

	private PlayerModel drPlayer;
	private PlayerLevel pLevel;
	private VitalityItem item;
	private int points = 0;
	private int slot = -1;
	private DecimalFormat df = new DecimalFormat("##.###");

	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}

	@Override
	public void execute(Player plr, ClickType click) {
		if (slot == -1) {
			for (Entry<Integer, MenuItem> entry : DynamicMenuModel.getMenu(plr).getDynamicItems().entrySet()) {
				if (((MenuItem) entry.getValue()).getItem().equals(getItem())) {
					slot = entry.getKey();
				}
			}
		}
	}

	@Override
	public ItemStack getItem() {
		int vit = pLevel.getVitPoints();
		int aPoints = item.getPoints() - vit; // allocated points
		boolean spent = (aPoints > 0) ? true : false;
		return new Builder(Material.TRIPWIRE_HOOK)
				.setName(
						ChatColor.RED + "Vitality Bonuses: " + vit
								+ (spent ? ChatColor.GREEN + " [+" + aPoints + "]" : ""))
				.setLore(
						Arrays.asList(ChatColor.GOLD + "HP: " + ChatColor.AQUA + df.format(vit * 0.034) + "%"
								+ (spent ? ChatColor.GREEN + " [+" + df.format(aPoints * 0.034) + "%]" : ""),
								ChatColor.GOLD + "HP REGEN: " + ChatColor.AQUA + df.format(vit * 0.03) + " HP/s"
										+ (spent ? ChatColor.GREEN + " [+" + df.format(aPoints * 0.03) + " HP/s]" : ""),
								ChatColor.GOLD + "ELE RESIST: " + ChatColor.AQUA + df.format(vit * 0.04) + "%"
										+ (spent ? ChatColor.GREEN + " [+" + df.format(aPoints * 0.04) + "%]" : ""),
								ChatColor.GOLD + "SWORD DMG: " + ChatColor.AQUA + df.format(vit * 0.01) + "%"
										+ (spent ? ChatColor.GREEN + " [+" + df.format(aPoints * 0.01) + "%]" : "")))
				.getItem();
	}

	@Override
	public void setBonusData(Object player) {
		drPlayer = (PlayerModel) player;
		pLevel = drPlayer.getPlayerLevel();
		points = pLevel.getVitPoints();
		pLevel.setTempFreePoints(pLevel.getFreePoints());
		for (Entry<Integer, MenuItem> entry : DynamicMenuModel.getMenu(drPlayer.getPlayer()).getDynamicItems().entrySet()) {
			if (entry.getValue() instanceof VitalityItem) {
				item = (VitalityItem) entry.getValue();
				points = item.getPoints();
			}
		}
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

}