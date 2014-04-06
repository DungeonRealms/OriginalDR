package me.vaqxine.MonsterMechanics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class Monster {
    // Some default references.
    private int mob_health = 1;
    private int max_mob_health = 1;
    private List<Integer> mob_damage = new ArrayList<Integer>();
    private int mob_tier = 1, mob_armor = 1;
    private List<ItemStack> mob_loot = new ArrayList<ItemStack>();
    private String mob_target;
    private String mob_spawn_ownership;
    private long mob_last_hurt, mob_last_hit = 0;
    private Entity ent;
    private long last_respawn;
    private boolean elite = false, powerStriking = false, whirlwinding = false, special_attacking = false;
    private int special_attack, power_strike, whirlwind;
    private float mob_yaw;

    public Monster(Entity initial, int mob_max_health, int mob_health, int mob_armor, int mob_tier, List<Integer> damage) {
        ent = initial;
        this.mob_armor = mob_armor;
        mob_damage = damage;
        this.max_mob_health = mob_max_health;
        this.mob_health = mob_health;
        this.mob_tier = mob_tier;
        mob_target = "";
    }

    public void setMobHealth(int health) {
        this.mob_health = health;
    }

    public Entity getEntity() {
        return ent;
    }

    public void setMobTarget(String s) {
        mob_target = s;
    }

    public List<ItemStack> getMobLoot() {
        return mob_loot;
    }

    public int getMobArmor() {
        return mob_armor;
    }

    public String getMobSpawn() {
        return mob_spawn_ownership;
    }

    public boolean isElite() {
        return elite;
    }

    public boolean isWhirlwind() {
        return whirlwinding;
    }

    public void setMobLastHurt(long hit) {
        mob_last_hurt = hit;
    }

    public void setMobLastHit(long hit) {
        mob_last_hit = hit;
    }

    public void setSpecialAttack(int attack) {
        special_attack = attack;
    }

    public void setMobMaxHealth(int health) {
        max_mob_health = health;
    }

    public void setPowerStrike(int strike) {
        power_strike = strike;
    }

    public void setMobDamage(List<Integer> damage) {
        this.mob_damage = damage;
    }
    
    public void setMobDrops(List<ItemStack> items){
        this.mob_loot = items;
    }
    public void setLastRespawn(long last_respawn) {
        this.last_respawn = last_respawn;
    }

    public void setWhirlwind(int wind) {
        whirlwind = wind;
    }

    public void setMobArmor(int armor) {
        mob_armor = armor;
    }

    /* This section is for special mobs in the mobs mechanics. */
    public void setIsSpecialAttack(boolean attack) {
        special_attacking = attack;
        if (!special_attacking && getSpecialAttack() > 0) {
            setSpecialAttack(0);
        }
    }

    public void setWhirlwinding(boolean bool) {
        this.whirlwinding = bool;
        if (!bool && getWhirlind() > 0) {
            setWhirlwind(0);
        }
    }

    public void setPowerStriking(boolean powerStriking) {
        this.powerStriking = powerStriking;
        if (!powerStriking && getPowerStrike() > 0) {
            setPowerStrike(0);
        }
    }

    public void setYaw(float yaw) {
        this.mob_yaw = yaw;
    }

    public void setMobSpawn(String s) {
        this.mob_spawn_ownership = s;
    }

    public int getMobTier() {
        return mob_tier;
    }

    public boolean isPowerStrike() {
        return powerStriking;
    }

    public long getLastRespawn() {
        return last_respawn;
    }

    public int getMobHealth() {
        return mob_health;
    }

    public int getMaxMobHealth() {
        return max_mob_health;
    }

    public int getSpecialAttack() {
        return special_attack;
    }

    public int getPowerStrike() {
        return power_strike;
    }

    public int getWhirlind() {
        return whirlwind;
    }

    public boolean isSpecialAttacking() {
        return special_attacking;
    }

    public float getMobYaw() {
        return mob_yaw;
    }

    public String getMobTarget() {
        return mob_target;
    }

    public List<Integer> getMobDamage() {
        return mob_damage;
    }

    public long getMobLastHit() {
        return mob_last_hit;
    }

    public long getMobLastHurt() {
        return mob_last_hurt;
    }
}
