package ch.swisssmp.vanillaportals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class EventListener implements Listener {
	
	private final double netherSizeRatio = 2;
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		if(event.getTo()==null) return;
		World fromWorld = event.getFrom().getWorld();
		World toWorld = event.getTo().getWorld();
		if(fromWorld==Bukkit.getWorlds().get(0) && toWorld.getName().equals(fromWorld.getName()+"_nether")){
			/**
			 * Spieler betritt Nether
			 * Notiert informationen über den Eingangspunkt eines Reisenden
			 */
			event.getPortalTravelAgent().setSearchRadius(25);
			Location rawLocation = event.getFrom();
			event.setTo(new Location(toWorld, rawLocation.getX() / netherSizeRatio, rawLocation.getY(), rawLocation.getZ() / netherSizeRatio, rawLocation.getYaw(), rawLocation.getPitch()));
			Bukkit.getScheduler().runTaskLater(NetherPortalFixer.getInstance(), ()->{
				setTravel(event.getPlayer(), event.getFrom(), event.getPlayer().getLocation());
			}, 1L);
		}
		else if(toWorld==Bukkit.getWorlds().get(0) && fromWorld.getName().equals(toWorld.getName()+"_nether")){
			/**
			 * Spieler verlässt Nether
			 * Stellt bei der Rückreise sicher, dass der Reisende zu seinem Eingangspunkt gesetzt wird
			 */
			Location rawLocation = event.getFrom();
			event.getPortalTravelAgent().setCanCreatePortal(false);
			event.setTo(new Location(toWorld, rawLocation.getX() * netherSizeRatio, rawLocation.getY(), rawLocation.getZ() * netherSizeRatio));
			TravelFromNetherToMain(event);
		}
	}
	
	/**
	 * Verhindert, dass Items oder Mobs vom Portal teleportiert werden
	 * @param event
	 */
	@EventHandler
	public void onPortalEnter(EntityPortalEvent event){
		if(event.getEntity() instanceof Player) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onNetherExited(EntityPortalExitEvent event){
		if(event.getTo()==null || !(event.getEntity() instanceof Player)) return;
		World toWorld = event.getTo().getWorld();
		if(toWorld!=Bukkit.getWorlds().get(0)) return;
		Location to = event.getTo();
		Block finalBlock = to.getBlock();
		while((finalBlock.getType()!=Material.AIR && finalBlock.getType()!=Material.NETHER_PORTAL) || 
				finalBlock.getRelative(BlockFace.UP).getType()!=Material.AIR && finalBlock.getRelative(BlockFace.UP).getType()!=Material.NETHER_PORTAL){
			finalBlock = finalBlock.getRelative(BlockFace.UP);
			to.add(0, 1, 0);
			event.setTo(to);
		}
		Block below = finalBlock.getRelative(BlockFace.DOWN);
		if(below.getType().isSolid()) return;
		List<Block> platform = new ArrayList<Block>();
		platform.add(below);
		platform.add(below.getRelative(BlockFace.NORTH));
		platform.add(below.getRelative(BlockFace.EAST));
		platform.add(below.getRelative(BlockFace.SOUTH));
		platform.add(below.getRelative(BlockFace.WEST));
		for(Block block : platform){
			if(block.getType()!=Material.AIR) continue;
			block.setType(Material.COBBLESTONE);
		}
	}
	
	/**
	 * Verhindert Portal-Erstellung im Nether
	 * @param event
	 */
	@EventHandler
	public void onPortalCreate(PortalCreateEvent event){
		if(!event.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()+"_nether") || event.getReason()==CreateReason.OBC_DESTINATION) return;
		Location location = event.getBlocks().get(0).getLocation();
		for(Entity entity : event.getWorld().getNearbyEntities(location, 15, 15, 15)){
			if(!(entity instanceof Player)) continue;
			((Player)entity).sendMessage("["+ChatColor.YELLOW+"Hinweis"+ChatColor.RESET+"] In der Oberwelt werden Portale nicht automatisch generiert.");
		}
	}
	
	private void TravelFromNetherToMain(PlayerPortalEvent event){
		YamlConfiguration travel = getTravel(event.getTo().getWorld(), event.getPlayer());
		if(travel==null || !travel.contains("travel")){
			//Bukkit.getLogger().info("No journey found");
			return;
		}
		ConfigurationSection travelSection = travel.getConfigurationSection("travel");
		Location from = event.getFrom();
		Location previousTo = travelSection.getLocation("to");
		double distance = Math.sqrt(Math.pow(from.getX()-previousTo.getX(), 2)+Math.pow(from.getZ()-previousTo.getZ(), 2));
		if(distance > 5){
			//Bukkit.getLogger().info("Not even close to home ("+from.getX()+","+from.getZ()+",\n"+previousTo.getX()+","+previousTo.getZ()+")");
			return;
		}
		Location previousFrom = travelSection.getLocation("from");
		event.setTo(previousFrom);
		//Bukkit.getLogger().info("Forcing Player home");
	}
	
	private YamlConfiguration getTravel(World world, Player player){
		File directory = getTravelsDirectory(world);
		File file = new File(directory, player.getUniqueId().toString()+".yml");
		if(!file.exists()) return null;
		YamlConfiguration result = YamlConfiguration.loadConfiguration(file);
		if(file!=null) file.delete();
		return result;
	}
	
	private void setTravel(Player player, Location from, Location to){
		World world = from.getWorld();
		File directory = getTravelsDirectory(world);
		if(!directory.exists()) directory.mkdirs();
		File file = new File(directory, player.getUniqueId().toString()+".yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection travelSection = yamlConfiguration.createSection("travel");
		ConfigurationSection fromSection = travelSection.createSection("from");
		fromSection.set("world", world.getName());
		fromSection.set("x", from.getX());
		fromSection.set("y", from.getY());
		fromSection.set("z", from.getZ());
		fromSection.set("yaw", 0);
		fromSection.set("pitch", 0);
		ConfigurationSection toSection = travelSection.createSection("to");
		toSection.set("world", to.getWorld().getName());
		toSection.set("x", to.getX());
		toSection.set("y", to.getY());
		toSection.set("z", to.getZ());
		toSection.set("yaw", 0);
		toSection.set("pitch", 0);
		yamlConfiguration.save(file);
	}
	
	private File getTravelsDirectory(World world){
		return new File(world.getWorldFolder(), "plugindata/NetherPortalFixer");
	}
}
