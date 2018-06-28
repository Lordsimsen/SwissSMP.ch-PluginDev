package ch.swisssmp.towercontrol;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.VectorKey;

public class GameEventListener implements Listener{
	private final Game game;
	protected GameEventListener(Game game){
		this.game = game;
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		EntityType killerType = player.getLastDamageCause().getEntityType();
		if(killerType == EntityType.PLAYER){
			Player killer = event.getEntity().getKiller();
			this.game.addScore(killer, 200, player.getDisplayName()+ChatColor.RESET+" getötet");
		}
		event.setKeepInventory(true);
	}
	@EventHandler
	private void onPlayerInteractBlock(PlayerInteractEvent event){
		if(event.getAction()==Action.PHYSICAL){
			if(event.getPlayer().getGameMode()!=GameMode.ADVENTURE) return;
			switch(this.game.getGameState()){
			case FIGHT:
			{
				Arena arena = Arena.get(event.getPlayer().getWorld());
				if(arena!=null)arena.trigger(new VectorKey(new Vector(event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ())), event.getPlayer());
				break;
			}
			default:
				return;
			}
		}
	}
}
