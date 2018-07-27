package ch.swisssmp.resourcepack;

import org.bukkit.entity.Player;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class VanillaResourcepackUpdater extends ResourcepackUpdater {

	public VanillaResourcepackUpdater() {
	}

	@Override
	public void updateResourcepack(Player player) {
		if(player==null) return;
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
				"player="+URLEncoder.encode(player.getUniqueId().toString()),
				"world="+URLEncoder.encode(player.getWorld().getName())
		});
		if(yamlConfiguration!=null && yamlConfiguration.contains("resourcepack")){
			String resourcepack = yamlConfiguration.getString("resourcepack");
			ResourcepackManager.setResourcepack(player, resourcepack);
		}
	}

}
