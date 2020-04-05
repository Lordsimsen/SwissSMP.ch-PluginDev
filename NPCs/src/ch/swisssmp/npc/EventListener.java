package ch.swisssmp.npc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import ch.swisssmp.npc.conversations.NPCConversation;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		
		NPCInstance npc = NPCInstance.get(event.getRightClicked());
		if(npc==null){
			return;
		}
		
		if(!event.getPlayer().isSneaking() && event.getHand()==EquipmentSlot.HAND) {
			
			NPCConversation current = NPCConversation.get(event.getPlayer(), npc);
			if(current!=null && !current.isFinished()) {
				current.onInteract();
				if(!current.isFinished() || current.preventDefaultOnLastInteraction()) {
					event.setCancelled(true);
					return;
				}
			}
			
			if(current==null) {
				List<String> dialog = npc.getDialog();
				if(dialog!=null && dialog.size()>0) {
					event.setCancelled(true);
					NPCConversation conversation = NPCConversation.start(npc, event.getPlayer(), 200);
					conversation.setLines(dialog);
					conversation.setPreventDefaultOnLastInteraction(false);
					return;
				}
			}
		}
		else {
		}

		boolean cancelEvent = triggerInteraction(event.getPlayer(), npc, event.getHand());
		event.setCancelled(cancelEvent);
	}
	
	private boolean triggerInteraction(Player player, NPCInstance npc, EquipmentSlot hand) {
		PlayerInteractNPCEvent interaction = new PlayerInteractNPCEvent(player, npc, hand);
		Bukkit.getPluginManager().callEvent(interaction);
		if(interaction.isCancelled()) return interaction.preventDefault();
		
		if(player.isSneaking() && player.hasPermission("npc.admin")){
			NPCEditorView.open(player, npc);
			return true;
		}
		
		return interaction.preventDefault();
	}
}
