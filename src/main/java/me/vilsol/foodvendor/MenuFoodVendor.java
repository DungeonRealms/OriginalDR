package me.vilsol.foodvendor;

import me.vilsol.menuengine.engine.DynamicMenu;
import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.enums.InventorySize;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenuFoodVendor extends DynamicMenuModel {

	public MenuFoodVendor() {
		super(DynamicFoodVendor.class);
	}
	
	@Override
	public void addItems(DynamicMenu d, Player arg1) {
		d.addItemDynamic(FoodItem.class, 0, 0);
		d.addItemDynamic(FoodItem.class, 1, 1);
		d.addItemDynamic(FoodItem.class, 2, 2);
		d.addItemDynamic(FoodItem.class, 3, 3);
		d.addItemDynamic(FoodItem.class, 4, 4);
		d.addItemDynamic(FoodItem.class, 5, 5);
		d.addItemDynamic(FoodItem.class, 6, 6);
		d.addItemDynamic(FoodItem.class, 7, 7);
		d.addItemDynamic(FoodItem.class, 8, 8);
		d.addItemDynamic(FoodItem.class, 9, 9);
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
	}

	@Override
	public void onPlaceItem(DynamicMenu arg0, ItemStack arg1, int arg2) {
	}

}
