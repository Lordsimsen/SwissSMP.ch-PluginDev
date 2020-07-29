package ch.swisssmp.ceremonies;

import ch.swisssmp.text.ClickEvent;
import ch.swisssmp.text.HoverEvent;
import ch.swisssmp.text.RawBase;
import ch.swisssmp.text.RawText;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public abstract class Ceremony implements Listener, Runnable {

	private final JavaPlugin plugin;
	
	private BukkitTask task;
	private Phase current;
	
	private final HashMap<UUID,Spectator> spectators = new HashMap<UUID,Spectator>();

	public Ceremony(JavaPlugin plugin){
		this.plugin = plugin;
	}

	public JavaPlugin getPlugin(){
		return plugin;
	}
	
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
	
	public void begin(JavaPlugin plugin){
		Ceremonies.add(this);
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
		this.current = this.getNextPhase();
		if(this.current!=null) this.current.begin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	protected void complete(){
		this.finish();
	}

	public void cancel(){
		this.finish();
	}
	
	protected void finish(){
		Ceremonies.remove(this);
		HandlerList.unregisterAll(this);
		if(this.task!=null){
			this.task.cancel();
		}
		if(this.current!=null && !this.current.isCompleted() && !this.current.isCancelled()){
			this.current.cancel();
			this.current.finish();
		}
		for(Spectator spectator : this.spectators.values()){
			Bukkit.getScheduler().runTaskLater(plugin, spectator::leave, 1L);
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

	public void broadcastSpectatorCommand(RawBase message, String key, Function<Player,Boolean> filter){
		Location initialSpectatorLocation = this.getInitialSpectatorLocation();
		World world = initialSpectatorLocation.getWorld();
		BaseComponent spectatorMessage = new RawText(message
				, new RawText(" Zuschauen")
				.color(ChatColor.YELLOW)
				.hoverEvent(HoverEvent.showText("Klicke um zuzuschauen"))
				.clickEvent(ClickEvent.runCommand("/zuschauen " + key))
		).spigot();
		for(Player player : world.getPlayers()){
			if(Ceremonies.isParticipantAnywhere(player)) continue;
			if(!filter.apply(player)) continue;
			player.spigot().sendMessage(spectatorMessage);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event){
		if(!(event.getEntity() instanceof Player) || !this.spectators.containsKey(event.getEntity().getUniqueId())) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpectatorQuit(PlayerQuitEvent event){
		this.cancelSpectator(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(this.spectators.containsKey(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
			return;
		}
		Item item = event.getItemDrop();
		TributeItem.setOwner(item.getItemStack(), player.getUniqueId());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(this.spectators.containsKey(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(InventoryOpenEvent event){
		if(this.spectators.containsKey(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDestroy(EntityDamageEvent event){
		if(event.getEntityType()!=EntityType.DROPPED_ITEM) return;
		Item item = (Item) event.getEntity();
		UUID ownerUid = TributeItem.getOwner(item.getItemStack()).orElse(null);
		if(ownerUid==null) return;
		ItemStack itemStack = item.getItemStack();
		item.remove();
		Player player = Bukkit.getPlayer(ownerUid);
		if(player==null || !(current instanceof ITributeListener)) return;
		((ITributeListener) current).payTribute(itemStack, player);
	}
	
	public void addSpectator(Player player){
		if(spectators.containsKey(player.getUniqueId()) || !player.hasPermission("ceremony.spectate") || this.isParticipant(player)) return;
		Spectator spectator = new Spectator(plugin, this, player);
		spectators.put(player.getUniqueId(), spectator);
		spectator.initialize();
	}

	public void cancelSpectator(Player player){
		if(!this.spectators.containsKey(player.getUniqueId())) return;
		Spectator spectator = this.spectators.get(player.getUniqueId());
		spectator.cancel();
		this.spectators.remove(player.getUniqueId());
	}

	public void removeSpectator(Player player){
		if(!this.spectators.containsKey(player.getUniqueId())) return;
		Spectator spectator = this.spectators.get(player.getUniqueId());
		spectator.leave();
		this.spectators.remove(player.getUniqueId());
	}

	protected Collection<Spectator> getSpectators(){return spectators.values();}
	
	protected abstract boolean isMatch(String key);
	public abstract Location getSpectatorLocation();
	public abstract Location getInitialSpectatorLocation();
	
	public abstract boolean isParticipant(Player player);
	
	protected abstract Phase getNextPhase();
	
	public abstract void broadcast(String message);
	public abstract void broadcastTitle(String title, String subtitle);
	public abstract void broadcastActionBar(String message);
}
