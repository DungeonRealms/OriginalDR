package minecade.dungeonrealms.MonsterMechanics;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class DamageTracker {
    ConcurrentHashMap<Player, Double> player_damage = new ConcurrentHashMap<Player, Double>();
    UUID uuid;

    public DamageTracker(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPlayersDamage(Player p, double to_add) {
        if (!player_damage.containsKey(p)) {
            player_damage.put(p, to_add);
            return;
        }
        player_damage.put(p, player_damage.get(p) + to_add);
    }

    public boolean hasPlayerDamaged(Player p) {
        return player_damage.containsKey(p);
    }

    public void removePlayer(Player p) {
        if (player_damage.containsKey(p)) {
            player_damage.remove(p);
        }
    }

    public Player getMostDamageDone() {
        double max_damage = 0;
        Player p = null;
        for (Entry<Player, Double> damages : player_damage.entrySet()) {
            Player pl = damages.getKey();
            double damage_done = damages.getValue();
            if (damage_done > max_damage) {
                max_damage = damage_done;
                p = pl;
            }
        }
        return p;
    }
}
