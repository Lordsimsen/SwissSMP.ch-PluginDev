package ch.swisssmp.zones;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class ItemManager {
	public static void updateItems(ZoneInfo zoneInfo){
		for(Player player : Bukkit.getOnlinePlayers()){
			updateItems(zoneInfo, player.getInventory());
		}
	}
	public static void updateItems(ZoneInfo zoneInfo, Inventory inventory){
		for(ItemStack item : inventory){
			if(item==null) continue;
			String regionId = ZoneInfo.getId(item);
			if(regionId==null || !regionId.equals(zoneInfo.getId())) continue;
			zoneInfo.apply(item);
		}
	}
}
