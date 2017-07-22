package ch.swisssmp.resourcepack;

import java.net.URLEncoder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		ResourcepackManager.updateResourcepack(event.getPlayer(), 5L);
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		ResourcepackManager.playerMap.remove(event.getPlayer());
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		ResourcepackManager.updateResourcepack(event.getPlayer(), 20L);
	}
	@EventHandler(ignoreCancelled=true)
	private void onResourcepackChange(PlayerResourcePackStatusEvent event){
		if(event.getStatus()==Status.ACCEPTED){
			event.getPlayer().setInvulnerable(true);
		}
		else if(event.getStatus()==Status.DECLINED){
			ResourcepackManager.playerMap.remove(event.getPlayer());
			YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("resourcepack/declined.php", new String[]{
				"player="+event.getPlayer().getUniqueId()	
			});
			if(yamlConfiguration.contains("message")){
				event.getPlayer().sendMessage(yamlConfiguration.getString("message"));
			}
		}
		else if(event.getStatus()==Status.SUCCESSFULLY_LOADED || event.getStatus()==Status.FAILED_DOWNLOAD){
			event.getPlayer().setInvulnerable(false);
			if(event.getStatus()==Status.FAILED_DOWNLOAD){
				try {
					DataSource.getResponse("server/alerts.php", new String[]{
							"plugin=ResourcepackManager",
							"player="+event.getPlayer().getUniqueId(),
							"message=Resourcepack "+URLEncoder.encode(ResourcepackManager.playerMap.get(event.getPlayer()), "utf-8")+" konnte nicht geladen werden."
						});
				}
				catch(Exception e){
					e.printStackTrace();
				}
				ResourcepackManager.playerMap.remove(event.getPlayer());
			}
		}
	}
}
