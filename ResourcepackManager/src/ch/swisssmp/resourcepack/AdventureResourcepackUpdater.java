package ch.swisssmp.resourcepack;

import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.DungeonInstance;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class AdventureResourcepackUpdater extends ResourcepackUpdater{

	public AdventureResourcepackUpdater() {
		
	}

	@Override
	public void updateResourcepack(Player player) {
		if(player==null) return;
		DungeonInstance dungeonInstance = DungeonInstance.get(player);
		YamlConfiguration yamlConfiguration;
		if(dungeonInstance==null){
			yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
					"player="+URLEncoder.encode(player.getUniqueId().toString()),
					"world="+URLEncoder.encode(player.getWorld().getName())
			});
		}
		else{
			yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
					"player="+URLEncoder.encode(player.getUniqueId().toString()),
					"dungeon="+URLEncoder.encode(String.valueOf(dungeonInstance.getDungeonId()))
			});
		}
		if(yamlConfiguration!=null && yamlConfiguration.contains("resourcepack")){
			String resourcepack = yamlConfiguration.getString("resourcepack");
			ResourcepackManager.setResourcepack(player, resourcepack);
		}
	}

}
