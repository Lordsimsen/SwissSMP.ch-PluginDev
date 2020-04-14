package ch.swisssmp.travel.phase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.BoundingBox;

import ch.swisssmp.npc.NPCs;
import ch.swisssmp.travel.Journey;
import ch.swisssmp.travel.PositionUtil;
import ch.swisssmp.travel.TravelStation;
import ch.swisssmp.travel.TravelSystem;
import ch.swisssmp.utils.Position;
import ch.swisssmp.world.WorldManager;
import ch.swisssmp.world.transfer.WorldTransferManager;
import ch.swisssmp.world.transfer.WorldTransferObserver;

public class EmbarkPhase extends Phase {

	private WorldTransferObserver observer;
	private World world;
	
	public EmbarkPhase(Journey journey) {
		super(journey);

	}

	@Override
	public void run() {
		
	}

	@Override
	public void cancel() {

		
	}

	@Override
	public void initialize() {
		String travelWorldName = this.getJourney().getStart().getTravelWorldName();
		if(travelWorldName==null){
			this.getJourney().cancel();
			return;
		}
		String localWorldName = this.getJourney().getTravelWorldInstanceName();
		observer = WorldTransferManager.downloadWorld(Bukkit.getConsoleSender(), travelWorldName, localWorldName);
		observer.addOnFinishListener(()->{
			this.world = WorldManager.loadWorld(localWorldName);
			NPCs.unpack(world);
			this.getJourney().setWorldInstance(world);
			if(world==null){
				this.getJourney().cancel();
				this.getJourney().sendMessage(TravelSystem.getPrefix()+ChatColor.RED+"Die Reise konnte nicht gestartet werden. Bitte kontaktiert ein Staff Mitglied.");
				return;
			}
			Bukkit.getScheduler().runTaskLater(TravelSystem.getInstance(), ()->{
				World fromWorld = getJourney().getStart().getWorld();
				world.setFullTime(fromWorld.getFullTime());
				world.setWeatherDuration(fromWorld.getWeatherDuration());
				world.setThundering(fromWorld.isThundering());
				world.setThunderDuration(fromWorld.getThunderDuration());
				world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
				teleportPlayers(world);
				teleportAnimals(world);
				setCompleted();
			}, 2l);
		});
	}

	@Override
	public void finish() {

		
	}

	@Override
	public void complete() {

		
	}
	
	private void teleportPlayers(World destination){
		for(Player player : this.getJourney().getPlayers()){
			Location location = this.getTeleportLocation(player, destination);
			PlayerTeleportEvent event = new PlayerTeleportEvent(player, player.getLocation(), location, TeleportCause.PLUGIN);
			Bukkit.getPluginManager().callEvent(event);
			player.teleport(location);
		}
	}
	
	private void teleportAnimals(World destination){
		TravelStation start = this.getJourney().getStart();
		Position min = start.getBoundingBoxMin();
		Position max = start.getBoundingBoxMax();
		World world = start.getWorld();
		BoundingBox box = new BoundingBox(min.getX(),min.getY(),min.getZ(),max.getX()+1,max.getY()+1,max.getZ()+1);
		for(Entity entity : world.getNearbyEntities(box)){
			//System.out.println("Entity: "+entity.getType());
			if(!(entity instanceof Animals)) continue;
			//System.out.println("Teleport: "+entity.getType());
			this.getJourney().join(entity);
			Location location = this.getTeleportLocation(entity, destination);
			entity.teleport(location);
		}
	}
	
	private Location getTeleportLocation(Entity entity, World destination){
		TravelStation start = this.getJourney().getStart();
		Position outsideAnchor = start.getOutsideAnchor();
		Position insideAnchor = start.getInsideAnchor();
		Position previous = new Position(entity.getLocation());
		Position after = PositionUtil.transform(previous, outsideAnchor, insideAnchor);
		Location result = after.getLocation(destination);
		boolean isGround = this.checkLocation(result);
		if(!isGround){
			if(this.getJourney().getStart().getRespawn()!=null){
				return this.getJourney().getStart().getRespawn().getLocation(destination);
			}
			return destination.getSpawnLocation();
		}
		return result;
	}
	
	private boolean checkLocation(Location location){
		Block block = location.getBlock();
		while(block.getType()==Material.AIR){
			block = block.getRelative(BlockFace.DOWN);
			if(block.getY()<5) break;
		}
		return block.getType()!=Material.AIR;
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Location location;
		if(this.world==null){
			TravelStation start = this.getJourney().getStart();
			location = start.getWaypoint().getLocation(start.getWorld());
		}
		else{
			location = this.world.getSpawnLocation();
		}
		event.setRespawnLocation(location);
	}
}
