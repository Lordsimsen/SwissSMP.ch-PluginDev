package ch.swisssmp.fortressassault;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;

public class Main extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	public static YamlConfiguration config;
	public static File configFile;
	protected static JavaPlugin plugin;
	protected static File dataFolder;
	private static String rootURL;
	private static String pluginToken;
	private static Random random = new Random();
	protected static Material crystalMaterial;
	protected static WorldGuardPlugin worldGuardPlugin;
	protected static Game game;
	protected static boolean debug = false;
	
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		dataFolder = this.getDataFolder();

		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("fortress").setExecutor(playerCommand);
		this.getCommand("sign").setExecutor(playerCommand);
		this.getCommand("teams").setExecutor(playerCommand);
		server.getPluginManager().registerEvents(this, this);
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		World world = Bukkit.getWorlds().get(0);
		Game.applyGamerules(world);
		
		configFile = new File(getDataFolder(), "config.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		loadYamls();
		try {
			PlayerClass.loadClasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		game = new Game();
	}
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		HandlerList.unregisterAll(plugin);
	}
    protected static void sendActionBar(Player player, String message){
    	if(player==null || message==null) return;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) craftPlayer).getHandle().playerConnection.sendPacket(ppoc);
    }
    protected static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    	if(!player.isOnline())return;
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(("{'text': '" + title + "'}").replace("'", "\""));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(("{'text': '" + subtitle + "'}").replace("'", "\""));
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
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
    protected static void loadYamls() {
        try {
        	config.load(configFile);
    		rootURL = config.getString("webserver");
    		if(!rootURL.endsWith("/")) rootURL+="/";
    		pluginToken = config.getString("token");
    		debug = config.getBoolean("debug");
    		crystalMaterial = Material.valueOf(config.getString("crystal"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	protected static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	protected static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000);
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			if(debug){
				Bukkit.getLogger().info(urlString);
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
	
	protected static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	protected static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
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
    protected static ItemStack createItemStack(ConfigurationSection dataSection){
    	Material material = Material.valueOf(dataSection.getString("mc_enum"));
    	short data = (short)dataSection.getInt("mc_id");
    	int amount = dataSection.getInt("amount");
    	ItemStack itemStack = new ItemStack(material, amount, data);
    	ItemMeta itemMeta = itemStack.getItemMeta();
    	if(dataSection.contains("name")&&!dataSection.getString("name").isEmpty())itemMeta.setDisplayName(dataSection.getString("name"));
    	if(dataSection.contains("lore")&&!dataSection.getString("lore").isEmpty()){
    		String[] lines = dataSection.getString("lore").split("\\r?\\n");
    		itemMeta.setLore(Arrays.asList(lines));
    	}
    	ConfigurationSection enchantmentsSection = dataSection.getConfigurationSection("enchantments");
    	for(String key : enchantmentsSection.getKeys(false)){
    		ConfigurationSection enchantmentSection = enchantmentsSection.getConfigurationSection(key);
    		Enchantment enchantment = Enchantment.getByName(enchantmentSection.getString("enchantment_enum"));
    		int level = enchantmentSection.getInt("level");
    		itemMeta.addEnchant(enchantment, level, true);
    	}
    	if(itemMeta instanceof PotionMeta && dataSection.contains("effect")){
    		PotionMeta potionMeta = (PotionMeta) itemMeta;
    		ConfigurationSection effectSection = dataSection.getConfigurationSection("effect");
    		PotionType potionType = PotionType.valueOf(effectSection.getString("type"));
    		boolean extended = effectSection.getInt("extended")==1;
    		boolean upgraded = effectSection.getInt("upgraded")==1;
    		PotionData potionData = new PotionData(potionType, extended, upgraded);
    		potionMeta.setBasePotionData(potionData);
    	}
    	itemStack.setItemMeta(itemMeta);
    	return itemStack;
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                  Map.Entry::getKey, 
                  Map.Entry::getValue, 
                  (e1, e2) -> e1, 
                  LinkedHashMap::new
                		));
    }
}
