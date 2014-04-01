package me.vaqxine.MonsterMechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.vaqxine.PetMechanics.PetMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class asyncEntityTarget extends Thread {

	public void run(){
		while(true){
			try {Thread.sleep(250);} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			
			if(MonsterMechanics.async_entity_target.size() <= 0){continue;}
			
			for(EntityTargetEvent e : MonsterMechanics.async_entity_target){
				if(!(e.getTarget() instanceof Player) || !(MonsterMechanics.mob_tier.containsKey(e.getEntity()))){
					MonsterMechanics.async_entity_target.remove(e);
					continue;
				}
				
				Entity ent = e.getEntity();
				final Player p = (Player)e.getTarget();

				if(!(PetMechanics.inv_pet_map.containsKey(e.getEntity())) && !MonsterMechanics.mob_target.containsKey(e.getEntity()) && !(MonsterMechanics.last_mob_message_get.containsKey(p.getName()))){
					int do_i_speak = new Random().nextInt(100);
					if(do_i_speak <= 12){
						String mob_type = MonsterMechanics.getMobType(e.getEntity(), true);
						List<String> possible_messages = new ArrayList<String>();
						if(!(MonsterMechanics.mob_messages.containsKey(mob_type))){continue;}
						possible_messages = MonsterMechanics.mob_messages.get(mob_type);
						int which_message = new Random().nextInt(possible_messages.size());
						
						String message = possible_messages.get(which_message);
						// message.substring(0, message.indexOf(":"))
						LivingEntity le = (LivingEntity)e.getEntity();

						if(le.hasMetadata("mobname")){
							if(message.contains(":")){
								// Bandit: Hey noob.
								MonsterMechanics.async_message_send.put(p, ChatColor.RED + le.getMetadata("mobname").get(0).asString() + ":" + ChatColor.WHITE + message.substring(message.indexOf(":") + 1, message.length()));
							}
							else{
								// Hey noob.
								MonsterMechanics.async_message_send.put(p, ChatColor.RED + le.getMetadata("mobname").get(0).asString() + ": " + ChatColor.WHITE + message);
							}
							MonsterMechanics.last_mob_message_get.put(p.getName(), System.currentTimeMillis());
						}
					}
				}

				LivingEntity le = (LivingEntity)ent;
				ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity)ent).getHandle().getEquipment()[0]);
				if (weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
					//is_elite = true;
					le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, 1));
				}

				if(ent instanceof LivingEntity && ent.hasMetadata("etype")){
					String elemental_type = le.getMetadata("etype").get(0).asString();
					if(elemental_type.equalsIgnoreCase("poison")){
						MonsterMechanics.attachPotionEffect(le, 0x669900);
					}
					if(elemental_type.equalsIgnoreCase("fire")){
						MonsterMechanics.attachPotionEffect(le, 0xCC0033);
					}
					if(elemental_type.equalsIgnoreCase("ice")){
						MonsterMechanics.attachPotionEffect(le, 0x33FFFF);
					}
					if(elemental_type.equalsIgnoreCase("pure")){
						MonsterMechanics.attachPotionEffect(le, 0xFFFFFF);
					}
				}
				
				if(ent.getVehicle() != null && ent.getVehicle() instanceof Creature){
					Creature c = (Creature)ent.getVehicle();
					c.setTarget(p);
				}
				
				if(!MonsterMechanics.approaching_mage_list.contains(ent) && (weapon.getType() == Material.WOOD_HOE || weapon.getType() == Material.STONE_HOE || weapon.getType() == Material.IRON_HOE || weapon.getType() == Material.DIAMOND_HOE || weapon.getType() == Material.GOLD_HOE)){
					/*Projectile pj = null;
					if(ItemMechanics.getItemTier((weapon)) == 1){
						pj = le.launchProjectile(Snowball.class);
					}
					if(ItemMechanics.getItemTier((weapon)) == 2){
						pj = le.launchProjectile(SmallFireball.class);
					}
					if(ItemMechanics.getItemTier((weapon)) == 3){
						pj = le.launchProjectile(EnderPearl.class);
						pj.setVelocity(pj.getVelocity().multiply(3));
					}
					if(ItemMechanics.getItemTier((weapon)) == 4){
						pj = le.launchProjectile(WitherSkull.class);
					}
					if(ItemMechanics.getItemTier((weapon)) == 5){
						pj = le.launchProjectile(LargeFireball.class);
					}
					
					ItemMechanics.projectile_map.put(pj, weapon);*/
					if(!(MonsterMechanics.approaching_mage_list.contains(ent))){
						MonsterMechanics.approaching_mage_list.add(ent);
					}
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2)); // Make them super slow so they shoot more.
				}

				le.setCustomNameVisible(true);
				MonsterMechanics.async_entity_target.remove(e);
			}
			

			//MonsterMechanics.async_entity_target.clear();
		}
	}

}
