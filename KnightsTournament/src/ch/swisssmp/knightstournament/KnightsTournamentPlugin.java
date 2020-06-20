package ch.swisssmp.knightstournament;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.customitems.CustomItemBuilder;

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

		LoanerEquipment.resetAll();
		TournamentLance.updateLegacyLances();
		TournamentLance.registerCraftingRecipe();

		PlayerReleaseUseListener.register();
		
		Bukkit.getLogger().info(getDescription().getName() + " has been enabled (Version: " + getDescription().getVersion() + ")");
		
	}
	

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		LanceCharge.cancelAll();
		LoanerEquipment.resetAll();
		for(KnightsArena arena : KnightsArena.getLoadedArenas()){
			if(arena.getTournament()==null) continue;
			arena.getTournament().cancel();
		}
		Bukkit.getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = getDescription();
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		Bukkit.getLogger().info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static KnightsTournamentPlugin getInstance(){
		return plugin;
	}

	public static String getPrefix(){return "["+ ChatColor.GREEN+plugin.getName()+ChatColor.RESET+"]";}
}
