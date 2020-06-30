package ch.swisssmp.city.ceremony.founding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.swisssmp.ceremonies.Ceremony;
import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.ceremony.effects.CityCeremonyCircleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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

public class CityFoundingCeremony extends Ceremony implements Listener {
	public static final float ceremonyRange = 15;
	private static Collection<Player> ceremoniesParticipants = new ArrayList<Player>();
	private static Collection<CityFoundingCeremony> ceremonies = new ArrayList<CityFoundingCeremony>();
	
	private final Block fire;
	private final Player initiator;
	
	private List<Player> participants = new ArrayList<Player>();
	
	private BukkitTask timeoutTask;
	private FoundingCeremonyPhase phase = null;
	
	private Material ringBaseMaterial;
	private Material ringCoreMaterial;
	private String cityName;
	
	private CityCeremonyCircleEffect ringEffect;
	private BukkitTask ringEffectTask;
	private BukkitTask musicTask;
	
	private long t;
	private final long orbitTime = 1200;
	private Location spectatorLocation;
	
	private CityFoundingCeremony(Block fire, Player initiator){
		super(CitySystemPlugin.getInstance());
		this.fire = fire;
		this.initiator = initiator;
		participants.add(initiator);
	}
	
	public Block getFire(){
		return fire;
	}
	
	public Player getInitiator(){
		return initiator;
	}
	
	public List<Player> getPlayers(){
		return participants;
	}
	
	public void setRingMaterials(Material baseMaterial, Material coreMaterial){
		this.ringBaseMaterial = baseMaterial;
		this.ringCoreMaterial = coreMaterial;
	}
	
	public String getRingType(){
		return ItemManager.getSigilType(ringBaseMaterial, ringCoreMaterial);
	}
	
	public void addParticipant(Player player){
		if(isParticipant(player)) return;
		participants.add(player);
		ceremoniesParticipants.add(player);
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
	
	public void setMusic(Location location, String music, long length){
		if(musicTask!=null) musicTask.cancel();
		musicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CitySystemPlugin.getInstance(), ()->{
			for(Player player : this.participants){
				player.stopSound(music,SoundCategory.RECORDS);
			}
			location.getWorld().playSound(location, music, SoundCategory.RECORDS, 15, 1);
		}, 0, length);
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
		if(!participants.contains(event.getPlayer())) return;
		participants.remove(event.getPlayer());
		ceremoniesParticipants.remove(event.getPlayer());
		if(initiator!=event.getPlayer() && this.participants.size()>=2) return;
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
		for(Player player : this.participants){
			ceremoniesParticipants.remove(player);
		}
		stopMusic();
		ceremonies.remove(this);
	}
	
	private void stopMusic(){
		if(musicTask!=null) musicTask.cancel();
		for(Player player : this.participants){
			player.stopSound("founding_ceremony_shaker", SoundCategory.RECORDS);
			player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
		}
	}

	@Override
	public boolean isParticipant(Player player) {
		return this.participants.contains(player);
	}

	@Override
	public void broadcast(String message) {
		for(Player player : this.participants){
			player.sendMessage(this.getPrefix()+message);
		}
	}

	@Override
	public void broadcastTitle(String title, String subtitle) {
		for(Player player : this.participants){
			SwissSMPler.get(player).sendTitle(title, subtitle);
		}
	}

	@Override
	public void broadcastActionBar(String message) {
		for(Player player : this.participants){
			SwissSMPler.get(player).sendActionBar(message);
		}
	}
	
	@Override
	protected boolean isMatch(String key){
		return (this.initiator.getName().toLowerCase().contains(key) || this.initiator.getDisplayName().toLowerCase().contains(key));
	}
	
	@Override
	public Location getSpectatorLocation(){
		return spectatorLocation.clone();
	}
	
	@Override
	public Location getInitialSpectatorLocation(){
		return initiator.getLocation().add(1, 0, 0);
	}
	
	private String getPrefix(){
		return ChatColor.WHITE+"["+ChatColor.DARK_PURPLE+"Stadtgründung"+ChatColor.WHITE+"] "+ChatColor.RESET;
	}
	
	public static List<Player> getNearbyPlayers(Location location){
		return getNearbyPlayers(location, CityFoundingCeremony.ceremonyRange);
	}
	
	public static List<Player> getNearbyPlayers(Location location, float radius){
		List<Player> result = new ArrayList<Player>();
		for(Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)){
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			if(player.getGameMode()!=GameMode.SURVIVAL) continue;
			if(ceremoniesParticipants.contains(player) || !player.hasPermission("citysystem.found")) continue;
			result.add(player);
		}
		return result;
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
