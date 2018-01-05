package minecade.dungeonrealms.Hive;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import net.minecraft.server.v1_8_R1.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ParticleEffect {
	
	HUGE_EXPLOSION("hugeexplosion", 0, EnumParticle.EXPLOSION_HUGE),
	LARGE_EXPLODE("largeexplode", 1, EnumParticle.EXPLOSION_LARGE),
	FIREWORKS_SPARK("fireworksSpark", 2, EnumParticle.FIREWORKS_SPARK),
	BUBBLE("bubble", 3, EnumParticle.WATER_BUBBLE),
	SUSPEND("suspend", 4, EnumParticle.SUSPENDED),
	DEPTH_SUSPEND("depthSuspend", 5, EnumParticle.SUSPENDED_DEPTH),
	TOWN_AURA("townaura", 6, EnumParticle.TOWN_AURA),
	CRIT("crit", 7, EnumParticle.CRIT),
	MAGIC_CRIT("magicCrit", 8, EnumParticle.CRIT_MAGIC),
	MOB_SPELL("mobSpell", 9, EnumParticle.SPELL_MOB),
	MOB_SPELL_AMBIENT("mobSpellAmbient", 10, EnumParticle.SPELL_MOB_AMBIENT),
	SPELL("spell", 11, EnumParticle.SPELL),
	INSTANT_SPELL("instantSpell", 12, EnumParticle.SPELL_INSTANT),
	WITCH_MAGIC("witchMagic", 13, EnumParticle.SPELL_WITCH),
	NOTE("note", 14, EnumParticle.NOTE),
	PORTAL("portal", 15, EnumParticle.PORTAL),
	ENCHANTMENT_TABLE("enchantmenttable", 16, EnumParticle.ENCHANTMENT_TABLE),
	EXPLODE("explode", 17, EnumParticle.EXPLOSION_NORMAL),
	FLAME("flame", 18, EnumParticle.FLAME),
	LAVA("lava", 19, EnumParticle.LAVA),
	FOOTSTEP("footstep", 20, EnumParticle.FOOTSTEP),
	SPLASH("splash", 21, EnumParticle.WATER_SPLASH),
	LARGE_SMOKE("largesmoke", 22, EnumParticle.SMOKE_LARGE),
	CLOUD("cloud", 23, EnumParticle.CLOUD),
	RED_DUST("reddust", 24, EnumParticle.REDSTONE),
	SNOWBALL_POOF("snowballpoof", 25, EnumParticle.SNOWBALL),
	DRIP_WATER("dripWater", 26, EnumParticle.DRIP_WATER),
	DRIP_LAVA("dripLava", 27, EnumParticle.DRIP_LAVA),
	SNOW_SHOVEL("snowshovel", 28, EnumParticle.SNOW_SHOVEL),
	SLIME("slime", 29, EnumParticle.SLIME),
	HEART("heart", 30, EnumParticle.HEART),
	ANGRY_VILLAGER("angryVillager", 31, EnumParticle.VILLAGER_ANGRY),
	HAPPY_VILLAGER("happyVillager", 32, EnumParticle.VILLAGER_HAPPY),
	ICONCRACK("iconcrack", 33, EnumParticle.BLOCK_CRACK),
	TILECRACK("tilecrack", 34, EnumParticle.ITEM_CRACK);
	
	private String name;
	private int id;
	private EnumParticle particle;
	
	ParticleEffect(String name, int id, EnumParticle particle) {
		this.name = name;
		this.id = id;
		this.particle = particle;
	}
	
	public String getName() {
		return name;
	}
	
	public EnumParticle getParticle() {
		return particle;
	}
	
	public int getId() {
		return id;
	}
	
	private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<Integer, ParticleEffect>();
	static {
		for(ParticleEffect effect : values()) {
			NAME_MAP.put(effect.name, effect);
			ID_MAP.put(effect.id, effect);
		}
	}
	
	public static ParticleEffect fromName(String name) {
		if(name == null) { return null; }
		for(Entry<String, ParticleEffect> e : NAME_MAP.entrySet()) {
			if(e.getKey().equalsIgnoreCase(name)) { return e.getValue(); }
		}
		return null;
	}
	
	public static ParticleEffect fromId(int id) {
		return ID_MAP.get(id);
	}
	
	public static void sendToPlayer(ParticleEffect effect, Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
		sendPacket(player, packet);
	}
	
	public static void sendToLocation(final ParticleEffect effect, final Location location, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int count) throws Exception {
		// Hive.log.info(effect.name + " @ " + location.toString());
				Object packet = null;
				try {
					packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
				} catch(Exception e) {
					e.printStackTrace();
				}
				double radius = 32D;
				
				for(String s : MonsterMechanics.player_locations.keySet()) {
					if(Bukkit.getPlayerExact(s) != null) {
						Player pl = Main.plugin.getServer().getPlayer(s);
						if(pl.getWorld().getName().equalsIgnoreCase(location.getWorld().getName()) && pl.getLocation().toVector().distanceSquared(location.toVector()) <= Math.pow(radius, 2)) {
							try {
								sendPacket(pl, packet);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
	}
	
	public static void sendCrackToPlayer(boolean icon, int id, byte data, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
		sendPacket(player, packet);
	}
	
	public static void sendCrackToLocation(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
		for(Player player : Bukkit.getOnlinePlayers()) {
			sendPacket(player, packet);
		}
	}
	
	public static Object createPacket(ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if(count <= 0) count = 1;
		Object packet = getPacket63WorldParticles();
		setValue(packet, "a", effect.getParticle());
		setValue(packet, "b", (float) location.getX());
		setValue(packet, "c", (float) location.getY());
		setValue(packet, "d", (float) location.getZ());
		setValue(packet, "e", offsetX);
		setValue(packet, "f", offsetY);
		setValue(packet, "g", offsetZ);
		setValue(packet, "h", speed);
		setValue(packet, "i", count);
		return packet;
	}
	
	public static Object createCrackPacket(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
		if(count <= 0) count = 1;
		Object packet = getPacket63WorldParticles();
		String modifier = "iconcrack_" + id;
		if(!icon) {
			modifier = "tilecrack_" + id + "_" + data;
		}
		setValue(packet, "a", modifier);
		setValue(packet, "b", (float) location.getX());
		setValue(packet, "c", (float) location.getY());
		setValue(packet, "d", (float) location.getZ());
		setValue(packet, "e", offsetX);
		setValue(packet, "f", offsetY);
		setValue(packet, "g", offsetZ);
		setValue(packet, "h", 0.1F);
		setValue(packet, "i", count);
		return packet;
	}
	
	private static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}
	
	private static Object getEntityPlayer(Player p) throws Exception {
		Method getHandle = p.getClass().getMethod("getHandle");
		return getHandle.invoke(p);
	}
	
	private static String getPackageName() {
		return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
	private static Object getPacket63WorldParticles() throws Exception {
		Class<?> packet = Class.forName(getPackageName() + ".PacketPlayOutWorldParticles");
		return packet.getConstructors()[0].newInstance();
	}
	
	private static void sendPacket(Player p, Object packet) throws Exception {
		Object eplayer = getEntityPlayer(p);
		Field playerConnectionField = eplayer.getClass().getField("playerConnection");
		Object playerConnection = playerConnectionField.get(eplayer);
		for(Method m : playerConnection.getClass().getMethods()) {
			if(m.getName().equalsIgnoreCase("sendPacket")) {
				m.invoke(playerConnection, packet);
				return;
			}
		}
	}
}
