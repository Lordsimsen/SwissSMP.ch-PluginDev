package ch.swisssmp.addonabnahme;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.commandscheduler.CommandScheduler;
import ch.swisssmp.webcore.DataSource;

public class AddonAbnahme extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		this.getCommand("AddonSign").setExecutor(new ConsoleCommand());
		
		server.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if((!lines[0].toLowerCase().equals("[addonabnahme]") && !lines[0].toLowerCase().equals("[addon]")) || !player.hasPermission("addonabnahme.request"))
			return;
		String city_name = lines[1];
		String addon_name = lines[2];
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("addons/register_addon.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"city="+URLEncoder.encode(city_name),
				"addon="+URLEncoder.encode(addon_name),
				"world="+URLEncoder.encode(event.getBlock().getWorld().getName()),
				"x="+event.getBlock().getX(),
				"y="+event.getBlock().getY(),
				"z="+event.getBlock().getZ()
		});
		if(yamlConfiguration==null){
			return;
		}
		if(yamlConfiguration.contains("message")){
			ConfigurationSection messageSection = yamlConfiguration.getConfigurationSection("message");
			ChatColor color = ChatColor.valueOf(messageSection.getString("color"));
			String text = messageSection.getString("text");
			player.sendMessage(color+text);
		}
		if(yamlConfiguration.contains("lines")){
			ConfigurationSection signSection = yamlConfiguration.getConfigurationSection("lines");
			for(String key : signSection.getKeys(false)){
				ConfigurationSection lineSection = signSection.getConfigurationSection(key);
				int line = lineSection.getInt("line");
				ChatColor color = ChatColor.valueOf(lineSection.getString("color"));
				String text = lineSection.getString("text");
				event.setLine(line, color+text);
			}
		}
		else{
			Bukkit.getScheduler().runTaskLater(this, new Runnable(){
				public void run(){CommandScheduler.runCommands();}
			}, 1);
			
		}
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Material material = block.getType();
		if(material != Material.SIGN_POST && material != Material.WALL_SIGN)
			return;
		BlockState state = block.getState();
		if(!(state instanceof Sign))
			return;
		Sign sign = (Sign) state;
		if(!sign.getLine(0).toLowerCase().contains("[addonabnahme]"))
			return;
		if(!event.getPlayer().hasPermission("addonabnahme.request")){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.DARK_RED+"Du kannst keine Addons an- oder abmelden.");
			return;
		}
		String city_name = sign.getLine(1);
		String addon_name = sign.getLine(2);
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("addons/unregister_addon.php", new String[]{
			"player="+player.getUniqueId().toString(),
			"city="+URLEncoder.encode(city_name),
			"addon="+URLEncoder.encode(addon_name),
			"x="+event.getBlock().getX(),
			"y="+event.getBlock().getY(),
			"z="+event.getBlock().getZ(),
			"world="+URLEncoder.encode(event.getBlock().getWorld().getName())
		});
		if(yamlConfiguration==null){
			return;
		}
		if(yamlConfiguration.contains("message")){
			ConfigurationSection messageSection = yamlConfiguration.getConfigurationSection("message");
			ChatColor color = ChatColor.valueOf(messageSection.getString("color"));
			String text = messageSection.getString("text");
			player.sendMessage(color+text);
		}
		if(yamlConfiguration.contains("allow")){
			if(yamlConfiguration.getInt("allow")==0){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onSignInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		Material material = block.getType();
		if(material != Material.SIGN_POST && material != Material.WALL_SIGN)
			return;
		BlockState state = block.getState();
		if(!(state instanceof Sign))
			return;
		Sign sign = (Sign) state;
		if(!sign.getLine(0).toLowerCase().contains("[addonabnahme]"))
			return;
		if(!event.getPlayer().hasPermission("addonabnahme.request")){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.DARK_RED+"Du kannst keine Addons an- oder abmelden.");
			return;
		}
		String city_name = sign.getLine(1);
		String addon_name = sign.getLine(2);
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("addons/toggle_approve.php", new String[]{
			"player="+player.getUniqueId().toString(),
			"city="+URLEncoder.encode(city_name),
			"addon="+URLEncoder.encode(addon_name),
		});
		if(yamlConfiguration==null){
			return;
		}
		if(yamlConfiguration.contains("message")){
			ConfigurationSection messageSection = yamlConfiguration.getConfigurationSection("message");
			ChatColor color = ChatColor.valueOf(messageSection.getString("color"));
			String text = messageSection.getString("text");
			player.sendMessage(color+text);
		}
		if(yamlConfiguration.contains("allow")){
			if(yamlConfiguration.getInt("allow")==0){
				event.setCancelled(true);
			}
		}
		CommandScheduler.runCommands();
	}
	
	protected static void editSign(ConfigurationSection dataSection){
		String worldName = dataSection.getString("world");
		World world = Bukkit.getWorld(worldName);
		if(world==null){
			logger.info("[AddonAbnahme] Konnte Schild nicht aktualisieren, Welt "+worldName+" unbekannt!");
			return;
		}
		int x = dataSection.getInt("x");
		int y = dataSection.getInt("y");
		int z = dataSection.getInt("z");
		Block block = world.getBlockAt(x, y, z);
		Material material = block.getType();
		if(material != Material.SIGN_POST && material != Material.WALL_SIGN)
			return;
		BlockState state = block.getState();
		if(!(state instanceof Sign))
			return;
		Sign sign = (Sign) state;
		ConfigurationSection linesSection = dataSection.getConfigurationSection("lines");
		for(String key : linesSection.getKeys(false)){
			ConfigurationSection lineSection = linesSection.getConfigurationSection(key);
			int line = lineSection.getInt("line");
			ChatColor color = ChatColor.valueOf(lineSection.getString("color"));
			String text = lineSection.getString("text");
			sign.setLine(line, color+text);
		}
		sign.update();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
