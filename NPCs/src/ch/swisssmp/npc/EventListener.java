package ch.swisssmp.npc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import ch.swisssmp.npc.event.PlayerInteractNPCEvent;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		NPCInstance npc = NPCInstance.get(event.getRightClicked());
		if(npc==null){
			return;
		}
		PlayerInteractNPCEvent interaction = new PlayerInteractNPCEvent(event.getPlayer(), npc, event.getHand());
		Bukkit.getPluginManager().callEvent(interaction);
		event.setCancelled(interaction.preventDefault());
		if(interaction.isCancelled()) return;
		
		if(event.getPlayer().isSneaking() && event.getPlayer().hasPermission("npc.admin")){
			event.setCancelled(true);
			NPCEditorView.open(event.getPlayer(), npc);
			return;
		}
	}
}
