package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import java.util.Arrays;
import java.util.Map.Entry;

import me.vilsol.menuengine.engine.BonusItem;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
import me.vilsol.menuengine.utils.Builder;
import minecade.dungeonrealms.LevelMechanics.PlayerLevel;
import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class IntellectItem implements MenuItem, BonusItem {

	private PlayerModel drPlayer;
	private PlayerLevel pLevel;
	private int points = 0;
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
		if (pLevel.getTempFreePoints() > 0) {
            switch (click) {
            case LEFT:
                allocatePoints(1, plr);
                break;
            case MIDDLE:
                plr.closeInventory();
                StatsGUIWorker.setCustomStatAllocationSlot(slot, plr, getItem());
                break;
            case RIGHT:
                allocatePoints(-1, plr);
                break;
            case SHIFT_LEFT:
                allocatePoints(PlayerLevel.POINTS_PER_LEVEL, plr);
                break;
            case SHIFT_RIGHT:
                allocatePoints(-PlayerLevel.POINTS_PER_LEVEL, plr);
                break;
            default:
                break;
            }
		}
	}

	@Override
	public ItemStack getItem() {
        return new Builder(Material.EMPTY_MAP)
                .setName(ChatColor.DARK_PURPLE + "Intellect")
                .setLore(
                        Arrays.asList(
                                ChatColor.GRAY + "Adds energy regeneration,  ",
                                ChatColor.GRAY + "elemental damage, critical ",
                                ChatColor.GRAY + "hit chance, and staff damage.",
                                ChatColor.AQUA
                                        + "Allocated Points: "
                                        + pLevel.getIntPoints()
                                        + (points - pLevel.getIntPoints() > 0 ? ChatColor.GREEN + " [+"
                                                + (points - pLevel.getIntPoints()) + "]" : ""), ChatColor.RED
                                        + "Free Points: " + pLevel.getTempFreePoints())).getItem();
	}

	@Override
	public void setBonusData(Object player) {
		drPlayer = (PlayerModel) player;
		pLevel = drPlayer.getPlayerLevel();
		points = pLevel.getIntPoints();
		pLevel.setTempFreePoints(pLevel.getFreePoints());
	}

	private void allocatePoints(int points, Player plr) {
	    if ((points > 0 && pLevel.getTempFreePoints() >= points
                || (points < 0 && (this.points - pLevel.getVitPoints()) >= Math.abs(points))) && this.points + points <= 300) {
	        this.points += points;
	        pLevel.setTempFreePoints(pLevel.getTempFreePoints() - points);
	        plr.playSound(plr.getLocation(), Sound.SHEEP_SHEAR, 1.0F, 1.3F);
	        for (Entry<Integer, MenuItem> entry : DynamicMenuModel.getMenu(plr).getDynamicItems().entrySet()) {
	            DynamicMenuModel.getMenu(plr).getInventory().setItem(entry.getKey(), entry.getValue().getItem());
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