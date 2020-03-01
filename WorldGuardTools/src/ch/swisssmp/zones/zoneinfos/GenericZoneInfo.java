package ch.swisssmp.zones.zoneinfos;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.ZoneType;

public class GenericZoneInfo extends PolygonZoneInfo {

	public GenericZoneInfo(World world, String regionId, ConfigurationSection dataSection) {
		super(world, regionId, ZoneType.GENERIC, dataSection);
		
	}

	public GenericZoneInfo() {
		super(ZoneType.GENERIC);
		
	}
}
