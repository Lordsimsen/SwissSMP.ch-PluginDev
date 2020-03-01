package ch.swisssmp.zones.zoneinfos;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.ZoneType;

public class NoHostileZoneInfo extends PolygonZoneInfo {

	public NoHostileZoneInfo(World world, String regionId, ConfigurationSection dataSection) {
		super(world, regionId, ZoneType.NO_HOSTILE, dataSection);
		
	}

	public NoHostileZoneInfo() {
		super(ZoneType.NO_HOSTILE);
		
	}
}
