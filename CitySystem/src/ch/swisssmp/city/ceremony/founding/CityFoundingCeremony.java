package ch.swisssmp.city.ceremony.founding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.ceremony.CityCeremony;
import ch.swisssmp.city.ceremony.effects.CityCeremonyCircleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;
import ch.swisssmp.city.ItemManager;
import ch.swisssmp.city.ceremony.founding.phases.BaptisePhase;
import ch.swisssmp.city.ceremony.founding.phases.BeginPhase;
import ch.swisssmp.city.ceremony.founding.phases.ForgeRingPhase;
import ch.swisssmp.city.ceremony.founding.phases.PledgeAllegiancePhase;
import ch.swisssmp.city.ceremony.founding.phases.PresentRingPhase;
import ch.swisssmp.utils.SwissSMPler;

public class CityFoundingCeremony extends CityCeremony implements Listener {
	public static final float ceremonyRange = 15;
	private static final Collection<CityFoundingCeremony> ceremonies = new ArrayList<CityFoundingCeremony>();
	
	private final Block fire;
	
	private BukkitTask timeoutTask;
	private FoundingCeremonyPhase phase = null;
	
	private Material ringBaseMaterial;
	private Material ringCoreMaterial;
	private String cityName;
	
	private CityCeremonyCircleEffect ringEffect;
	private BukkitTask ringEffectTask;
	
	private long t;
	private final long orbitTime = 1200;
	private Location spectatorLocation;
	
	private CityFoundingCeremony(Block fire, Player initiator){
		super(initiator);
		this.fire = fire;
	}
	
	public Block getFire(){
		return fire;
	}
	
	public void setRingMaterials(Material baseMaterial, Material coreMaterial){
		this.ringBaseMaterial = baseMaterial;
		this.ringCoreMaterial = coreMaterial;
	}
	
	public String getRingType(){
		return ItemManager.getSigilType(ringBaseMaterial, ringCoreMaterial);
	}

	@Override
	public void addParticipant(Player player){
		if(isParticipant(player)) return;
		super.addParticipant(player);
		this.broadcast(player.getDisplayName()+ChatColor.RESET+ChatColor.LIGHT_PURPLE+" hat seine Treue geschworen.");
	}
	
	public void setCityName(String name){
		cityName = name;
	}
	
	public Material getBaseMaterial(){
		return ringBaseMaterial;
	}
	
	public Material getCoreMaterial(){
		return ringCoreMaterial;
	}
	
	public String getCityName(){
		return cityName;
	}
	
	public CityCeremonyCircleEffect getRingEffect(){
		return ringEffect;
	}
	
	private void updateSpectatorLocation(){
		t++;
		final double radius = 7;
		float progress = t / (float) orbitTime;
		double radians = 2*Math.PI*progress;
		double x = Math.cos(radians) * radius;
		double y = 6;
		double z = Math.sin(radians) * radius;
		float yaw = progress*360 + 90;
		float pitch = 35;
		spectatorLocation = new Location(fire.getWorld(),fire.getX()+x,fire.getY()+y,fire.getZ()+z,yaw,pitch);
	}
	
	@EventHandler
	public void playerInteractArmorStand(PlayerArmorStandManipulateEvent event){
		ArmorStand armorStand = (ArmorStand) event.getRightClicked();
		if(!armorStand.hasMetadata("interactable")) return;
		List<MetadataValue> values = armorStand.getMetadata("interactable");
		for(MetadataValue value : values){
			if(value.getOwningPlugin()!=CitySystemPlugin.getInstance()) continue;
			if(value.asBoolean()!=false) return;
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Collection<Player> participants = this.getParticipants();
		Player initiator = this.getInitiator();
		if(!participants.contains(event.getPlayer())) return;
		participants.remove(event.getPlayer());
		ceremoniesParticipants.remove(event.getPlayer());
		if(initiator!=event.getPlayer() && participants.size()>=2) return;
		this.cancel();
	}

	@Override
	protected Phase getNextPhase() {
		if(phase==null){
			Bukkit.getLogger().info("Begin now");
			phase = FoundingCeremonyPhase.Begin;
			return new BeginPhase(this);
		}
		switch(phase){
		case Begin:{
			Bukkit.getLogger().info("Pledge allegiance now");
			phase = FoundingCeremonyPhase.PledgeAllegiance;
			return new PledgeAllegiancePhase(this);
		}
		case PledgeAllegiance:{
			Bukkit.getLogger().info("Forge thy ring now");
			phase = FoundingCeremonyPhase.ForgeRing;
			return new ForgeRingPhase(this);
		}
		case ForgeRing:{
			phase = FoundingCeremonyPhase.Baptise;
			return new BaptisePhase(this);
		}
		case Baptise:{
			phase = FoundingCeremonyPhase.PresentRing;
			return new PresentRingPhase(this);
		}
		case PresentRing:{
			this.complete();
			return null;
		}
		default: return null;
		}
	}
	
	@Override
	public void begin(JavaPlugin plugin){
		super.begin(CitySystemPlugin.getInstance());
		this.updateSpectatorLocation();
		Location center = fire.getLocation().clone().add(0.5, 0.5, 0.5);
		ringEffect = new CityCeremonyCircleEffect(center);
		ringEffectTask = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), ringEffect, 0, 1);
		timeoutTask = Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
			this.cancel();
		}, 6000L); //Breche Zeremonie nach 5 Minuten automatisch ab
	}
	
	@Override
	public void run(){
		super.run();
		this.updateSpectatorLocation();
		if(fire.getType()!=Material.FIRE){
			cancel();
		}
	}
	
	@Override
	protected void finish(){
		super.finish();
		if(timeoutTask!=null) timeoutTask.cancel();
		if(ringEffectTask!=null) ringEffectTask.cancel();
		HandlerList.unregisterAll(this);
		stopMusic();
		ceremonies.remove(this);
	}
	
	@Override
	public Location getSpectatorLocation(){
		return spectatorLocation.clone();
	}

	@Override
	protected String getPrefix(){
		return ChatColor.WHITE+"["+ChatColor.DARK_PURPLE+"Stadtgründung"+ChatColor.WHITE+"] "+ChatColor.RESET;
	}
	
	public static CityFoundingCeremony start(Block fire, Player initiator){
		if(ceremoniesParticipants.contains(initiator)) { // || !initiator.hasPermission("citysystem.found")) return null;
			Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Already participating somewhere");
			return null;
		}
		List<Player> nearbyPlayers = getNearbyPlayers(fire.getLocation());
//		if(nearbyPlayers.size()<2) return null;
		if(nearbyPlayers.size()<1) {
			Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Not enough players to start ceremony");
			return null;
		}
		for(CityFoundingCeremony nearby : ceremonies){
			if(nearby.getFire().getLocation().distanceSquared(fire.getLocation())<10000){
				SwissSMPler.get(initiator).sendActionBar(ChatColor.RED+"Es findet bereits eine Gründung in der Nähe statt.");
				return null;
			}
		}
		ceremoniesParticipants.add(initiator);
		CityFoundingCeremony result = new CityFoundingCeremony(fire, initiator);
		ceremonies.add(result);
		Bukkit.getPluginManager().registerEvents(result, CitySystemPlugin.getInstance());
		result.begin(CitySystemPlugin.getInstance());
		return result;
	}
	
	private enum FoundingCeremonyPhase{
		Begin,
		ForgeRing,
		PledgeAllegiance,
		Baptise,
		PresentRing
	}
}
