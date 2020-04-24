package ch.swisssmp.zones.zoneinfos;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.zones.ZoneType;

public class ProjectZoneInfo extends CuboidZoneInfo {

	public ProjectZoneInfo(World world, String regionId, ConfigurationSection dataSection) {
		super(world, regionId, ZoneType.PROJECT, dataSection);
		
	}

	public ProjectZoneInfo() {
		super(ZoneType.PROJECT);
		
	}
}
