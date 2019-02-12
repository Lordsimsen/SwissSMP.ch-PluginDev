package ch.swisssmp.warehouse;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class ItemManager {
	
	public static CustomItemBuilder getWarehouseBuilder(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("STOCK_LEDGER");
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setAttackDamage(0);
		return itemBuilder;
	}
	
	public static ItemStack getWarehouseTool(){
		return getWarehouseTool(null);
	}
	
	public static ItemStack getWarehouseTool(UUID master_id){
		CustomItemBuilder itemBuilder = getWarehouseBuilder();
		ItemStack result = itemBuilder.build();
		StockLedgerInfo info = new StockLedgerInfo();
		if(master_id!=null) info.setId(master_id);
		info.apply(result);
		return result;
	}
	
	public static void updateWarehouseTools(){
		for(Player player : Bukkit.getOnlinePlayers()){
			updateWarehouseTools(player.getInventory());
		}
	}
	
	public static void updateWarehouseTools(Inventory inventory){
		for(ItemStack itemStack : inventory){
			if(itemStack==null || itemStack.getType()!=Material.DIAMOND_SWORD) continue;
			StockLedgerInfo ledger = StockLedgerInfo.get(itemStack);
			if(ledger==null){
				continue;
			}
			ledger.apply(itemStack);
		}
	}
}
