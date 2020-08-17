package ch.swisssmp.travel.phase;

import ch.swisssmp.utils.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import ch.swisssmp.travel.Journey;
import ch.swisssmp.travel.TravelStation;
import ch.swisssmp.travel.TravelSystem;
import ch.swisssmp.world.WorldManager;

public class ArrivePhase extends Phase {

	private boolean playersTeleported = false;
	
	public ArrivePhase(Journey journey) {
		super(journey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if(!playersTeleported){
			this.teleportPlayers();
			this.teleportAnimals();
		}
		this.setCompleted();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		super.finish();
		Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
			String worldName = this.getJourney().getTravelWorldInstanceName();
			WorldManager.deleteWorld(worldName);
		}, 10L);
	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub
		
	}

	private void teleportPlayers(){
		playersTeleported = true;
		TravelStation destination = this.getJourney().getDestination();
		Location location = destination.getWaypoint().getLocation(destination.getWorld());
		for(Player player : this.getJourney().getPlayers()){
			PlayerTeleportEvent event = new PlayerTeleportEvent(player, player.getLocation(), location, TeleportCause.PLUGIN);
			Bukkit.getPluginManager().callEvent(event);
			Bukkit.getLogger().info("Teleport "+player.getName()+" to "+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+" in "+location.getWorld().getName());
			player.teleport(location);
			
		}
	}
	
	private void teleportAnimals(){
		TravelStation destination = this.getJourney().getDestination();
		Location location = destination.getWaypoint().getLocation(destination.getWorld());
		location.setY(Math.ceil(location.getY()+0.2));
		for(Entity entity : this.getJourney().getEntities()){
			try{
				if(!entity.isValid()){
					if(entity instanceof LivingEntity){
						((LivingEntity)entity).setHealth(((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					}
					EntityUtil.deserialize(location, EntityUtil.serialize(entity));
					continue;
				}
				entity.teleport(location);
			}
			catch(Exception e){
				String debugString = null;
				try{
					debugString = EntityUtil.serialize(entity).toString();
				}
				catch(Exception e2){
					e2.printStackTrace();
				}
				Bukkit.getLogger().info(TravelSystem.getPrefix()+" Failed to teleport entity: "+debugString);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		TravelStation destination = this.getJourney().getDestination();
		Location location = destination.getWaypoint().getLocation(destination.getWorld());
		event.setRespawnLocation(location);
	}
}
