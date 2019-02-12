package ch.swisssmp.stalker.listeners.enchantment;

import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ch.swisssmp.stalker.LogEntry;
import ch.swisssmp.stalker.Stalker;
import ch.swisssmp.utils.SwissSMPUtils;

public class EnchantmentEventListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	private void onEnchantItem(EnchantItemEvent event){
		if(event.isCancelled()) return;
		LogEntry logEntry = new LogEntry(event.getEnchanter());
		logEntry.setWhat("ENCHANT_ITEM");
		logEntry.setWhere(event.getEnchantBlock());
		JsonObject extraData = new JsonObject();
		JsonArray enchantmentsArray = new JsonArray();
		for(Entry<Enchantment,Integer> enchantment : event.getEnchantsToAdd().entrySet()){
			JsonObject enchantmentSection = new JsonObject();
			enchantmentSection.addProperty(enchantment.getKey().toString(), enchantment.getValue());
			enchantmentsArray.add(enchantmentSection);
		}
		extraData.add("enchantments", enchantmentsArray);
		extraData.addProperty("item", SwissSMPUtils.encodeItemStack(event.getItem()));
		logEntry.setExtraData(extraData);
		Stalker.log(logEntry);
	}
}
