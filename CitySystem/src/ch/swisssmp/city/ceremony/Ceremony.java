package ch.swisssmp.city.ceremony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.city.CitySystemPlugin;

public abstract class Ceremony implements Listener, Runnable {
	private static List<Ceremony> ceremonies = new ArrayList<Ceremony>();
	
	private BukkitTask task;
	private Phase current;
	
	private HashMap<UUID,Spectator> spectators = new HashMap<UUID,Spectator>();
	
	@Override
	public void run(){
		if(current==null){
			this.cancel();
			return;
		}
		current.run();
		if(current.isCompleted()){
			current.complete();
			current.finish();
			current = this.getNextPhase();
			if(current!=null) current.begin();
		}
		for(Spectator spectator : this.spectators.values()){
			spectator.update(this.getSpectatorLocation());
		}
	}
	
	public void begin(){
		ceremonies.add(this);
		this.task = Bukkit.getScheduler().runTaskTimer(CitySystemPlugin.getInstance(), this, 1L, 1L);
		this.current = this.getNextPhase();
		if(this.current!=null) this.current.begin();
		Bukkit.getPluginManager().registerEvents(this, CitySystemPlugin.getInstance());
	}
	
	protected void complete(){
		this.finish();
	}
	
	public void cancel(){
		this.finish();
	}
	
	protected void finish(){
		ceremonies.remove(this);
		HandlerList.unregisterAll(this);
		if(this.task!=null){
			this.task.cancel();
		}
		if(this.current!=null && !this.current.isCompleted()){
			this.current.cancel();
			this.current.finish();
		}
		for(Spectator spectator : this.spectators.values()){
			Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
				spectator.leave();
			}, 1L);
		}
	}
	
	public void setPhase(Phase phase){
		if(this.current!=null){
			this.current.cancel();
			this.current.finish();
		}
		this.current = phase;
		phase.begin();
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event){
		if(!(event.getEntity() instanceof Player) || !this.spectators.containsKey(event.getEntity().getUniqueId())) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpectatorQuit(PlayerQuitEvent event){
		this.removeSpectator(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(this.spectators.containsKey(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
			return;
		}
		Item item = event.getItemDrop();
		item.setMetadata("ceremony_owner", new FixedMetadataValue(CitySystemPlugin.getInstance(),player.getUniqueId()));
	}

	@EventHandler
	public void onItemDestroy(EntityDamageEvent event){
		if(event.getEntityType()!=EntityType.DROPPED_ITEM) return;
		Item item = (Item) event.getEntity();
		if(!item.hasMetadata("ceremony_owner")) return;
		MetadataValue value = item.getMetadata("ceremony_owner").stream()
				.filter(data->data.getOwningPlugin()==CitySystemPlugin.getInstance())
				.findFirst()
				.orElse(null);
		if(value==null) return;
		ItemStack itemStack = item.getItemStack();
		item.removeMetadata("ceremony_owner", CitySystemPlugin.getInstance());
		item.remove();
		Player player = Bukkit.getPlayer((UUID) value.value());
		if(player==null || !(current instanceof ISacrificeListener)) return;
		((ISacrificeListener) current).sacrifice(itemStack, player);
	}
	
	public void addSpectator(Player player){
		if(spectators.containsKey(player.getUniqueId()) || !player.hasPermission("citysystem.spectate") || this.isParticipant(player)) return;
		Spectator spectator = new Spectator(this, player);
		spectators.put(player.getUniqueId(), spectator);
		spectator.initialize();
	}
	
	public void removeSpectator(Player player){
		if(!this.spectators.containsKey(player.getUniqueId())) return;
		Spectator spectator = this.spectators.get(player.getUniqueId());
		spectator.leave();
		this.spectators.remove(player.getUniqueId());
	}
	
	public static Ceremony get(String key){
		for(Ceremony ceremony : ceremonies){
			if(!ceremony.isMatch(key)) continue;
			return ceremony;
		}
		return null;
	}
	
	public static Ceremony getLast(){
		if(ceremonies.size()==0) return null;
		return ceremonies.get(ceremonies.size()-1);
	}
	
	public static boolean isParticipantAnywhere(Player player){
		for(Ceremony ceremony : ceremonies){
			if(ceremony.isParticipant(player)) return true;
		}
		return false;
	}
	
	protected abstract boolean isMatch(String key);
	public abstract Location getSpectatorLocation();
	public abstract Location getInitialSpectatorLocation();
	
	public abstract boolean isParticipant(Player player);
	
	protected abstract Phase getNextPhase();
	
	public abstract void broadcast(String message);
	public abstract void broadcastTitle(String title, String subtitle);
	public abstract void broadcastActionBar(String message);
}
