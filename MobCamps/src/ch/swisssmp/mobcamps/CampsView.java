package ch.swisssmp.mobcamps;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.InventoryUtil;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class CampsView extends InventoryView implements Listener {

	private final Player player;
	private final Inventory inventory;
	
	private CampsView(Player player){
		this.player = player;
		this.inventory = Bukkit.createInventory(null, 36, "Camps");
		this.createLootTableTokens();
	}
	
	private void createLootTableTokens(){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse(MobCamps.getInstance(), "list_camps.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("mob_camps")){
			this.player.sendMessage("[MobCamps] "+ChatColor.RED+"Konnte Camps nicht anzeigen.");
			return;
		}
		List<Integer> mobCampsList = yamlConfiguration.getIntegerList("mob_camps");
		MobCamp mobCamp;
		for(int mob_camp_id : mobCampsList){
			mobCamp = MobCamp.get(mob_camp_id);
			this.inventory.addItem(mobCamp.getInventoryToken(1));
		}
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=this.inventory) return;
		ItemStack itemStack = this.inventory.getItem(event.getSlot());
		if(itemStack==null){
			Bukkit.getScheduler().runTaskLater(MobCamps.plugin, ()->{
				inventory.setItem(event.getSlot(), null);
			}, 1L);
			return;
		}
		if(itemStack!=null && event.getCursor()!=null && event.getCursor().getType()!=Material.AIR && itemStack!=event.getCursor()){
			int camp_id = ItemUtil.getInt(itemStack, "mob_camp");
			if(camp_id==ItemUtil.getInt(event.getCursor(), "mob_camp")){
				itemStack.setAmount(0);
				return;
			}
			event.setCancelled(true);
		}
		InventoryUtil.refillInventorySlot(inventory, event.getSlot(), itemStack.clone());
		
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		if(Collections.min(event.getRawSlots())>this.inventory.getSize()) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		HandlerList.unregisterAll(this);
	}

	@Override
	public Inventory getBottomInventory() {
		return this.player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
	}

	@Override
	public Inventory getTopInventory() {
		return this.inventory;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
	protected static CampsView open(Player player){
		CampsView result = new CampsView(player);
		Bukkit.getPluginManager().registerEvents(result, MobCamps.plugin);
		player.openInventory(result);
		return result;
	}
}
