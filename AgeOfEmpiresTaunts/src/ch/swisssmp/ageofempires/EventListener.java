package ch.swisssmp.ageofempires;

import java.util.Optional;

import ch.swisssmp.text.HoverEvent;
import ch.swisssmp.text.RawBase;
import ch.swisssmp.text.RawText;
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
		RawBase message = new RawText(
				new RawText("["),
				new RawText(event.getPlayer().getDisplayName())
					.hoverEvent(HoverEvent.showEntity(event.getPlayer())),
				new RawText("] "),
				new RawText(taunt.getKey()).hoverEvent(HoverEvent.showText(
						new RawText(taunt.getDisplay())
							.color(ChatColor.YELLOW)
				))
		);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			TauntSetting setting = PlayerSettings.get(player);
			if(setting==TauntSetting.ALLOW) {
				player.playSound(player.getEyeLocation().add(new Vector(0,2,0)), soundId, SoundCategory.VOICE, 2f, 1);
			}
			player.spigot().sendMessage(message.spigot());
		}
	}
}
