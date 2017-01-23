package ch.swisssmp.elytrarace;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;

public class Main extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	protected static JavaPlugin plugin;
	protected static File dataFolder;
	protected static WorldGuardPlugin worldGuardPlugin;
	protected static HashMap<UUID, Long> highscores = new HashMap<UUID, Long>();
	protected static HashMap<UUID, PlayerRace> races = new HashMap<UUID, PlayerRace>(); 
	
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		dataFolder = this.getDataFolder();

		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("spielen").setExecutor(playerCommand);
		this.getCommand("zuschauen").setExecutor(playerCommand);
		this.getCommand("rangliste").setExecutor(playerCommand);
		this.getCommand("reset").setExecutor(playerCommand);
		server.getPluginManager().registerEvents(this, this);
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		HandlerList.unregisterAll(plugin);
	}
	@EventHandler(ignoreCancelled=true)
	private void onRegionEnter(RegionEnterEvent event){
		if(event.getPlayer().getGameMode()!=GameMode.ADVENTURE) return;
		String regionName = event.getRegion().getId();
		PlayerRace race = races.get(event.getPlayer().getUniqueId());
		if(regionName.equals("start")){
			if(race==null)
				race = new PlayerRace(event.getPlayer());
			race.start();
		}
		else if(regionName.equals("finish")){
			if(race==null) return;
			race.finish();
		}
		else if(regionName.contains("checkpoint_")){
			if(race==null) return;
			race.passCheckpoint(regionName);
		}
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerLogin(PlayerLoginEvent event){
		Runnable runnable = new Runnable(){
			public void run(){
				preparePlayerPlay(event.getPlayer());
			}
		};
		Bukkit.getScheduler().runTaskLater(this, runnable, 20L);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		PlayerRace race = races.get(player.getUniqueId());
		if(race!=null) race.cancel();
	}
	@EventHandler(ignoreCancelled=true)
	private void onWeatherChange(WeatherChangeEvent event){
		event.getWorld().setWeatherDuration(0);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		PlayerRace race = races.get(player.getUniqueId());
		if(race!=null && race.running) race.cancel();
		else return;
		player.teleport(new Location(player.getWorld(), 468, 107, -970));
		player.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
		player.playEffect(EntityEffect.HURT);
		event.setCancelled(true);
	}
	protected static void preparePlayerPlay(Player player){
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(new Location(player.getWorld(), 468, 106, -970));
		player.getInventory().clear();
		player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
		player.setHealth(20);
	}
	protected static void preparePlayerSpectate(Player player){
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(new Location(player.getWorld(), 468, 106, -970));
	}
    protected static void sendActionBar(Player player, String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    	if(!player.isOnline())return;
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(("{'text': '" + title + "'}").replace("'", "\""));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(("{'text': '" + subtitle + "'}").replace("'", "\""));
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                  Map.Entry::getKey, 
                  Map.Entry::getValue, 
                  (e1, e2) -> e1, 
                  LinkedHashMap::new
                ));
  }
}
