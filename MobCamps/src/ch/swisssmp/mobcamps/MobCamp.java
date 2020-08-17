package ch.swisssmp.mobcamps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sqlite.util.StringUtils;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.random.RandomItemUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.EntityUtil;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class MobCamp {
	private static HashMap<Integer,MobCamp> camps = new HashMap<Integer,MobCamp>();
	
	private final int camp_id;
	private String name;
	private int spawnRadius;
	private int maxNearbyEntities;
	private int requiredPlayerRange;
	private final Inventory contents;
	private final Random random = new Random();
	
	private MobCamp(ConfigurationSection dataSection){
		this.camp_id = dataSection.getInt("id");
		this.name = dataSection.getString("name");
		this.spawnRadius = dataSection.getInt("spawn_radius");
		this.maxNearbyEntities = dataSection.getInt("max_nearby_entities");
		this.requiredPlayerRange = dataSection.getInt("required_player_range");
		this.contents = Bukkit.createInventory(null, 54, this.name);
		if(dataSection.contains("contents")){
			ConfigurationSection contentsSection = dataSection.getConfigurationSection("contents");
			ConfigurationSection contentSection;
			int slot;
			ItemStack itemStack;
			for(String key : contentsSection.getKeys(false)){
				contentSection = contentsSection.getConfigurationSection(key);
				slot = contentSection.getInt("slot");
				itemStack = contentSection.getItemStack("item");
				if(itemStack==null)continue;
				this.contents.setItem(slot, itemStack);
			}
		}
		camps.put(this.camp_id, this);
	}
	
	public int getCampId(){
		return this.camp_id;
	}
	
	public void setName(String name){
		this.name = name;
		this.save();
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setSpawnRadius(int spawnRadius){
		this.spawnRadius = Math.abs(spawnRadius);
		this.save();
	}
	
	public int getSpawnRadius(){
		return this.spawnRadius;
	}
	
	public void setMaxNearbyEntities(int maxNearbyEntities){
		this.maxNearbyEntities = Math.abs(maxNearbyEntities);
		this.save();
	}
	
	public int getMaxNearbyEntities(){
		return this.maxNearbyEntities;
	}
	
	public void setRequiredPlayerRange(int requiredPlayerRange){
		this.requiredPlayerRange = Mathf.clamp(requiredPlayerRange, 16, 257);
		this.save();
	}
	
	public int getRequiredPlayerRange(){
		return this.requiredPlayerRange;
	}
	
	public Inventory getContents(){
		return this.contents;
	}
	
	public void openEditor(Player player){
		MobCampEditor.open(player, this);
	}
	
	public ItemStack getInventoryToken(int amount){
		String displayName = ChatColor.LIGHT_PURPLE+this.getName();
		CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder("MOB_CAMP");
		customItemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		customItemBuilder.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		customItemBuilder.setUnbreakable(true);
		customItemBuilder.setAmount(amount);
		customItemBuilder.setDisplayName(displayName);
		customItemBuilder.setLore(this.getInfo());
		ItemStack result = customItemBuilder.build();
		ItemUtil.setInt(result, "mob_camp", this.camp_id);
		return result;
	}
	
	public List<String> getInfo(){
		ArrayList<String> result = new ArrayList<String>();
		result.add(ChatColor.GRAY+"Spawn Radius: "+ChatColor.WHITE+this.spawnRadius+(this.spawnRadius==1?" Block":" Blöcke"));
		result.add(ChatColor.GRAY+"Auslöser: "+ChatColor.WHITE+this.requiredPlayerRange+" Blöcke");
		result.add(ChatColor.GRAY+"Spawn Limit: "+ChatColor.WHITE+this.maxNearbyEntities+(this.maxNearbyEntities==1?" Entity":" Entities"));
		int totalCount = 0;
		String line;
		for(ItemStack itemStack : this.contents){
			if(itemStack==null) continue;
			if(result.size()<3){
				line = RandomItemUtil.getDescriptiveItemString(itemStack);
				result.add(line);
			}
			totalCount++;
		}
		if(result.size()<totalCount){
			result.add(ChatColor.GRAY+""+ChatColor.ITALIC+"Und "+(totalCount-result.size())+" mehr...");
		}
		if(result.size()==0){
			result.add(ChatColor.WHITE+"Leeres Mob Camp");
		}
		return result;
	}
	
	public void save(){
		List<String> arguments = new ArrayList<String>();
		ItemStack itemStack;
		int slot;
		int index = 0;
		for(int x = 0; x < 9; x++){
			for(int y = 0; y < 6; y++){
				slot = x*6+y;
				itemStack = this.contents.getItem(slot);
				if(itemStack==null) continue;
				arguments.add("contents[item_"+index+"][item]="+URLEncoder.encode(SwissSMPUtils.encodeItemStack(itemStack)));
				arguments.add("contents[item_"+index+"][slot]="+slot);
				index++;
			}
		}
		DataSource.getResponse(MobCamps.getInstance(), "save_camp.php", new String[]{
			"id="+this.camp_id,
			"name="+URLEncoder.encode(name),
			"spawn_radius="+this.spawnRadius,
			"max_nearby_entities="+this.maxNearbyEntities,
			"required_player_range="+this.requiredPlayerRange,
			StringUtils.join(arguments, "&")
		});
		this.updateInstances();
		this.updateTokens();
	}
	
	public boolean canSpawn(Entity spawner){
		if(spawner.getPassengers().size()<1) return false;
		Entity display = spawner.getPassengers().get(0);
		double closestDistance = Double.MAX_VALUE;
		double minDistanceSquared = Math.pow(16, 2);
		double currentDistance;
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player.getWorld()!=spawner.getWorld()) continue;
			currentDistance = player.getLocation().distanceSquared(display.getLocation());
			if(currentDistance<minDistanceSquared) return false;
			closestDistance = Math.min(currentDistance, closestDistance);
		}
		if(closestDistance>Math.pow(requiredPlayerRange, 2)) return false;
		return display.getNearbyEntities(16, 4, 16).size()<this.maxNearbyEntities;
	}
	
	public List<Entity> spawnAt(Location location){
		Location center = new Location(location.getWorld(), Mathf.floorToInt(location.getX())+0.5, Mathf.floorToInt(location.getY())+0.1, Mathf.floorToInt(location.getZ())+0.5);
		List<Entity> result = new ArrayList<Entity>();
		for(int i = 0; i < 8; i++){
			result.addAll(this.spawnEntitySlot(i, center));
		}
		return result;
	}
	
	private List<Entity> spawnEntitySlot(int index, Location center){
		List<Entity> result = new ArrayList<Entity>();
		ItemStack spawnEggTemplate = this.contents.getItem(index);
		if(spawnEggTemplate==null) return result;
		ItemStack spawnEgg = RandomItemUtil.buildItemStack(spawnEggTemplate);
		if(spawnEgg==null) return result;
		EntityType entityType = MobCamps.getEntityType(spawnEgg.getType());
		if(entityType==null) return result;
		World world = center.getWorld();
		List<ItemStack> equipment;
		Entity spawned;
		Location location;
		for(int i = 0; i < spawnEgg.getAmount(); i++){
			location = center.clone();
			location.add(
					(random.nextDouble()*2-1)*this.spawnRadius,
					Mathf.floorToInt((random.nextDouble()*2-1)*3),
					(random.nextDouble()*2-1)*this.spawnRadius);
			if(location.getBlock().getType()!=Material.AIR || location.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) continue;
			spawned = world.spawnEntity(center, entityType);
			this.applyEntitySettings(spawned, spawnEgg);
			if(!(spawned instanceof LivingEntity)) continue;
			equipment = this.getEntityEquipment(index);
			if(equipment.size()>0){
				EntityUtil.equip((LivingEntity)spawned, equipment);
			}
		}
		return result;
	}
	
	private void applyEntitySettings(Entity entity, ItemStack spawnEgg){
		if(!spawnEgg.hasItemMeta()) return;
		ItemMeta itemMeta = spawnEgg.getItemMeta();
		if(itemMeta.hasDisplayName()){
			entity.setCustomName(itemMeta.getDisplayName());
			entity.setCustomNameVisible(false);
		}
	}
	
	private List<ItemStack> getEntityEquipment(int index){
		List<ItemStack> result = new ArrayList<ItemStack>();
		ItemStack template;
		ItemStack itemStack;
		for(int i = 1; i < 6; i++){
			template = this.contents.getItem(index+i*9);
			if(template==null) continue;
			itemStack = RandomItemUtil.buildItemStack(template);
			if(itemStack==null) continue;
			result.add(itemStack);
		}
		return result;
	}
	
	public void updateInstances(){
		MobCampInstance.updateAll(this);
	}
	
	public void updateInstance(MobCampInstance instance){
		instance.updateState();
	}
	
	protected void updateTokens(){
		int camp_id;
		ItemStack tokenStack = this.getInventoryToken(1);
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				camp_id = ItemUtil.getInt(itemStack, "mob_camp");
				if(camp_id!=this.camp_id) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	private static void load(int camp_id, Consumer<MobCamp> callback){
		if(camps.containsKey(camp_id)){
			callback.accept(camps.get(camp_id));
			return;
		}
		MobCamp.load(new String[]{
				"id="+camp_id
		}, callback);
	}
	
	private static void load(String name, boolean createNew, Consumer<MobCamp> callback){
		for(MobCamp camp : camps.values()){
			if(camp.name.toLowerCase().equals(name.toLowerCase())){
				callback.accept(camp);
				return;
			}
		}
		MobCamp.load(new String[]{
				"name="+URLEncoder.encode(name),
				"create_missing="+(createNew?"true":"false")
		}, callback);
	}
	
	private static void load(String[] params, Consumer<MobCamp> callback){
		HTTPRequest request = DataSource.getResponse(MobCamps.getInstance(), "get_camp.php", params);
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("camp")){
				callback.accept(null);
				return;
			}
			callback.accept(new MobCamp(yamlConfiguration.getConfigurationSection("camp")));
		});
	}

	public static MobCamp get(Entity entity){
		if(entity==null) return null;
		if(entity.isInsideVehicle()) return MobCamp.get(entity.getVehicle());
		if(entity.getType()!=EntityType.ARMOR_STAND) return null;
		if(entity.getCustomName()==null || !entity.getCustomName().startsWith("§rCamp_")) return null;
		int camp_id = Integer.parseInt(entity.getCustomName().split("_")[1]);
		return MobCamp.get(camp_id);
	}

	public static MobCamp get(int camp_id){
		if(camps.containsKey(camp_id)) return camps.get(camp_id);
		return null;
	}

	public static MobCamp get(String name){
		for(MobCamp camp : camps.values()){
			if(camp.name.toLowerCase().equals(name.toLowerCase())) return camp;
		}
		return null;
	}

	public static void create(String name, Consumer<MobCamp> callback){
		MobCamp.load(name, true, callback);
	}

	public static MobCamp get(ItemStack tokenStack){
		return MobCamp.get(ItemUtil.getInt(tokenStack, "mob_camp"));
	}
}
