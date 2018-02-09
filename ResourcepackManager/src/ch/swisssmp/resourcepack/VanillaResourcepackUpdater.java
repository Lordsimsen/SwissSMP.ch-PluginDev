package ch.swisssmp.resourcepack;

import java.io.IOException;
import java.net.URLEncoder;

import org.bukkit.entity.Player;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class VanillaResourcepackUpdater extends ResourcepackUpdater {

	public VanillaResourcepackUpdater() {
	}

	@Override
	public void updateResourcepack(Player player) {
		if(player==null) return;
		try {
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("resourcepack/get.php", new String[]{
					"player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8"),
					"world="+URLEncoder.encode(player.getWorld().getName(), "utf-8")
			});
			if(yamlConfiguration!=null && yamlConfiguration.contains("resourcepack")){
				String resourcepack = yamlConfiguration.getString("resourcepack");
				ResourcepackManager.setResourcepack(player, resourcepack);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
