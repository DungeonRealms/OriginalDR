package me.vaqxine.TradeMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RestrictionMechanics.RestrictionMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TradeMechanics.commands.CommandToggleTrade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

//TODO: Add combat support, if you get hit, close window.
//TODO: Add a trade request system perhaps? They have to type /accept? Alternatively, allow players to disable trades.

public class TradeMechanics implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	static Inventory TradeWindowTemplate;
	ItemStack divider = new ItemStack(Material.THIN_GLASS, 1);
	public ItemStack gray_button = setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", "");
	public ItemStack green_button = setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 10), ChatColor.GREEN.toString() + "Trade ACCEPTED.", ChatColor.GRAY.toString() + "Modify the trade to unaccept.");
	public static HashMap<Player, Player> trade_map = new HashMap<Player, Player>();
	static HashMap<Player, Player> trade_partners = new HashMap<Player, Player>();
	static HashMap<Player, Inventory> trade_secure = new HashMap<Player, Inventory>();
	static HashMap<String, Long> last_inventory_close = new HashMap<String, Long>();
	
	TradeMechanics tm = null;

	public void onEnable() {
		tm = this;
		
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		Main.plugin.getCommand("toggletrade").setExecutor(new CommandToggleTrade());
		
		ItemMeta im = divider.getItemMeta();
		im.setDisplayName(" ");
		divider.setItemMeta(im);

		new BukkitRunnable(){
			@Override
			public void run() {
				doOverheadEffect();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 2 * 20L, 20L);

		//gray_button = setIinfo(gray_button, ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", "");
		//reen_button = setIinfo(green_button, ChatColor.GREEN.toString() + "Trade ACCEPTED.", ChatColor.GRAY.toString() + "Modify the trade to unaccept.");
		loadTradeTemplate();
		log.info("[TradeMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[TradeMechanics] has been disabled.");
	}

	public void doOverheadEffect(){
		//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0), Particle.HAPPY_VILLAGER, 4F, 1);
		for(Player pl : trade_map.keySet()){
			try{
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 2F, 1);
				//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0), Particle.HAPPY_VILLAGER, 2F, 1);
			} catch(Exception err){
				continue;
			}
		}
		for(String s : MerchantMechanics.in_npc_shop){
			try{
				if(Bukkit.getPlayer(s) != null){
					Player pl = Bukkit.getPlayer(s);
					ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 2F, 1);
					//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0), Particle.HAPPY_VILLAGER, 2F, 1);
				}
			} catch(Exception err){
				continue;
			}
		}
	}

	public void loadTradeTemplate(){
		TradeWindowTemplate = Bukkit.getServer().createInventory(null, 27);
		TradeWindowTemplate.setItem(4, divider); 
		TradeWindowTemplate.setItem(13, divider); 
		TradeWindowTemplate.setItem(22, divider); 
		TradeWindowTemplate.setItem(0, new ItemStack(gray_button));
		TradeWindowTemplate.setItem(8, new ItemStack(gray_button));
	}

	public static ItemStack setIinfo(ItemStack orig_i, String name, String desc){
		/*ItemStack iss = orig_i;
        CraftItemStack css = new CraftItemStack(iss);
        net.minecraft.server.ItemStack nms = css.getHandle();
        NBTTagCompound tag = nms.tag;
        tag = new NBTTagCompound();
        tag.setCompound("display", new NBTTagCompound());
        nms.tag = tag;
        tag = nms.tag.getCompound("display");

            NBTTagList lore = nms.getTag().getCompound("display").getList("Lore");

           if(!(desc.equalsIgnoreCase(""))){
            if(lore == null){lore = new NBTTagList("Lore");}  
            lore.add(new NBTTagString("", desc));
            tag.set("Lore", lore);
           }

        tag.setString("Name", name);
        nms.tag.setCompound("display", tag);
        css.getHandle().setTag(nms.tag);
		orig_i.setDurability(orig_i.getDurability());*/

		List<String> new_lore = new ArrayList<String>();

		for(String s : desc.split(",")){
			if(s.length() <= 1){continue;}
			new_lore.add(s);
		}

		ItemMeta im = orig_i.getItemMeta();
		im.setLore(new_lore);
		im.setDisplayName(name);

		orig_i.setItemMeta(im);

		return orig_i;
	}

	public static Player getTarget(Player trader) {
		List<Entity> nearbyE = trader.getNearbyEntities(4.0D, 4.0D, 4.0D);
		ArrayList<Player> livingE = new ArrayList<Player>();

		for (Entity e : nearbyE) {
			if (e.getType() == EntityType.PLAYER && !e.hasMetadata("NPC")) {
				livingE.add((Player) e);
			}
		}

		Player target = null;
		BlockIterator bItr = new BlockIterator(trader, 4);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (LivingEntity e : livingE) {
				if(e instanceof Player){
					if(ModerationMechanics.vanish_list.contains(((Player)e).getName())){
						continue; // Ignore vanish'd GM's.
					}
				}
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5)) {
					// entity is close enough, set target and stop
					target = (Player) e;
					break;
				}
			}
		}

		return target;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerHit(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			if(e.getDamage() <= 0 || e.isCancelled()){return;}

			Player closer = (Player) e.getEntity();
			Player trade_partner = trade_map.get(closer);
			if(trade_map.containsKey(closer)){
				boolean left_side = false;
				Inventory tradeInv = closer.getOpenInventory().getTopInventory();

				if(trade_partners.containsKey(closer)){
					left_side = true;
				}
				if(trade_partners.containsKey(trade_partner)){
					left_side = false;
				}

				int slot_var = -1;
				if(left_side == true){
					while(slot_var <= 27){
						slot_var++;
						if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
							continue;
						}
						ItemStack i = tradeInv.getItem(slot_var);
						if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
							continue;
						}
						closer.getInventory().setItem(closer.getInventory().firstEmpty(), i); 

					}

					slot_var = -1;

					while(slot_var <= 27){
						slot_var++;
						if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
							continue;
						}
						ItemStack i = tradeInv.getItem(slot_var);
						if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
							continue;
						}
						trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), i); 
					}
				}

				if(left_side == false){
					while(slot_var <= 27){
						slot_var++;
						if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
							continue;
						}
						ItemStack i = tradeInv.getItem(slot_var);
						if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
							continue;
						}
						trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), i); 
					}
					slot_var = -1;

					while(slot_var <= 27){
						slot_var++;
						if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
							continue;
						}
						ItemStack i = tradeInv.getItem(slot_var);
						if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
							continue;
						}
						closer.getInventory().setItem(closer.getInventory().firstEmpty(), i); 
					}
				} 

				if(closer.getOpenInventory().getTopInventory().getName().contains(closer.getName())){
					closer.getOpenInventory().getTopInventory().clear();
				}

				if(trade_partner.getOpenInventory().getTopInventory().getName().contains(trade_partner.getName())){
					trade_partner.getOpenInventory().getTopInventory().clear();
				}


				trade_map.remove(closer);
				trade_map.remove(trade_partner);
				trade_partners.remove(closer);
				trade_partners.remove(trade_partner);
				trade_secure.remove(closer);
				trade_secure.remove(trade_partner);

				closer.closeInventory();
				trade_partner.closeInventory();

				closer.sendMessage(ChatColor.RED + "Trade cancelled, entered combat.");
				trade_partner.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + closer.getName() + ChatColor.RED + " entered combat, trade cancelled.");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemPickup(PlayerPickupItemEvent e){
		// Don't let players in a trade pickup items, prevent full inventory.
		Player pl = e.getPlayer();
		if(TradeMechanics.trade_map.containsKey(pl) || pl.getOpenInventory().getTitle().contains(pl.getName())){
			e.setCancelled(true);
		}

		if(last_inventory_close.containsKey(pl.getName()) && ((System.currentTimeMillis() - last_inventory_close.get(pl.getName()) <= (2 * 1000)))){
			e.setCancelled(true);
		}
	}

	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDrop(PlayerDropItemEvent e){
		Player pl = e.getPlayer();
		if(last_inventory_close.containsKey(pl.getName()) && ((System.currentTimeMillis() - last_inventory_close.get(pl.getName()) <= (2 * 1000)))){
			e.setCancelled(true);
			pl.updateInventory();
		}
	}*/

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player closer = e.getPlayer();
		Player trade_partner = trade_map.get(closer);

		last_inventory_close.remove(closer.getName());

		if(trade_map.containsKey(closer)){
			boolean left_side = false;
			Inventory tradeInv = closer.getOpenInventory().getTopInventory();

			if(trade_partners.containsKey(closer)){
				left_side = true;
			}
			if(trade_partners.containsKey(trade_partner)){
				left_side = false;
			}

			int slot_var = -1;
			if(left_side == true){
				while(slot_var <= 27){
					slot_var++;
					if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
						continue;
					}
					ItemStack i = tradeInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
						continue;
					}
					if(i.getType() == Material.EMERALD){
						i = MoneyMechanics.makeGems(i.getAmount());
					}
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), makeNormal(i));

				}

				slot_var = -1;

				while(slot_var <= 27){
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
						continue;
					}
					ItemStack i = tradeInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
						continue;
					}
					if(i.getType() == Material.EMERALD){
						i = MoneyMechanics.makeGems(i.getAmount());
					}
					trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), makeNormal(i));
				}
			}

			if(left_side == false){
				while(slot_var <= 27){
					slot_var++;
					if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
						continue;
					}
					ItemStack i = tradeInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i)| i.getType() == Material.THIN_GLASS){
						continue;
					}
					trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), makeNormal(i));
				}
				slot_var = -1;

				while(slot_var <= 27){
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
						continue;
					}
					ItemStack i = tradeInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
						continue;
					}
					if(i.getType() == Material.EMERALD){
						i = MoneyMechanics.makeGems(i.getAmount());
					}
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), makeNormal(i));
				}
			} 

			if(closer.getOpenInventory().getTopInventory().getName().contains(closer.getName())){
				closer.getOpenInventory().getTopInventory().clear();
			}

			if(trade_partner.getOpenInventory().getTopInventory().getName().contains(trade_partner.getName())){
				trade_partner.getOpenInventory().getTopInventory().clear();
			}

			trade_partner.closeInventory();

			trade_map.remove(closer);
			trade_map.remove(trade_partner);
			trade_partners.remove(closer);
			trade_partners.remove(trade_partner);
			trade_secure.remove(closer);
			trade_secure.remove(trade_partner);
			trade_partner.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + closer.getName() + ChatColor.RED + " logged out, trade cancelled.");
		}
	}

	public static String generateTitle(String lPName, String rPName){
		String title = "  " + lPName;
		while ((title.length() + rPName.length()) < (28)){
			title += " ";
		}
		return title += rPName;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent e){
		final Player trader = e.getPlayer();
		final Player tradie = getTarget(trader);

		if(tradie == null){
			return;
		}

		if(tradie.hasMetadata("no_trade") || trader.hasMetadata("no_trade")){
			e.setCancelled(true);
			trader.updateInventory();
			return;
		}
		
		if(tradie.hasMetadata("NPC") || tradie.getPlayerListName().equalsIgnoreCase("")){
			return;
		}

		if(MoneyMechanics.split_map.containsKey(trader)){
			trader.sendMessage(ChatColor.RED + "Please finish splitting your bank note before attempting to trade with another player. Type 'cancel' to void the split.");
			e.setCancelled(true);
			return;
		}

		if(HealthMechanics.in_combat.containsKey(trader.getName())){
			trader.sendMessage(ChatColor.YELLOW + "You cannot trade while in combat.");
			trader.sendMessage(ChatColor.GRAY + "Wait " + ChatColor.BOLD + "a few seconds" + ChatColor.GRAY + " and try again.");
			if(e.getItemDrop().getItemStack().getType() == Material.EMERALD){
				if(e.getItemDrop().getItemStack().getType() == Material.EMERALD){
					if(trader.getInventory().firstEmpty() != -1){
						trader.getInventory().setItem(trader.getInventory().firstEmpty(), e.getItemDrop().getItemStack());
					}
					e.getItemDrop().remove(); 
				}
			}
			else{
				e.setCancelled(true);
			}
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					trader.updateInventory();
				}
			}, 2L);
			return;
		}

		//if(e.getItemDrop().isEmpty()){return;}
		final ItemStack being_dropped = e.getItemDrop().getItemStack();

		if(!RealmMechanics.isItemTradeable(being_dropped)){
			return;
		}

		if(CommunityMechanics.isSocialBook(e.getItemDrop().getItemStack()) || e.getItemDrop().getItemStack().getType() == Material.NETHER_STAR){
			return;
		}

		if(e.isCancelled()){return;}

		if(tradie != null){

			if(HealthMechanics.in_combat.containsKey(tradie.getName())){
				trader.sendMessage(ChatColor.YELLOW + tradie.getName() + " is currently in combat.");
				e.setCancelled(true);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						trader.updateInventory();
					}
				}, 2L);
				return;
			}

			if(CommunityMechanics.isPlayerOnIgnoreList(tradie, trader.getName()) || CommunityMechanics.isPlayerOnIgnoreList(trader, tradie.getName())){
				trader.sendMessage(ChatColor.YELLOW + tradie.getName() + " has trade disabled.");
				e.setCancelled(true);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						trader.updateInventory();
					}
				}, 2L);
				return;
			}

			if(trade_map.containsKey(tradie)){
				trader.sendMessage(ChatColor.YELLOW + tradie.getName() + " is already trading with someone else.");
				e.setCancelled(true);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						trader.updateInventory();
					}
				}, 2L);
				return;
			}

			if(CommunityMechanics.toggle_list.containsKey(tradie.getName()) && CommunityMechanics.toggle_list.get(tradie.getName()).contains("trade")){
				trader.sendMessage(ChatColor.YELLOW + tradie.getName() + " has trade disabled.");
				e.setCancelled(true);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						trader.updateInventory();
					}
				}, 2L);
				return;
			}

			if(tradie.getOpenInventory().getTopInventory().getName().startsWith("Bank Chest") 
					|| tradie.getOpenInventory().getTopInventory().getName().contains("@")
					|| tradie.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Loot Chest")
					|| tradie.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Collection Bin")
					|| tradie.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Chest")
					|| tradie.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Realm Material Store")
					|| tradie.getOpenInventory().getTopInventory().getName().contains("     ")
					|| tradie.getOpenInventory().getTopInventory().getName().contains("container.chest")
					|| tradie.getOpenInventory().getTopInventory().getName().contains("container.bigchest")
					|| ShopMechanics.current_item_being_bought.containsKey(tradie.getName())
					|| ShopMechanics.current_item_being_stocked.containsKey(tradie.getName())
					|| RealmMechanics.current_item_being_bought.containsKey(tradie.getName())
					|| MoneyMechanics.split_map.containsKey(tradie)
					|| RestrictionMechanics.in_inventory.contains(tradie.getName())){
				trader.sendMessage(ChatColor.YELLOW + tradie.getName() + " is currently busy.");
				e.setCancelled(true);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						trader.updateInventory();
					}
				}, 2L);

				return;
			}

			if(trader.hasMetadata("no_trade") || tradie.hasMetadata("no_trade")){
				/*trader.removeMetadata("no_trade", this);
				tradie.removeMetadata("no_trade", this);*/
				log.info("Skipping trade due to no_trade -- " + trader.getName() + " -> " + tradie.getName());
				e.setCancelled(true);
				trader.updateInventory();
				return;
			}

			trader.setMetadata("no_trade", new FixedMetadataValue(Main.plugin, true));
			tradie.setMetadata("no_trade", new FixedMetadataValue(Main.plugin, true));

			e.getItemDrop().remove();

			log.info("TRADE EVENT: " + trader.getName() + " -> " + tradie.getName());
			final Inventory TradeWindow = Bukkit.createInventory(null, 27, generateTitle(trader.getName(), tradie.getName()));
			TradeWindow.setItem(4, divider); 
			TradeWindow.setItem(13, divider); 
			TradeWindow.setItem(22, divider); 
			//gray_button = (ItemStack)setIinfo(gray_button, ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", "");
			TradeWindow.setItem(0, setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			TradeWindow.setItem(8, setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			trader.setItemOnCursor(new ItemStack(Material.AIR, 1)); // Fix for dupe bug when player drops item directly from inventory.

			if(tradie.getItemOnCursor() != null){
				if(RealmMechanics.isItemTradeable(tradie.getItemOnCursor())){
					TradeWindow.setItem(5, makeUnique(tradie.getItemOnCursor()));
				}
				else{
					tradie.getInventory().addItem(tradie.getItemOnCursor());
				}
				tradie.setItemOnCursor(new ItemStack(Material.AIR));
			}

			trader.closeInventory();
			tradie.closeInventory();

			trade_partners.put(trader, tradie);
			trade_map.put(trader, tradie);
			trade_map.put(tradie, trader);
			trader.openInventory(TradeWindow);
			tradie.openInventory(TradeWindow);
			trader.playSound(trader.getLocation(), Sound.WOOD_CLICK, 1F, 0.8F);
			tradie.playSound(tradie.getLocation(), Sound.WOOD_CLICK, 1F, 0.8F);
			trader.sendMessage(ChatColor.YELLOW + "Trading with " + ChatColor.BOLD + tradie.getName() + ChatColor.YELLOW + "...");
			tradie.sendMessage(ChatColor.YELLOW + "Trading with " + ChatColor.BOLD + trader.getName() + ChatColor.YELLOW  +"...");
			TradeWindow.setItem(1, makeUnique(being_dropped));
			//trader.getItemInHand().setType(Material.AIR);
			// TRADE
			trader.updateInventory();
			
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					trader.removeMetadata("no_trade", Main.plugin);
					tradie.removeMetadata("no_trade", Main.plugin);
				}
			}, 2 * 20L);
		}
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e){
		Player p = (Player)e.getPlayer();

		if(trade_map.containsKey(p)){
			if(!e.getInventory().getName().toLowerCase().contains(p.getName().toLowerCase())){
				e.setCancelled(true); 
			}
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e){
		last_inventory_close.put(e.getPlayer().getName(), System.currentTimeMillis());
		if(!trade_map.containsKey((Player) e.getPlayer())){return;}
		final Player closer = (Player) e.getPlayer();
		final Player trade_partner = trade_map.get(closer);
		boolean left_side = false;
		Inventory tradeInv = closer.getOpenInventory().getTopInventory();

		if(trade_partners.containsKey(closer)){
			left_side = true;
		}
		if(trade_partners.containsKey(trade_partner)){
			left_side = false;
		}

		ItemStack closer_oc = closer.getItemOnCursor();
		ItemStack trade_partner_oc = trade_partner.getItemOnCursor();

		closer.setItemOnCursor(new ItemStack(Material.AIR));
		trade_partner.setItemOnCursor(new ItemStack(Material.AIR));

		if(closer.getInventory().firstEmpty() != -1){
			closer.getInventory().setItem(closer.getInventory().firstEmpty(), closer_oc);
		}
		if(trade_partner.getInventory().firstEmpty() != -1){
			trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), trade_partner_oc);
		}

		int slot_var = -1;
		if(left_side == true){
			while(slot_var <= 27){
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
					continue;
				}

				if(i.getType() == Material.EMERALD){
					i = MoneyMechanics.makeGems(i.getAmount());
				}

				if(closer.getInventory().firstEmpty() == -1){ // TODO: Need to automatically stack items on cancel.
					closer.getWorld().dropItemNaturally(closer.getLocation(), i);
				}
				else{
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), makeNormal(i));
				}

			}

			slot_var = -1;

			while(slot_var <= 27){
				slot_var++;
				if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
					continue;
				}
				if(i.getType() == Material.EMERALD){
					i = MoneyMechanics.makeGems(i.getAmount());
				}
				if(trade_partner.getInventory().firstEmpty() == -1){
					trade_partner.getWorld().dropItemNaturally(trade_partner.getLocation(), i);
				}
				else{
					trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), makeNormal(i));
				}
			}
		}

		if(left_side == false){
			while(slot_var <= 27){
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
					continue;
				}
				if(i.getType() == Material.EMERALD){
					i = MoneyMechanics.makeGems(i.getAmount());
				}
				if(trade_partner.getInventory().firstEmpty() == -1){
					trade_partner.getWorld().dropItemNaturally(trade_partner.getLocation(), i);
				}
				else{
					trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), makeNormal(i));
				}
			}
			slot_var = -1;

			while(slot_var <= 27){
				slot_var++;
				if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
					continue;
				}
				if(i.getType() == Material.EMERALD){
					i = MoneyMechanics.makeGems(i.getAmount());
				}
				if(closer.getInventory().firstEmpty() == -1){ // TODO: Need to automatically stack items on cancel.
					closer.getWorld().dropItemNaturally(closer.getLocation(), i);
				}
				else{
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), makeNormal(i));
				}
			}
		} 

		if(closer.getOpenInventory().getTopInventory().getName().contains(closer.getName())){
			closer.getOpenInventory().getTopInventory().clear();
		}

		if(trade_partner.getOpenInventory().getTopInventory().getName().contains(trade_partner.getName())){
			trade_partner.getOpenInventory().getTopInventory().clear();
		}

		trade_map.remove(closer);
		trade_map.remove(trade_partner);
		trade_partners.remove(closer);
		trade_partners.remove(trade_partner);
		trade_secure.remove(closer);
		trade_secure.remove(trade_partner);

		//closer.closeInventory();
		trade_partner.closeInventory();

		closer.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Trade cancelled.");
		trade_partner.sendMessage(ChatColor.YELLOW + "Trade cancelled by " + ChatColor.BOLD.toString() + closer.getName() + ChatColor.YELLOW.toString() + ".");

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				trade_partner.updateInventory();
				closer.updateInventory();
			}
		}, 2L);

	}

	public boolean isItemTradeable(ItemStack i){
		return RealmMechanics.isItemTradeable(i);
	}

	public ItemStack makeUnique(ItemStack is){
		/*if(is == null){return null;}
		if(is.getMaxStackSize() == 1){return is;}

		ItemStack unique = new ItemStack(is.getType(), 1, is.getDurability());
		ItemMeta im = unique.getItemMeta();

		if(im != null && im.hasDisplayName()){
			im.setDisplayName(im.getDisplayName() + ChatColor.BLACK.toString());
		}
		else{
			im.setDisplayName(RealmMechanics.getFormalMatName(is.getType(), is.getDurability()) + ChatColor.BLACK.toString());
		}

		unique.setItemMeta(im);

		return unique;
		//return is;*/
		return is;
	}

	public ItemStack makeNormal(ItemStack is){
		/*if(is == null){return null;}
		if(is.getMaxStackSize() == 1){return is;}
		if(!(is.hasItemMeta())){return is;}
		ItemStack normal = CraftItemStack.asCraftCopy(is);
		ItemMeta im = normal.getItemMeta();

		String o_display = im.getDisplayName();
		o_display = o_display.substring(0, o_display.lastIndexOf(ChatColor.BLACK.toString()));
		im.setDisplayName(o_display);
		normal.setItemMeta(im);

		return normal;
		//return is;*/
		return is;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Was Normal / Low
	public void onPlayerInventoryClick(InventoryClickEvent e){
		if(!(e.getWhoClicked().getType() == EntityType.PLAYER)){return;}
		final Player clicker = (Player) e.getWhoClicked();
		final String p_name = clicker.getName();
		
		//log.info("Click event (" + p_name + ") in " + e.getInventory().toString());
		
		if(clicker.hasMetadata("click_event")){
			// Prevent Left+Right spam abuse (due to timers!)
			e.setCancelled(true);
			clicker.updateInventory();
			return;
		}
		
		if(!clicker.getOpenInventory().getTitle().equalsIgnoreCase("container.crafting")){
			clicker.setMetadata("click_event", new FixedMetadataValue(Main.plugin, true));
		}
		
		Main.plugin.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable(){
			public void run(){
				if(Bukkit.getPlayer(p_name) != null){
					Bukkit.getPlayer(p_name).removeMetadata("click_event", Main.plugin);
				}
			}
		}, 5L);
		
		if(!(trade_map.containsKey(clicker))){
			if(clicker.getInventory().getName().contains(clicker.getName()) && !(clicker.getInventory().getName().contains("Merchant"))){
				// Close that shit.
				clicker.closeInventory();
			}
			return;
		}

		
		Inventory tradeWin = e.getInventory();
		//if(e.getCurrentItem() == null){return;}
		Material m = Material.AIR;
		if(e.getCurrentItem() != null){
			m = e.getCurrentItem().getType();
		}

		if((e.isLeftClick() && e.isRightClick())){
			e.setCancelled(true);
			clicker.updateInventory();
			clicker.sendMessage(ChatColor.RED + "This feature has been " + ChatColor.UNDERLINE + "temporarily" + ChatColor.RED + " disabled.");
			return;
		}

		Material cursor = e.getCursor().getType();
		boolean left_side = false;

		if(e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(e.getCurrentItem()) || !(isItemTradeable(e.getCursor())) || !(isItemTradeable(e.getCurrentItem())))){
			e.setCancelled(true);
			clicker.updateInventory();
			clicker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " perform this action with an " + ChatColor.ITALIC + "untradeable" + ChatColor.RED + " item.");
			return;
		}
		if(e.isRightClick()){
			e.setCancelled(true);
			clicker.updateInventory();
			return;
		}

		if(m == Material.THIN_GLASS){
			e.setCancelled(true);
			clicker.updateInventory();
			return;
		}

		int slot_num = e.getRawSlot();


		if(trade_partners.containsKey(clicker)){
			// Left Side
			//log.info("left side : " + slot_num);
			left_side = true;
			if(!(e.isShiftClick()) || (e.isShiftClick() && slot_num < 27)){
				if(slot_num >= 27){return;}
				if(!(slot_num == 0 || slot_num == 1 || slot_num == 2 || slot_num == 3 || slot_num == 9 || slot_num == 10 || slot_num == 11 || slot_num == 12 || slot_num == 18 || slot_num == 19 || slot_num == 20 || slot_num == 21) && !(slot_num > 27)){
					e.setCancelled(true);
					clicker.updateInventory();
					if(MerchantMechanics.isTradeButton(e.getCurrentItem())){
						clicker.sendMessage(ChatColor.RED + "Wrong button.");
					}
					return;
				}

			}
		}
		if(!(trade_partners.containsKey(clicker))){
			// Right Side
			//log.info("right side : " + slot_num);
			left_side = false;
			if(!(e.isShiftClick()) || (e.isShiftClick() && slot_num < 27)){
				if(e.getInventory().getItem(0).getType() != Material.INK_SACK){return;}
				if(slot_num >= 27){return;}
				if(!(slot_num == 5 || slot_num == 6 || slot_num == 7 || slot_num == 8 || slot_num == 14 || slot_num == 15 || slot_num == 16 || slot_num == 17 || slot_num == 23 || slot_num == 24 || slot_num == 25 || slot_num == 26) && !(slot_num > 27)){
					e.setCancelled(true);

					clicker.updateInventory();
					if(MerchantMechanics.isTradeButton(e.getCurrentItem())){
						clicker.sendMessage(ChatColor.RED + "Wrong button.");
					}
					return;
				}
			}
		}

		if(!(MerchantMechanics.isTradeButton(e.getCurrentItem())) && !(m == Material.AIR && cursor == Material.AIR)){
			if(trade_secure.containsKey(clicker)){
				trade_secure.remove(clicker);
				trade_secure.remove(trade_map.get(clicker));
				tradeWin.setItem(0, setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
				tradeWin.setItem(8, setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
				//tradeWin.getItem(0).setDurability((short)8);
				//tradeWin.getItem(8).setDurability((short)8);
				clicker.sendMessage(ChatColor.RED + "Trade modified, unaccepted.");
				trade_map.get(clicker).sendMessage(ChatColor.RED + "Trade modified by " + ChatColor.BOLD + clicker.getName() + ChatColor.RED + ", unaccepted.");

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						clicker.updateInventory();
						trade_map.get(clicker).updateInventory();
					}
				}, 1L);

				//trade_map.get(clicker).updateInventory();
				return; // POSSIBLE ISSUE
			}
		}


		if(e.isShiftClick() && slot_num > 27 && !(e.isCancelled())){
			e.setCancelled(true);
			ItemStack to_move = e.getCurrentItem();
			if(to_move == null){
				return;
			}
			//int to_move_slot = e.getRawSlot();
			int local_to_move_slot = e.getSlot();
			int x = -1;
			if(left_side == true){
				while(x <= 27){
					x++;
					if(!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)){
						continue;
					}
					ItemStack i = tradeWin.getItem(x);
					if((i != null && i.getType() != Material.AIR)){
						continue;
					}

					tradeWin.setItem(x, makeUnique(to_move));
					/*if(tradeWin.getItem(x) != null){
						tradeWin.getItem(x).setAmount(to_move.getAmount());
					}*/

					clicker.getInventory().remove(local_to_move_slot);
					clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
					clicker.updateInventory();
					break;
				}
			}
			if(left_side == false){
				while(x <= 27){
					x++;
					if(!(x == 5 || x == 6 || x == 7 || x == 8 || x == 14 || x == 15 || x == 16 || x == 17 || x == 23 || x == 24 || x == 25 || x == 26)){
						continue;
					}
					ItemStack i = tradeWin.getItem(x);
					if((i != null && i.getType() != Material.AIR)){
						continue;
					}

					tradeWin.setItem(x, makeUnique(to_move));

					clicker.getInventory().remove(local_to_move_slot);
					clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
					clicker.updateInventory();
					break;
				}
			}
		}


		if(MerchantMechanics.isTradeButton(e.getCurrentItem())){
			e.setCancelled(true);
			if(!(clicker.getItemOnCursor() == null || clicker.getItemOnCursor().getType() == Material.AIR)){
				clicker.updateInventory();
				return;
			}
			if(e.getCurrentItem().getDurability() == 8){ // Gray button
				e.getCurrentItem().setDurability((short)10);
				e.setCurrentItem(setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 10), ChatColor.GREEN.toString() + "Trade ACCEPTED.", ChatColor.GRAY.toString() + "Modify the trade to unaccept."));
				clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 2.0F);

				if(tradeWin.getItem(0).getDurability() == (short) 10 && tradeWin.getItem(8).getDurability() == (short) 10){
					final Player tradie = trade_map.get(clicker);

					int tradie_slots = 0;
					int clicker_slots = 0;

					int tradie_slots_needed = 0;
					int clicker_slots_needed = 0;

					for(ItemStack is : tradeWin.getContents()){
						if(is == null){continue;}
						if(PetMechanics.isPermUntradeable(is) || !(RealmMechanics.isItemTradeable(is))){
							tradeWin.remove(is);
						}
					}

					for(ItemStack i : tradie.getInventory().getContents()){
						if(i == null || i.getType() == Material.AIR){
							tradie_slots++;
						}
					}

					for(ItemStack i : clicker.getInventory().getContents()){
						if(i == null || i.getType() == Material.AIR){
							clicker_slots++;
						}
					}

					int slot_var = -1;
					if(left_side == true){

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}

							tradie_slots_needed++;
							//tradie.getInventory().setItem(tradie.getInventory().firstEmpty(), (i));

						}

						if(tradie_slots < tradie_slots_needed){
							tradie.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Not enough room.");
							tradie.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (tradie_slots_needed - tradie_slots) + ChatColor.GRAY + " more free slots to complete this trade.");
							clicker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + tradie.getName() + " does not have enough room for this trade.");
							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									InventoryCloseEvent close_tradie = new InventoryCloseEvent(tradie.getOpenInventory());
									InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
									Bukkit.getServer().getPluginManager().callEvent(close_tradie);
									Bukkit.getServer().getPluginManager().callEvent(close_clicker);
								}
							}, 2L);
							return;
						}

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}

							clicker_slots_needed++;
							//clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), (i));
						}

						if(clicker_slots < clicker_slots_needed){
							clicker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Not enough room.");
							clicker.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (tradie_slots_needed - tradie_slots) + ChatColor.GRAY + " more free slots to complete this trade.");
							tradie.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + clicker.getName() + " does not have enough room for this trade.");
							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									InventoryCloseEvent close_tradie = new InventoryCloseEvent(tradie.getOpenInventory());
									InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
									Bukkit.getServer().getPluginManager().callEvent(close_tradie);
									Bukkit.getServer().getPluginManager().callEvent(close_clicker);
								}
							}, 2L);
							return;
						}

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}
							if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || !(isItemTradeable(i))){
								continue;
							}
							if(i.getType() == Material.EMERALD){
								i = MoneyMechanics.makeGems(i.getAmount());
							}
							tradie.getInventory().setItem(tradie.getInventory().firstEmpty(), makeNormal(i));					 
						}

						tradie.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Trade accepted.");
						tradie.playSound(tradie.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);

						slot_var = -1; 
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}
							if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || !(isItemTradeable(i))){
								continue;
							}
							if(i.getType() == Material.EMERALD){
								i = MoneyMechanics.makeGems(i.getAmount());
							}
							clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), makeNormal(i));
						}
						clicker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Trade accepted.");
						clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
					}

					if(left_side == false){

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}

							clicker_slots_needed++;
							//tradie.getInventory().setItem(tradie.getInventory().firstEmpty(), (i));

						}

						if(clicker_slots < clicker_slots_needed){
							clicker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Not enough room.");
							clicker.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (tradie_slots_needed - tradie_slots) + ChatColor.GRAY + " more free slots to complete this trade.");
							tradie.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + clicker.getName() + " does not have enough room for this trade.");
							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									InventoryCloseEvent close_tradie = new InventoryCloseEvent(tradie.getOpenInventory());
									InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
									Bukkit.getServer().getPluginManager().callEvent(close_tradie);
									Bukkit.getServer().getPluginManager().callEvent(close_clicker);
								}
							}, 2L);
							return;
						}

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}
							if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || !(isItemTradeable(i))){
								continue;
							}
							if(i.getType() == Material.EMERALD){
								i = MoneyMechanics.makeGems(i.getAmount());
							}
							tradie_slots_needed++;
							//clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), (i));
						}

						if(tradie_slots < tradie_slots_needed){
							tradie.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Not enough room.");
							tradie.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (tradie_slots_needed - tradie_slots) + ChatColor.GRAY + " more free slots to complete this trade.");
							clicker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + tradie.getName() + " does not have enough room for this trade.");
							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									InventoryCloseEvent close_tradie = new InventoryCloseEvent(tradie.getOpenInventory());
									InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
									Bukkit.getServer().getPluginManager().callEvent(close_tradie);
									Bukkit.getServer().getPluginManager().callEvent(close_clicker);
								}
							}, 2L);
							return;
						}

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}
							if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || !(isItemTradeable(i))){
								continue;
							}
							if(i.getType() == Material.EMERALD){
								i = MoneyMechanics.makeGems(i.getAmount());
							}
							clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), makeNormal(i));
						}

						clicker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Trade accepted.");
						clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
						slot_var = -1;

						slot_var = -1;
						while(slot_var <= 27){
							slot_var++;
							if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)){
								continue;
							}
							ItemStack i = tradeWin.getItem(slot_var);
							if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.THIN_GLASS){
								continue;
							}
							if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || !(isItemTradeable(i))){
								continue;
							}
							if(i.getType() == Material.EMERALD){
								i = MoneyMechanics.makeGems(i.getAmount());
							}
							tradie.getInventory().setItem(tradie.getInventory().firstEmpty(), makeNormal(i));
						}

						tradie.playSound(tradie.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
						tradie.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Trade accepted.");
					}

					if(!tradeWin.getName().equalsIgnoreCase("container.crafting")){
						tradeWin.clear();
					}

					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							tradie.updateInventory();
							clicker.updateInventory();
							trade_map.remove(clicker);
							trade_map.remove(tradie);
							trade_partners.remove(clicker);
							trade_partners.remove(tradie);
							trade_secure.remove(clicker);
							trade_secure.remove(tradie);
							tradie.closeInventory();
							clicker.closeInventory();
						}
					}, 1L);

				}
				else{
					trade_secure.put(clicker, tradeWin);
					trade_secure.put(trade_map.get(clicker), tradeWin);
					clicker.sendMessage(ChatColor.YELLOW + "Trade accepted, waiting for " + ChatColor.BOLD + trade_map.get(clicker).getName() + ChatColor.YELLOW + "...");
					trade_map.get(clicker).sendMessage(ChatColor.GREEN + clicker.getName() + " has accepted the trade.");
					trade_map.get(clicker).sendMessage(ChatColor.GRAY + "Click the gray button (dye) to confirm.");
				}

			}
		}
	}

}
