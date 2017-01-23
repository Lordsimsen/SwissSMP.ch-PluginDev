package ch.swisssmp.craftpolice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import ch.swisssmp.craftpolice.CraftPolicePlayerCommand;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static YamlConfiguration policecorps;
	public static File policecorpsFile;
	public static ConfigurationSection corps;
	public static ConfigurationSection chiefs;
	public static YamlConfiguration _config;
	public static ConfigurationSection config;
	public static File configFile;
	public static PluginDescriptionFile pdfFile;
	public static WorldGuardPlugin worldguard;
	public static RegionContainer container;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		this.getCommand("CraftPolice").setExecutor(new CraftPolicePlayerCommand());
		worldguard = getWorldGuard();
		container = Main.worldguard.getRegionContainer();
		
		server.getPluginManager().registerEvents(this, this);
		
		createRecipes();
		policecorpsFile = new File(getDataFolder(), "policecorps.yml");
		configFile = new File(getDataFolder(), "config.yml");
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		policecorps = new YamlConfiguration();
		_config = new YamlConfiguration();
		loadYamls();
		corps = policecorps.getConfigurationSection("corps");
		chiefs = policecorps.getConfigurationSection("chiefs");
		if(chiefs==null)
			chiefs = policecorps.createSection("chiefs");
		if(corps==null)
			corps = policecorps.createSection("corps");
		config = _config.getConfigurationSection("CraftPolice_Configuration");
		if(config==null){
			config = _config.createSection("CraftPolice_Configuration");
			config.set("max_officers", 3);
			config.set("max_inmates", 1);
		}
		
	}
	public void createRecipes(){
		Items.createBadgeRecipe();
		Items.createEquipmentRecipe();
		Items.createEquipmentItems();
	}
	
	@Override
	public void onDisable() {
		saveYamls();
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        logger.info("WorldGuard benötigt!");
	    }
	
	    return (WorldGuardPlugin) plugin;
	}
	
    @EventHandler
    public void craftItem(PrepareItemCraftEvent event) {
    	ItemStack result = event.getRecipe().getResult();
    	if(result.hashCode() == Items.badge.hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!Officer.isChiefOfPolice(crafter)){
            		event.getInventory().setResult(null);
            		return;
            	}
            }
    	}
    	else if(result.hashCode() == Items.equipment.hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!isOfficer(crafter)){
            		event.getInventory().setResult(null);
            		return;
            	}
            }
    	}
    }
    public static boolean isOfficer(Player player){
    	return (Officer.isChiefOfPolice(player)||player.hasPermission("craftpolice.officer"));
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	Player player = event.getPlayer();
    	Entity target = event.getRightClicked();
    	Integer hand = player.getInventory().getHeldItemSlot();
    	ItemStack item = player.getInventory().getItem(hand);
    	if(item!=null && target!=null){
    		if(item.hashCode() == Items.badge.hashCode() && target instanceof Player && event.getHand() == EquipmentSlot.HAND){
    			ChiefOfPolice.promoteOfficer(event);
        	}
        	else if(item.hashCode() == Items.bat.hashCode() && target instanceof Player && event.getHand() == EquipmentSlot.HAND){
        		Player targetPlayer = (Player)target;
        		PermissionUser targetUser = PermissionsEx.getUser(targetPlayer);
        		if(!targetUser.inGroup("CraftPrisoner")){
            		Officer.arrestPlayer(event);
        		}
        		else Officer.freePlayer(event);
        	}
    	}
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
    	Player player = event.getPlayer();
    	ItemStack item = event.getItem();
    	if(item!=null){
        	if(item.hashCode() == Items.equipment.hashCode() && event.getAction() == Action.RIGHT_CLICK_AIR && event.getHand() == EquipmentSlot.HAND){
        		Officer.unpackEquipment(event);
        	}
        	else if(Officer.isChiefOfPolice(player) && item.getType() == Material.PAPER && item.getItemMeta().getDisplayName().equals("Arbeitsvertrag") && event.getAction() == Action.RIGHT_CLICK_AIR && event.getHand() == EquipmentSlot.HAND){
        		ChiefOfPolice.firePoliceman(event);
        	}
        	else if(Officer.isChiefOfPolice(player) && item.hashCode() == Items.bat.hashCode() && event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK){
        		Block block = event.getClickedBlock();
        		if(block.getType() == Material.BED_BLOCK && event.getBlockFace() != BlockFace.UP && event.getBlockFace() != BlockFace.DOWN){
            		ChiefOfPolice.createPrisonCell(event);
        		}
        		else player.sendMessage(ChatColor.GRAY+"Halte die 'Schleichen'-Taste gedrückt und rechtsklicke an die Seite von einem Bett, um eine Zelle zu erstellen.");
        	}
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
    	Block block = event.getBlock();
    	if(block.getType() == Material.WALL_SIGN){
    		ChiefOfPolice.removePrisonCell(event);
    	}
    }
    
    private void firstRun() throws Exception {
        if(!policecorpsFile.exists()){
        	policecorpsFile.getParentFile().mkdirs();
            copy(getResource("policecorps.yml"), policecorpsFile);
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
        	policecorps.save(policecorpsFile);
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
        	policecorps.load(policecorpsFile);
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
