package minecade.dungeonrealms.ItemMechanics;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class HomingArrow extends BukkitRunnable
{
private static final double MaxRotationAngle = 0.12D;
  private static final double TargetSpeed = 1.4D;
  Arrow arrow;
  LivingEntity target;

  public HomingArrow(Arrow arrow, LivingEntity target, Plugin plugin)
  {
    this.arrow = arrow;
    this.target = target;
    runTaskTimer(plugin, 1L, 1L);
  }

  public void run()
  {
    double speed = this.arrow.getVelocity().length();
    if ((this.arrow.isOnGround()) || (this.arrow.isDead()) || (this.target.isDead())) {
      cancel();
      return;
    }

    Vector toTarget = this.target.getLocation().clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(this.arrow.getLocation()).toVector();

    Vector dirVelocity = this.arrow.getVelocity().clone().normalize();
    Vector dirToTarget = toTarget.clone().normalize();
    double angle = dirVelocity.angle(dirToTarget);

    double newSpeed = 0.9D * speed + 0.14D;

    if (((this.target instanceof Player)) && (this.arrow.getLocation().distance(this.target.getLocation()) < 8.0D)) {
      Player player = (Player)this.target;
      if (player.isBlocking())
        newSpeed = speed * 0.6D;
    }
    Vector newVelocity;
    if (angle < 0.12D) {
      newVelocity = dirVelocity.clone().multiply(newSpeed);
    } else {
      Vector newDir = dirVelocity.clone().multiply((angle - 0.12D) / angle).add(dirToTarget.clone().multiply(0.12D / angle));
      newDir.normalize();
      newVelocity = newDir.clone().multiply(newSpeed);
    }

    this.arrow.setVelocity(newVelocity.add(new Vector(0.0D, 0.03D, 0.0D)));
    this.arrow.getWorld().playEffect(this.arrow.getLocation(), Effect.SMOKE, 0);
  }
}