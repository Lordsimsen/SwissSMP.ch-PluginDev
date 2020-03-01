package ch.swisssmp.travel;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.Mathf;

public class TravelStationsEditor implements Listener {

	private final Player player;
	private final Inventory inventory;
	
	private InventoryView view;
	
	private TravelStationsEditor(Player player, World world){
		this(player, TravelStations.get(world), "Stationen in "+world.getName().replace('_', ' '));
	}
	
	private TravelStationsEditor(Player player, Collection<TravelStation> stations, String label){
		this.player = player;

		int cellCount = Mathf.ceilToInt(stations.size() / 9f)*9;
		this.inventory = Bukkit.createInventory(null, cellCount, label);
		this.createItems(stations);
	}
	
	private void createItems(Collection<TravelStation> stations){
		for(TravelStation station : stations){
			ItemStack tokenStack = station.getTokenStack();
			if(tokenStack==null) continue;
			this.inventory.addItem(tokenStack);
		}
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this.view) return;
		HandlerList.unregisterAll(this);
	}

	public HumanEntity getPlayer() {
		return this.player;
	}
	
	private void open(){
		this.view = this.player.openInventory(this.inventory);
	}
	
	public static TravelStationsEditor open(Player player, boolean showAll){
		TravelStationsEditor editor;
		if(showAll){
			if(TravelStations.getAll().size()==0){
				player.sendMessage(TravelSystem.getPrefix()+"Es wurde noch keine Station erstellt. Verwende /travelstation erstelle [Name] um eine Station zu erstellen.");
				return null;
			}
			editor = new TravelStationsEditor(player, TravelStations.getAll(), "Alle Stationen");
		}
		else{
			Collection<TravelStation> stations = TravelStations.get(player.getWorld());
			if(stations.size()==0){
				player.sendMessage(TravelSystem.getPrefix()+"In dieser Welt gibt es keine Stationen. Verwende /travelstations um alle geladenen Stationen anzuzeigen.");
				return null;
			}
			editor = new TravelStationsEditor(player,stations,"Stationen in "+player.getWorld().getName());
		}
		Bukkit.getPluginManager().registerEvents(editor, TravelSystem.getInstance());
		editor.open();
		return editor;
	}
}
