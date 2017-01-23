package ch.swisssmp.dynamicwarpportals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.mccraftaholics.warpportals.api.WarpPortalsEvent;
import com.mccraftaholics.warpportals.objects.CoordsPY;
import com.mccraftaholics.warpportals.objects.PortalInfo;

import ch.swisssmp.dynamicwarpportals.PlayerCommand;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin implements Listener{
	public static Logger logger;
	public static Server server;
	public static PluginDescriptionFile pdfFile;
	public static YamlConfiguration destinations;
	public static File destinationsFile;
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		server = getServer();
		
		server.getPluginManager().registerEvents(this, this);
		
		destinationsFile = new File(getDataFolder(), "destinations.yml");
		this.getCommand("DynamicWarpPortals").setExecutor(new PlayerCommand());
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		destinations = new YamlConfiguration();
		loadYamls();
	}
    
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	@EventHandler
	public void enterPortal(WarpPortalsEvent event) throws Exception{
		PortalInfo portal = event.getPortal();
		if(!event.hasPermission() || !portal.name.toLowerCase().contains("dynamic"))
			return;
		if(!portal.name.toLowerCase().contains("return")){
			enterMainworldPortal(event);
		}
		else{ 
			enterReturnPortal(event);
		}
	}
	private void enterMainworldPortal(WarpPortalsEvent event){
		Player player = event.getPlayer();
		PermissionUser permissionUser = PermissionsEx.getUser(player);
		String oldReturnDest = getReturnDest(permissionUser);
		if(oldReturnDest.length()>0){
			permissionUser.removePermission("dynamicwarpportals.return."+oldReturnDest);
		}
		PortalInfo portal = event.getPortal();
		String portalName = portal.name;
		String returnDestName = portalName+"returndest";
		permissionUser.addPermission("dynamicwarpportals.return."+returnDestName);
		//player.sendMessage(ChatColor.DARK_PURPLE+"Deine Seele wurde an dieses Portal gebunden!");
	}
	private void enterReturnPortal(WarpPortalsEvent event) throws Exception{
		Player player = event.getPlayer();
		PermissionUser permissionUser = PermissionsEx.getUser(player);
		String returnDest = getReturnDest(permissionUser);
		if(returnDest.length()>0){
			CoordsPY newTPC = new CoordsPY(getDestination(returnDest));
			event.setTeleportCoordsPY(newTPC);
			permissionUser.removePermission("dynamicwarpportals.return."+returnDest);
			//player.sendMessage(ChatColor.DARK_PURPLE+"Du kannst wieder alle Portale betreten.");
		}
	}
	private Location getDestination(String name){
		ConfigurationSection locationSection = destinations.getConfigurationSection(name);
		Vector vector = locationSection.getVector("vector");
		World world = server.getWorld(locationSection.getString("world"));
		return new Location(world, vector.getX(), vector.getY(), vector.getZ());
	}
	private String getReturnDest(PermissionUser permissionUser){
		Map<String, List<String>> permissions = permissionUser.getAllPermissions();
		String result = "";
		for(List<String> permissionList : permissions.values()){
			for(String permission : permissionList){
				if(permission.toLowerCase().contains("dynamicwarpportals.return.")){
					result = permission.split("[.]")[2];
					break;
				}
			}
		}
		return result;
	}
    private void firstRun() throws Exception {
        if(!destinationsFile.exists()){
        	destinationsFile.getParentFile().mkdirs();
            copy(getResource("destinations.yml"), destinationsFile);
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
        	destinations.save(destinationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
        	destinations.load(destinationsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
