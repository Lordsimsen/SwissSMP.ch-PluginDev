package ch.swisssmp.webcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.server.ServerManager;

public class WebCore extends JavaPlugin{
	protected static Logger logger;
	protected static File configFile;
	protected static YamlConfiguration config;
	protected static PluginDescriptionFile pdfFile;

	protected static File dataFolder;
	protected static WebCore plugin;
	protected static boolean debug;
	
	private ServerManager serverManager;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		this.serverManager = new ServerManager(this);
		
		this.getCommand("webcore").setExecutor(new ch.swisssmp.webcore.PlayerCommand());
		this.getCommand("servermanager").setExecutor(new ch.swisssmp.server.PlayerCommand());
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
		
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		HandlerList.unregisterAll(this);
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
    public void loadYamls() {
        try {
        	config.load(configFile);
    		DataSource.rootURL = config.getString("webserver");
    		DataSource.pluginToken = config.getString("token");
    		if(config.contains("user") && config.contains("password")){
    			DataSource.htaccess = Base64.getEncoder().encodeToString((config.getString("user")+":"+config.getString("password")).getBytes());
    			String testresponse1 = DataSource.getResponse("checks/htaccess.php", RequestMethod.GET);
    			String testresponse2 = DataSource.getResponse("checks/htaccess.php", RequestMethod.POST);
    			if(!testresponse1.equals("htaccess successful")){
    				throw new Exception("Htaccess authorization (via GET) failed! Please check user and password in the config.");
    			}
    			if(!testresponse2.equals("htaccess successful")){
    				throw new Exception("Htaccess authorization (via POST) failed! Please check user and password in the config.");
    			}
    		}
    		debug = config.getBoolean("debug");
    		this.serverManager.reload();
    		if(!DataSource.rootURL.endsWith("/")){
    			DataSource.rootURL+="/";
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getConfigServerId(){
    	if(!config.contains("server_id")) return -1;
    	return config.getInt("server_id");
    }
    
    public String getConfigServerName(){
    	if(!config.contains("server_name")) return "UnnamedServer";
    	return config.getString("server_name");
    }
    
    @Override
    public void saveConfig(){
    	try {
			config.save(configFile);
		} catch (IOException e) {
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
    public static WebCore getInstance(){
    	return plugin;
    }
}
