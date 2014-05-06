package me.vilsol.betanpc.workers;

import me.vilsol.betanpc.enums.ItemTier;
import me.vilsol.betanpc.enums.ItemType;
import me.vilsol.betanpc.enums.SpawnStage;
import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;

import org.bukkit.entity.Player;

public class ItemSpawnWorker extends DynamicMenu {

	public ItemType type;
	public ItemTier tier = ItemTier.T1;
	public SpawnStage stage = SpawnStage.TYPE_CHOICE;
	
	public ItemSpawnWorker(int size, DynamicMenuModel parent, Player owner) {
		super(size, parent, owner);
	}
	
}
