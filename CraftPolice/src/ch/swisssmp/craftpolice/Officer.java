package ch.swisssmp.craftpolice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Officer {
    public  static void arrestPlayer(PlayerInteractEntityEvent event){
    	Player player = event.getPlayer();
    	Player target = (Player)event.getRightClicked();
    	//is responsible player officer?
		if(!Main.isOfficer(player)){
			player.sendMessage(ChatColor.RED+"Nur Polizisten können Spieler verhaften!");
			event.setCancelled(true);
			return;
		}
		//which is the officer's town?
		ConfigurationSection corps = getCorps(player);
		//this shouldn't be possible happen
		if(corps==null){
			player.sendMessage(ChatColor.RED+"Dein Corps wurde nicht gefunden. Bitte kontaktiere die Spielleitung.");
			event.setCancelled(true);
			return;
		}
		//is there an open cell?
		ConfigurationSection emptyCell = PrisonCell.getEmptyCell(corps);
		if(emptyCell==null){
			player.sendMessage(ChatColor.RED+"Dein Corps hat keine freien Zellen mehr!");
			event.setCancelled(true);
			return;
		}
		//is targeted player in town?
		Location targetLocation = target.getLocation();
		ApplicableRegionSet applicableRegionSet = Main.container.get(targetLocation.getWorld()).getApplicableRegions(targetLocation);
		Set<ProtectedRegion> regionSet = applicableRegionSet.getRegions();
		boolean isHome = false;
		for(ProtectedRegion region : regionSet){
			if(region.getId().equals(corps.getName())){
				isHome = true;
				break;
			}
		}
		//if not, is targeted player a member of the officer's town?
		PermissionUser targetUser = PermissionsEx.getUser(target);
		if(!isHome){
			if(targetUser==null){
				player.sendMessage(ChatColor.RED+"Dein Ziel ist kein Spieler.");
    			event.setCancelled(true);
				return;
			}
			if(!targetUser.inGroup(corps.getName())){
				player.sendMessage(ChatColor.RED+"Du kannst nur Bürger deiner eigenen Stadt ausserhalb der Stadtgrenzen verhaften!");
    			event.setCancelled(true);
				return;
			}
		}
		if(targetUser.inGroup("CraftPrisoner")){
			player.sendMessage(ChatColor.GOLD+target.getDisplayName()+" ist bereits verhaftet!");
			event.setCancelled(true);
			return;
		}
		Vector prisonVector = emptyCell.getVector("location");
		Vector bedVector = emptyCell.getVector("bed");
		World prisonWorld = Main.server.getWorld(emptyCell.getString("world"));
		Location prisonLocation = new Location(prisonWorld, prisonVector.getX(), prisonVector.getY(), prisonVector.getZ());
		Location bedLocation = new Location(prisonWorld, bedVector.getX(), bedVector.getY(), bedVector.getZ());
    	Sign sign = (Sign)prisonWorld.getBlockAt(prisonLocation).getState();
    	sign.setLine(3, target.getDisplayName());
    	sign.update();
		ConfigurationSection prisoners;
		if(!emptyCell.contains("prisoner"))
			prisoners = emptyCell.createSection("prisoner");
		else prisoners = emptyCell.getConfigurationSection("prisoner");
		
		ConfigurationSection prisonerSection = prisoners.createSection(target.getName());
		prisonerSection.set("original_spawnpoint", target.getBedSpawnLocation().toVector());
		prisonerSection.set("original_world", target.getBedSpawnLocation().getWorld().getName());
		targetUser.addGroup("CraftPrisoner");
		target.setBedSpawnLocation(bedLocation);
		target.damage(10000);
		targetUser.save();
		Main.saveYamls();
		//arrest targeted player
		target.sendMessage(ChatColor.RED+"Du wurdest verhafted!");
		Main.server.broadcastMessage(ChatColor.GOLD+player.getDisplayName()+ChatColor.RED+" hat "+ChatColor.GOLD+((Player)target).getDisplayName()+ChatColor.RED+" verhaftet!");
    }
    public static void freePlayer(PlayerInteractEntityEvent event){
    	Player player = event.getPlayer();
    	Player target = (Player)event.getRightClicked();
    	//is responsible player officer?
		if(!Main.isOfficer(player)){
			player.sendMessage(ChatColor.RED+"Nur Polizisten können Spieler freilassen!");
			event.setCancelled(true);
			return;
		}
		ConfigurationSection corps = getCorps(player);
		ConfigurationSection cellsSection = corps.getConfigurationSection("cells");
		if(cellsSection==null){
			player.sendMessage(ChatColor.GOLD+target.getDisplayName()+ChatColor.RED+" ist nicht in deiner Stadt verhaftet!");
			event.setCancelled(true);
			return;
		}
		ConfigurationSection cell = null;
		for(String cellName : cellsSection.getKeys(false)){
			cell = cellsSection.getConfigurationSection(cellName);
			ConfigurationSection prisoner = cell.getConfigurationSection("prisoner");
			if(prisoner.contains(target.getName()))
				break;
			else cell = null;
		}
		//this should not be possible to happen
		if(cell==null){
			player.sendMessage(ChatColor.GOLD+target.getDisplayName()+ChatColor.RED+" ist nicht in deiner Stadt verhaftet!");
			event.setCancelled(true);
			return;
		}
		PrisonCell.setFree(cell, target.getName());
		player.sendMessage(ChatColor.GREEN+"Du hast "+ChatColor.GOLD+target.getDisplayName()+ChatColor.GREEN+" befreit!");
    }
    public static void unpackEquipment(PlayerInteractEvent event){
    	Player player = event.getPlayer();
    	ItemStack item = event.getItem();
		if(!Main.isOfficer(player)){
			player.sendMessage(ChatColor.RED+"Diese Ausrüstung kann nur ein Polizist auspacken!");
			event.setCancelled(true);
			return;
		}
		HashMap<Integer, ItemStack> excess = player.getInventory().addItem(Items.helmet, Items.chestplate, Items.leggins, Items.boots, Items.bat);
		for (Map.Entry<Integer, ItemStack> me : excess.entrySet()) {
			player.getWorld().dropItem(player.getLocation(), me.getValue());
		}
		player.getInventory().remove(item);
		event.setCancelled(true);
    }
    public static boolean isChiefOfPolice(Player player){
    	ConfigurationSection chiefSection = Main.policecorps.getConfigurationSection("chiefs");
    	if(chiefSection==null)
    		return false;
    	return (chiefSection.contains(player.getName()) && player.hasPermission("craftpolice.chiefofpolice"));
    }
    public static ConfigurationSection getCorps(Player officer){
    	if(isChiefOfPolice(officer)){
    		String city = Main.chiefs.getConfigurationSection(officer.getName()).getString("city");
    		return Main.corps.getConfigurationSection(city);
    	}
    	else{
    		Set<String> corpNames = Main.corps.getKeys(false);
    		ConfigurationSection cityCorps = null;
    		for(String corpName : corpNames){
    			cityCorps = Main.corps.getConfigurationSection(corpName);
    			if(cityCorps==null){
    				Main.logger.info(ChatColor.RED+"Corps "+corpName+" nicht gefunden!");
    				Main.logger.info(ChatColor.GRAY+"Verfügbar: "+Main.corps.getKeys(false));
    				continue;
    			}
    			List<String> officers = cityCorps.getStringList("officers");
    			if(officers==null){
    				Main.logger.info(ChatColor.RED+"Corps hat keine Mitglieder!");
    				continue;
    			}
    			if(officers.contains(officer.getName())){
    				break;
    			}
    		}
    		return cityCorps;
    	}
    }
    public static BlockFace  getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw()+180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 22.5) {
            return BlockFace.NORTH;
        } else if (22.5 <= rotation && rotation < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return BlockFace.EAST;
        } else if (112.5 <= rotation && rotation < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return BlockFace.SOUTH;
        } else if (202.5 <= rotation && rotation < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return BlockFace.WEST;
        } else if (292.5 <= rotation && rotation < 337.5) {
            return BlockFace.NORTH_WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }
}
