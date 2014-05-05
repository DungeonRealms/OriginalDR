package minecade.dungeonrealms;

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
	
	
}
