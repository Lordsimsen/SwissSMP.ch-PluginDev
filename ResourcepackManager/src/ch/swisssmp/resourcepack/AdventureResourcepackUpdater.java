package ch.swisssmp.resourcepack;

import java.io.IOException;
import java.net.URLEncoder;

import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class AdventureResourcepackUpdater extends ResourcepackUpdater{

	public AdventureResourcepackUpdater() {
		
	}

	@Override
	public void updateResourcepack(Player player) {
		if(player==null) return;
		try {
			DungeonInstance dungeonInstance = Dungeon.getInstance(player);
			YamlConfiguration yamlConfiguration;
			if(dungeonInstance==null){
				yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
						"player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8"),
						"world="+URLEncoder.encode(player.getWorld().getName(), "utf-8")
				});
			}
			else{
				yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
						"player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8"),
						"dungeon="+URLEncoder.encode(String.valueOf(dungeonInstance.getDungeonId()), "utf-8")
				});
			}
			if(yamlConfiguration!=null && yamlConfiguration.contains("resourcepack")){
				String resourcepack = yamlConfiguration.getString("resourcepack");
				ResourcepackManager.setResourcepack(player, resourcepack);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
