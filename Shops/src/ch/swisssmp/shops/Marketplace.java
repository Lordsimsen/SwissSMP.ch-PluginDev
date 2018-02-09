package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Villager;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

class Marketplace {

	private final ShoppingWorld shoppingWorld;
	private final int addon_instance_id;
	private final String name;
	private final String regionName;
	private final Material agentMarker;
	private final Material shopMarker;
	private Integer[] representedShops;
	
	private Marketplace(ShoppingWorld shoppingWorld, ConfigurationSection dataSection){
		this.shoppingWorld = shoppingWorld;
		this.addon_instance_id = dataSection.getInt("addon_instance_id");
		this.name = dataSection.getString("name");
		this.regionName = dataSection.getString("region");
		this.agentMarker = dataSection.getMaterial("agent_marker");
		this.shopMarker = dataSection.getMaterial("shop_marker");
		if(this.agentMarker==null){
			Bukkit.getLogger().info("[ShopManager] Marktplatz "+this.addon_instance_id+" hat ungültigen Agenten-Marker: "+dataSection.getString("agent_marker"));
		}
		if(this.shopMarker==null){
			Bukkit.getLogger().info("[ShopManager] Marktplatz "+this.addon_instance_id+" hat ungültigen Shop-Marker: "+dataSection.getString("shop_marker"));
		}
		this.updateRepresentedShops();
	}
	
	protected int getAddonInstanceId(){
		return this.addon_instance_id;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected void updateRepresentedShops(){
		if(this.regionName==null || this.regionName.isEmpty()){
			return;
		}
		ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(shoppingWorld.getWorld()).getRegion(this.regionName);
		if(region==null){
			Bukkit.getLogger().info("[ShopManager] Markt-Region "+this.regionName+" fehlt!");
			return;
		}
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();
		
		Vector2D centerVector = min.toVector2D().add(max.toVector2D().subtract(min.toVector2D()).divide(2));
		Location center = new Location(this.shoppingWorld.getWorld(), centerVector.getX(), min.getY()+((max.getY()-min.getY())/2), centerVector.getZ());
		
		Block[] shopLocations = this.getShopLocations(center);
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("shop/representatives.php", new String[]{
			"marketplace="+this.addon_instance_id,
			"shops="+shopLocations.length
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("agents")){
			Bukkit.getLogger().info("[ShopManager] Marktplatz "+this.addon_instance_id+" hat keine Handelsagenten.");
		}
		List<Integer> agents;
		if(yamlConfiguration!=null && yamlConfiguration.contains("agents")) agents = yamlConfiguration.getIntegerList("agents");
		else agents = new ArrayList<Integer>();
		this.representedShops = agents.toArray(new Integer[agents.size()]);
		World world = this.shoppingWorld.getWorld();
		Chunk chunkMin = world.getChunkAt(world.getBlockAt(min.getBlockX(), min.getBlockY(), min.getBlockZ()));
		Chunk chunkMax = world.getChunkAt(world.getBlockAt(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
		Chunk current;
		List<Chunk> temporaryLoadedChunks = new ArrayList<Chunk>();
		Map<Villager,Shop> chunkAgents;
		for(int x = chunkMin.getX(); x <= chunkMax.getX(); x++){
			for(int z = chunkMin.getZ(); z <= chunkMax.getZ(); z++){
				current = world.getChunkAt(x,z);
				if(!current.isLoaded()){
					temporaryLoadedChunks.add(current);
					current.load();
				}
				chunkAgents = this.shoppingWorld.getAgents(current);
				for(Entry<Villager,Shop> entry : chunkAgents.entrySet()){
					if(entry.getValue().getMarketplaceId()==this.addon_instance_id || entry.getValue().getMarketplaceId()<0){
						continue;
					}
					entry.getValue().removeAgent(entry.getKey());
				}
			}
		}

		int shop_id;
		Shop shop;
		for(int i = 0; i<this.representedShops.length && i < shopLocations.length; i++){
			Block block = shopLocations[i];
			shop_id = this.representedShops[i];
			shop = this.shoppingWorld.getShop(shop_id);
			if(shop==null){
				continue;
			}
			if(block==null){
				continue;
			}
			shop.spawnAgent(block.getLocation().add(0.5, 0, 0.5));
		}
		
		for(Chunk chunk : temporaryLoadedChunks){
			chunk.unload();
		}
	}
	
	private Block[] getShopLocations(Location center){
		if(this.agentMarker==null){
			return new Block[0];
		}
		Map<Block, Double> shopLocations = new HashMap<Block, Double>();
		World world = this.shoppingWorld.getWorld();
		ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(shoppingWorld.getWorld()).getRegion(this.regionName);
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();
		for(int x = 0; x < max.getX()-min.getX(); x++){
			for(int z = 0; z < max.getZ()-min.getZ(); z++){
				for(int y = 0; y < max.getY()-min.getY(); y++){
					Block block = world.getBlockAt(min.getBlockX()+x, min.getBlockY()+y, min.getBlockZ()+z);
					if(block.getType()!=this.agentMarker) continue;
					if(block.getRelative(BlockFace.UP).getType()!=Material.AIR) continue;
					if(block.getRelative(BlockFace.UP, 2).getType()!=Material.AIR) continue;
					shopLocations.put(block, block.getLocation().distanceSquared(center));
				}
			}
		}
		shopLocations = ShopUtil.sortByValue(shopLocations);
		return shopLocations.keySet().toArray(new Block[shopLocations.size()]);
	}
	
	protected Material getShopMarker(){
		return this.shopMarker;
	}
	
	protected Material getAgentMarker(){
		return this.agentMarker;
	}
	
	protected static Marketplace load(ShoppingWorld shoppingWorld, ConfigurationSection dataSection){
		return new Marketplace(shoppingWorld, dataSection);
	}
}
