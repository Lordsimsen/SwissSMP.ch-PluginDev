package ch.swisssmp.dungeongenerator;

import java.util.Arrays;
import java.util.HashMap;

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

import ch.swisssmp.customitems.CustomItemBuilder;

public class GeneratorEditorView extends InventoryView implements Listener{
	private static HashMap<Integer,Inventory> activeEditorViews = new HashMap<Integer,Inventory>();
	
	private final DungeonGenerator generator;
	private final Player player;
	private final Inventory inventory;
	
	private GeneratorEditorView(DungeonGenerator generator, Inventory inventory, Player player){
		this.generator = generator;
		this.player = player;
		this.inventory = inventory;
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=this.inventory) return;
		ItemStack itemStack = this.inventory.getItem(event.getSlot());
		event.setCancelled(true);
		if(itemStack==null) return;
		int change;
		switch(event.getClick()){
		case LEFT: change = 1; break;
		case SHIFT_LEFT: change = 5; break;
		case RIGHT: change = -1; break;
		case SHIFT_RIGHT: change = -5; break;
		case MIDDLE: change = -(this.getDefault(event.getSlot())-itemStack.getAmount()); break;
		default: return;
		}
		itemStack.setAmount(Math.max(1, itemStack.getAmount()+change));
		switch(event.getSlot()){
		case 0: generator.setDefaultSize(itemStack.getAmount()*10);break;
		case 1: generator.setCorridorLength(itemStack.getAmount());break;
		case 2: generator.setChamberCount(itemStack.getAmount());break;
		case 3: generator.setBranchDensity(itemStack.getAmount());break;
		default: return;
		}
	}
	
	@EventHandler
	private void onInventoryDrag(InventoryDragEvent event){
		if(event.getView()!=this) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		if(event.getView()!=this) return;
		this.generator.saveSettings();
		HandlerList.unregisterAll(this);
		GeneratorEditorView.activeEditorViews.remove(this.generator.getId());
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
	
	private int getDefault(int slot){
		switch(slot){
		case 0: return 10; //default size (amount of parts per generation) divided by 10
		case 1: return 10; //corridor length between chambers
		case 2: return 5; //chambers per floor
		case 3: return 3; //amount of branches
		default: return 1; //unkown setting
		}
	}
	
	private static ItemStack createDefaultSizeItem(int amount){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(amount);
		itemBuilder.setMaterial(Material.COBBLESTONE);
		itemBuilder.setDisplayName(ChatColor.AQUA+"Gesamtgrösse");
		itemBuilder.setLore(Arrays.asList(
			ChatColor.RESET+"Anzahl Teile bei",
			ChatColor.RESET+"der Generierung"
		));
		return itemBuilder.build();
	}
	
	private static ItemStack createCorridorLengthItem(int amount){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(amount);
		itemBuilder.setMaterial(PartType.CORRIDOR.getMaterial());
		itemBuilder.setDisplayName(ChatColor.AQUA+"Korridore");
		itemBuilder.setLore(Arrays.asList(
			ChatColor.RESET+"Länge der Gänge",
			ChatColor.RESET+"Zwischen Kammern"
		));
		return itemBuilder.build();
	}
	
	private static ItemStack createChamberCountItem(int amount){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(amount);
		itemBuilder.setMaterial(PartType.CHAMBER.getMaterial());
		itemBuilder.setDisplayName(ChatColor.AQUA+"Kammern");
		itemBuilder.setLore(Arrays.asList(
			ChatColor.RESET+"Anzahl Kammern pro Ebene"
		));
		return itemBuilder.build();
	}
	
	private static ItemStack createBranchDensityItem(int amount){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setAmount(amount);
		itemBuilder.setMaterial(PartType.FORK.getMaterial());
		itemBuilder.setDisplayName(ChatColor.AQUA+"Verzweigungen");
		itemBuilder.setLore(Arrays.asList(
			ChatColor.RESET+"Anzahl Verzweigungen pro Ebene"
		));
		return itemBuilder.build();
	}
	
	private static void createEditorItems(DungeonGenerator generator, Inventory inventory){
		inventory.setItem(0, createDefaultSizeItem(generator.getDefaultSize()/10));
		inventory.setItem(1, createCorridorLengthItem(generator.getCorridorLength()));
		inventory.setItem(2, createChamberCountItem(generator.getChamberCount()));
		inventory.setItem(3, createBranchDensityItem(generator.getBranchDensity()));
	}
	
	protected static GeneratorEditorView open(DungeonGenerator generator, Player player){
		Inventory inventory;
		if(GeneratorEditorView.activeEditorViews.containsKey(generator.getId())){
			inventory = GeneratorEditorView.activeEditorViews.get(generator.getId());
		}
		else{
			inventory = Bukkit.createInventory(null, 9, generator.getName());
			inventory.setMaxStackSize(1000);
			GeneratorEditorView.createEditorItems(generator, inventory);
		}
		GeneratorEditorView result = new GeneratorEditorView(generator, inventory, player);
		Bukkit.getPluginManager().registerEvents(result, DungeonGeneratorPlugin.getInstance());
		player.openInventory(result);
		GeneratorEditorView.activeEditorViews.put(generator.getId(), inventory);
		return result;
	}
}
