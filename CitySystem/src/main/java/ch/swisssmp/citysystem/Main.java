package ch.swisssmp.citysystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	public static YamlConfiguration cities;
	public static File citiesFile;
	public static YamlConfiguration _config;
	public static ConfigurationSection config;
	public static File configFile;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		citiesFile = new File(getDataFolder(), "cities.yml");
		configFile = new File(getDataFolder(), "config.yml");
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		cities = new YamlConfiguration();
		_config = new YamlConfiguration();
		loadYamls();
		config = _config.getConfigurationSection("CitySystem_Configuration");
		if(config==null){
			config = _config.createSection("CitySystem_Configuration");
		}
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    
    private void firstRun() throws Exception {
        if(!citiesFile.exists()){
        	citiesFile.getParentFile().mkdirs();
            copy(getResource("cities.yml"), citiesFile);
        }
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
        	cities.save(citiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	_config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
        	cities.load(citiesFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        	_config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}