package ch.swisssmp.zones.zoneinfos;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.ZoneType;

public class AllowSpawnZoneInfo extends CuboidZoneInfo {

	public AllowSpawnZoneInfo(World world, String regionId, ConfigurationSection dataSection) {
		super(world, regionId, ZoneType.ALLOW_SPAWN, dataSection);
		
	}

	public AllowSpawnZoneInfo() {
		super(ZoneType.ALLOW_SPAWN);
		
	}
}
