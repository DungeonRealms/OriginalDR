package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import java.util.Arrays;
import java.util.Map.Entry;

import me.vilsol.menuengine.engine.BonusItem;
import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.enums.ClickType;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.LevelMechanics.PlayerLevel;
import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmItem implements MenuItem, BonusItem {

	private PlayerModel drPlayer;
	int slot = -1;

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
		PlayerLevel pLevel = drPlayer.getPlayerLevel();
		pLevel.setFreePoints(pLevel.getTempFreePoints());
		DynamicMenu plrMenu = DynamicMenuModel.getMenu(plr);
		for (Entry<Integer, MenuItem> entry : plrMenu.getDynamicItems().entrySet()) {
			if (entry.getValue() instanceof StrengthItem) {
				drPlayer.setStrength(drPlayer.getStrength() + (((StrengthItem) entry.getValue()).getPoints()
						- pLevel.getStrPoints()));
				pLevel.setStrPoints(((StrengthItem) entry.getValue()).getPoints());
			}
			else if (entry.getValue() instanceof DexterityItem) {
				drPlayer.setDexterity(drPlayer.getDexterity() + (((DexterityItem) entry.getValue()).getPoints()
						- pLevel.getDexPoints()));
				pLevel.setDexPoints(((DexterityItem) entry.getValue()).getPoints());
			}
			else if (entry.getValue() instanceof IntelligenceItem) {
				drPlayer.setIntelligence(drPlayer.getIntelligence() + (((IntelligenceItem) entry.getValue()).getPoints()
						- pLevel.getIntPoints()));
				pLevel.setIntPoints(((IntelligenceItem) entry.getValue()).getPoints());
			}
			else if (entry.getValue() instanceof VitalityItem) {
				drPlayer.setVitality(drPlayer.getVitality() + (((VitalityItem) entry.getValue()).getPoints()
						- pLevel.getVitPoints()));
				pLevel.setVitPoints(((VitalityItem) entry.getValue()).getPoints());
			}
		}
		plr.playSound(plr.getLocation(), Sound.CLICK, 1.0F, 1.0F);
		plr.closeInventory();
	}

	@Override
	public ItemStack getItem() {
		return new Builder(Material.INK_SACK)
				.setDurability((short) 10)
				.setName(ChatColor.AQUA + "Confirm")
				.setLore(
						Arrays.asList(ChatColor.GREEN + "Click to confirm your stat ", ChatColor.GREEN
								+ "point allocation.  If you ", ChatColor.GREEN + "want to undo your changes, ", ChatColor.GREEN + "press escape.")).getItem();
	}

	@Override
	public void setBonusData(Object player) {
		drPlayer = (PlayerModel) player;
		DynamicMenuModel.getMenu(((PlayerModel) player).getPlayer()).setName(
				ChatColor.GREEN.toString() + ChatColor.BOLD + "Allocate Stat Points");
	}

}