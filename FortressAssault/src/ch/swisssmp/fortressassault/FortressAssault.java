package ch.swisssmp.fortressassault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class FortressAssault extends JavaPlugin implements Listener{
	private Logger logger;
	private Server server;
	public static YamlConfiguration config;
	public static File configFile;
	protected static String scoreSymbol = ChatColor.RESET.toString();
	protected static FortressAssault plugin;
	protected static File dataFolder;
	protected static Material crystalMaterial;
	protected static WorldGuardPlugin worldGuardPlugin;
	protected static Game game;
	protected static boolean debug = false;

	protected static final HashMap<UUID, Integer> players = new HashMap<UUID, Integer>();
	protected static final HashMap<UUID, FortressTeam> teamMap = new HashMap<UUID, FortressTeam>();
	
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
		loadYamls();
		try {
			PlayerClass.loadClasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Objective objective : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()){
			objective.unregister();
		}
		for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()){
			for(String entry : team.getEntries()){
				team.removeEntry(entry);
			}
			team.unregister();
		}
		game = new Game();
	}
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin) plugin);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}

	public static FortressAssault getInstance(){
		return plugin;
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
	protected static World getLobby(){
		return Bukkit.getWorld("FortressAssault");
	}
	protected static Location getPoint(World world, String name){
		Vector vector = FortressAssault.config.getVector(name);
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}
    protected static void updateTabList(Player player, FortressTeam team){
    	String fullDisplayName;
    	if(team!=null){
    		fullDisplayName = team.color+player.getName()+ChatColor.RESET;
    	}
    	else{
    		fullDisplayName = player.getName()+ChatColor.RESET;
    	}
		String header = ChatColor.RED+"SwissSMP.ch";
		String footer = ChatColor.GRAY+"Fortress Assault Event";
		player.setDisplayName(fullDisplayName);
		player.setPlayerListName(fullDisplayName);
		TabList.setHeaderFooter(player, header, footer);
    }
    protected static void loadYamls() {
        try {
        	config = YamlConfiguration.loadConfiguration(configFile);
    		debug = config.getBoolean("debug");
    		crystalMaterial = Material.valueOf(config.getString("crystal"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
