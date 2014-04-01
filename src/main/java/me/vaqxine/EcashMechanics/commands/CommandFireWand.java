package me.vaqxine.EcashMechanics.commands;

import me.vaqxine.MerchantMechanics.MerchantMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class CommandFireWand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player ps = null;
		if(sender instanceof Player){
			ps = (Player)sender;
			if(!(ps.isOp())){
				return true;
			}
		}

		if(ps != null){
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.firework_wand));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.flame_trail));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.flaming_armor));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.musical_spirit));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.old_music_box));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.global_microphone));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.global_delay_buff));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.increased_drops));

			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_lore_tag));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.item_ownership_tag));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.profession_exp_boost));

			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.skeleton_horse));
			ps.getInventory().addItem(CraftItemStack.asCraftCopy(MerchantMechanics.undead_horse));
		}
		return true;
	}
	
}