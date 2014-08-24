package minecade.dungeonrealms.MonsterMechanics;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityIronGolem;
import net.minecraft.server.v1_7_R4.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R4.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R4.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R4.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.PathfinderGoalTarget;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

public class Golem extends EntityIronGolem {
	
	public Golem(World world) {
		super(world);
		clearGoalSelectors();
		this.goalSelector.a(0, new PathfinderGoalMeleeAttack(this, 1.0D, true));
		this.goalSelector.a(1, new PathfinderGoalMoveTowardsTarget(this, 1.5D, 40.0F));
		this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
		this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
		this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
	}
	
	@SuppressWarnings("rawtypes")
	public void clearGoalSelectors() {
		try {
			Field a = PathfinderGoalSelector.class.getDeclaredField("b");
			Field b = PathfinderGoalSelector.class.getDeclaredField("c");
			a.setAccessible(true);
			b.setAccessible(true);
			((UnsafeList) a.get(this.goalSelector)).clear();
			((UnsafeList) b.get(this.goalSelector)).clear();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void clearTargetSelectors() {
		try {
			Field a = PathfinderGoalTarget.class.getDeclaredField("b");
			Field b = PathfinderGoalTarget.class.getDeclaredField("c");
			a.setAccessible(true);
			b.setAccessible(true);
			((UnsafeList) a.get(this.targetSelector)).clear();
			((UnsafeList) b.get(this.targetSelector)).clear();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
