package minecade.dungeonrealms.PetMechanics;

import java.lang.reflect.Field;

import minecade.dungeonrealms.Main;
import net.minecraft.server.v1_7_R2.EntityHuman;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.EntitySpider;
import net.minecraft.server.v1_7_R2.GenericAttributes;
import net.minecraft.server.v1_7_R2.World;

public class MountSpider extends EntitySpider {

    public MountSpider(World world) {
        super(world);
        getAttributeInstance(GenericAttributes.d).setValue(1.5D);
    }

    @Override
    public void e(float sideMot, float forMot) {
        if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
            super.e(sideMot, forMot);
            this.W = 0.5F; // Make sure the entity can walk over half slabs, instead of jumping
            return;
        }
        // this.getAttributeInstance(GenericAttributes.d).setValue(1D);
        this.lastYaw = this.yaw = this.passenger.yaw;
        this.pitch = this.passenger.pitch * 0.5F;

        // Set the entity's pitch, yaw, head rotation etc.
        this.b(this.yaw, this.pitch); // [url]https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/Entity.java#L163-L166[/url]
        this.aO = this.aM = this.yaw;

        this.W = 1.0F; // The custom entity will now automatically climb up 1 high blocks

        sideMot = ((EntityLiving) this.passenger).bd * 0.5F;
        Main.d("be = " + ((EntityLiving) this.passenger).be);
        forMot = ((EntityLiving) this.passenger).be * 1.3F;
        Main.d("forMot after = " + forMot);
        if (forMot <= 0.0F) {
            forMot *= 0.25F; // Make backwards slower
        }
        sideMot *= 0.75F; // Also make sideways slower

        Field jump = null;
        try {
            jump = EntityLiving.class.getDeclaredField("bc");
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        }
        jump.setAccessible(true);

        if (jump != null && this.onGround) { // Wouldn't want it jumping while not on the ground would we?
            try {
                if (jump.getBoolean(this.passenger)) {
                    double jumpHeight = 0.5D;
                    this.motY = jumpHeight; // Used all the time in NMS for entity jumping
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.e(sideMot, forMot); // Apply the motion to the entity
    }

    @Override
    public boolean bj() {
        // TODO Auto-generated method stub
        return true;
    }
}
