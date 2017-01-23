package ch.swisssmp.craftpolice;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChiefOfPolice {
	public static void promoteOfficer(PlayerInteractEntityEvent event){
    	Player player = event.getPlayer();
    	Entity target = event.getRightClicked();
    	Player officer = (Player)target;
    	Integer hand = player.getInventory().getHeldItemSlot();
		if(!Officer.isChiefOfPolice(player)){
			player.sendMessage(ChatColor.RED+"Du kannst keine Polizisten ernennen!");
			return;
		}
		String city = Main.chiefs.getConfigurationSection(player.getName()).getString("city");
    	ConfigurationSection corpsSection = Main.corps.getConfigurationSection(city);
    	List<String> officers = corpsSection.getStringList("officers");
    	
    	Integer officercount = 0;
    	if(officers!=null)
    		officercount = officers.size();
    	Integer maxofficercount = Main.config.getInt("max_officers");
    	//chief.sendMessage("Max: "+maxofficercount+" | Aktuell: "+officercount);
    	if(officercount >= maxofficercount){
    		player.sendMessage(ChatColor.RED+"Dein Corps hat bereits die maximale Grösse.");
			return;
    	}
    	PermissionUser officerUser = PermissionsEx.getUser(officer);
    	if(officerUser==null){
    		//this should never happen.
    		player.sendMessage(ChatColor.RED+"Fehler! Bitte Support kontaktieren.");
    		return;
    	}
    	if(!officerUser.inGroup(city)){
    		player.sendMessage(ChatColor.RED+officer.getDisplayName()+" ist kein Bürger der Stadt "+city+".");
    		return;
    	}
    	if(officers.contains(officer.getName())){
    		player.sendMessage(ChatColor.RED+officer.getDisplayName()+" ist bereits Hilfspolizist in deiner Stadt!");
    		return;
    	}
    	if(player.hashCode()==officer.hashCode()){
    		player.sendMessage(ChatColor.RED+"Du kannst nicht dein eigener Hilfspolizist sein. Vergiss es ;)");
    	}
    	Integer remaining = maxofficercount-officercount-1;
    	officerUser.addPermission("craftpolice.officer");
    	officers.add(officer.getName());
    	corpsSection.set("officers", officers);
    	player.getInventory().setItem(hand, null);
    	player.sendMessage(ChatColor.AQUA+"Du hast "+officer.getDisplayName()+" als Hilfspolizist eingestellt!");
    	String helpers = "Hilfspolizisten";
    	if(remaining==1)
    		helpers = "Hilfspolizist";
    	player.sendMessage(ChatColor.GRAY+"Du kannst noch "+ChatColor.YELLOW+remaining+ChatColor.GRAY+" "+helpers+" einstellen.");
    	officer.sendMessage(ChatColor.AQUA+"Du bist nun Hilfspolizist im Polizei Corps von "+city+"!");
    	player.getWorld().dropItem(player.getLocation(), Items.createContract(officer));
    	Main.saveYamls();
    }
    public static void firePoliceman(PlayerInteractEvent event){  
    	Player player = event.getPlayer();
    	ItemStack item = event.getItem();  		
    	ItemMeta meta = item.getItemMeta();
		String[] lore = new String[meta.getLore().size()];
		meta.getLore().toArray(lore);
		PermissionUser officerUser = PermissionsEx.getUser(lore[0]);
		if(!officerUser.has("craftpolice.officer")){
			player.sendMessage(ChatColor.GOLD+lore[0]+ChatColor.RED+" ist kein Polizist mehr!");
			event.setCancelled(true);
			return;
		}
		String city = Main.chiefs.getConfigurationSection(player.getName()).getString("city");
		ConfigurationSection cityCorps = Main.corps.getConfigurationSection(city);
		//this shouldn't be possible
		if(cityCorps==null){
			player.sendMessage(ChatColor.RED+"Polizei Corps von "+city+" nicht gefunden!");
			Set<String> corpsList = Main.corps.getKeys(false);
			player.sendMessage(ChatColor.GRAY+"Verfügbar: "+corpsList.toString());
			event.setCancelled(true);
			return;
		}
		List<String> officers = cityCorps.getStringList("officers");
		if(!officers.contains(lore[0])){
			player.sendMessage(ChatColor.GOLD+lore[0]+ChatColor.RED+" gehört nicht zu deinem Corps!");
			event.setCancelled(true);
			return;
		}
		//at this point we know the player bound to this contract is an officer in the chief's city, so we fire him
		officerUser.removePermission("craftpolice.officer");
		officers.remove(lore[0]);
		cityCorps.set("officers", officers);
		player.getInventory().remove(item);
		player.sendMessage(ChatColor.GRAY+"Du hast "+ChatColor.GOLD+lore[0]+ChatColor.GRAY+" gefeuert!");
		Player firedOfficer = Main.server.getPlayer(lore[0]);
		if(firedOfficer!=null){
			firedOfficer.sendMessage(ChatColor.GOLD+player.getDisplayName()+ChatColor.RED+" hat deinen Vertrag bei der Polizei gekündigt!");
		}
		Main.saveYamls();
	}
	public static void createPrisonCell(PlayerInteractEvent event){
    	Player player = event.getPlayer();
    	PermissionUser permissionUser = PermissionsEx.getUser(player);
    	Block block = event.getClickedBlock();
    	Location targetLocation = block.getLocation();
    	ConfigurationSection cityCorps = Officer.getCorps(player);
    	Integer maxCells = Main.config.getInt("max_cells");
    	ConfigurationSection cells = cityCorps.getConfigurationSection("cells");
    	Integer cellCount = 0;
    	PermissionGroup permissionGroup = PermissionsEx.getPermissionManager().getGroup(cityCorps.getName());
    	if(permissionGroup == null){
    		player.sendMessage(cityCorps.getName());
    		player.sendMessage(permissionUser.getParentIdentifiers(targetLocation.getWorld().getName()).toString());
    		return;
    	}
    	if(!permissionGroup.has("craftpolice.prison")){
        	if(cells!=null){
        		cellCount = cells.getKeys(false).size();
            	if(cellCount >= maxCells){
            		player.sendMessage(ChatColor.RED+"Dein Corps hat bereits die maximale Anzahl Zellen!");
            		event.setCancelled(true);
            		return;
            	}
        	}
    	}
    	if(cells==null) cells = cityCorps.createSection("cells");
    	String city = cityCorps.getName();
    	ApplicableRegionSet regionSet = Main.container.get(targetLocation.getWorld()).getApplicableRegions(targetLocation);
    	boolean isHome = false;
    	for(ProtectedRegion region : regionSet){
    		if(region.getId().toLowerCase().equals(city.toLowerCase())){
    			isHome = true;
    			break;
    		}
    	}
    	if(!isHome){
    		player.sendMessage(ChatColor.RED+"Du kannst hier keine Zelle eröffnen!");
    		event.setCancelled(true);
    		return;
    	}
    	targetLocation = block.getRelative(event.getBlockFace()).getLocation();
    	Block centerBlock = targetLocation.getBlock();
    	centerBlock.setType(Material.WALL_SIGN);
    	Sign sign = (Sign)centerBlock.getState();
    	sign.setLine(0, "[Polizei]");
    	sign.setLine(1, Officer.getCorps(player).getName());
    	sign.setLine(2, "Zelle");
    	sign.setLine(3, "-frei-");
    	org.bukkit.material.Sign matData = (org.bukkit.material.Sign)sign.getData();
    	matData.setFacingDirection(event.getBlockFace());
    	sign.update();
    	ConfigurationSection newCell = cells.createSection(cellCount+"");
    	newCell.set("location", targetLocation.toVector());
    	newCell.set("world", targetLocation.getWorld().getName());
    	newCell.set("bed", event.getClickedBlock().getLocation().toVector());
		player.sendMessage(ChatColor.GREEN+"Zelle erstellt!");
    	Main.saveYamls();
	}
	public static void removePrisonCell(BlockBreakEvent event){
		Block block = event.getBlock();
		Sign sign = (Sign)block.getState();
		if(!sign.getLine(0).equals("[Polizei]")){
			//event.getPlayer().sendMessage("hi");
			return;
		}
		Player player = event.getPlayer();
		String city = sign.getLine(1);
		PermissionUser permissionUser = PermissionsEx.getUser(player);
		if(!Officer.isChiefOfPolice(player) || !permissionUser.inGroup(city)){
			player.sendMessage(ChatColor.RED+"Nur der Polizeikommandant von "+city+" kann diese Zelle aufheben!");
			event.setCancelled(true);
			return;
		}
		Location location = block.getLocation();
		ConfigurationSection cells = Officer.getCorps(player).getConfigurationSection("cells");
		if(cells==null){
			Main.logger.info("Something went wrong trying to remove a prisoncell at "+location.toString());
			Main.logger.info("The corresponding city does not contain any cells");
			event.setCancelled(true);
			return;
		}
		ConfigurationSection cell = null;
		for(String cellName : cells.getKeys(false)){
			cell = cells.getConfigurationSection(cellName);
			Vector cellLocation = cell.getVector("location");
			World world = Main.server.getWorld(cell.getString("world"));
			if(cellLocation==null){
				player.sendMessage(cell.getKeys(false).toString());
				continue;
			}
			if(cellLocation.equals(location.toVector()) && world.equals(location.getWorld()))
				break;
			else cell = null;
		}
		if(cell == null){
			Main.logger.info("Something went wrong trying to remove a prisoncell at "+location.toString());
			Main.logger.info("The corresponding city does not contain a cell at this location.");
			event.setCancelled(true);
			return;
		}
		PrisonCell.delete(cell);
		player.sendMessage(ChatColor.GREEN+"Zelle gelöscht!");
    	Main.saveYamls();
	}
}
