package minecade.dungeonrealms.LevelMechanics.StatsGUI;

import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.enums.InventorySize;
import minecade.dungeonrealms.managers.PlayerManager;
import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatsGUI extends DynamicMenuModel {

	public StatsGUI() {
		super(StatsGUIWorker.class);
	}

	@Override
	public void addItems(DynamicMenu i, Player player) {
		PlayerModel drplayer = PlayerManager.getPlayerModel(player);
//		i.addItemDynamic(EmptySlot.class, 0);
//		i.addItemDynamic(EmptySlot.class, 1);
		i.addItemDynamic(StrengthItem.class, 2, drplayer);
		i.addItemDynamic(DexterityItem.class, 3, drplayer);
		i.addItemDynamic(IntellectItem.class, 4, drplayer);
		i.addItemDynamic(VitalityItem.class, 5, drplayer);
		i.addItemDynamic(ConfirmItem.class, 6, drplayer);
//		i.addItemDynamic(EmptySlot.class, 7);
//		i.addItemDynamic(EmptySlot.class, 8);
//		i.addItemDynamic(EmptySlot.class, 9);
//		i.addItemDynamic(EmptySlot.class, 10);
		i.addItemDynamic(StrengthStatsItem.class, 11, drplayer);
		i.addItemDynamic(DexterityStatsItem.class, 12, drplayer);
		i.addItemDynamic(IntellectStatsItem.class, 13, drplayer);
		i.addItemDynamic(VitalityStatsItem.class, 14, drplayer);
		i.addItemDynamic(StatsInfoItem.class, 15, drplayer);
//		i.addItemDynamic(EmptySlot.class, 16);
//		i.addItemDynamic(EmptySlot.class, 17);
	}

	@Override
	public boolean canPlaceItem(DynamicMenu arg0, Player arg1, int arg2, ItemStack arg3) {
		return false;
	}

	@Override
	public InventorySize getSize(Player arg0) {
		return InventorySize.S_18;
	}

	@Override
	public void onPickupItem(DynamicMenu arg0, ItemStack arg1, int arg2) {
		return;
	}

	@Override
	public void onPlaceItem(DynamicMenu arg0, ItemStack arg1, int arg2) {
		return;
	}
	

}
