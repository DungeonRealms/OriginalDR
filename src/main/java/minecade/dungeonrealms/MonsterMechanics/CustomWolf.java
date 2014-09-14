package minecade.dungeonrealms.MonsterMechanics;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityWolf;
import net.minecraft.server.v1_7_R4.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R4.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_7_R4.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

public class CustomWolf extends EntityWolf {

    public CustomWolf(World world) {
        super(world);
        a(0.6F, 0.8F);
        getNavigation().a(true);
        clearGoalSelectors();
        setAngry(true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, true));
        setTamed(false);
        // this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed(this, EntityHuman.class, 200, false));
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
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
