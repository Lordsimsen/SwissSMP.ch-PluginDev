package ch.swisssmp.resourcepack;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

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
			event.getPlayer().setInvulnerable(false);
			ResourcepackManager.playerMap.remove(event.getPlayer());
			HTTPRequest request = DataSource.getResponse(ResourcepackManager.getInstance(), "declined.php", new String[]{
				"player="+event.getPlayer().getUniqueId()	
			});
			request.onFinish(()->{
				YamlConfiguration yamlConfiguration = request.getYamlResponse();
				if(yamlConfiguration.contains("message")){
					event.getPlayer().sendMessage(yamlConfiguration.getString("message"));
				}
			});
		}
		else if(event.getStatus()==Status.SUCCESSFULLY_LOADED || event.getStatus()==Status.FAILED_DOWNLOAD){
			event.getPlayer().setInvulnerable(false);
			if(event.getStatus()==Status.FAILED_DOWNLOAD){
				Bukkit.getLogger().info("[ResourcepackManager] Resourcepack "+URLEncoder.encode(ResourcepackManager.playerMap.get(event.getPlayer()))+" konnte bei "+event.getPlayer().getName()+" nicht geladen werden.");
				ResourcepackManager.playerMap.remove(event.getPlayer());
			}
		}
	}
}
