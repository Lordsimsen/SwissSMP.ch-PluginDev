package ch.swisssmp.ageofempires;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Vector;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.text.RawTextObject;
import ch.swisssmp.text.properties.ColorProperty;
import ch.swisssmp.text.properties.ColorProperty.Color;
import ch.swisssmp.text.properties.ExtraProperty;
import ch.swisssmp.text.properties.HoverEventProperty;

public class EventListener implements Listener {
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("aoe");
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	private void OnPlayerAsyncChat(AsyncPlayerChatEvent event) {
		if(!event.getPlayer().hasPermission("aoetaunts.use")) {
			return;
		}
		Optional<TauntEntry> tauntQueryResult = TauntEntry.get(event.getMessage());
		if(!tauntQueryResult.isPresent()) return;
		TauntEntry taunt = tauntQueryResult.get();
		String soundId = taunt.getAudio();

		event.setCancelled(true);
		RawTextObject message = new RawTextObject("["+event.getPlayer().getDisplayName()+ChatColor.RESET+"] ");
		RawTextObject tooltippedPart = new RawTextObject(taunt.getKey());
		RawTextObject tooltipMessage = new RawTextObject(taunt.getDisplay(), new ColorProperty(Color.YELLOW));
		tooltippedPart.add(new HoverEventProperty(tooltipMessage));
		message.add(new ExtraProperty(tooltippedPart));
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			TauntSetting setting = PlayerSettings.get(player);
			if(setting==TauntSetting.ALLOW) {
				player.playSound(player.getEyeLocation().add(new Vector(0,2,0)), soundId, SoundCategory.VOICE, 2f, 1);
			}
			player.spigot().sendMessage(message.toSpigot());
		}
	}
}
