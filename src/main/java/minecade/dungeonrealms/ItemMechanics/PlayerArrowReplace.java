package minecade.dungeonrealms.ItemMechanics;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerArrowReplace {
    Player p;
    ItemStack item;
    int item_slot;

    public PlayerArrowReplace(Player p, ItemStack item, int item_slot) {
        this.p = p;
        this.item = item;
        this.item_slot = item_slot;
    }

    public int getItemSlot() {
        return item_slot;
    }

    public Player getPlayer() {
        return p;
    }

    public ItemStack getItem() {
        return item;
    }
}
