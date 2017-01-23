package ch.swisssmp.prismsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	protected static JavaPlugin plugin;
	private Logger logger;
	protected File dataFolder;
	private File configFile;
	private static String rootURL;
	private static String pluginToken;
	private static Random random = new Random();
	protected YamlConfiguration config;
	protected static HashMap<UUID, Search> searches = new HashMap<UUID, Search>();

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("search").setExecutor(playerCommand);
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
		rootURL = config.getString("webserver");
		if(!rootURL.endsWith("/")) rootURL+="/";
		pluginToken = config.getString("token");
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
    private void loadYamls() {
        try {
        	config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000);
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String tempString = "";
			while(null!=(tempString = br.readLine())){
				resultString+= tempString;
			}
			if(resultString.isEmpty()){
				return "";
			}
			return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
		String resultString = convertWebYamlString(getResponse(relativeURL, params));
		if(resultString.isEmpty()){
			return new YamlConfiguration();
		}
		try{
			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			yamlConfiguration.loadFromString(resultString);
			return yamlConfiguration;
		}
		catch(Exception e){
			e.printStackTrace();
			return new YamlConfiguration();
		}
	}
    private static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
}
