package ch.swisssmp.zones.zoneinfos;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.ZoneType;

public class NoCreeperZoneInfo extends PolygonZoneInfo {

	public NoCreeperZoneInfo(World world, String regionId, ConfigurationSection dataSection) {
		super(world, regionId, ZoneType.NO_CREEPER, dataSection);
		
	}

	public NoCreeperZoneInfo() {
		super(ZoneType.NO_CREEPER);
		
	}
}
