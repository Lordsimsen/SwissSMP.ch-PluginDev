package ch.swisssmp.adventuredungeons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.block.MmoBlock;
import ch.swisssmp.adventuredungeons.command.CampCommand;
import ch.swisssmp.adventuredungeons.command.AdventureDungeonsCommand;
import ch.swisssmp.adventuredungeons.command.DungeonCommand;
import ch.swisssmp.adventuredungeons.command.PlayerCommand;
import ch.swisssmp.adventuredungeons.command.AdventureWorldCommand;
import ch.swisssmp.adventuredungeons.event.MmoEventManager;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.AdventureWorld;
import ch.swisssmp.adventuredungeons.world.AdventureWorldInstance;

public class AdventureDungeons extends JavaPlugin{
	private static Logger logger;
	public static File configFile;
	public static YamlConfiguration config;
	public static PluginDescriptionFile pdfFile;
	public static File dataFolder;
	public static MmoEventManager eventManager;
	public static AdventureDungeons plugin;
	public static boolean debug;
	
	public static WorldGuardPlugin worldGuardPlugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		eventManager = new MmoEventManager(this);
		AdventureDungeonsCommand mmoCommand = new AdventureDungeonsCommand();
		PlayerCommand mmoPlayerCommand = new PlayerCommand();
		DungeonCommand dungeonCommand = new DungeonCommand();
		this.getCommand("AdventureDungeons").setExecutor(mmoCommand);
		this.getCommand("rename").setExecutor(mmoCommand);
		this.getCommand("join").setExecutor(mmoPlayerCommand);
		this.getCommand("refuse").setExecutor(mmoPlayerCommand);
		this.getCommand("choose").setExecutor(mmoPlayerCommand);
		this.getCommand("invite").setExecutor(mmoPlayerCommand);
		this.getCommand("MmoWorld").setExecutor(new AdventureWorldCommand());
		this.getCommand("camp").setExecutor(new CampCommand());
		this.getCommand("dungeon").setExecutor(dungeonCommand);
		this.getCommand("ready").setExecutor(dungeonCommand);
		
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
		debug = config.getBoolean("debug");

		Bukkit.getScheduler().runTaskLater(AdventureDungeons.plugin, new Runnable(){
			@Override
			public void run(){
				AdventureDungeons.loadAll();
			}
		}, 1L);
	}
	
	public static synchronized void loadAll(){
		//templates
		try {
			MmoBlock.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//actual objects
		try {
			AdventureWorld.loadWorlds(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Dungeon.loadDungeons(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		for(World world : Bukkit.getWorlds()){
			AdventureWorldInstance worldInstance = AdventureWorld.getInstance(world);
			if(worldInstance!=null){
				worldInstance.blockScheduler.saveAll();
			}
		}
		/*for(MmoDungeonInstance instance : MmoDungeon.instances.values()){
			for(String player_uuidString : instance.player_uuids){
				UUID player_uuid = UUID.fromString(player_uuidString);
				instance.leave(player_uuid);
			}
		}*/
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    private void firstRun() throws Exception {
        if(!configFile.exists()){
        	configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
    }
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
        	config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void info(String info){
    	if(debug){
        	logger.info(info);
    	}
    }
    public static void debug(String info){
    	logger.info(info);
    }
}
