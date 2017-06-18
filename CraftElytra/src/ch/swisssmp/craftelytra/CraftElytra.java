package ch.swisssmp.craftelytra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class CraftElytra extends JavaPlugin implements Listener{
	private Logger logger;
	private ItemStack elytra;
	private Server server;
	private ShapedRecipe elytraRecipe;
	protected NamespacedKey namespacedKey;
	protected static JavaPlugin plugin;
	protected static File gatesFile;
	protected static File dataFolder;
	protected static WorldEditPlugin worldEditPlugin;
	protected static WorldGuardPlugin worldGuardPlugin;
	protected static ArrayList<ElytraGate> gates = new ArrayList<ElytraGate>();
	protected static HashMap<Block, ElytraGate> gatesMap = new HashMap<Block,ElytraGate>();
	
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		dataFolder = this.getDataFolder();
		namespacedKey = new NamespacedKey(this, "Flugplattform");

		this.getCommand("elytragate").setExecutor(new PlayerCommand());
		server.getPluginManager().registerEvents(this, this);
		createRecipe();
		Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(worldEdit instanceof WorldEditPlugin){
			worldEditPlugin = (WorldEditPlugin) worldEdit;
		}
		else{
			new NullPointerException("WorldEdit missing");
		}
		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard instanceof WorldGuardPlugin){
			worldGuardPlugin = (WorldGuardPlugin) worldGuard;
		}
		else{
			new NullPointerException("WorldGuard missing");
		}
		gatesFile = new File(dataFolder, "gates.yml");
		loadGates();
		
	}
	public void onDisable() {
		CraftElytra.saveGates();
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
		HandlerList.unregisterAll(plugin);
	}
	public void createRecipe(){
		logger = Logger.getLogger("Minecraft");
		MaterialData mat_data = new MaterialData(Material.ELYTRA);
		elytra = mat_data.toItemStack(1);
		elytraRecipe = new ShapedRecipe(namespacedKey, elytra);
		elytraRecipe.shape("cdc","fnf","e e");
		elytraRecipe.setIngredient('c', Material.CHORUS_FLOWER);
		elytraRecipe.setIngredient('d', Material.DIAMOND);
		elytraRecipe.setIngredient('f', Material.FEATHER);
		elytraRecipe.setIngredient('n', Material.NETHER_STAR);
		elytraRecipe.setIngredient('e', Material.END_CRYSTAL);
		server.addRecipe(elytraRecipe);
		logger.info("Custom Recipe has been created");
	}
    @EventHandler(ignoreCancelled=true)
    private void craftItem(PrepareItemCraftEvent event) {
    	if(event.getRecipe()==null) return;
    	ItemStack result = event.getRecipe().getResult();
    	if(result.hashCode() == elytra.hashCode()){
            HumanEntity human = event.getView().getPlayer();
            if(human instanceof Player){
            	Player crafter = (Player)human;
            	if(!crafter.hasPermission("craftelytra.craft"))
            		event.getInventory().setResult(null);
            }
    	}
    }
    @EventHandler(ignoreCancelled=true)
    private void onLeverInteract(PlayerInteractEvent event){
    	if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
    	Block block = event.getClickedBlock();
    	if(block==null) return;
    	if(block.getType()!=Material.LEVER) return;
    	ElytraGate elytraGate = gatesMap.get(block);
    	if(elytraGate==null) return;
    	if(!event.getPlayer().hasPermission("craftelytra.elytragate.build")){
    		event.getPlayer().sendMessage(ChatColor.RED+"Du hast keine Berechtigung für dieses Elytra Gate.");
    		event.setCancelled(true);
    		return;
    	}
    	Bukkit.getScheduler().runTaskLater(this, new Runnable(){
    	public void run(){
        	elytraGate.elevate(((Lever)block.getState().getData()).isPowered());
    	}
    	}, 1L);
    }
    @EventHandler(ignoreCancelled=true)
    private void onSignPlace(SignChangeEvent event){
    	//is it the overworld?
    	if(event.getBlock().getWorld().getEnvironment()!=Environment.NORMAL){
    		return;
    	}
    	String[] lines = event.getLines();
    	//are there any lines on the sign?
    	if(lines==null) return;
    	if(lines.length<1) return;
    	//is it a wall sign?
    	Block block = event.getBlock();
    	if(block.getType()!=Material.WALL_SIGN) return;
    	//do the lines indicate the player wants to create an Elytra Gate?
    	boolean summonsElytraGate = false;
    	int elevation = 50;
    	for(String s : lines){
    		if(s.isEmpty()) continue;
    		if(s.equals("[ElytraGate]")){
    			summonsElytraGate = true;
    		}
    		//if we find a number let's use that as the elevation
    		else if(StringUtils.isNumeric(s))
    			elevation = Integer.valueOf(s);
    	}
    	if(!summonsElytraGate) return;
    	//what should the elevation of the gate be?
    	elevation = Math.min(220-block.getY(), Math.max(10, elevation));
    	Player player = event.getPlayer();
    	//does the player have permission?
    	if(!player.hasPermission("craftelytra.elytragate.build")){
    		player.sendMessage(ChatColor.RED+"Du kannst keine Elytra Gates bauen.");
    		return;
    	}
    	//can a gate be built on this height?
    	if(block.getLocation().getY()>200){
    		event.getPlayer().sendMessage(ChatColor.RED+"Auf dieser Höhe können keine Elytra Gates eingerichtet werden.");
    		return;
    	}
    	//is there already another gate at this location?
    	if(gatesMap.containsKey(block)){
    		event.getPlayer().sendMessage(ChatColor.RED+"Hier ist bereits ein Elytra Gate.");
    		return;
    	}
    	Sign sign = (Sign)block.getState().getData();
    	BlockFace direction = sign.getFacing();
    	Block attached = block.getRelative(direction.getOppositeFace());
    	Block centerBottom = attached.getRelative(direction.getOppositeFace()).getRelative(BlockFace.DOWN);
    	//is the Elytra Gate construction valid?
    	if(ElytraGate.validateConstruction(centerBottom, direction, -1, player)){
        	event.setLine(0, ChatColor.DARK_PURPLE+"Elytra Gate");
        	event.setLine(1, ChatColor.RED+"DEAKTIVIERT");
        	event.setLine(2, String.valueOf(elevation));
        	event.setLine(3, player.getName());
        	//create a new gate
    		new ElytraGate(block, centerBottom, direction, elevation, player);
    	}
    	else{
    		player.sendMessage(ChatColor.RED+"Bitte die Konstruktion prüfen.");
    	}
    }
    protected static void saveGates(){
    	YamlConfiguration yamlConfiguration = new YamlConfiguration();
    	int index = 0;
    	for(ElytraGate elytraGate : gates){
    		ConfigurationSection gateSection = yamlConfiguration.createSection("gate_"+index);
    		elytraGate.save(gateSection);
    		index++;
    	}
    	try {
			yamlConfiguration.save(gatesFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private static void loadGates(){
    	YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(gatesFile);
    	for(String key : yamlConfiguration.getKeys(false)){
    		new ElytraGate(yamlConfiguration.getConfigurationSection(key));
    	}
    }
}
