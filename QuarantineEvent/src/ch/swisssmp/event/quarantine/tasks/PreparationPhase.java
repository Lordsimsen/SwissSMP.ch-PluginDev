package ch.swisssmp.event.quarantine.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineEventPlugin;

/**
 * Registriere alle Spieler innerhalb der Arena und setze sie in den Warten-Modus.
 * Beenden mit /quarantine start [Arena-Id]
 * @author Oliver
 *
 */
public class PreparationPhase extends QuarantineEventInstanceTask implements Listener {

	private final long aqcuirePlayersTimout = 40;
	
	private long timeout = 0;
	
	public PreparationPhase(QuarantineEventInstance instance) {
		super(instance);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onInitialize() {
		Collection<UUID> players = getPlayers(getArena());
		QuarantineEventInstance instance = getInstance();
		instance.setPlayers(players);
		Bukkit.getPluginManager().registerEvents(this, QuarantineEventPlugin.getInstance());
	}

	@Override
	public void run() {
		if(timeout>0) timeout--;
		else {
			timeout = aqcuirePlayersTimout;
			QuarantineEventInstance instance = getInstance();
			for(UUID player : getPlayers(this.getArena())) {
				instance.addPlayer(player);
			}
		}
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		QuarantineEventInstance instance = getInstance();
		if(instance.canJoin(event.getPlayer()) && instance.getArena().contains(event.getPlayer().getLocation())) {
			instance.addPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		QuarantineEventInstance instance = getInstance();
		instance.removePlayer(event.getPlayer().getUniqueId());
	}
	
	@Override
	protected void onFinish() {
		HandlerList.unregisterAll(this);
	}

	private Collection<UUID> getPlayers(QuarantineArena arena){
		World world = arena.getWorld();
		QuarantineEventInstance instance = getInstance();
		
		List<UUID> players = new ArrayList<UUID>();
		
		for(Player player : world.getPlayers()) {
			if(!instance.canJoin(player) || !arena.contains(player.getLocation())) continue;
			players.add(player.getUniqueId());
		}
		
		return players;
	}
}
