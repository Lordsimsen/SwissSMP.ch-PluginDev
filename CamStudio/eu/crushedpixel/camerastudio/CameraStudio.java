package eu.crushedpixel.camerastudio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_13_R2.PacketPlayOutMapChunk;
import net.minecraft.server.v1_13_R2.PlayerConnection;

public class CameraStudio extends JavaPlugin implements Listener {
	public static JavaPlugin instance;
	static String prefix = ChatColor.AQUA + "[" + ChatColor.DARK_AQUA + "CP" + ChatColor.AQUA + "CameraStudio] "
			+ ChatColor.GREEN;
	static HashSet<UUID> travelling = new HashSet<UUID>();
	static HashSet<UUID> stopping = new HashSet<UUID>();

	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin)this);
		getLogger().info("CameraStudio disabled");
	}

	public void onEnable() {

		instance = this;

		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("cam").setExecutor(new CamCommand());
		getConfig().options().copyDefaults(true);
		saveConfig();
		getLogger().info(prefix + "CPCameraStudioReborn has been enabled!");
	}

	public static double round(double unrounded, int precision) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, 4);
		return rounded.doubleValue();
	}

	@EventHandler
	public void onPlayerJoined(final PlayerJoinEvent event) {
		if (getConfig().getBoolean("show-join-message")) {
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					event.getPlayer()
							.sendMessage(prefix + "This server is running the Camera Studio Plugin v"
									+ instance.getDescription().getVersion() + " by " + ChatColor.AQUA
									+ "CrushedPixel. Updated by chrismin13.");
					event.getPlayer().sendMessage(prefix + ChatColor.YELLOW + "http://youtube.com/CrushedPixel");
				}
			}, 10L);
		}
	}

	@EventHandler
	public void onPlayerLeave(final PlayerQuitEvent event) {
		if (getConfig().getBoolean("clear-points-on-disconnect")
				&& CamCommand.points.get(event.getPlayer().getUniqueId()) != null)
			CamCommand.points.get(event.getPlayer().getUniqueId()).clear();
		if(travelling.contains(event.getPlayer().getUniqueId())){
			stopping.add(event.getPlayer().getUniqueId());
		}
	}
	
	protected static CameraPath loadPath(int path_id, World world){
		return CameraPath.load(path_id, world);
	}
	
	public static void travel(final Player player, int path_id, int time){
		CameraPath path = loadPath(path_id, player.getWorld());
		travel(player,path,time);
	}
	
	public static void travel(final Player player, CameraPath path, int time){
		travel(player,path.getPoints(),time*20,null,null);
	}
	
	public static void travel(final Player player ,CameraPath path, int time, Runnable callback){
		travel(player,path.getPoints(),time*20,null,null,callback);
	}

	public static void travel(final Player player, List<Location> locations, int time, String FailMessage,
			final String CompletedMessage) {
		travel(player,locations,time,FailMessage,CompletedMessage,null);
	}

	public static void travel(final Player player, List<Location> locations, int time, String FailMessage,
			final String CompletedMessage, final Runnable callback) {

		final List<Location> motionPath = createMotionPath(player.getWorld(),locations,time);

		try {
			final GameMode oldGameMode = player.getGameMode();
			player.setGameMode(GameMode.SPECTATOR);
			Location start = motionPath.get(0);
			player.teleport(start);
			loadChunkArea(player,start.getBlockX()>>4,start.getBlockZ()>>4,10);
			travelling.add(player.getUniqueId());
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CameraStudio.instance, new Runnable() {
				private int ticks = 0;

				public void run() {
					if (this.ticks < motionPath.size()) {

						player.teleport((Location) motionPath.get(this.ticks));

						if (!stopping.contains(player.getUniqueId())) {
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CameraStudio.instance, this, 1L);
						} else {
							stopping.remove(player.getUniqueId());
							travelling.remove(player.getUniqueId());
						}

						this.ticks += 1;
					} else {
						travelling.remove(player.getUniqueId());
						if (CompletedMessage != null)
							player.sendMessage(CompletedMessage);
						player.setGameMode(oldGameMode);
						if(callback!=null){
							callback.run();
						}
					}
				}
			}, 1L);
		} catch (Exception e) {
			if (FailMessage != null)
				player.sendMessage(FailMessage);
		}
	}
	
	public static void travelSimple(Player player, List<Location> locations, int time, Runnable callback){
		final List<Location> motionPath = createMotionPath(player.getWorld(),locations,time);

		try {
			Location start = motionPath.get(0);
			player.teleport(start);
			loadChunkArea(player,start.getBlockX()>>4,start.getBlockZ()>>4,10);
			travelling.add(player.getUniqueId());
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CameraStudio.instance, new Runnable() {
				private int ticks = 0;

				public void run() {
					if (this.ticks < motionPath.size()) {

						player.teleport((Location) motionPath.get(this.ticks));

						if (!stopping.contains(player.getUniqueId())) {
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CameraStudio.instance, this, 1L);
						} else {
							stopping.remove(player.getUniqueId());
							travelling.remove(player.getUniqueId());
						}

						this.ticks += 1;
					} else {
						travelling.remove(player.getUniqueId());
						if(callback!=null){
							callback.run();
						}
					}
				}
			}, 1L);
		} catch (Exception e) {

		}
	}
	
	private static List<Location> createMotionPath(World world, List<Location> locations, int time){
		List<Double> diffs = new ArrayList<Double>();
		List<Integer> travelTimes = new ArrayList<Integer>();
		List<Location> motionPath = new ArrayList<Location>();

		double totalDiff = 0.0D;

		for (int i = 0; i < locations.size() - 1; i++) {
			Location s = (Location) locations.get(i);
			Location n = (Location) locations.get(i + 1);
			double diff = CameraStudio.positionDifference(s, n);
			totalDiff += diff;
			diffs.add(Double.valueOf(diff));
		}

		for (Iterator<Double> n = diffs.iterator(); n.hasNext();) {
			double d = ((Double) n.next()).doubleValue();
			travelTimes.add(Integer.valueOf((int) (d / totalDiff * time)));
		}
		for (int i = 0; i < locations.size() - 1; i++) {
			Location s = (Location) locations.get(i);
			Location n = (Location) locations.get(i + 1);
			int t = ((Integer) travelTimes.get(i)).intValue();

			double moveX = n.getX() - s.getX();
			double moveY = n.getY() - s.getY();
			double moveZ = n.getZ() - s.getZ();
			double movePitch = n.getPitch() - s.getPitch();

			double yawDiff = Math.abs(n.getYaw() - s.getYaw());
			double c = 0.0D;

			if (yawDiff <= 180.0D) {
				if (s.getYaw() < n.getYaw()) {
					c = yawDiff;
				} else {
					c = -yawDiff;
				}
			} else if (s.getYaw() < n.getYaw()) {
				c = -(360.0D - yawDiff);
			} else {
				c = 360.0D - yawDiff;
			}

			double d = c / t;

			for (int x = 0; x < t; x++) {
				Location l = new Location(world, s.getX() + moveX / t * x, s.getY() + moveY / t * x,
						s.getZ() + moveZ / t * x, (float) (s.getYaw() + d * x),
						(float) (s.getPitch() + movePitch / t * x));
				motionPath.add(l);
			}
		}
		return motionPath;
	}
	
	public static void loadChunkArea(Player player, int chunk_x, int chunk_z, int radius){
		World world = player.getWorld();
		chunk_x-=radius;
		chunk_z-=radius;
		PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
		Chunk chunk;
		for(int x = 0; x < radius*2+1; x++){
			for(int z = 0; z < radius*2+1; z++){
				chunk = world.getChunkAt(chunk_x+x, chunk_z+z);
				if(!chunk.isLoaded())chunk.load();
				playerConnection.sendPacket(new PacketPlayOutMapChunk(((CraftChunk)chunk).getHandle(),65535));
			}
		}
	}

	public static double positionDifference(Location cLoc, Location eLoc) {
		double cX = cLoc.getX();
		double cY = cLoc.getY();
		double cZ = cLoc.getZ();

		double eX = eLoc.getX();
		double eY = eLoc.getY();
		double eZ = eLoc.getZ();

		double dX = eX - cX;
		if (dX < 0.0D) {
			dX = -dX;
		}
		double dZ = eZ - cZ;
		if (dZ < 0.0D) {
			dZ = -dZ;
		}
		double dXZ = Math.hypot(dX, dZ);

		double dY = eY - cY;
		if (dY < 0.0D) {
			dY = -dY;
		}
		double dXYZ = Math.hypot(dXZ, dY);

		return dXYZ;
	}

	public static boolean isTravelling(UUID PlayerUUID) {
		if (travelling.contains(PlayerUUID))
			return true;
		return false;
	}

	public static void stop(final UUID playerUUID) {
		stopping.add(playerUUID);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CameraStudio.instance, new Runnable() {
			public void run() {
				stopping.remove(playerUUID);
			}
		}, 2L);
	}

}