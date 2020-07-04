package ch.swisssmp.adventuredungeons;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;

import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.world.transfer.WorldTransferManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.world.WorldEditor;
import ch.swisssmp.world.WorldManager;
import ch.swisssmp.world.transfer.WorldTransferObserver;
import ch.swisssmp.worldguardmanager.WorldGuardManager;

public class Dungeon{
	private static int auto_increment = 0;
	private static HashMap<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	private static Random random = new Random();
	
	private final String background_music;
	private final String customEnum;
	private final Integer dungeon_id;
	private final HashMap<String, String> gamerules = new HashMap<String,String>();
	private Position lobby_join;
	private Position lobby_leave;
	private final Integer lobby_trigger;
	
	private final int maxPlayers;
	private final Long music_loop_time;
	
	private String name;
	
	private Dungeon(ConfigurationSection dataSection){
		this.dungeon_id = dataSection.getInt("dungeon_id");
		this.name = dataSection.getString("name");
		this.customEnum = dataSection.getString("custom_enum");
		this.maxPlayers = dataSection.getInt("max_players");
		this.background_music = dataSection.getString("background_music") != null ? dataSection.getString("background_music") : "";
		this.music_loop_time = dataSection.getLong("looptime");
		this.lobby_trigger = dataSection.getInt("lobby_trigger");
		this.lobby_join = dataSection.getPosition("lobby_join");
		this.lobby_leave = dataSection.getPosition("lobby_leave");
		ConfigurationSection gamerulesSection = dataSection.getConfigurationSection("gamerules");
		if(gamerulesSection!=null){
			for(String key : gamerulesSection.getKeys(false)){
				this.gamerules.put(key, gamerulesSection.getString(key));
			}
		}
	}
	
	public String getBackgroundMusic(){
		return this.background_music;
	}
	
	public int getDungeonId(){
		return this.dungeon_id;
	}
	
	public Position getLobbyJoin(){
		return this.lobby_join;
	}
	
	public Position getLobbyLeave(){
		if(this.lobby_leave!=null){
			return this.lobby_leave;
		}
		else{
			World defaultWorld = Bukkit.getWorlds().get(0);
			return new Position(defaultWorld.getSpawnLocation());
		}
	}
	
	public int getLobbyTrigger(){
		return this.lobby_trigger;
	}
	
	public int getMaxPlayers(){
		return this.maxPlayers;
	}
	
	public long getMusicLoopTime(){
		return this.music_loop_time;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getTemplateName(){
		return "dungeon_template_"+this.dungeon_id;
	}
	
	public void initiateEditor(Player player){
		String template_name = this.getTemplateName();
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			if(WorldTransferManager.localWorldExists(template_name)){
				//World is present on local disk, load it and then teleport into it
				world = WorldManager.loadWorld(template_name);
			}
			else{
				HTTPRequest remoteCheck = WorldTransferManager.remoteWorldExists(template_name);
				remoteCheck.onFinish(()->{
					if(remoteCheck.getResponse().equals("true")){
						//World is present on remote disk, download it, load it, then teleport into it
						WorldTransferObserver observer = WorldTransferManager.downloadWorld(player, template_name);
						observer.addOnFinishListener(new Runnable(){
							public void run(){
								initiateEditor(player);
							}
						});
					}
					else{
						//World does not exist, create it then teleport into it
						WorldEditor worldEditor = WorldEditor.open(template_name, player);
						worldEditor.onWorldGenerate((World newWorld)->{
							this.applyGamerules(newWorld, Difficulty.EASY, false);
							WorldGuardPlugin.inst().reloadConfig();
							for(ProtectedRegion protectedRegion : WorldGuardManager.getRegionManager(newWorld).getRegions().values()){
								protectedRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
							};
							this.initiateEditor(player);
						});
					}
				});
				return;
			}
		}
		if(world!=null){
			//World is loaded, teleport into it
			player.teleport(this.lobby_join.getLocation(world));
		}
		else{
			player.sendMessage("[AdventureDungeons] Konnte den Editor nicht initiieren.");
		}
	}
	
	public void initiateInstance(Player player, Difficulty difficulty){
		int instance_id = auto_increment;
		auto_increment++;
		this.initiateInstance(instance_id, player, difficulty);
	}
	
	public void join(Player player, DungeonInstance targetInstance, Difficulty difficulty){
		if(player==null)return;
		//check if the player is currently in a dungeon (and remove them there if they are)
		DungeonInstance currentInstance = DungeonInstance.get(player);
		if(currentInstance!=null){
			if(currentInstance.getDungeonId()!=this.dungeon_id){
				currentInstance.getPlayerManager().leave(player.getUniqueId());
			}
			else return;
		}
		if(targetInstance!=null){
			targetInstance.getPlayerManager().join(player);
		}
		else{
			//no instance has been found, so lets attempt to create a new one
			initiateInstance(player, difficulty);
		}
	}
	
	public void leave(UUID player_uuid){
		DungeonInstance dungeonInstance = DungeonInstance.get(player_uuid);
		if(dungeonInstance==null)return;
		dungeonInstance.getPlayerManager().leave(player_uuid);
	}
	
	public boolean saveTemplate(CommandSender sender){
		String template_name = this.getTemplateName();
		World world = Bukkit.getWorld(template_name);
		if(world==null){
			return false;
		}
		Location leavePoint = this.lobby_leave.getLocation(Bukkit.getWorlds().get(0));
		if(leavePoint!=null){
			for(Player player : world.getPlayers().toArray(new Player[world.getPlayers().size()])){
				player.teleport(leavePoint);
			}
		}
		//save the world one last time before unloading
		world.save();
		Bukkit.getScheduler().runTaskLater(AdventureDungeonsPlugin.getInstance(), new Runnable(){
			public void run(){
				WorldTransferObserver transferObserver = WorldTransferManager.uploadWorld(sender, template_name);
				transferObserver.addOnFinishListener(new Runnable(){
					public void run(){
						WorldManager.deleteWorld(world.getName());
					}
				});
			}
		}, 100L);
		return true;
	}
	
	protected void saveSettings(){
		DataSource.getResponse(AdventureDungeonsPlugin.getInstance(), "save_dungeon_settings.php", new String[]{
			"id="+dungeon_id,
			"name="+URLEncoder.encode(this.name),
			this.lobby_join.getURLString("lobby_join"),
			this.lobby_leave.getURLString("lobby_leave")
		});
		this.updateTokens();
	}
	
	protected void setLobbyJoin(Position position){
		this.lobby_join = position;
	}
	
	protected void setLobbyLeave(Position position){
		this.lobby_leave = position;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	private void updateTokens(){
		ItemStack tokenStack = ItemManager.getDungeonToken(this.dungeon_id, this.name, this.customEnum);
		int dungeon_id;
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				dungeon_id = ItemUtil.getInt(itemStack, "dungeon_id");
				if(this.dungeon_id!=dungeon_id) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void applyGamerules(World world, Difficulty difficulty, boolean isInstance){
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		if(isInstance){
			world.setGameRuleValue("doMobCampSpawning", "true");
			for(Entry<String,String> gamerule : this.gamerules.entrySet()){
				world.setGameRuleValue(gamerule.getKey(), gamerule.getValue());
			}
		}
		world.setDifficulty(difficulty);
	}
	
	private void initiateInstance(int instance_id, Player player, Difficulty difficulty){
		String worldName = "dungeon_instance_"+instance_id;
		World world = Bukkit.getWorld(worldName);
		if(world==null){
			if(WorldTransferManager.localWorldExists(worldName)){
				world = WorldManager.loadWorld(worldName);
			}
			else{
				String template_name = getTemplateName();
				HTTPRequest remoteCheck = WorldTransferManager.remoteWorldExists(template_name);
				remoteCheck.onFinish(()-> {
					if (remoteCheck.getResponse().equals("true")) {
						//World is present on remote disk, download it, load it, then teleport into it
						WorldTransferObserver observer = WorldTransferManager.downloadWorld(player, this.getTemplateName(), worldName);
						observer.addOnFinishListener(new Runnable(){
							public void run(){
								initiateInstance(instance_id, player, difficulty);
							}
						});
					}
					else{
						player.sendMessage("["+this.name+ChatColor.RESET+"] "+ChatColor.RED+"Dieser Dungeon ist noch nicht eingerichtet.");
					}
				});
				return;
			}
		}
		if(world==null){
			player.sendMessage("["+this.name+ChatColor.RESET+"] "+ChatColor.RED+"Konnte die Instanz nicht öffnen. Bitte kontaktiere die Spielleitung.");
			return;
		}
		WorldGuardPlugin.inst().reloadConfig();
		RegionManager regionManager = WorldGuardManager.getRegionManager(world);
		for(ProtectedRegion protectedRegion : regionManager.getRegions().values()){
			protectedRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
		};
		this.applyGamerules(world, difficulty, true);
		DungeonInstance dungeonInstance = DungeonInstance.create(instance_id, this, difficulty, world, random.nextLong());
		dungeonInstance.getPlayerManager().join(player);
	}
	
	public static Dungeon get(DungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return dungeons.get(dungeonInstance.getDungeonId());
	}

	public static Dungeon get(int dungeon_id){
		return dungeons.get(dungeon_id);
	}

	public static void get(int dungeon_id, Consumer<Dungeon> callback){
		if(dungeons.containsKey(dungeon_id)){
			callback.accept(dungeons.get(dungeon_id));
			return;
		}
		Dungeon.load(dungeon_id, callback);
	}

	public static Dungeon get(String identifier){
		return dungeons.values()
				.stream()
				.filter(d->d.getName().toLowerCase().contains(identifier.toLowerCase()))
				.findAny()
				.orElse(null);
	}
	
	/**
	 * Searches for a Dungeon with a name containing the provided identifier.
	 * The search starts at the Dungeon with lowest ID.
	 * @param identifier - The key to look for in the name
	 */
	public static void get(String identifier, Consumer<Dungeon> callback){
		for(Dungeon dungeon : dungeons.values()){
			if(dungeon.getName().toLowerCase().contains(identifier.toLowerCase())){
				callback.accept(dungeon);
				return;
			}
		}
		Dungeon.load(identifier, callback);
	}
	
	private static void load(int dungeon_id, Consumer<Dungeon> callback){
		Dungeon.load(new String[]{
				"id="+dungeon_id
		}, callback);
	}

	private static void load(String identifier, Consumer<Dungeon> callback){
		Dungeon.load(new String[]{
				"identifier="+identifier
		}, callback);
	}
	
	private static void load(String[] args, Consumer<Dungeon> callback){
		HTTPRequest request = DataSource.getResponse(AdventureDungeonsPlugin.getInstance(), "get_dungeon.php", args);
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("dungeon")){
				Bukkit.getLogger().info("[AdventureDungeons] Konnte den Dungeon ("+StringUtils.join(args,", ")+") nicht laden.");
				callback.accept(null);
				return;
			}
			ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("dungeon");
			Dungeon result = new Dungeon(dataSection);
			dungeons.put(result.getDungeonId(), result);
			callback.accept(result);
		});
	}
}
