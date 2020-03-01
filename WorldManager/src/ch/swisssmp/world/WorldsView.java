package ch.swisssmp.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.Mathf;

public class WorldsView implements Listener{
	
	private final Player player;
	private final Inventory inventory;
	
	private InventoryView view;
	
	private final List<String> worlds = new ArrayList<String>();
	
	private WorldsView(Player player){
		this.player = player;
		this.inventory = Bukkit.createInventory(null, Mathf.ceilToInt(Bukkit.getWorlds().size()/9f)*9, "Geladene Welten");
		this.loadWorldItems();
	}
	
	private void loadWorldItems(){
		this.inventory.clear();
		CustomItemBuilder itemBuilder;
		List<String> description;
		String topologyString;
		for(World world : Bukkit.getWorlds()){
			this.worlds.add(world.getName());
			switch(world.getEnvironment()){
			case NORMAL: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_OVERWORLD"); itemBuilder.setDisplayName(ChatColor.GREEN+world.getName()); break;
			case NETHER: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_NETHER"); itemBuilder.setDisplayName(ChatColor.RED+world.getName()); break;
			case THE_END: itemBuilder = CustomItems.getCustomItemBuilder("WORLD_THE_END"); itemBuilder.setDisplayName(ChatColor.YELLOW+world.getName()); break;
			default: continue;
			}
			description = new ArrayList<String>();
			
			switch(world.getWorldType()){
			case NORMAL: topologyString = "Normal"; break;
			case AMPLIFIED: topologyString = "Zerklüftet"; break;
			case LARGE_BIOMES: topologyString = "Grosse Biome"; break;
			case FLAT: topologyString = "Flach"; break;
			default: topologyString = world.getWorldType().toString(); break;
			}
			description.add(ChatColor.WHITE+"Topologie: "+topologyString);
			description.add(ChatColor.WHITE+"Generiere Strukturen: "+(world.canGenerateStructures() ? "Ja":"Nein"));
			description.add(ChatColor.WHITE+"Seed: "+world.getSeed());
			itemBuilder.setLore(description);
			itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			this.inventory.addItem(itemBuilder.build());
		}
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this.view) return;
		event.setCancelled(true);
		if(event.getClickedInventory()!=this.inventory || event.getSlot()>=this.worlds.size()) return;
		WorldEditor.open(this.worlds.get(event.getSlot()), player);
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this.view) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this.view) return;
		HandlerList.unregisterAll(this);
	}

	public HumanEntity getPlayer() {
		return this.player;
	}
	
	protected static WorldsView open(Player player){
		WorldsView result = new WorldsView(player);
		Bukkit.getPluginManager().registerEvents(result, WorldManager.plugin);
		result.view = player.openInventory(result.inventory);
		return result;
	}
}
