package ch.swisssmp.zones;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.editor.ActionResult;
import ch.swisssmp.zones.zoneinfos.PolygonZoneInfo;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class ZoneEditor implements Runnable, Listener {
	
	private static HashMap<Player, ZoneEditor> editors = new HashMap<Player, ZoneEditor>();
	
	private final double maxDistanceSquared = 2500;
	private final double animationSpeed = 0.2; //Blocks Per Frame (20 Frames / Second)
	private final double averageMarkerDistance = 15;

	private final World world;
	private final Player player;
	private ItemStack itemStack;
	
	private final ZoneInfo zoneInfo;
	
	private List<Location> points;
	private List<Edge> edges;
	private float segmentLength;
	private int segmentCount;
	
	private BukkitTask task;
	private long t;
	private long hintCooldown = 0;
	
	private ZoneEditor(Player player, ItemStack itemStack, ZoneInfo zoneInfo){
		this.world = player.getWorld();
		this.player = player;
		this.itemStack = itemStack;
		
		this.zoneInfo = zoneInfo;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	@Override
	public void run() {
		if(edges==null || edges.size()==0) return;
		markRegion(edges);
		markPoints(points);
		showHint();
		t++;
	}
	
	@EventHandler
	private void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.getPlayer()!=this.player) return;
		event.setCancelled(true);
		if(itemStack.getType()==Material.AIR){
			itemStack = event.getItemDrop().getItemStack();
		}
		this.complete();
	}
	
	@EventHandler
	private void onPlayerOpenInventory(InventoryOpenEvent event){
		if(event.getPlayer()!=this.player) return;
		event.setCancelled(true);
		this.complete();
	}
	
	@EventHandler(ignoreCancelled=false)
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer()!=this.player || event.getHand()!=EquipmentSlot.HAND) return;
		if(player.getGameMode()!=GameMode.CREATIVE){
			String regionId = ZoneInfo.getId(event.getItem());
			if(!this.zoneInfo.getId().equals(regionId)) return;
			ItemStack feather = event.getHand()==EquipmentSlot.HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
			if(feather==null || feather.getType()!=Material.FEATHER) return;
		}
		Location location = (event.getAction()==Action.LEFT_CLICK_BLOCK || event.getAction()==Action.RIGHT_CLICK_BLOCK) 
				? event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5)
				: player.getEyeLocation();
		ItemStack cost = this.zoneInfo.getCost(event.getAction());
		if(cost!=null){
			int redstoneIndex = player.getInventory().first(cost.getType());
			ItemStack item = redstoneIndex>=0 ? player.getInventory().getItem(redstoneIndex) : null;
			if(item==null || item.getAmount()<cost.getAmount()){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Du brauchst "+cost.getType().name()+".");
				return;
			}
			
			item.setAmount(item.getAmount()-cost.getAmount());
		}
		
		event.setCancelled(true);
		ActionResult result = this.zoneInfo.edit(location, event.getAction());
		if(result==ActionResult.NONE) return;
		SwissSMPler.get(event.getPlayer()).sendActionBar(result.getFeedback());
		this.recalculate(this.zoneInfo.getPoints());
	}
	
	private void start(){
		editors.put(player, this);
		if(zoneInfo.getId()==null){
			zoneInfo.createId(this.player.getWorld());
			zoneInfo.addMember(this.player.getUniqueId(), MemberRole.OWNER);
		}
		List<Location> locations = zoneInfo.getPoints();
		this.recalculate(locations);
		Bukkit.getPluginManager().registerEvents(this, ZonesPlugin.getInstance());
		this.task = Bukkit.getScheduler().runTaskTimer(ZonesPlugin.getInstance(), this, 0, 1);

		System.out.println("Kontrolle 1: "+ItemUtil.getData(itemStack).asString());
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(Arrays.asList(ChatColor.YELLOW+"In Bearbeitung"));
		itemStack.setItemMeta(itemMeta);

		System.out.println("Kontrolle 2: "+ItemUtil.getData(itemStack).asString());
	}
	
	public ProtectedRegion complete(){
		ProtectedRegion result = this.zoneInfo.saveChanges();
		ZoneContainer container = ZoneContainer.get(world);
		if(container!=null){
			System.out.println("Container is not null.");
			container.add(zoneInfo);
			container.save();
		}
		finish();
		System.out.println("Name: "+zoneInfo.getName());
		Bukkit.getScheduler().runTaskLater(ZonesPlugin.getInstance(), ()->{
			relinkItemStack();
			zoneInfo.apply(itemStack);
			ZoneEditorView.open(this.player, this.itemStack, this.zoneInfo);
		}, 2L);
		return result;
	}
	
	public void cancel(){
		finish();
	}
	
	private void finish(){
		editors.remove(player);
		if(task!=null) task.cancel();
		HandlerList.unregisterAll(this);
	}
	
	private void relinkItemStack(){
		for(ItemStack itemStack : player.getInventory()){
			if(itemStack==null) continue;
			String customEnum = CustomItems.getCustomEnum(itemStack);
			if(customEnum==null || !itemStack.equals(this.itemStack)) continue;
			this.itemStack = itemStack;
			return;
		}
		System.out.println("ItemStack not found!!!");
	}
	
	private void markPoints(List<Location> locations){
		Location playerLocation = player.getLocation();
		DustOptions pointDust = new DustOptions(Color.AQUA, 1);
		for(int i = 0; i < locations.size(); i++){
			Location location = locations.get(i);
			if(location==null || location.getWorld()==null || location.distanceSquared(playerLocation)>maxDistanceSquared) continue;
			world.spawnParticle(Particle.REDSTONE, location, 1, pointDust);
		}
	}
	
	private void markRegion(List<Edge> edges){
		DustOptions lineDust = new DustOptions(this.zoneInfo instanceof PolygonZoneInfo ? Color.YELLOW : Color.RED, 1);
		Location playerLocation = player.getLocation();
		for(int i = 0; i < segmentCount; i++){
			double offset = i*segmentLength + t * animationSpeed;
			Edge edge = edges.get(0);
			int edgeIndex = 0;
			while(offset>edge.getLength()){
				offset-=edge.getLength();
				edgeIndex++;
				if(edgeIndex>=edges.size())
					edgeIndex = 0;
				edge = edges.get(edgeIndex);
			}
			Location location = edge.step(offset);
			if(location.distanceSquared(playerLocation)>maxDistanceSquared) continue;
			world.spawnParticle(Particle.REDSTONE, location, 1, lineDust);
		}
	}
	
	private void showHint(){
		if(hintCooldown>0){
			hintCooldown--;
			return;
		}
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		if(!this.itemStack.isSimilar(itemStack)){
			SwissSMPler.get(player).sendActionBar(ChatColor.GOLD+"Zonenplan in die Hand nehmen");
			return;
		}
		SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW+zoneInfo.getZoneType().getHint());
		hintCooldown = 100;
	}

	private void recalculate(List<Location> locations){
		segmentCount = 0;
		if(locations.size()<2) return;
		this.points = locations;
		if(zoneInfo.getRegionType()==RegionType.POLYGON){
			this.edges = ZoneUtil.buildPolygonEdges(locations);
		}
		else{
			this.edges = ZoneUtil.buildBoxEdges(locations.get(0), locations.get(1));
		}
		float length = 0;
		for(Edge edge : this.edges){
			length+=edge.getLength();
		}
		this.segmentCount = Mathf.ceilToInt(length/averageMarkerDistance);
		this.segmentLength = length / segmentCount;
	}
	
	public static ZoneEditor start(Player player, ItemStack itemStack, ZoneInfo zoneInfo){
		ZoneEditor result = new ZoneEditor(player, itemStack, zoneInfo);
		result.start();
		return result;
	}
	
	public static ZoneEditor get(Player player){
		return editors.get(player);
	}
}
