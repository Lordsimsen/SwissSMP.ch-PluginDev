package ch.swisssmp.travel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.WorldManager;

public class TravelStation {
	
	private final World world;
	private final UUID station_id; //id of this station in the save files
	private String name; //name of this station, visible to the public
	private TravelStationType type; //what type of station like airship, train etc.
	private Position waypoint; //where the player is teleported to upon arrival
	private Position respawnPosition; //where the player respawns after death during the journey
	
	private Position outsideAnchor; //reference point in the start world
	private Position insideAnchor; //reference point in the journey world
	
	private Position boundingBoxMin; //min point for cargo space
	private Position boundingBoxMax; //max point for cargo space
	
	private String travelWorldName; //name of the template world to use for the journey
	
	private Journey activeJourney;
	
	private TravelStation(World world, UUID station_id, String name){
		this.world = world;
		this.station_id = station_id;
		this.name = name;
		this.type = TravelStationType.AIRSHIP;
	}
	
	private TravelStation(World world, ConfigurationSection dataSection){
		this.world = world;
		this.station_id = UUID.fromString(dataSection.getString("id"));
		this.name = dataSection.getString("name");
		this.type = TravelStationType.getByType(dataSection.getString("type"));
		this.waypoint = dataSection.getPosition("waypoint");
		this.respawnPosition = dataSection.getPosition("respawn");
		
		this.outsideAnchor = dataSection.getPosition("outside_anchor");
		this.insideAnchor = dataSection.getPosition("inside_anchor");

		this.boundingBoxMin = dataSection.getPosition("bounding_box_min");
		this.boundingBoxMax = dataSection.getPosition("bounding_box_max");
		
		this.travelWorldName = dataSection.getString("travel_world");
	}
	
	public World getWorld(){
		return world;
	}
	
	public UUID getId(){
		return station_id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
		TravelStations.save(world);
	}
	
	public String getTravelWorldName(){
		return travelWorldName;
	}
	
	public void setTravelWorldName(String travelWorldName){
		this.travelWorldName = travelWorldName;
		TravelStations.save(world);
	}
	
	public Position getWaypoint(){
		return waypoint;
	}
	
	public void setWaypoint(Position position){
		this.waypoint = position;
		TravelStations.save(world);
	}
	
	public Position getRespawn(){
		return this.respawnPosition;
	}
	
	public void setRespawn(Position position){
		this.respawnPosition = position;
		TravelStations.save(world);
	}
	
	public Position getOutsideAnchor(){
		return outsideAnchor;
	}
	
	public void setOutsideAnchor(Position position){
		outsideAnchor = position;
		TravelStations.save(world);
	}
	
	public Position getInsideAnchor(){
		return insideAnchor;
	}
	
	public void setInsideAnchor(Position position){
		insideAnchor = position;
		TravelStations.save(world);
	}
	
	public TravelStationType getStationType(){
		return this.type;
	}
	
	public void setStationType(TravelStationType type){
		this.type = type;
		TravelStations.save(world);
	}
	
	public Position getBoundingBoxMin(){
		return boundingBoxMin;
	}
	
	public void setBoundingBoxMin(Position position){
		this.boundingBoxMin = position;
		TravelStations.save(world);
	}
	
	public Position getBoundingBoxMax(){
		return boundingBoxMax;
	}
	
	public void setBoundingBoxMax(Position position){
		this.boundingBoxMax = position;
		TravelStations.save(world);
	}
	
	public Journey getJourney(){
		return activeJourney;
	}
	
	public ItemStack getTokenStack(){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(this.type.getIcon());
		itemBuilder.setDisplayName(ChatColor.AQUA + this.name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> description = this.getDescription();
		itemBuilder.setLore(description);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "travelstation", station_id.toString());
		return result;
	}
	
	public ItemStack getDestinationSelectionItem(TravelStation start){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(this.type.getIcon());
		itemBuilder.setDisplayName(ChatColor.AQUA + this.name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> description = new ArrayList<String>();
		long travelDuration = start.getTravelTime(this);
		description.add(ChatColor.YELLOW+"Welt: "+WorldManager.getDisplayName(world));
		description.add(ChatColor.GRAY+"Reisezeit: "+TimeUtil.format(travelDuration));
		itemBuilder.setLore(description);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "travelstation", station_id.toString());
		return result;
	}
	
	public ItemStack getEmbarkNowItem(TravelStation start){
		CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("EMBARK_NOW");
		itemBuilder.setDisplayName(ChatColor.AQUA + this.name);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> description = new ArrayList<String>();
		long travelDuration = start.getTravelTime(this);
		description.add(ChatColor.GRAY+"Reisezeit: "+TimeUtil.format(travelDuration));
		description.add(ChatColor.YELLOW+"Klicke um die Reise");
		description.add(ChatColor.YELLOW+"sofort zu starten");
		itemBuilder.setLore(description);
		ItemStack result = itemBuilder.build();
		ItemUtil.setString(result, "travelstation", station_id.toString());
		return result;
	}
	
	public void updateTokens(){
		ItemStack tokenStack = this.getTokenStack();
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				if(itemStack==null) continue;
				TravelStation travelStation = TravelStation.get(itemStack);
				if(travelStation!=this) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	public TravelStationEditor openEditor(Player player){
		return TravelStationEditor.open(player, this);
	}
	
	public DestinationSelectionView openDestinationSelection(Player player){
		return DestinationSelectionView.open(player, this);
	}
	
	public void save(ConfigurationSection dataSection){
		dataSection.set("id", this.station_id.toString());
		dataSection.set("name", this.name);
		dataSection.set("type", this.type.toString());
		dataSection.set("travel_world", this.travelWorldName);
		if(this.waypoint!=null){
			savePosition(dataSection, "waypoint", this.waypoint);
		}
		if(this.respawnPosition!=null){
			savePosition(dataSection, "respawn", this.respawnPosition);
		}
		if(this.outsideAnchor!=null){
			savePosition(dataSection, "outside_anchor", this.outsideAnchor);
		}
		if(this.insideAnchor!=null){
			savePosition(dataSection, "inside_anchor", this.insideAnchor);
		}
		if(this.boundingBoxMin!=null){
			savePosition(dataSection, "bounding_box_min", this.boundingBoxMin);
		}
		if(this.boundingBoxMax!=null){
			savePosition(dataSection, "bounding_box_max", this.boundingBoxMax);
		}
	}
	
	public void unload(){
		TravelStations.remove(station_id);
	}
	
	public long getTravelTime(TravelStation destination){
		if(destination.getWorld()!=this.getWorld()){
			return 180l * 20;
		}
		Position from = this.getOutsideAnchor();
		Position to = destination.getOutsideAnchor();
		if(from==null || to==null) return -1;
		double distance = from.getLocation(this.getWorld()).distance(to.getLocation(destination.getWorld()));
		return Mathf.ceilToInt(Math.min(distance/40,180)*20) ;
	}
	
	public void setTravelGuide(NPCInstance npc){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.set("travelstation", this.station_id.toString());
		yamlConfiguration.set("name", npc.getName());
		npc.setYamlConfiguration(yamlConfiguration);
	}
	
	public void prepareJourney(TravelStation destination){
		if(this.activeJourney!=null){
			this.activeJourney.setDestination(destination);
			return;
		}
		this.activeJourney = Journey.prepare(this, destination);
		this.activeJourney.addOnEmbarkListener(()->{
			activeJourney = null;
		});
	}
	
	public void cancelJourney(){
		if(this.activeJourney==null) return;
		this.activeJourney.cancel();
		this.activeJourney = null;
	}
	
	public boolean isSetupComplete(){
		return !(
				this.waypoint==null || 
				this.outsideAnchor==null || 
				this.insideAnchor==null || 
				this.boundingBoxMin==null || 
				this.boundingBoxMax==null || 
				this.travelWorldName==null || 
				this.travelWorldName.isEmpty());
	}
	
	private List<String> getDescription(){
		List<String> result = new ArrayList<String>();
		if(this.waypoint!=null) result.add(ChatColor.GRAY+"X: "+Mathf.floorToInt(waypoint.getX())+", Z: "+Mathf.floorToInt(waypoint.getZ()));
		if(!this.isSetupComplete()){
			result.add(ChatColor.YELLOW+"Diese Station ist noch");
			result.add(ChatColor.YELLOW+"nicht fertig aufgesetzt.");
		}
		return result;
	}
	
	private static void savePosition(ConfigurationSection dataSection, String label, Position position){
		ConfigurationSection positionSection = dataSection.createSection(label);
		positionSection.set("x", position.getX());
		positionSection.set("y", position.getY());
		positionSection.set("z", position.getZ());
		positionSection.set("yaw", position.getYaw());
		positionSection.set("pitch", position.getPitch());
	}
	
	public static TravelStation create(World world, String name){
		TravelStation existing = TravelStation.get(name, true);
		if(existing!=null) return existing;
		UUID newStationId = UUID.randomUUID();
		TravelStation result = TravelStation.create(world, newStationId, name);
		TravelStations.save(world);
		return result;
	}
	
	private static TravelStation create(World world, UUID station_id, String name){
		TravelStation result = new TravelStation(world, station_id, name);
		TravelStations.put(result.station_id, result);
		return result;
	}
	
	protected static TravelStation load(World world, ConfigurationSection dataSection){
		return new TravelStation(world, dataSection);
	}
	
	public static TravelStation get(String name, boolean exactMatch){
		for(TravelStation station : TravelStations.getAll()){
			if(exactMatch && !station.getName().toLowerCase().equals(name.toLowerCase())) continue;
			if(station.getName().toLowerCase().contains(name.toLowerCase())) return station;
		}
		return null;
	}
	
	public static TravelStation get(UUID station_id){
		if(TravelStations.containsKey(station_id)) return TravelStations.get(station_id);
		return null;
	}
	
	public static TravelStation get(ItemStack tokenStack){
		String uuidString = ItemUtil.getString(tokenStack, "travelstation");
		if(uuidString==null) return null;
		UUID station_id = UUID.fromString(uuidString);
		if(station_id==null) return null;
		return get(station_id);
	}
}
