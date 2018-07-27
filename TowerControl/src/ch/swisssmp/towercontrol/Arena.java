package ch.swisssmp.towercontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import ch.swisssmp.towercontrol.transformations.TransformationArea;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.VectorKey;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class Arena {
	private static HashMap<Integer, Arena> arenas = new HashMap<Integer, Arena>();
	private static HashMap<String, Arena> worldMap = new HashMap<String, Arena>();
	
	private final int arena_id;
	private String name;
	private final World world;
	private final WorldType worldType;
	private final Environment environment;
	private final String soundtrack;
	
	//spawns
	private List<Location> redSpawns;
	private List<Location> blueSpawns;
	
	//triggers
	private HashMap<VectorKey,ArenaTower> redTowers;
	private HashMap<VectorKey,ArenaTower> blueTowers;
	private VectorKey sniperTrigger;
	private VectorKey potionTrigger;
	private VectorKey bypassTrigger;
	private VectorKey passageTrigger;
	
	//transformations
	private HashMap<String,TransformationArea> transformationAreas = new HashMap<String,TransformationArea>();
	private String sniperTowerTransformation;
	private String potionTowerTransformation;
	private String bypassTowerTransformation;
	private String passageTowerTransformation;
	
	private String sniperTransformation;
	private String bypassTransformation;
	private String passageTransformation;
	
	//ownership
	private TowerControlTeam sniperTowerOwner;
	private TowerControlTeam potionTowerOwner;
	private TowerControlTeam bypassTowerOwner;
	private TowerControlTeam passageTowerOwner;
	
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	private List<ItemStack> inventoryTemplate = new ArrayList<ItemStack>();
	
	private Random random = new Random();
	
	private Arena(ConfigurationSection dataSection){
		this.arena_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.worldType = WorldType.valueOf(dataSection.getString("world_type"));
		this.environment = Environment.valueOf(dataSection.getString("environment"));
		World world = Bukkit.getWorld(dataSection.getString("world"));
		if(world==null){
			WorldCreator worldCreator = new WorldCreator(dataSection.getString("world"));
			worldCreator.type(this.worldType);
			worldCreator.environment(this.environment);
			world = Bukkit.createWorld(worldCreator);
		}
		this.world = world;
		this.soundtrack = dataSection.getString("soundtrack");
		ConfigurationSection spawnsSection = dataSection.getConfigurationSection("spawns");
		if(spawnsSection!=null){
			this.redSpawns = new ArrayList<Location>();
			this.blueSpawns = new ArrayList<Location>();
			ConfigurationSection teamSpawnsLocation;
			Location teamSpawnLocation;
			teamSpawnsLocation = spawnsSection.getConfigurationSection("red");
			for(String key : teamSpawnsLocation.getKeys(false)){
				teamSpawnLocation = teamSpawnsLocation.getLocation(key, this.world);
				if(teamSpawnLocation!=null) this.redSpawns.add(teamSpawnLocation);
			}
			teamSpawnsLocation = spawnsSection.getConfigurationSection("blue");
			for(String key : teamSpawnsLocation.getKeys(false)){
				teamSpawnLocation = teamSpawnsLocation.getLocation(key, this.world);
				if(teamSpawnLocation!=null) this.blueSpawns.add(teamSpawnLocation);
			}
		}
		ConfigurationSection towersSection = dataSection.getConfigurationSection("towers");
		if(towersSection!=null){
			ConfigurationSection teamTowersSection;
			teamTowersSection = towersSection.getConfigurationSection("red");
			Block block;
			this.redTowers = new HashMap<VectorKey,ArenaTower>();
			for(String key : teamTowersSection.getKeys(false)){
				block = teamTowersSection.getLocation(key, this.world).getBlock();
				this.redTowers.put(new VectorKey(new Vector(block.getX(),block.getY(),block.getZ())), new ArenaTower(block, "red"));
			}
			teamTowersSection = towersSection.getConfigurationSection("blue");
			this.blueTowers = new HashMap<VectorKey,ArenaTower>();
			for(String key : teamTowersSection.getKeys(false)){
				block = teamTowersSection.getLocation(key, this.world).getBlock();
				this.blueTowers.put(new VectorKey(new Vector(block.getX(),block.getY(),block.getZ())), new ArenaTower(block, "blue"));
			}
		}
		ConfigurationSection triggersSection = dataSection.getConfigurationSection("triggers");
		if(triggersSection!=null){
			this.sniperTrigger = triggersSection.getVectorKey("sniper");
			this.potionTrigger = triggersSection.getVectorKey("potion");
			this.bypassTrigger = triggersSection.getVectorKey("bypass");
			this.passageTrigger = triggersSection.getVectorKey("passage");
		}
		ConfigurationSection potionEffectsSection = dataSection.getConfigurationSection("potion_effects");
		if(potionEffectsSection!=null){
			potionEffects.clear();
			PotionEffect potionEffect;
			for(String key : potionEffectsSection.getKeys(false)){
				Bukkit.getLogger().info("[TowerControl] Loading PotionEffect "+key);
				potionEffect = potionEffectsSection.getPotionEffect(key);
				if(potionEffect!=null){
					potionEffects.add(potionEffect);
					Bukkit.getLogger().info("[TowerControl] Loaded PotionEffect "+potionEffect.getType());
				}
				else{
					Bukkit.getLogger().info("[TowerControl] PotionEffect is invalid.");
				}
			}
		}
		ConfigurationSection transformationsSection = dataSection.getConfigurationSection("transformations");
		if(transformationsSection!=null){
			this.sniperTransformation = transformationsSection.getString("sniper");
			this.bypassTransformation = transformationsSection.getString("bypass");
			this.passageTransformation = transformationsSection.getString("passage");
			this.sniperTowerTransformation = transformationsSection.getString("sniper_tower");
			this.potionTowerTransformation = transformationsSection.getString("potion_tower");
			this.bypassTowerTransformation = transformationsSection.getString("bypass_tower");
			this.passageTowerTransformation = transformationsSection.getString("passage_tower");
		}
		ConfigurationSection itemsSection = dataSection.getConfigurationSection("player_inventory");
		if(itemsSection!=null){
			for(String key : itemsSection.getKeys(false)){
				this.inventoryTemplate.add(itemsSection.getItemStack(key));
			}
		}
		this.loadTransformations();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getSoundtrack(){
		return this.soundtrack;
	}
	
	public void trigger(VectorKey vectorKey, Player player){
		Bukkit.getLogger().info("Triggering "+vectorKey.toString());
		TowerControlTeam towerControlTeam = TowerControlTeam.get(player);
		if(towerControlTeam==null)return;
		if(vectorKey.equals(this.sniperTrigger)){
			if(this.sniperTowerOwner!=towerControlTeam){
				TowerControl.game.addScore(player, 100, "Sniper Turm erobert");
				this.setSniperOwner(towerControlTeam);
			}
		}
		else if(vectorKey.equals(this.potionTrigger)){
			if(this.potionTowerOwner!=towerControlTeam){
				TowerControl.game.addScore(player, 100, "Trank Turm erobert");
				this.setPotionOwner(towerControlTeam);
			}
		}
		else if(vectorKey.equals(this.bypassTrigger)){
			if(this.bypassTowerOwner!=towerControlTeam){
				TowerControl.game.addScore(player, 100, "Bypass Turm erobert");
				this.setBypassOwner(towerControlTeam);
			}
		}
		else if(vectorKey.equals(this.passageTrigger)){
			if(this.passageTowerOwner!=towerControlTeam){
				TowerControl.game.addScore(player, 100, "Passage Turm erobert");
				this.setPassageOwner(towerControlTeam);
			}
		}
		else{
			ArenaTower redTower = this.redTowers.get(vectorKey);
			if(redTower!=null){
				redTower.trigger();
				return;
			}
			ArenaTower blueTower = this.blueTowers.get(vectorKey);
			if(blueTower!=null){
				blueTower.trigger();
				return;
			}
		}
	}
	
	public void reset(){
		for(TransformationArea area : this.transformationAreas.values()){
			area.set("neutral");
		}
		this.bypassTowerOwner = null;
		this.passageTowerOwner = null;
		this.potionTowerOwner = null;
		this.sniperTowerOwner = null;
	}
	
	public void setSniperOwner(TowerControlTeam team){
		this.sniperTowerOwner = team;
		TransformationArea sniperTowerTransformation = this.transformationAreas.get(this.sniperTowerTransformation);
		if(sniperTowerTransformation!=null) sniperTowerTransformation.set(team.getSide());
		TransformationArea sniperTransformation = this.transformationAreas.get(this.sniperTransformation);
		if(sniperTransformation!=null) sniperTransformation.set(team.getSide());
	}
	
	public void setPotionOwner(TowerControlTeam team){
		this.potionTowerOwner = team;
		TransformationArea potionTowerTransformation = this.transformationAreas.get(this.potionTowerTransformation);
		if(potionTowerTransformation!=null) potionTowerTransformation.set(team.getSide());
		this.potionTowerOwner = team;
	}
	
	public void setBypassOwner(TowerControlTeam team){
		this.bypassTowerOwner = team;
		TransformationArea bypassTowerTransformation = this.transformationAreas.get(this.bypassTowerTransformation);
		if(bypassTowerTransformation!=null) bypassTowerTransformation.set(team.getSide());
		TransformationArea bypassTransformation = this.transformationAreas.get(this.bypassTransformation);
		if(bypassTransformation!=null) bypassTransformation.set(team.getSide());
	}
	
	public void setPassageOwner(TowerControlTeam team){
		this.passageTowerOwner = team;
		TransformationArea passageTowerTransformation = this.transformationAreas.get(this.passageTowerTransformation);
		if(passageTowerTransformation!=null) passageTowerTransformation.set(team.getSide());
		TransformationArea passageTransformation = this.transformationAreas.get(this.passageTransformation);
		if(passageTransformation!=null) passageTransformation.set(team.getSide());
	}
	
	public int getArenaId(){
		return this.arena_id;
	}
	
	public TowerControlTeam getPotionTowerOwner(){
		return this.potionTowerOwner;
	}
	
	public List<PotionEffect> getPotionEffects(){
		return this.potionEffects;
	}
	
	public List<ItemStack> getInventoryTemplate(){
		return this.inventoryTemplate;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public int getSpawnPositionCount(String side){
		if(side.equals("red")){
			return this.redSpawns.size();
		}
		else if(side.equals("blue")){
			return this.blueSpawns.size();
		}
		else return 0;
	}
	
	public Location getSpawn(String side){
		if(side.equals("red")){
			return getSpawn(side, random.nextInt(this.redSpawns.size()));
		}
		else if(side.equals("blue")){
			return getSpawn(side, random.nextInt(this.blueSpawns.size()));
		}
		else return null;
	}
	
	public Location getSpawn(String side, int index){
		if(side.equals("red")){
			if(index>=this.redSpawns.size()) return null;
			return this.redSpawns.get(index);
		}
		else if(side.equals("blue")){
			if(index>=this.blueSpawns.size()) return null;
			return this.blueSpawns.get(index);
		}
		else return null;
	}
	
	public void checkGameFinished(Game game, String side){
		Bukkit.getLogger().info("[TowerControl] Checking if team "+side+" has lost.");
		if(side.equals("red")){
			for(ArenaTower tower : this.redTowers.values()){
				if(!tower.isTriggered()){
					return;
				}
			}
			game.setFinished(game.getTeamBlue(), game.getTeamRed());
		}
		else if(side.equals("blue")){
			for(ArenaTower tower : this.blueTowers.values()){
				if(!tower.isTriggered()){
					return;
				}
			}
			game.setFinished(game.getTeamRed(), game.getTeamBlue());
		}
	}
	
	public void loadTransformations(){
		this.transformationAreas.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("transformations/get.php", new String[]{
				"world="+URLEncoder.encode(this.world.getName())
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("transformations")) return;
		ConfigurationSection transformationsSection = yamlConfiguration.getConfigurationSection("transformations");
		ConfigurationSection transformationSection;
		TransformationArea transformationArea;
		for(String key : transformationsSection.getKeys(false)){
			transformationSection = transformationsSection.getConfigurationSection(key);
			transformationArea = new TransformationArea(this.world, transformationSection);
			transformationAreas.put(transformationArea.getTransformationEnum(), transformationArea);
		}
	}
	
	public TransformationArea getTransformation(String transformation_enum){
		return this.transformationAreas.get(transformation_enum);
	}
	
	public Collection<TransformationArea> getTransformations(){
		return this.transformationAreas.values();
	}
	
	protected static void loadArenas(){
		arenas.clear();
		worldMap.clear();
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("towercontrol/get_arenas.php");
		if(yamlConfiguration==null || !yamlConfiguration.contains("arenas")){
			Bukkit.getLogger().info("[TowerControl] Konnte Arenen nicht laden.");
			return;
		}
		ConfigurationSection arenasSection = yamlConfiguration.getConfigurationSection("arenas");
		ConfigurationSection arenaSection;
		Arena arena;
		for(String key : arenasSection.getKeys(false)){
			arenaSection = arenasSection.getConfigurationSection(key);
			arena = new Arena(arenaSection);
			arenas.put(arena.arena_id, arena);
			worldMap.put(arena.world.getName(), arena);
		}
	}
	
	protected static Arena get(int arena_id){
		return arenas.get(arena_id);
	}
	
	public static Arena get(World world){
		return worldMap.get(world.getName());
	}
	
	public static Collection<Arena> getArenas(){
		return arenas.values();
	}
}
