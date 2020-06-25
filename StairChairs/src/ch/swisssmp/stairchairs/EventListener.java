package ch.swisssmp.stairchairs;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener {
	
	private static final String[] WoodTypes = {"oak","birch","spruce","jungle","dark_oak","acacia"};
	
	private static final boolean DebugOutput = false;
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) {
			debug("Not Mainhand");
			return;
		}
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) {
			debug("Not RightClickBlock");
			return;
		}
		Player player = event.getPlayer();
		if((player.getGameMode()!=GameMode.SURVIVAL && player.getGameMode()!=GameMode.CREATIVE) || player.getVehicle()!=null) {
			debug("Wrong GameMode or riding a vehicle");
			return;
		}
		if(event.getItem()!=null) {
			debug("Item in Hand");
			return;
		}
		Block block = event.getClickedBlock();
		if(isObstructed(block.getRelative(BlockFace.UP))) {
			debug("Obstructed");
			return;
		}
		if(!isChair(block)) {
			debug("Not a chair");
			return;
		}
		if(StairChairs.playerMap.containsKey(block)) {
			debug("Occupied");
			return;
		}
		Location location = block.getLocation();
		location.add(0.5, 0, 0.5);
		if(player.getLocation().distanceSquared(location)>1.75){
			debug("Out of range");
			return;
		}
		location.add(0, 0.30, 0);
		BlockData data = block.getBlockData();
		if(data instanceof Stairs){
			Stairs stairs = (Stairs) data;
			if(stairs.getHalf()==Half.TOP) {
				debug("Stairs upside down");
				return;
			}
			double offset = 0.2;
			switch(stairs.getFacing()){
			case SOUTH:
				location = location.add(0, 0, -offset);
				location.setDirection(new Vector(0,0,-1));
				break;
			case WEST:
				location = location.add(offset, 0, 0);
				location.setDirection(new Vector(1,0,0));
				break;
			case NORTH:
				location = location.add(0, 0, offset);
				location.setDirection(new Vector(0,0,1));
				break;
			case EAST:
				location = location.add(-offset, 0, 0);
				location.setDirection(new Vector(-1,0,0));
				break;
			default:
				debug("What is this direction: "+stairs.getFacing());
				return;
			}
		}
		else if(data instanceof Slab){
			Vector direction = new Vector(0, 0, 0);
			Block north = block.getRelative(BlockFace.NORTH);
			Block east = block.getRelative(BlockFace.EAST);
			Block south = block.getRelative(BlockFace.SOUTH);
			Block west = block.getRelative(BlockFace.WEST);
			if(isTable(north)){
				direction.add(new Vector(0,0,-1));
			}
			else if(north.getType()!=Material.AIR){
				direction.add(new Vector(0.001,0,1));
			}
			if(isTable(east)){
				direction.add(new Vector(1,0,0));
			}
			else if(east.getType()!=Material.AIR){
				direction.add(new Vector(-1,0,0.001));
			}
			if(isTable(south)){
				direction.add(new Vector(0,0,1));
			}
			else if(south.getType()!=Material.AIR){
				direction.add(new Vector(0.001,0,-1));
			}
			if(isTable(west)){
				direction.add(new Vector(-1,0,0));
			}
			else if(west.getType()!=Material.AIR){
				direction.add(new Vector(1,0,0.001));
			}
			if(direction.lengthSquared()!=0){
				location.setDirection(direction);
			}
		}
		else {
			debug("Neither stair nor slab");
			return;
		}
		StairChairs.locationMap.put(player, player.getLocation().clone());
		final Location chairLocation = location.clone();
		final Location soundLocation = location.clone().add(0, 1, 0);
		ArmorStand armorStand = location.getWorld().spawn(chairLocation, ArmorStand.class, (spawned)->{
			spawned.setInvulnerable(true);
			spawned.setVisible(false);
			spawned.setGravity(false);
			spawned.setCustomName("§cStairChair");
			spawned.setCustomNameVisible(false);
			spawned.setMarker(true);
		});
		armorStand.addPassenger(player);
		soundLocation.getWorld().playSound(soundLocation, isWood(block) ? Sound.BLOCK_WOOD_STEP : Sound.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1, 2f);
		StairChairs.playerMap.put(block, player);
		StairChairs.entityMap.put(armorStand, block);
		debug(player.getName()+" sits!");
	}
	
	private static boolean isWood(Block block) {
		String typeString = block.getType().toString().toLowerCase();
		for(String woodType : WoodTypes) {
			if(typeString.contains(woodType)) return true;
		}
		return false;
	}
	
	private static boolean isChair(Block block){
		BlockData blockData = block.getBlockData();
		if(blockData instanceof Stairs){
			return ((Stairs)blockData).getHalf()!=Half.TOP;
		}
		else if(blockData instanceof Slab){
			return ((Slab)blockData).getType()!=Type.TOP;
		}
		return false;
	}
	
	private static boolean isObstructed(Block block){
		Material material = block.getType();
		return  !(material == Material.AIR ||
				material == Material.ACACIA_WALL_SIGN ||
				material == Material.BIRCH_WALL_SIGN ||
				material == Material.DARK_OAK_WALL_SIGN ||
				material == Material.JUNGLE_WALL_SIGN ||
				material == Material.OAK_WALL_SIGN ||
				material == Material.SPRUCE_WALL_SIGN ||
				material == Material.ITEM_FRAME ||
				material == Material.PAINTING ||
				material == Material.TORCH ||
				material == Material.REDSTONE_TORCH ||
				material == Material.REDSTONE_WALL_TORCH);
	}
	
	private static boolean isTable(Block block){
		BlockData blockData = block.getBlockData();
		if(blockData instanceof Stairs){
			return ((Stairs)blockData).getHalf()!=Half.TOP;
		}
		else if(blockData instanceof Slab){
			return ((Slab)blockData).getType()!=Type.TOP;
		}
		else if(blockData instanceof TrapDoor){
			return ((TrapDoor)blockData).getHalf()!=Half.TOP && !((TrapDoor)blockData).isOpen();
		}
		else if(block.getType().toString().contains("FENCE")) return true;
		else if(block.getType()==Material.COBBLESTONE_WALL) return true;
		else return false;
	}
	
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock().getRelative(BlockFace.DOWN);
		if(block==null) return;
		if(!StairChairs.playerMap.containsKey(block)) return;
		Player player = StairChairs.playerMap.get(event.getBlock());
		Entity entity = player.getVehicle();
		entity.eject();
		StairChairs.entityMap.remove(entity);
		StairChairs.playerMap.remove(event.getBlock());
		entity.remove();
	}
	@EventHandler
	private void onBlockChange(BlockBurnEvent event){
		if(!StairChairs.playerMap.containsKey(event.getBlock()))return;
		Player player = StairChairs.playerMap.get(event.getBlock());
		Entity entity = player.getVehicle();
		entity.eject();
		StairChairs.entityMap.remove(entity);
		StairChairs.playerMap.remove(event.getBlock());
		entity.remove();
	}
	@EventHandler
	private void onBlockBreak(BlockBreakEvent event){
		if(!StairChairs.playerMap.containsKey(event.getBlock()))return;
		Player player = StairChairs.playerMap.get(event.getBlock());
		Entity entity = player.getVehicle();
		entity.eject();
		StairChairs.entityMap.remove(entity);
		StairChairs.playerMap.remove(event.getBlock());
		entity.remove();
		
	}
	@EventHandler
	private void onEntityDismount(EntityDismountEvent event){
		Entity dismounted = event.getDismounted();
		if(dismounted.getType()!=EntityType.ARMOR_STAND) return;
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if(StairChairs.locationMap.containsKey(player)){
				Location location = StairChairs.locationMap.get(player);
				StairChairs.locationMap.remove(player);
				Bukkit.getScheduler().runTaskLater(StairChairs.plugin, new Runnable(){
					public void run(){
						location.setDirection(player.getLocation().getDirection());
						player.teleport(location);
					}
				}, 1L);
			}
		}
		if(!StairChairs.entityMap.containsKey(dismounted)){
			return;
		}
		Block block = StairChairs.entityMap.get(dismounted);
		StairChairs.entityMap.remove(dismounted);
		StairChairs.playerMap.remove(block);
		dismounted.remove();
	}
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(StairChairs.plugin, new Runnable(){
			public void run(){
				StairChairs.removeUnusedArmorStands(event.getPlayer().getNearbyEntities(10, 10, 10));
			}
		}, 20L);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(player.getVehicle()==null) return;
		if(player.getVehicle().getType()!=EntityType.ARMOR_STAND) return;
		ArmorStand armorStand = (ArmorStand)player.getVehicle();
		if(armorStand.getCustomName()==null) return;
		if(!armorStand.getCustomName().equals("§cStairChair")) return;
		Block block = StairChairs.entityMap.get(armorStand);
		StairChairs.playerMap.remove(block);
		StairChairs.entityMap.remove(armorStand);
		armorStand.eject();
		armorStand.remove();
	}
	
	private static void debug(String s) {
		if(DebugOutput) Bukkit.getLogger().info("["+StairChairs.pdfFile.getName()+"] "+s);
	}
}
