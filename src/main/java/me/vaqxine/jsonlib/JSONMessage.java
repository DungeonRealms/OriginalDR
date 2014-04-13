package me.vaqxine.jsonlib;

import net.minecraft.server.v1_7_R2.ChatSerializer;
import net.minecraft.server.v1_7_R2.PacketPlayOutChat;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class JSONMessage {
	
	private JSONObject json = new JSONObject();
	
	public JSONMessage() {
		new JSONMessage("");
	}
	
	public JSONMessage(String text) {
		new JSONMessage(text, ChatColor.WHITE);
	}
	
	public JSONMessage(String text, ChatColor color) {
		json.put("text", text);
		json.put("color", color.name().toLowerCase());
		json.put("extra", new JSONArray());
	}
	
	private JSONArray getExtra() {
		if(!json.containsKey("extra")) json.put("extra", new JSONArray());
		return (JSONArray) json.get("extra");
	}
	
	public void addText(String text) {
		addText(text, ChatColor.WHITE);
	}
	
	public void addText(String text, ChatColor color) {
		JSONObject data = new JSONObject();
		data.put("text", text);
		data.put("color", color.name().toLowerCase());
		getExtra().add(data);
	}
	
	public void addInsertionText(String text, ChatColor color, String insertion){
		JSONObject o = new JSONObject();
		o.put("text", text);
		o.put("color", color.name().toLowerCase());
		o.put("insertion", insertion);
		getExtra().add(o);
	}

	public void addURL(String text, ChatColor color, String url) {
		JSONObject o = new JSONObject();
		o.put("text", text);
		o.put("color", color.name().toLowerCase());
		
		JSONObject u = new JSONObject();
		u.put("action", "open_url");
		u.put("value", url);
		
		o.put("clickEvent", u);
		getExtra().add(o);
	}
	
	public void addItem(ItemStack item, String text) {
		addItem(item, text, ChatColor.WHITE);
	}
	
	@SuppressWarnings("deprecation")
	public void addItem(ItemStack item, String text, ChatColor color) {
		if(item == null) return;
		
		JSONObject o = new JSONObject();
		o.put("text", text);
		o.put("color", color.name().toLowerCase());
		
		JSONObject a = new JSONObject();
		a.put("action", "show_item");

		String x = "{id:" + item.getTypeId() + ",Damage:" + item.getDurability();
		
		if(item.getItemMeta() != null && (item.getItemMeta().getDisplayName() != null || (item.getItemMeta().getLore() != null && item.getItemMeta().getLore().size() > 0))) {
			x += ",tag:{display:{";
			
			ItemMeta m = item.getItemMeta();
			if(m.getDisplayName() != null) x += "Name:" + m.getDisplayName();
			if(m.getLore() != null) x += "Lore:" + JSONArray.toJSONString(m.getLore());
			
			x += "}}";
		}
		x += "}";
		
		a.put("value", x);
		o.put("hoverEvent", a);
		getExtra().add(o);
	}
	
	public void addSuggestCommand(String text, ChatColor color, String cmd) {
		JSONObject o = new JSONObject();
		o.put("text", text);
		o.put("color", color.name().toLowerCase());
		
		JSONObject u = new JSONObject();
		u.put("action", "suggest_command");
		u.put("value", cmd);
		
		o.put("clickEvent", u);
		getExtra().add(o);
	}
	
	@Override
	public String toString(){
		return json.toJSONString();
	}
	
	public void sendToPlayer(Player p){
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(json.toJSONString()), true));
	}
	
}
