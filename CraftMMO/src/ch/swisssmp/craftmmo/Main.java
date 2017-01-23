package ch.swisssmp.craftmmo;

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

import ch.swisssmp.craftmmo.mmoattribute.MmoSkill;
import ch.swisssmp.craftmmo.mmoblock.MmoBlock;
import ch.swisssmp.craftmmo.mmocommand.MmoCampCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoDungeonCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoItemCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoMobCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoMultiStateCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoPlayerPartyCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoRegionCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoTalkCommand;
import ch.swisssmp.craftmmo.mmocommand.MmoWorldCommand;
import ch.swisssmp.craftmmo.mmoentity.MmoEntityType;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;
import ch.swisssmp.craftmmo.mmoevent.MmoEventManager;
import ch.swisssmp.craftmmo.mmoitem.MmoItemclass;
import ch.swisssmp.craftmmo.mmoitem.MmoMining;
import ch.swisssmp.craftmmo.mmoplayer.MmoPlayerParty;
import ch.swisssmp.craftmmo.mmosound.MmoSound;
import ch.swisssmp.craftmmo.mmoworld.MmoDungeon;
//import ch.swisssmp.craftmmo.mmoplayer.MmoInventoryListener;
import ch.swisssmp.craftmmo.mmoworld.MmoWorld;
import ch.swisssmp.craftmmo.mmoworld.MmoWorldInstance;
import ch.swisssmp.craftmmo.util.MmoDelayedThreadTask;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

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
	public void onLoad()
	{
		try {
			MmoEntityType.registerEntities();
		} 
		catch (Exception e) {
			e.printStackTrace();
			this.setEnabled(false);
		}
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		eventManager = new MmoEventManager(this);
		MmoCommand mmoCommand = new MmoCommand();
		MmoItemCommand mmoItemCommand = new MmoItemCommand();
		MmoMultiStateCommand multiStateCommand = new MmoMultiStateCommand();
		MmoDungeonCommand dungeonCommand = new MmoDungeonCommand();
		this.getCommand("CraftMMO").setExecutor(mmoCommand);
		this.getCommand("rename").setExecutor(mmoCommand);
		this.getCommand("MmoItem").setExecutor(mmoItemCommand);
		this.getCommand("MmoShop").setExecutor(mmoItemCommand);
		this.getCommand("q").setExecutor(mmoItemCommand);
		this.getCommand("quest").setExecutor(mmoItemCommand);
		this.getCommand("quests").setExecutor(mmoItemCommand);
		this.getCommand("MmoMob").setExecutor(new MmoMobCommand());
		this.getCommand("MmoWorld").setExecutor(new MmoWorldCommand());
		this.getCommand("MmoCamp").setExecutor(new MmoCampCommand());
		this.getCommand("MmoRegion").setExecutor(new MmoRegionCommand());
		this.getCommand("MmoDungeon").setExecutor(dungeonCommand);
		this.getCommand("ready").setExecutor(dungeonCommand);
		this.getCommand("MmoMultiState").setExecutor(multiStateCommand);
		this.getCommand("MultiState").setExecutor(multiStateCommand);
		this.getCommand("party").setExecutor(new MmoPlayerPartyCommand());
		this.getCommand("talk").setExecutor(new MmoTalkCommand());
		
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
			MmoMining.loadMining();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoBlock.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoItemclass.loadClasses(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoMob.loadMobs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MmoSkill.loadSkills();
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
		try {
			MmoPlayerParty.loadParties();
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
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		// tell Bukkit about EntityExtendedZombie
		try {
			MmoEntityType.unregisterEntities();
		} 
		catch (Exception e) {
			e.printStackTrace();
			this.setEnabled(false);
		}
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
