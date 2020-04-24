package ch.swisssmp.zones;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.editor.ZoneEditor;
import ch.swisssmp.zones.editor.ZoneEditorView;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class EventListener implements Listener {
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("zones");
	}
	
	@EventHandler
	private void onItemBuilderCreate(CreateCustomItemBuilderEvent event) {
		if(!event.getConfigurationSection().contains("ZoneType")) {
			return;
		}
		
		CustomItemBuilder itemBuilder = event.getCustomItemBuilder();
		ZoneType zoneType = ZoneType.get(event.getConfigurationSection().getString("zone_type"));
		itemBuilder.addComponent((ItemStack itemStack)->{
			zoneType.apply(itemStack);
		});
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		ZoneEditor editor = ZoneEditor.get(event.getPlayer());
		if(editor!=null) editor.cancel();
	}
	
	@EventHandler
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		ZoneEditor editor = ZoneEditor.get(event.getPlayer());
		if(editor!=null) editor.cancel();
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode()==GameMode.SPECTATOR) return;
		if(this.onCartographyTableInteract(event)) return;
		if(this.onZoneEditorClick(event)) return;
	}
	
	@EventHandler
	private void onPrepareCraft(PrepareItemCraftEvent event){
		CraftingInventory inventory = event.getInventory();
		if(inventory.getResult()==null) return;
		ZoneInfo zoneInfo = ZoneInfo.get(inventory.getResult());
		if(zoneInfo==null) return;
		String permission = "zones."+zoneInfo.getZoneType().getPermissionIdentifier()+".craft";
		if(event.getView().getPlayer().hasPermission(permission)) return;
		inventory.setResult(null);
	}
	
	@EventHandler
	private void onPrepareAnvilCraft(PrepareAnvilEvent event){
		ItemStack itemStack = event.getResult();
		if(itemStack==null) return;
		ZoneInfo zoneInfo = ZoneInfo.get(itemStack);
		if(zoneInfo==null) return;
		zoneInfo.setName(event.getInventory().getRenameText());
		zoneInfo.apply(itemStack);
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		ZoneContainer.load(event.getWorld());
	}
	
	@EventHandler
	private void onWorldSave(WorldSaveEvent event){
		ZoneContainer container = ZoneContainer.get(event.getWorld());
		if(container!=null) container.save();
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		ZoneContainer.unload(event.getWorld());
	}
	
	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event){
		ZoneUtil.updateZoneInfos(event.getInventory());
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		ZoneUtil.updateZoneInfos(event.getPlayer().getInventory());
	}
	
	private boolean onZoneEditorClick(PlayerInteractEvent event){
		if(event.getItem()==null) return false;
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return false;
		Block block = event.getClickedBlock();
		if(block!=null){
			if(block.getType().isInteractable() && !event.getPlayer().isSneaking()) return false;
		}
		Player player = event.getPlayer();
		if(player.getOpenInventory()!=null && player.getOpenInventory().getType()!=InventoryType.CRAFTING && player.getOpenInventory().getType()!=InventoryType.CREATIVE){
			return false;
		}
		if(ZoneEditor.get(player)!=null) return false;
		ZoneInfo zoneInfo = ZoneInfo.get(event.getItem());
		if(zoneInfo==null) return false;
		String permission = "zones."+zoneInfo.getZoneType().getPermissionIdentifier()+".use";
		if(!player.hasPermission(permission)){
			if(zoneInfo.getZoneType()==ZoneType.GENERIC){
				return true;
			}
			SwissSMPler.get(player).sendActionBar("Noch nicht freigeschaltet.");
			return true;
		}
		ZoneEditorView.open(player, event.getItem(), zoneInfo);
		event.setCancelled(true);
		return true;
	}
	
	private boolean onCartographyTableInteract(PlayerInteractEvent event) {
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType()!=Material.CARTOGRAPHY_TABLE) return false;
		if(event.getItem()==null) return false;
		//TODO add code
		return true;
	}
	
	/*
	private boolean onDrawingBoardInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.LEFT_CLICK_BLOCK) return false;
		Player player = event.getPlayer();
		if(player.getOpenInventory()!=null && player.getOpenInventory().getType()!=InventoryType.CRAFTING && player.getOpenInventory().getType()!=InventoryType.CREATIVE){
			return false;
		}
		Block block = event.getClickedBlock();
		if(block==null || block.getType()!=Material.BARRIER) return false;
		//System.out.println("Check if its a board");
		DrawingBoard board = DrawingBoard.get(block);
		if(board==null){
			//System.out.println("Its not a board");
			return false;
		}
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(event.getPlayer().isSneaking() && event.getItem()!=null){
				return false;
			}
			//System.out.println("Attempt to use the board");
			if(!event.getPlayer().hasPermission("zones.drawingboard.use")){
				SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.WHITE+"Noch nicht freigeschaltet.");
				return true;
			}
			DrawingBoardView.open(event.getPlayer(), event.getItem());
			event.setCancelled(true);
			System.out.println("Open DrawingBoard");
			return true;
		}
		else if(event.getAction()==Action.LEFT_CLICK_BLOCK){
			//System.out.println("Break the board");
			if(event.getPlayer().getGameMode()==GameMode.CREATIVE){
				board.remove();
			}
			else{
				board.breakNaturally();
			}
			event.setCancelled(true);
			return true;
		}
		return false;
	}
	
	private boolean onDrawingBoardPlace(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK || event.getClickedBlock()==null) return false;
		ItemStack item = event.getItem();
		if(item==null) return false;
		String customEnum = CustomItems.getCustomEnum(item);
		if(customEnum==null || !customEnum.equals("DRAWING_BOARD")) return false;
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		Location location = block.getLocation().add(0.5,0,0.5);
		location.setYaw(event.getPlayer().getLocation().getYaw());
		DrawingBoard board = DrawingBoard.create(location, item);
		if(board!=null && event.getPlayer().getGameMode()!=GameMode.CREATIVE){
			event.getPlayer().getInventory().remove(item);
		}
		event.setCancelled(true);
		return true;
	}*/
}
