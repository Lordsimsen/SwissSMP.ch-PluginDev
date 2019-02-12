package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.utils.Mathf;

public class DestinationSelectionView extends InventoryView implements Listener {

	private final Player player;
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	
	private final TravelStation start;
	
	private DestinationSelectionView(Player player,TravelStation start, HashMap<Integer,ItemStack> items, String label){
		this.player = player;
		this.playerInventory = player.getInventory();

		this.start = start;
		
		int highestSlot = Collections.max(items.keySet());
		this.inventory = Bukkit.createInventory(null, Math.max(Mathf.ceilToInt(highestSlot / 9f),1) * 9, label);
		for(Entry<Integer,ItemStack> item : items.entrySet()){
			inventory.setItem(item.getKey(), item.getValue());
		}
		if(this.start.getJourney()!=null){
			this.createCancelJourneyItem();
		}
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=inventory) return;
		event.setCancelled(true);
		int slot = event.getSlot();
		if(slot==8){
			this.start.cancelJourney();
			this.closeLater();
			return;
		}
		ItemStack itemStack = inventory.getItem(slot);
		if(itemStack==null || itemStack.getType()==Material.AIR) return;
		TravelStation destination = TravelStation.get(itemStack);
		if(destination==null){
			inventory.setItem(event.getSlot(), null);
			return;
		}
		if(this.start.getJourney()!=null && this.start.getJourney().getDestination()==destination){
			this.embarkNow();
			return;
		}
		this.start.prepareJourney(destination);
		this.start.getJourney().join(player);
		player.sendMessage(TravelSystem.getPrefix()+ChatColor.GRAY+"Reise nach "+destination.getName()+" startet in 30 Sekunden.");
		player.sendMessage(TravelSystem.getPrefix()+ChatColor.GRAY+"Andere Spieler können jetzt beitreten.");
		this.closeLater();
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		HandlerList.unregisterAll(this);
	}

	@Override
	public Inventory getBottomInventory() {
		return this.playerInventory;
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
	
	private void embarkNow(){
		this.closeLater();
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			this.start.getJourney().embarkNow();
		}, 2L);
	}
	
	private void open(){
		this.player.openInventory(this);
	}
	
	private void closeLater(){
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			close();
		}, 1L);
	}
	
	private static HashMap<Integer,ItemStack> createItems(TravelStation start, Collection<TravelStation> stations){
		Collection<DestinationWorld> worlds = groupByWorld(start, stations);
		HashMap<Integer,ItemStack> items = new HashMap<Integer,ItemStack>();
		int row = 0;
		int column = 0;
		TravelStation currentSelected = start.getJourney()!=null ? start.getJourney().getDestination() : null;
		for(DestinationWorld world : worlds){
			column = 0;
			items.put(row * 9 + column, world.getItem());
			column++;
			for(TravelStation station : world.getStations()){
				if(station==start || !station.isSetupComplete()) continue;
				ItemStack itemStack = (currentSelected!=station) ? station.getDestinationSelectionItem(start) : station.getEmbarkNowItem(start);
				if(itemStack==null) continue;
				
				items.put(row * 9 + column, itemStack);
				column++;
				if(column>=8){
					column = 1;
					row++;
				}
			}
			row++;
		}
		return items;
	}
	
	private static Collection<DestinationWorld> groupByWorld(TravelStation start, Collection<TravelStation> stations){
		HashMap<World,DestinationWorld> worldMap = new HashMap<World,DestinationWorld>();
		TravelStation destination = start.getJourney()!=null ? start.getJourney().getDestination() : null;
		for(TravelStation station : stations){
			if(station==start || (destination!=null && station!=destination)) continue;
			World world = station.getWorld();
			if(!worldMap.containsKey(world)){
				worldMap.put(world, new DestinationWorld(world));
			}
			DestinationWorld destinationWorld = worldMap.get(world);
			destinationWorld.addStation(station);
		}
		List<DestinationWorld> worlds = new ArrayList<DestinationWorld>(worldMap.values());
		worlds.sort(new Comparator<DestinationWorld>() {
	         @Override
	         public int compare(DestinationWorld a, DestinationWorld b) {
	                 return a.getStationCount() - b.getStationCount();
	         }
	     });
		return worlds;
	}
	
	private void createCancelJourneyItem(){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setMaterial(Material.BARRIER);
		itemBuilder.setDisplayName(ChatColor.RED+"Reise abbrechen");
		itemBuilder.setAmount(1);
		ItemStack result = itemBuilder.build();
		inventory.setItem(8, result);
	}
	
	public static DestinationSelectionView open(Player player, TravelStation start){
		DestinationSelectionView editor;
		if(TravelStations.getAll().size()==0){
			player.sendMessage(TravelSystem.getPrefix()+"Es wurde keine Stationen erstellt.");
			return null;
		}
		HashMap<Integer,ItemStack> items = createItems(start, TravelStations.getAll());
		if(items.size()==0) return null;
		editor = new DestinationSelectionView(player, start, items, "Zielort auswählen");
		Bukkit.getPluginManager().registerEvents(editor, TravelSystem.getInstance());
		editor.open();
		return editor;
	}
}
