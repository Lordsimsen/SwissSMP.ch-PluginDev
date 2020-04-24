package ch.swisssmp.zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.zones.zoneinfos.AllowSpawnZoneInfo;
import ch.swisssmp.zones.zoneinfos.GenericZoneInfo;
import ch.swisssmp.zones.zoneinfos.NoCreeperZoneInfo;
import ch.swisssmp.zones.zoneinfos.NoHostileZoneInfo;
import ch.swisssmp.zones.zoneinfos.ProjectZoneInfo;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public enum ZoneType {
	GENERIC("Allgemeine Zone", "GENERIC_ZONE_MAP", "genericzone", null),
	PROJECT("Projektplan", "PROJECT_ZONE_MAP", "projectzone", RegionType.CUBOID),
	NO_HOSTILE("Mobfreie Zone", "ANTI_HOSTILE_ZONE_MAP", "antihostilezone", RegionType.POLYGON),
	NO_CREEPER("Creeperfreie Zone", "ANTI_CREEPER_ZONE_MAP", "anticreeperzone", RegionType.POLYGON),
	ALLOW_SPAWN("Umkehrzone", "INVERSION_ZONE_MAP", "inversionzone", RegionType.CUBOID)
	;
	
	private final String name;
	private final String customEnum;
	private final String permission;
	private final RegionType regionType;
	
	private ZoneType(String name, String custom_enum, String permission, RegionType regionType){
		this.name = name;
		this.customEnum = custom_enum;
		this.permission = permission;
		this.regionType = regionType;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPermissionIdentifier(){
		return permission;
	}
	
	public RegionType getRegionType(){
		return regionType;
	}
	
	public List<String> getDescription(){
		List<String> result = new ArrayList<String>();
		switch(this){
		case ALLOW_SPAWN:
			result.add("In dieser Region");
			result.add("spawnen Mobs normal.");
			break;
		case GENERIC:
			result.add("Normale WorldGuard Zone");
			break;
		case NO_CREEPER:
			result.add("In dieser Region");
			result.add("spawnen keine Creeper.");
			result.add("Creeper-Explosionen");
			result.add("beschädigen keine Blöcke.");
			break;
		case NO_HOSTILE:
			result.add("In dieser Region");
			result.add("spawnen keine Monster.");
			result.add("Creeper-Explosionen");
			result.add("beschädigen keine Blöcke.");
			break;
		case PROJECT:
			break;
		default:
			break;
		}
		return result;
	}
	
	public ItemStack getCost(){
		switch(this){
		case ALLOW_SPAWN: return new ItemStack(Material.REDSTONE, 8);
		case GENERIC: return new ItemStack(Material.EMERALD, 8);
		case NO_CREEPER: return new ItemStack(Material.CACTUS, 8);
		case NO_HOSTILE: return new ItemStack(Material.GOLD_INGOT, 8);
		case PROJECT: return new ItemStack(Material.INK_SAC, 8);
		default: return null;
		}
	}
	
	public String getHint(){
		switch(this){
		case ALLOW_SPAWN:
		case PROJECT: return "Linksklick: Punkt 1 | Rechtsklick: Punkt 2";
		default: return "Linksklick: Punkt hinzufügen | Rechtsklick: Punkt entfernen";
		}
	}
	
	public static ZoneType get(String arg0){
		try{
			return ZoneType.valueOf(arg0);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public CustomItemBuilder getItemBuilder() {
		return CustomItems.getCustomItemBuilder(this.customEnum);
	}
	
	public void apply(ItemStack itemStack) {
		ItemUtil.setString(itemStack, "ZoneType", this.toString());
	}
	
	public ZoneInfo createZoneInfo(){
		switch(this){
		case ALLOW_SPAWN: return new AllowSpawnZoneInfo();
		case GENERIC: return new GenericZoneInfo();
		case NO_CREEPER: return new NoCreeperZoneInfo();
		case NO_HOSTILE: return new NoHostileZoneInfo();
		case PROJECT: return new ProjectZoneInfo();
		default: return null;
		}
	}
	
	public ItemStack toItemStack(){
		return createZoneInfo().createItemStack();
	}
	
	public String getNewLabel(){
		switch(this){
		case PROJECT: return ChatColor.RESET+"Neuer";
		default: return ChatColor.RESET+"Neue";
		}
	}
	
	public void applyFlags(ProtectedRegion region){
		Set<EntityType> entityTypes = new HashSet<EntityType>();
		switch(this){
		case GENERIC:
			return;
		case NO_CREEPER:
			entityTypes.add(new EntityType("CREEPER"));
			region.setFlag(Flags.CREEPER_EXPLOSION, State.DENY);
			region.setPriority(0);
			break;
		case NO_HOSTILE:
			entityTypes.add(new EntityType("CREEPER"));
			entityTypes.add(new EntityType("ZOMBIE"));
			entityTypes.add(new EntityType("STRAY"));
			entityTypes.add(new EntityType("ZOMBIE_PIGMAN"));
			entityTypes.add(new EntityType("ZOMBIE_VILLAGER"));
			entityTypes.add(new EntityType("SKELETON"));
			entityTypes.add(new EntityType("HUSK"));
			entityTypes.add(new EntityType("SPIDER"));
			entityTypes.add(new EntityType("CAVE_SPIDER"));
			entityTypes.add(new EntityType("DROWNED"));
			entityTypes.add(new EntityType("PHANTOM"));
			entityTypes.add(new EntityType("ENDERMAN"));
			entityTypes.add(new EntityType("SKELETON_HORSE"));
			entityTypes.add(new EntityType("SILVERFISH"));
			region.setFlag(Flags.CREEPER_EXPLOSION, State.DENY);
			region.setPriority(1);
			break;
		case ALLOW_SPAWN:
			region.setFlag(Flags.CREEPER_EXPLOSION, null);
			region.setPriority(2);
		default:
			break;
		}
		region.setFlag(Flags.DENY_SPAWN, entityTypes);
	}
}
