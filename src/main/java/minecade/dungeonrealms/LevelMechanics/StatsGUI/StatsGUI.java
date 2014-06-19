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
		i.addItemDynamic(EmptySlot.class, 0);
		i.addItemDynamic(EmptySlot.class, 1);
		i.addItemDynamic(StrengthItem.class, 2, drplayer);
		i.addItemDynamic(DexterityItem.class, 3, drplayer);
		i.addItemDynamic(IntelligenceItem.class, 4, drplayer);
		i.addItemDynamic(VitalityItem.class, 5, drplayer);
		i.addItemDynamic(ConfirmItem.class, 6, drplayer);
		i.addItemDynamic(EmptySlot.class, 7);
		i.addItemDynamic(EmptySlot.class, 8);
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
