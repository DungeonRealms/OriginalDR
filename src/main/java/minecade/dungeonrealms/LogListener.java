package minecade.dungeonrealms;

import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.enums.LogType;
import minecade.dungeonrealms.jsonlib.JsonBuilder;
import minecade.dungeonrealms.models.LogModel;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LogListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player) return;
        if(!(e.getDamager() instanceof Player)) return;
        if(e.getDamage() <= 0) return;
        if((MonsterMechanics.getMHealth(e.getEntity()) - e.getDamage()) > 0) return;
        new LogModel(LogType.MOB_KILL, ((HumanEntity) e.getDamager()).getName(), new JsonBuilder("entity", e.getEntity().getType()).setData("location", e.getEntity().getLocation()).getJson());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		if(e.getEntity().getKiller() != null){
			new LogModel(LogType.DEATH, e.getEntity().toString(), new JsonBuilder("killer", e.getEntity().getKiller().getName()).setData("location", e.getEntity().getLocation()).getJson());
			new LogModel(LogType.PLAYER_KILL, e.getEntity().toString(), new JsonBuilder("target", e.getEntity().getName()).setData("location", e.getEntity().getLocation()).getJson());
		}else{
			new LogModel(LogType.DEATH, e.getEntity().toString(), new JsonBuilder("location", e.getEntity().getLocation()).getJson());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent e){
		if(e.getResult() == PlayerLoginEvent.Result.ALLOWED){
			new LogModel(LogType.LOGIN, e.getPlayer().getName(), new JsonBuilder("shard", Utils.getShard()).getJson());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogout(PlayerQuitEvent e){
		new LogModel(LogType.LOGOUT, e.getPlayer().getName(), new JsonBuilder("shard", Utils.getShard()).setData("xp", LevelMechanics.getPlayerData(e.getPlayer()).getXP()).getJson());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e){
		if(e.isCancelled()) return;
		if (e.getMessage().startsWith("sendpacket") || e.getMessage().startsWith("/sendpacket")) return;
		new LogModel(LogType.COMMAND, e.getPlayer().getName(), new JsonBuilder("command", e.getMessage()).getJson());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatEvent(AsyncPlayerChatEvent e){
		if(e.isCancelled()) return;
		new LogModel(LogType.CHAT_MESSAGE, e.getPlayer().getName(), new JsonBuilder("message", e.getMessage()).getJson());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent e){
		if(e.isCancelled()) return;
		ItemStack i = e.getItem().getItemStack();
		new LogModel(LogType.ITEM_PICKUP, e.getPlayer().getName()
				, new JsonBuilder("name", i.getItemMeta().getDisplayName() == null ? i.getType().name() : i.getItemMeta().getDisplayName())
					.setData("lore", i.getItemMeta().getLore() == null ? "" : i.getItemMeta().getLore())
					.setData("damage", i.getDurability())
					.setData("amount", i.getAmount())
					.getJson());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDropItem(PlayerDropItemEvent e){
		if(e.isCancelled()) return;
		ItemStack i = e.getItemDrop().getItemStack();
		new LogModel(LogType.ITEM_DROP, e.getPlayer().getName()
				, new JsonBuilder("name", i.getItemMeta().getDisplayName() == null ? i.getType().name() : i.getItemMeta().getDisplayName())
					.setData("lore", i.getItemMeta().getLore() == null ? "" : i.getItemMeta().getLore())
					.setData("damage", i.getDurability())
					.setData("amount", i.getAmount())
					.getJson());
	}
	
}
