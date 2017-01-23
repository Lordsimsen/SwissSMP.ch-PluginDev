package ch.swisssmp.interactivelore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	
	public static Main plugin;
	public static EventManager eventManager;

	public static YamlConfiguration config;
	public static File configFile;
	
	public static String rootURL;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		server = getServer();
		
		eventManager = new EventManager(this);
		server.getPluginManager().registerEvents(eventManager, this);

		configFile = new File(getDataFolder(), "config.yml");
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
		rootURL = config.getString("webserver");
		if(!rootURL.endsWith("/")){
			rootURL+="/";
		}
	}

	@Override
	public void onDisable() {
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
    public static void saveYamls() {
        try {
        	config.save(configFile);
        } catch (IOException e) {
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
}