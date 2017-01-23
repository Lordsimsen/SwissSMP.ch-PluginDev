package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.Bukkit;

import org.bukkit.BanList.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoitem.MmoItemManager;
import ch.swisssmp.craftmmo.mmoplayer.MmoDelayedLoginTask;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoPlayerLoginEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
	
	private final Player player;
	
	public MmoPlayerLoginEvent(Player player){
		this.player = player;
		if(player!=null){
			Runnable task = new MmoDelayedLoginTask(player);
			Bukkit.getScheduler().runTaskLater(Main.plugin, task, 20L);
			MmoItemManager.updateInventory(player.getInventory());
		}
		String response = MmoResourceManager.getResponse("login.php", new String[]{
			"player_uuid="+player.getUniqueId().toString(),
			"player_name="+player.getName(),
		});
		if(response.equals("1")){
			Bukkit.getBanList(Type.NAME).addBan(player.getName(), "", null, "Admin");
		}
	}
	
	public Player getPlayer(){
		return this.player;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
