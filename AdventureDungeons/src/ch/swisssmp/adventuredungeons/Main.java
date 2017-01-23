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

import com.plexon21.AreaSounds.AreaSounds;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.adventuredungeons.mmoblock.MmoBlock;
import ch.swisssmp.adventuredungeons.mmocommand.MmoCampCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoDungeonCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoMultiStateCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoPlayerCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoRegionCommand;
import ch.swisssmp.adventuredungeons.mmocommand.MmoWorldCommand;
import ch.swisssmp.adventuredungeons.mmoevent.MmoEventManager;
import ch.swisssmp.adventuredungeons.mmosound.MmoSound;
import ch.swisssmp.adventuredungeons.mmoworld.MmoDungeon;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorld;
import ch.swisssmp.adventuredungeons.mmoworld.MmoWorldInstance;
import ch.swisssmp.adventuredungeons.util.MmoDelayedThreadTask;
import ch.swisssmp.adventuredungeons.util.MmoResourceManager;

public class Main extends JavaPlugin{
	private static Logger logger;
	public static File configFile;
	public static YamlConfiguration config;
	public static PluginDescriptionFile pdfFile;
	public static File dataFolder;
	public static MmoEventManager eventManager;
	public static Main plugin;
	public static boolean debug;
	
	public static WorldGuardPlugin worldGuardPlugin;
	public static WorldEditPlugin worldEditPlugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		eventManager = new MmoEventManager(this);
		MmoCommand mmoCommand = new MmoCommand();
		MmoPlayerCommand mmoPlayerCommand = new MmoPlayerCommand();
		MmoMultiStateCommand multiStateCommand = new MmoMultiStateCommand();
		MmoDungeonCommand dungeonCommand = new MmoDungeonCommand();
		this.getCommand("CraftMMO").setExecutor(mmoCommand);
		this.getCommand("rename").setExecutor(mmoCommand);
		this.getCommand("join").setExecutor(mmoPlayerCommand);
		this.getCommand("refuse").setExecutor(mmoPlayerCommand);
		this.getCommand("choose").setExecutor(mmoPlayerCommand);
		this.getCommand("invite").setExecutor(mmoPlayerCommand);
		this.getCommand("MmoWorld").setExecutor(new MmoWorldCommand());
		this.getCommand("MmoCamp").setExecutor(new MmoCampCommand());
		this.getCommand("MmoRegion").setExecutor(new MmoRegionCommand());
		this.getCommand("MmoDungeon").setExecutor(dungeonCommand);
		this.getCommand("ready").setExecutor(dungeonCommand);
		this.getCommand("MmoMultiState").setExecutor(multiStateCommand);
		this.getCommand("MultiState").setExecutor(multiStateCommand);
		
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(worldEdit instanceof WorldEditPlugin){
			worldEditPlugin = (WorldEditPlugin) worldEdit;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		
		Plugin areaSounds = Bukkit.getPluginManager().getPlugin("AreaSounds");
		if(areaSounds instanceof AreaSounds){
			MmoSound.soundManager = (AreaSounds)areaSounds;
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
		MmoResourceManager.rootURL = config.getString("webserver");
		MmoResourceManager.pluginToken = config.getString("token");
		debug = config.getBoolean("debug");
		if(!MmoResourceManager.rootURL.endsWith("/")){
			MmoResourceManager.rootURL+="/";
		}

		Runnable task = new MmoDelayedThreadTask(new Runnable(){
			@Override
			public void run(){
				Main.loadAll();
			}
		});
		Bukkit.getScheduler().runTaskLater(Main.plugin, task, 1L);
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
			MmoWorld.loadWorlds(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoDungeon.loadDungeons(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		for(World world : Bukkit.getWorlds()){
			MmoWorldInstance worldInstance = MmoWorld.getInstance(world);
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
