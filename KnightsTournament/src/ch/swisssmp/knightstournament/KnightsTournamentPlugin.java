package ch.swisssmp.knightstournament;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class KnightsTournamentPlugin extends JavaPlugin{
	protected static KnightsTournamentPlugin plugin;
	
	protected static CustomItemBuilder tournamentLanceBuilder = null;
	protected static Recipe tournamentLanceRecipe;
	
	protected static String prefix = "[§4Ritterspiele§r]";
	
	@Override
	public void onEnable() {
		plugin = this;

		/*
		Register early because of CustomItemBuilders in RegisterCraftingRecipe
		 */
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		KnightsTournamentCommand knightsTournamentCommand = new KnightsTournamentCommand();
		this.getCommand("knightstournament").setExecutor(knightsTournamentCommand);
		
		this.getCommand("knightsarena").setExecutor(new KnightsArenaCommand());
		
		this.getCommand("knightsarenas").setExecutor(new KnightsArenasCommand());
		
		for(World world : Bukkit.getWorlds()) {
			KnightsArena.load(world);
		}

		for(Player player : Bukkit.getOnlinePlayers()){
			TournamentLance.updateLegacyLances(player.getInventory());
		}

		TournamentLance.registerCraftingRecipe();

		PlayerDiggingListener.register();
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
		
	}
	

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		LanceCharge.cancelAll();
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static KnightsTournamentPlugin getInstance(){
		return plugin;
	}
}
