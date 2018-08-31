package ch.swisssmp.adventuredungeons;

import java.util.Arrays;

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Position;

public class DungeonEditorView extends InventoryView implements Listener{
	private final Dungeon dungeon;
	private final Player player;
	private final Inventory inventory;
	
	private String name;
	private Position lobbyJoin;
	private Position lobbyLeave;
	
	private DungeonEditorView(Dungeon dungeon, Player player){
		this.dungeon = dungeon;
		this.player = player;
		this.inventory = Bukkit.createInventory(null, 9, dungeon.getName());
		
		this.name = dungeon.getName();
		this.lobbyJoin = dungeon.getLobbyJoin();
		this.lobbyLeave = dungeon.getLobbyLeave();
		this.createEditorItems();
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent event){
		if(event.getView()!=this || event.getClickedInventory()!=this.inventory) return;
		ItemStack itemStack = this.inventory.getItem(event.getSlot());
		if(itemStack==null){
			event.setCancelled(true);
			return;
		}
		ItemStack cursor = event.getCursor();
		if(cursor==null || cursor.getType()==Material.AIR || cursor==itemStack){
			System.out.println("Cursor is null.");
			ItemManager.refillInventorySlot(inventory, event.getSlot(), itemStack.clone());
			switch(event.getSlot()){
			case 0: itemStack.setItemMeta(this.createNamePickItem(this.name).getItemMeta());break;
			case 1: itemStack.setItemMeta(this.createLobbyJoinPickItem(this.lobbyJoin).getItemMeta());break;
			case 2: itemStack.setItemMeta(this.createLobbyLeavePickItem(this.lobbyLeave).getItemMeta());break;
			default: event.setCancelled(true);return;
			}
		}
		else{
			System.out.println("Cursor is not null");
			if(cursor.getType()!=itemStack.getType()){
				event.setCancelled(true); 
				return;
			}
			ItemStack template;
			switch(event.getSlot()){
			case 0:{
				if(!cursor.hasItemMeta() || !cursor.getItemMeta().hasDisplayName()) return;
				String name = cursor.getItemMeta().getDisplayName();
				this.name = name;
				template = this.createNameItem(name);
				break;
			}
			case 1:{
				Position position = ItemUtil.getPosition(cursor, "position");
				if(position==null){event.setCancelled(true); return;}
				this.lobbyJoin = position;
				template = this.createLobbyJoinItem(position);
				break;
			}
			case 2:{
				Position position = ItemUtil.getPosition(cursor, "position");
				if(position==null){event.setCancelled(true); return;}
				this.lobbyLeave = position;
				template = this.createLobbyLeaveItem(position);
				break;
			}
			default: event.setCancelled(true);return;
			}
			cursor.setItemMeta(template.getItemMeta());
			cursor.setDurability(template.getDurability());
			itemStack.setAmount(0);
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
		this.dungeon.setName(this.name);
		this.dungeon.setLobbyJoin(this.lobbyJoin);
		this.dungeon.setLobbyLeave(this.lobbyLeave);
		this.dungeon.saveSettings();
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
	
	private void createEditorItems(){
		this.inventory.setItem(0, this.createNameItem(this.name));
		this.inventory.setItem(1, this.createLobbyJoinItem(this.lobbyJoin));
		this.inventory.setItem(2, this.createLobbyLeaveItem(this.lobbyLeave));
	}
	
	private ItemStack createNameItem(String name){
		CustomItemBuilder itemBuilder = new CustomItemBuilder();
		itemBuilder.setDisplayName(name);
		itemBuilder.setLore(Arrays.asList(
				"Ändere den Namen",
				"dieses Dungeons"
				));
		itemBuilder.setMaterial(Material.NAME_TAG);
		itemBuilder.setAmount(1);
		return itemBuilder.build();
	}
	
	private ItemStack createLobbyJoinItem(Position position){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("MARKER_BLUE");
		itemBuilder.setDisplayName(ChatColor.WHITE+"Portal (Dungeon)");
		itemBuilder.setAmount(1);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(Arrays.asList(
				ChatColor.WHITE+"X: "+Mathf.roundToInt(position.getX())+", "+
				ChatColor.WHITE+"Y: "+Mathf.roundToInt(position.getY())+", "+
				ChatColor.WHITE+"Z: "+Mathf.roundToInt(position.getZ()),
				"Spieler werden beim Betreten",
				"zu diesem Punkt teleportiert."
				));
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, "dungeon_tool", "POSITION");
		ItemUtil.setPosition(itemStack, "position", position);
		return itemStack;
	}
	
	private ItemStack createLobbyLeaveItem(Position position){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("MARKER_RED");
		itemBuilder.setDisplayName(ChatColor.WHITE+"Portal (Welt)");
		itemBuilder.setAmount(1);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setLore(Arrays.asList(
				ChatColor.WHITE+"X: "+Mathf.roundToInt(position.getX())+", "+
				ChatColor.WHITE+"Y: "+Mathf.roundToInt(position.getY())+", "+
				ChatColor.WHITE+"Z: "+Mathf.roundToInt(position.getZ()),
				"Spieler werden beim Verlassen",
				"zu diesem Punkt teleportiert."
				));
		ItemStack itemStack = itemBuilder.build();
		ItemUtil.setString(itemStack, "dungeon_tool", "POSITION");
		ItemUtil.setPosition(itemStack, "position", position);
		return itemStack;
	}
	
	private ItemStack createNamePickItem(String name){
		ItemStack itemStack = this.createNameItem(name);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(Arrays.asList(
				"Benenne den Dungeon"
				));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	private ItemStack createLobbyJoinPickItem(Position position){
		ItemStack itemStack = this.createLobbyJoinItem(position);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(ItemManager.getPositionTokenLore(position));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	private ItemStack createLobbyLeavePickItem(Position position){
		ItemStack itemStack = this.createLobbyLeaveItem(position);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(ItemManager.getPositionTokenLore(position));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	protected static DungeonEditorView open(Dungeon dungeon, Player player){
		DungeonEditorView result = new DungeonEditorView(dungeon, player);
		Bukkit.getPluginManager().registerEvents(result, AdventureDungeons.getInstance());
		player.openInventory(result);
		return result;
	}
}
