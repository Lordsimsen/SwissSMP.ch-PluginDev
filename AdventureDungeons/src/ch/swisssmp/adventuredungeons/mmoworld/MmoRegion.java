package ch.swisssmp.adventuredungeons.mmoworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.Main;
import ch.swisssmp.adventuredungeons.mmocamp.MmoCamp;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEvent;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventType;
import ch.swisssmp.adventuredungeons.mmoevent.MmoPlayerDeathEvent;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;

public class MmoRegion {
	public final World world;
	public final int mmo_region_id;
	public final String name;
	public final String region_name;
	public HashMap<UUID, Player> players = new HashMap<UUID, Player>();
	public final ArrayList<Integer> registeredCamps = new ArrayList<Integer>();
	public final HashMap<MmoEventType, MmoEvent> events = new HashMap<MmoEventType, MmoEvent>();

	public MmoRegion(MmoWorldInstance worldInstance, ConfigurationSection dataSection){
		this.world = worldInstance.world;
		this.mmo_region_id = dataSection.getInt("id");
		this.region_name = dataSection.getString("region_name");
		this.name = dataSection.getString("name");
		MmoEvent.registerAll(dataSection, events);
		worldInstance.regionTriggers.put(region_name, this);
		worldInstance.regions.put(mmo_region_id, this);
		Main.info("Registered region with id "+this.mmo_region_id);
	}
	public void playerEnter(Player player, GameMode gameMode){
		if(gameMode==GameMode.ADVENTURE){
			MmoEvent.fire(events, MmoEventType.REGION_ENTER, player.getUniqueId());
			players.put(player.getUniqueId(), player);
		}
		if(players.size()>0){
			MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
			for(Integer mmo_camp_id : registeredCamps){
				MmoCamp mmoCamp = worldInstance.getCamp(mmo_camp_id);
				if(mmoCamp.isActive() && !mmoCamp.isSpawning()){
					mmoCamp.enableSpawning(player);
				}
			}
		}
	}
	public void playerLeave(Player player, GameMode gameMode){
		if(players.containsKey(player.getUniqueId())){
			MmoEvent.fire(events, MmoEventType.REGION_LEAVE, player.getUniqueId());
		}
		players.remove(player.getUniqueId());
		if(players.size()<1){
			MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
			for(Integer mmo_camp_id : registeredCamps){
				MmoCamp mmoCamp = worldInstance.getCamp(mmo_camp_id);
				if(mmoCamp.isSpawning()&&mmoCamp.isActive()){
					mmoCamp.disableSpawning();
				}
			}
		}
	}
	public void playerDeath(Player player){
		MmoPlayerDeathEvent playerDeathEvent = new MmoPlayerDeathEvent(this, player);
		Bukkit.getPluginManager().callEvent(playerDeathEvent);
	}
	public MmoWorldInstance getMmoWorldInstance(){
		return MmoWorld.getInstance(this.world);
	}
	
	public static boolean regionMatch(Location location, int mmo_region_id){
		if(mmo_region_id>0){
			MmoWorldInstance worldInstance = MmoWorld.getInstance(location.getWorld());
			MmoRegion mmoRegion = worldInstance.getRegion(mmo_region_id);
			if(mmoRegion!=null){
				ApplicableRegionSet regions = Main.worldGuardPlugin.getRegionManager(mmoRegion.getMmoWorldInstance().world).getApplicableRegions(location);
				for(ProtectedRegion region : regions){
					Main.info("comparing region "+region.getId()+" against "+mmoRegion.region_name);
					if(mmoRegion.region_name.equals(region.getId())){
						return true;
					}
				}
			}
			else{
				Main.info("Region "+mmo_region_id+" not found");
			}
		}
		else{
			return true;
		}
		return false;
	}
	
	public static boolean regionMatch(World world, ProtectedRegion region, int mmo_region_id){
		if(mmo_region_id<=0){
			return true;
		}
		MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
		MmoRegion mmoRegion = worldInstance.getRegion(mmo_region_id);
		return mmoRegion.region_name.equals(region.getId());
	}
	public synchronized static void loadRegions(MmoWorldInstance worldInstance, boolean fullload) throws Exception{
		Main.info("Starting to load regions for world "+worldInstance.world.getName());
		worldInstance.regions = new HashMap<Integer, MmoRegion>();
		worldInstance.regionTriggers = new HashMap<String, MmoRegion>();
		YamlConfiguration mmoRegionsConfiguration = MmoResourceManager.getYamlResponse("regions.php", new String[]{
				"world="+worldInstance.system_name
		});
		for(String regionIDstring : mmoRegionsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoRegionsConfiguration.getConfigurationSection(regionIDstring);
			new MmoRegion(worldInstance, dataSection);
		}
		Main.info("Finished loading camps for world "+worldInstance.world.getName());
		if(fullload){
			try{
				MmoCamp.loadCamps(worldInstance);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
