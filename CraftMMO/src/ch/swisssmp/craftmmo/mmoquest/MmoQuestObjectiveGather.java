package ch.swisssmp.craftmmo.mmoquest;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoworld.MmoRegion;
import net.md_5.bungee.api.ChatColor;

public class MmoQuestObjectiveGather extends MmoQuestObjective{

	public final HashMap<Integer, MmoQuestGatherItem> items = new HashMap<Integer, MmoQuestGatherItem>();
	public final int mmo_region_id;
	
	public MmoQuestObjectiveGather(UUID player_uuid, ConfigurationSection dataSection) {
		super(player_uuid, dataSection);
		ConfigurationSection configurationSection = dataSection.getConfigurationSection("configuration");
		int mmo_item_id = configurationSection.getInt("item");
		int target_amount = configurationSection.getInt("count");
		this.mmo_region_id = configurationSection.getInt("region");
		MmoItem mmoItem = MmoItem.get(mmo_item_id);
		if(mmoItem!=null){
			ItemStack itemStack = mmoItem.toItemStack();
			this.items.put(itemStack.hashCode(), new MmoQuestGatherItem(itemStack, target_amount));
		}
		if(localSaveData.exists()){
			load();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onItemPickup(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		//optional parameter checkParty defaults to objective setting
		if(!canContribute(player.getUniqueId())){
			return;
		}
		if(!MmoRegion.regionMatch(event.getItem().getLocation(), this.mmo_region_id)){
			return;
		}
		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		int hashCode = itemStack.hashCode();
		if(items.containsKey(hashCode)){
			event.setCancelled(true);
			item.remove();
			MmoQuestGatherItem gatherItem = this.items.get(hashCode);
			gatherItem.current_amount+=itemStack.getAmount();
			save();
			updateCleared();
			if(!isCompleted()){
				updateObjective();
			}
		}
	}
	
	public void updateCleared(){
		for(MmoQuestGatherItem gatherItem : items.values()){
			if(gatherItem.current_amount<gatherItem.target_amount){
				return;
			}
		}
		this.setCompleted(true);
	}
	
	@Override
	public String getObjectiveText(){
		String result = getObjectiveHeader();
		result+= createSubtitle("Fortschritt:");
		for(MmoQuestGatherItem gatherItem : items.values()){
			result+= ChatColor.RESET+String.valueOf(gatherItem.current_amount)+"/"+gatherItem.target_amount+" "+gatherItem.itemStack.getItemMeta().getDisplayName()+"\n";
		}
		return result;
	}

	@Override
	public void save() {
		YamlConfiguration saveData = new YamlConfiguration();
		for(Entry<Integer, MmoQuestGatherItem> gatherItemEntry : items.entrySet()){
			saveData.set(String.valueOf(gatherItemEntry.getKey()), gatherItemEntry.getValue().current_amount);
		}
		try {
			saveData.save(localSaveData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		YamlConfiguration saveData = new YamlConfiguration();
		try {
			saveData.load(localSaveData);
			for(String hashCodeString : saveData.getKeys(false)){
				int current_amount = saveData.getInt(hashCodeString);
				items.get(Integer.parseInt(hashCodeString)).current_amount = current_amount;
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
