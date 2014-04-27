package minecade.dungeonrealms.ItemMechanics;

import org.bukkit.entity.Player;

public class PlayerStaff {

    Player p;
    int total_shots;
    int shots_left;
    long last_shot;
    long cooldown_between_bursts;
    public PlayerStaff(Player p, int total_shots) {
        this.p = p;
        this.total_shots = total_shots;
    }

    public void shoot() {
        if (canShoot()) {
            
        }
    }

    public boolean canShoot() {
        if(shots_left == 0){
            //if(last)
        }
        return true;
    }
}
