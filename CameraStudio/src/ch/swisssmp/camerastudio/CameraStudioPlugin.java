package ch.swisssmp.camerastudio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CameraStudioPlugin extends JavaPlugin {
	private static CameraStudioPlugin instance;

	public void onEnable() {

		instance = this;
		CameraStudio.init(this);

		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("cam").setExecutor(new CamCommand());
		getConfig().options().copyDefaults(true);
		saveConfig();

		CameraStudioWorlds.loadAll();

		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
	}

	public void onDisable() {
		CameraStudioWorlds.unloadAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getLogger().info(getDescription().getName() + " has been disabled (Version: " + getDescription().getVersion() + ")");
	}

	public static CameraStudioPlugin getInstance(){
		return instance;
	}
	public static String getPrefix(){
		return "["+ChatColor.YELLOW+ instance.getName()+ChatColor.RESET+"]";
	}
}