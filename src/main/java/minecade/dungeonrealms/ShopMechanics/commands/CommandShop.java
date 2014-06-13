package minecade.dungeonrealms.ShopMechanics.commands;

import java.util.ArrayList;
import java.util.List;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.PetMechanics.PetMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandShop implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if (!PermissionMechanics.getRank(p.getName()).equalsIgnoreCase("gm") && !(p.isOp())) {
			return true;
		}
		
		if (args.length == 2) {
			ShopMechanics.downloadShopDatabaseData(args[1]);
			if (args[0].equalsIgnoreCase("info")) {
				if (ShopMechanics.doesPlayerHaveShopSQL(args[1])) {
					if (ShopMechanics.hasLocalShop(args[1]) && ShopMechanics.inverse_shop_owners.containsKey(args[1])) {
						Block shop = ShopMechanics.inverse_shop_owners.get(args[1]);
						p.sendMessage(ChatColor.YELLOW + "Player " + args[1] + " has "
								+ (ShopMechanics.isShopOpen(shop) ? "a closed" : "an open") + " shop on this server.");
						p.sendMessage(ChatColor.GRAY + "Shop Location: " + (int) shop.getLocation().getX() + ", "
								+ (int) shop.getLocation().getY() + ", " + (int) shop.getLocation().getZ());
						return true;
					}
					int server_num = ShopMechanics.getServerLocationOfShop(args[1]);
					String motd = Bukkit.getMotd();
					int local_server_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
					String prefix = "US-";

					if (server_num > 1000 && server_num < 2000) {
						server_num -= 1000;
						prefix = "EU-";
					}
					if (server_num > 2000) {
						server_num -= 2000;
						prefix = "BR-";
					}
					String server_name = prefix + server_num;
					if (local_server_num != server_num) {
						p.sendMessage(ChatColor.GREEN + "Player " + args[1] + " has a shop on " + server_name);
						p.sendMessage(ChatColor.GRAY + "Type /shop info " + args[1] + " on that server for more information.");
					}
					else {
						p.sendMessage(ChatColor.RED + args[1] + " has an unnamed shop with nothing in it on this server.");
					}
				}
				else {
					p.sendMessage(ChatColor.RED + "Player " + args[1] + " does not have a shop on any server. (This command is case sensitive!)");
					// TODO make this command case insensitive
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("open")) {
				if (!ShopMechanics.hasLocalShop(args[1]) || !ShopMechanics.inverse_shop_owners.containsKey(args[1])) {
					p.sendMessage(ChatColor.RED + "Player " + args[1] + " does not have a shop on this server.");
					if (ShopMechanics.doesPlayerHaveShopSQL(args[1])) {
						int server_num = ShopMechanics.getServerLocationOfShop(args[1]);
						String motd = Bukkit.getMotd();
						int local_server_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
						String prefix = "US-";

						if (server_num > 1000 && server_num < 2000) {
							server_num -= 1000;
							prefix = "EU-";
						}
						if (server_num > 2000) {
							server_num -= 2000;
							prefix = "BR-";
						}
						String server_name = prefix + server_num;
						if (local_server_num != server_num) {
							p.sendMessage(ChatColor.GRAY + "Player " + args[1] + " has a shop on " + server_name);
							p.sendMessage(ChatColor.GRAY + "You must be on the same server as the shop to open it.");
						}
						else {
							p.sendMessage(ChatColor.RED + args[1] + " has an unnamed shop that is empty.  It cannot be opened.");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + " Player " + args[1] + " does not have a shop on any server.");
					}
				}
				else {
					Block shop = ShopMechanics.inverse_shop_owners.get(args[1]);
					if (ShopMechanics.isShopOpen(shop)) {
						p.sendMessage(ChatColor.YELLOW + "Player " + args[1] + "'s shop is already open.");
					}
					else {
						Inventory i = ShopMechanics.shop_stock.get(args[1]);
						int shop_slots = ShopMechanics.getShopSlots(ShopMechanics.getShopLevel(args[1]));
						int slot = (shop_slots - 1);
						// open the store
						i.setItem(slot, ShopMechanics.green_button);

						List<ItemStack> to_remove = new ArrayList<ItemStack>();
						for(ItemStack is : i.getContents()) {
							if(is == null) {
								continue;
							}
							if(!(RealmMechanics.isItemTradeable(is)) || ItemMechanics.isSoulbound(is) || PetMechanics.isPermUntradeable(is) || CommunityMechanics.isSocialBook(is) || is.getType() == Material.QUARTZ ||is.getType() == Material.NETHER_STAR || InstanceMechanics.isDungeonItem(is)) {
								to_remove.add(is);
							}
						}
						if(to_remove.size() > 0) {
							for(ItemStack is : to_remove) {
								i.remove(is);
							}
						}

						ShopMechanics.open_shops.add(shop);
						ShopMechanics.open_shops.add(ShopMechanics.chest_partners.get(shop));
						//modifying_stock.remove(p.getName());
						ShopMechanics.setStoreColor(shop, ChatColor.GREEN);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1.3F);
						p.sendMessage(ChatColor.GREEN + "Opened " + args[1] + "'s store.");
						return true;
					}
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("close")) {
				if (!ShopMechanics.hasLocalShop(args[1]) || !ShopMechanics.inverse_shop_owners.containsKey(args[1])) {
					p.sendMessage(ChatColor.RED + "Player " + args[1] + " does not have a shop on this server.");
					if (ShopMechanics.doesPlayerHaveShopSQL(args[1])) {
						int server_num = ShopMechanics.getServerLocationOfShop(args[1]);
						String motd = Bukkit.getMotd();
						int local_server_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
						String prefix = "US-";

						if (server_num > 1000 && server_num < 2000) {
							server_num -= 1000;
							prefix = "EU-";
						}
						if (server_num > 2000) {
							server_num -= 2000;
							prefix = "BR-";
						}
						String server_name = prefix + server_num;
						if (local_server_num != server_num) {
							p.sendMessage(ChatColor.GRAY + "Player " + args[1] + " has a shop on " + server_name);
							p.sendMessage(ChatColor.GRAY + "You must be on the same server as the shop to close it.");
						}
						else {
							p.sendMessage(ChatColor.RED + args[1] + " has an unnamed shop that is empty on this server.  It is already closed.");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + " Player " + args[1] + " does not have a shop on any server. (This command is case sensitive!)");
					}
				}
				else {
					Block shop = ShopMechanics.inverse_shop_owners.get(args[1]);
					if (!ShopMechanics.isShopOpen(shop)) {
						p.sendMessage(ChatColor.YELLOW + "Player " + args[1] + "'s shop is already closed.");
					}
					else {
						Inventory i = ShopMechanics.shop_stock.get(args[1]);
						// close the store
						int button_slot = (i.getSize() - 1);
						i.setItem(button_slot, ShopMechanics.gray_button);
						ShopMechanics.open_shops.remove(shop);
						ShopMechanics.open_shops.remove(ShopMechanics.chest_partners.get(shop));
						//modifying_stock.add(p.getName());
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 0.70F);
						ShopMechanics.setStoreColor(shop, ChatColor.RED);

						List<Player> viewers = new ArrayList<Player>();

						if(i.getViewers() != null && i.getViewers().size() > 0) {
							for(HumanEntity he : i.getViewers()) {
								Player he_p = (Player) he;
								viewers.add(he_p);
							}
							for(Player he_p : viewers) {
								if(!he_p.getName().equalsIgnoreCase(args[1])) {
									he_p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
									he_p.closeInventory();
									//he_p.sendMessage(ChatColor.RED + "Shop closed.");
									continue;
								}
							}
						}
						
						p.sendMessage(ChatColor.GREEN + "Closed " + args[1] + "'s store.");
						return true;
					}
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("see")) {
				if (!ShopMechanics.hasLocalShop(args[1]) || !ShopMechanics.inverse_shop_owners.containsKey(args[1])) {
					p.sendMessage(ChatColor.RED + "Player " + args[1] + " does not have a shop on this server.");
					if (ShopMechanics.doesPlayerHaveShopSQL(args[1])) {
						int server_num = ShopMechanics.getServerLocationOfShop(args[1]);
						String motd = Bukkit.getMotd();
						int local_server_num = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
						String prefix = "US-";

						if (server_num > 1000 && server_num < 2000) {
							server_num -= 1000;
							prefix = "EU-";
						}
						if (server_num > 2000) {
							server_num -= 2000;
							prefix = "BR-";
						}
						String server_name = prefix + server_num;
						if (local_server_num != server_num) {
							p.sendMessage(ChatColor.GRAY + "Player " + args[1] + " has a shop on " + server_name);
							p.sendMessage(ChatColor.GRAY + "You must be on the same server as the shop to view it.");
						}
						else {
							p.sendMessage(ChatColor.RED + args[1] + " has an unnamed shop on this server that is empty.");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + "Player " + args[1] + " does not have a shop on any server. (This command is case sensitive!)");
					}
					return true;
				}
				else {
					p.openInventory(ShopMechanics.shop_stock.get(args[1]));
					return true;
				}
			}
		}
		else {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax");
			p.sendMessage(ChatColor.RED + "/shop");
			p.sendMessage(ChatColor.GRAY + "Usage: /shop info, /shop open, /shop close, /shop see.");
			return true;
		}

		return false;
	}

}
