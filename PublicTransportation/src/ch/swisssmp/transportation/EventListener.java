package ch.swisssmp.transportation;

import java.util.List;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(event.getHand()!=EquipmentSlot.HAND) return;
		if(event.getItem()!=null) return;
		Block block = event.getClickedBlock();
		if(block.getType()!=Material.ACTIVATOR_RAIL) return;
		if(block.getRelative(BlockFace.DOWN).getType()!=Material.IRON_BLOCK) return;
		World world = block.getWorld();
		BlockVector3 vector = BlockVector3.at(block.getX(), block.getY(), block.getZ());
		ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getApplicableRegions(vector);
		List<String> stations = PublicTransportation.trainStations.get(block.getWorld());
		boolean enterMinecart = false;
		for(ProtectedRegion region : regions){
			if(stations.contains(region.getId())){
				enterMinecart = true;
			}
		}
		if(enterMinecart){
			Minecart minecart = (Minecart)block.getWorld().spawnEntity(new Location(block.getWorld(), block.getLocation().getX()+0.5f, block.getLocation().getY(), block.getLocation().getZ()+0.5f), EntityType.MINECART);
			minecart.setCustomName("§cMetroSystems");
			minecart.setMaxSpeed(2D);
			if(!minecart.addPassenger(event.getPlayer())){
				minecart.remove();
				SwissSMPler.get(event.getPlayer()).sendActionBar("Konnte MetroSystems nicht betreten.");
				return;
			}
			SwissSMPler.get(event.getPlayer()).sendActionBar("Gute Reise!");
		}
	}
	
	@EventHandler
	private void onVehicleDestroy(VehicleDestroyEvent event){
		Vehicle vehicle = event.getVehicle();
		if(!(vehicle instanceof Minecart)) return;
		Minecart minecart = (Minecart) vehicle;
		if(minecart.getCustomName()==null) return;
		if(minecart.getCustomName().equals("§cMetroSystems")){
			event.setCancelled(true);
			minecart.remove();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onEntityDismount(EntityDismountEvent event){
		if(event.getDismounted()==null) return;
		if(event.getDismounted().getType()!=EntityType.MINECART) return;
		if(event.getDismounted().getCustomName()==null) return;
		if(!event.getDismounted().getCustomName().equals("§cMetroSystems")) return;
		Bukkit.getScheduler().runTaskLater(PublicTransportation.plugin, new Runnable(){
			public void run(){
				event.getDismounted().remove();
			}
		}, 1L);
		if(event.getEntity() instanceof Player){
			SwissSMPler.get((Player)event.getEntity()).sendActionBar("MetroSystems verlassen");
		}
	}
}
