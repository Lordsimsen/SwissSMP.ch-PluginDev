package ch.swisssmp.zones.zoneinfos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import ch.swisssmp.world.WorldManager;
import ch.swisssmp.zones.MemberRole;
import ch.swisssmp.zones.WorldGuardUtil;
import ch.swisssmp.zones.ZoneContainer;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.ZoneUtil;
import ch.swisssmp.zones.editor.ActionResult;

public abstract class ZoneInfo {

	private World world;
	private String zoneId;
	private final ZoneType zoneType;
	private ProtectedRegion region;
	private ZoneInfoState state;

	private String name; //user defined display name
	private HashMap<UUID,MemberRole> members = new HashMap<UUID, MemberRole>();
	
	protected ZoneInfo(World world, String regionId, ZoneType zoneType, ConfigurationSection dataSection){
		this.world = world;
		this.zoneId = regionId;
		this.zoneType = zoneType;
		this.state = world!=null ? ZoneInfoState.ACTIVE : ZoneInfoState.INACTIVE;
	}
	
	public ZoneInfo(ZoneType zoneType){
		this.world = null;
		this.zoneId = null;
		this.zoneType = zoneType;
		this.state = ZoneInfoState.PENDING;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDisplayName(){
		boolean isIdUUID;
		try{
			isIdUUID = UUID.fromString(zoneId)!=null;
		}
		catch(Exception e){
			isIdUUID = false;
		}
		if(name!=null){
			return ChatColor.AQUA + name;
		}
		if(state==ZoneInfoState.PENDING){
			return zoneType==ZoneType.GENERIC ? ChatColor.RESET+"Leerer Zonenplan" : zoneType.getNewLabel()+" "+zoneType.getName();
		}
		return ChatColor.AQUA + (!isIdUUID ? zoneId : "Unbenannte Zone");
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getId(){
		return zoneId;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public ZoneType getZoneType(){
		return zoneType;
	}
	
	public ItemStack createItemStack(){
		ItemStack result = new ItemStack(Material.AIR);
		this.apply(result);
		return result;
	}
	
	public String createId(World world){
		if(zoneId!=null) return zoneId;
		this.world = world;
		zoneId = UUID.randomUUID().toString();
		return zoneId;
	}
	
	public void apply(ItemStack itemStack){
		if(itemStack==null) return;
		CustomItemBuilder itemBuilder = zoneType!=null ? zoneType.getItemBuilder() : null;
		if(itemBuilder!=null){
			itemBuilder.update(itemStack);
		}
		
		//Apply ItemMeta
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(this.getDisplayName());
		List<String> description = new ArrayList<String>();
		if(this.zoneId!=null) description.add(ChatColor.GRAY+zoneType.getName());
		if(region!=null){
			description.add(ChatColor.GRAY+"Welt: "+WorldManager.getDisplayName(world));
			BlockVector3 from = region.getMinimumPoint();
			BlockVector3 to = region.getMaximumPoint();
			description.add(ChatColor.GRAY+"Von: "+from.getBlockX()+", "+from.getBlockY()+", "+from.getBlockZ());
			description.add(ChatColor.GRAY+"Bis: "+to.getBlockX()+", "+to.getBlockY()+", "+to.getBlockZ());
		}
		itemMeta.setLore(description);
		itemStack.setItemMeta(itemMeta);
		
		//Apply NBT
		NBTTagCompound nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null) nbtTag = new NBTTagCompound();
		if(zoneId!=null && world!=null){
			NBTTagCompound zoneSection = new NBTTagCompound();
			zoneSection.setString("Id", this.zoneId);
			zoneSection.setString("World", this.world.getName());
			nbtTag.set("Zone", zoneSection);
		}
		ItemUtil.setData(itemStack, nbtTag);
	}
	
	public void apply(ProtectedRegion region){
		if(region==null) return;
		this.zoneType.applyFlags(region);
	}
	
	public void setRegion(World world, ProtectedRegion region){
		this.world = world;
		this.region = region;
	}
	
	public ProtectedRegion getRegion(){
		return this.region;
	}
	
	public void addMember(UUID uuid, MemberRole role){
		this.members.put(uuid, role);
	}
	
	public void setRole(UUID uuid, MemberRole role){
		this.members.put(uuid, role);
	}
	
	public void removeMember(UUID uuid){
		members.remove(uuid);
	}
	
	public MemberRole getRole(UUID uuid){
		return members.get(uuid);
	}
	
	public Map<UUID,MemberRole> getMembers(){
		return Collections.unmodifiableMap(this.members);
	}
	
	public Collection<UUID> getMembers(MemberRole role){
		return members.entrySet().stream().filter(e->e.getValue()==role).map(e->e.getKey()).collect(Collectors.toList());
	}
	
	public static Collection<UUID> getMembers(Map<UUID,MemberRole> members, MemberRole role){
		return members.entrySet().stream().filter(e->e.getValue()==role).map(e->e.getKey()).collect(Collectors.toList());
	}
	
	public ZoneInfoState getState(){
		return state;
	}
	
	public abstract RegionType getRegionType();
	public abstract List<Location> getPoints();
	public abstract ZoneSnapshot getSnapshot();
	public abstract void applySnapshot(ZoneSnapshot snapshot);
	public abstract ActionResult edit(Location location, Action action);
	public abstract ItemStack getCost(Action action);
	
	protected abstract ProtectedRegion createRegion();
	
	protected static void applyMembers(ProtectedRegion region, Map<UUID,MemberRole> members){
		DefaultDomain ownersDomain = new DefaultDomain();
		DefaultDomain membersDomain = new DefaultDomain();
		for(UUID member : getMembers(members, MemberRole.OWNER)){
			ownersDomain.addPlayer(member);
		}
		for(UUID member : getMembers(members, MemberRole.MEMBER)){
			membersDomain.addPlayer(member);
		}
		for(UUID member : getMembers(members, MemberRole.JUNIOR)){
			membersDomain.addPlayer(member);
		}
		region.setOwners(ownersDomain);
		region.setMembers(membersDomain);
	}
	
	public boolean remove(){
		if(world==null || zoneId==null){
			state = ZoneInfoState.PENDING;
			return false;
		}
		RegionManager manager = WorldGuardUtil.getManager(world);
		ZoneContainer container = ZoneContainer.get(world);
		if(manager==null || container==null){
			state = ZoneInfoState.INACTIVE;
			return false;
		}
		manager.removeRegion(zoneId, RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
		container.remove(this);
		state = ZoneInfoState.MISSING;
		
		for(Player player : Bukkit.getOnlinePlayers()){
			ZoneUtil.updateZoneInfos(player.getInventory());
			if(player.getOpenInventory()==null) continue;
			ZoneUtil.updateZoneInfos(player.getOpenInventory().getTopInventory());
		}
		return true;
	}
	
	public ProtectedRegion saveChanges(){
		ProtectedRegion result = this.createRegion();
		if(result==null) return null;
		state = ZoneInfoState.ACTIVE;
		return result;
	}
	
	public void save(ConfigurationSection dataSection){
		dataSection.set("type", zoneType.toString());
		ConfigurationSection membersSection = dataSection.createSection("members");
		for(Entry<UUID,MemberRole> entry : members.entrySet()){
			membersSection.set(entry.getKey().toString(), entry.getValue().toString());
		}
	}
	
	@Override
	public String toString(){
		return "(world: "+(world!=null ? world.getName() : null)+", id: "+zoneId+", type: "+zoneType.toString()+")";
	}
	
	public static String getId(ItemStack itemStack){
		if(itemStack==null) return null;
		NBTTagCompound data = ItemUtil.getData(itemStack);
		if(data==null || !data.hasKey("region_id")) return null;
		return data.getString("region_id");
	}
	
	public static ZoneInfo get(ItemStack itemStack){
		NBTTagCompound nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null || !nbtTag.hasKey("zone_type")) return null;
		String regionId = nbtTag.getString("region_id");
		String worldName = nbtTag.getString("world");
		ZoneInfoState state;
		if(regionId!=null && worldName!=null){
			World world = Bukkit.getWorld(worldName);
			ZoneContainer container = world!=null ? ZoneContainer.get(world) : null;
			ZoneInfo existing = container!=null ? container.getZone(regionId) : null;
			if(existing!=null){
				return existing;
			}
			state = container!=null ? ZoneInfoState.MISSING : ZoneInfoState.INACTIVE;
		}
		else{
			state = ZoneInfoState.PENDING;
		}
		ZoneType zoneType = ZoneType.get(nbtTag.getString("zone_type"));
		ZoneInfo zoneInfo = zoneType.createZoneInfo();
		ItemMeta itemMeta = itemStack.getItemMeta();
		String displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "";
		if(displayName.startsWith(ChatColor.AQUA+"")) zoneInfo.setName(displayName.replace(ChatColor.AQUA+"", ""));
		zoneInfo.state = state;
		return zoneInfo;
	}
	
	public static ZoneInfo get(World world, String regionId){
		ZoneContainer container = ZoneContainer.get(world);
		if(container==null) return null;
		return container.getZone(regionId);
	}
	
	public static ZoneInfo load(World world, String regionId, ConfigurationSection dataSection){
		ZoneType zoneType = ZoneType.get(dataSection.getString("type"));
		ZoneInfo result = ZoneInfo.create(zoneType, world, regionId, dataSection);
		if(dataSection.contains("members")){
			ConfigurationSection membersSection = dataSection.getConfigurationSection("members");
			for(String key : membersSection.getKeys(false)){
				UUID uuid;
				MemberRole role;
				try{
					uuid = UUID.fromString(key);
					role = MemberRole.get(membersSection.getString(key));
				}
				catch(Exception e){
					continue;
				}
				result.members.put(uuid, role);
			}
		}
		return result;
	}
	
	public static void clear(ItemStack itemStack){
		ZoneInfo override = ZoneType.GENERIC.createZoneInfo();
		override.apply(itemStack);
	}
	
	protected static void saveBlock(ConfigurationSection dataSection, String key, Block block){
		ConfigurationSection blockSection = dataSection.createSection(key);
		blockSection.set("x", block.getX());
		blockSection.set("y", block.getY());
		blockSection.set("z", block.getZ());
	}
	
	private static ZoneInfo create(ZoneType zoneType, World world, String regionId, ConfigurationSection dataSection){
		switch(zoneType){
		case ALLOW_SPAWN: return new AllowSpawnZoneInfo(world, regionId, dataSection);
		case GENERIC: return new GenericZoneInfo(world, regionId, dataSection);
		case NO_CREEPER: return new NoCreeperZoneInfo(world, regionId, dataSection);
		case NO_HOSTILE: return new NoHostileZoneInfo(world, regionId, dataSection);
		case PROJECT: return new ProjectZoneInfo(world, regionId, dataSection);
		default: return null;
		}
	}
}
