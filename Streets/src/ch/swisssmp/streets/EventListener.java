package ch.swisssmp.streets;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;

import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener{
	@EventHandler
	private void onRegionEnter(RegionEnteredEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.SURVIVAL && player.getGameMode()!=GameMode.ADVENTURE) return;
		Street street = Street.get(event.getRegion().getId());
		if(street==null) return;
		PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, street.getSpeed(), true, false);
		player.addPotionEffect(speedEffect);
		SwissSMPler.get(player).sendActionBar(street.getStreetLabel());
	}
	@EventHandler
	private void onRegionExit(RegionLeftEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode()!=GameMode.SURVIVAL && player.getGameMode()!=GameMode.ADVENTURE) return;
		Street street = Street.get(event.getRegion().getId());
		if(street==null) return;
		player.removePotionEffect(PotionEffectType.SPEED);
	}
}
