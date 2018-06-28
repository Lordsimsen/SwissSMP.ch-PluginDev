package ch.swisssmp.taxcollector;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public abstract class TaxInventory{
	private static HashMap<Inventory,TaxInventory> inventoryMap = new HashMap<Inventory,TaxInventory>();
	
	protected final Player player;
	protected final Inventory inventory;
	protected int iron_ingots = 0;
	protected int gold_ingots = 0;
	protected int diamonds = 0;
	protected int glowstone = 0;
	protected int quartz = 0;
	
	public TaxInventory(Player player, Inventory inventory, ConfigurationSection dataSection){
		this.player = player;
		ConfigurationSection debtsSection = dataSection.getConfigurationSection("debt");
		if(debtsSection!=null){
			ConfigurationSection debtSection;
			for(String key : debtsSection.getKeys(false)){
				debtSection = debtsSection.getConfigurationSection(key);
				iron_ingots+=debtSection.getInt("iron_ingots_remaining");
				gold_ingots+=debtSection.getInt("gold_ingots_remaining");
				diamonds+=debtSection.getInt("diamonds_remaining");
				glowstone+=debtSection.getInt("glowstone_blocks_remaining");
				quartz+=debtSection.getInt("quartz_blocks_remaining");
			}
		}
		this.inventory = inventory;
		ConfigurationSection creditSection = dataSection.getConfigurationSection("credit");
		if(creditSection!=null){
			ItemStack itemStack;
			for(String key : creditSection.getKeys(false)){
				itemStack = creditSection.getItemStack(key);
				if(itemStack==null){
					Bukkit.getLogger().info("[TaxCollector] Encountered an invalid ItemStack at '"+key+"' when opening TaxInventory for '"+player.getName()+"'!");
					continue;
				}
				this.inventory.addItem(itemStack);
			}
		}
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.7f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.9f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 0.95f);
		
		inventoryMap.put(this.inventory, this);
	}
	
	public abstract void close();
	
	protected void finish(YamlConfiguration response){
		inventoryMap.remove(this.inventory);
		if(response!=null && response.contains("message")){
			player.sendMessage(response.getString("message"));
		}

		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.4f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.8f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2f, 1.9f);
	}
	
	protected static TaxInventory get(Inventory inventory){
		return inventoryMap.get(inventory);
	}

	protected static TaxInventory open(Player humanEntity, Inventory inventory, ConfigurationSection dataSection) {
		if(dataSection.contains("temple_id")){
			if(TributeInventory.get(dataSection.getInt("temple_id"))!=null) return null; //tribute inventory is already open, cannot allow it to be opened twice
		}
		if(dataSection.getBoolean("is_penalty")){
			return new PenaltyInventory((Player)humanEntity, dataSection);
		}
		else{
			return new TributeInventory((Player)humanEntity, inventory, dataSection);
		}
	}
	
	protected static void closeAll(){
		TaxInventory[] openInventories = new TaxInventory[inventoryMap.size()];
		inventoryMap.values().toArray(openInventories);
		for(TaxInventory entry : openInventories){
			entry.player.closeInventory();
			entry.player.sendMessage("["+ChatColor.LIGHT_PURPLE+"Gedankenstimme"+ChatColor.RESET+"] "+ChatColor.GRAY+"Die Akroma-Abgaben werden jetzt eingesammelt.");
		}
	}
}
