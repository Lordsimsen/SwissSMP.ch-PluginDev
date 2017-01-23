package ch.swisssmp.craftpolice;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PrisonCell {
	private static void setFree(ConfigurationSection cell){
		setFree(cell, null);
	}
	public static void setFree(ConfigurationSection cell, String prisonerName){
		ConfigurationSection prisonersSection = cell.getConfigurationSection("prisoner");
		Vector prisonVector = cell.getVector("location");
		World prisonWorld = Main.server.getWorld(cell.getString("world"));
		Location prisonLocation = new Location(prisonWorld, prisonVector.getX(), prisonVector.getY(), prisonVector.getZ());
		if(prisonersSection==null)
			return;
		Set<String> prisoners = prisonersSection.getKeys(false);
		for(String prisonername : prisoners){
			if(!prisonername.equals(prisonerName) && prisonerName != null)
				continue;
			ConfigurationSection prisonerSection = prisonersSection.getConfigurationSection(prisonername);
			Vector original_spawnpoint = prisonerSection.getVector("original_spawnpoint");
			String original_world = prisonerSection.getString("original_world");
			PermissionUser prisonerUser = PermissionsEx.getUser(prisonername);
			if(prisonerUser==null){
				Main.logger.info("Tried to set "+prisonername+" free but couldn't find that player!");
				return;
			}
			prisonerUser.removeGroup("CraftPrisoner");
			List<String> oldgroups = prisonerSection.getStringList("oldgroups");
			for(String oldgroup : oldgroups){
				prisonerUser.addGroup(oldgroup);
			}
			prisonerUser.save();
			
			Player prisoner = Main.server.getPlayer(prisonerName);
			if(prisoner == null){
				Main.logger.info("The player "+prisonername+" seems to be offline. His spawnpoint remains in the cell as it can only be changed when the player is online.");
				return;
			}
			prisoner.sendMessage(ChatColor.GREEN+"Du bist wieder frei!");
			prisoner.setBedSpawnLocation(
					new Location(
							Main.server.getWorld(original_world), 
							original_spawnpoint.getX(), 
							original_spawnpoint.getY(), 
							original_spawnpoint.getZ()
							)
					);
		}
		cell.set("prisoner", null);
		Block signBlock = prisonWorld.getBlockAt(prisonLocation);
		if(signBlock instanceof Sign){
	    	Sign sign = (Sign)signBlock.getState();
	    	sign.setLine(3, "-frei-");
	    	sign.update();
		}
		else Main.logger.info(signBlock.getClass()+"");
	}
	public static void delete(ConfigurationSection cell){
		Vector location = cell.getVector("location");
		World world = Main.server.getWorld(cell.getString("world"));
		Block block = world.getBlockAt(new Location(world, location.getX(), location.getY(), location.getZ()));
		block.setType(Material.AIR);
		setFree(cell);
		cell.getParent().set(cell.getName(), null);
	}
	public static ConfigurationSection getEmptyCell(ConfigurationSection corps){
		ConfigurationSection cells = corps.getConfigurationSection("cells");
		if(cells==null)
			return null;
		Set<String> cellNames = cells.getKeys(false);
		ConfigurationSection result = null;
		for(String cellName : cellNames){
			ConfigurationSection cell = cells.getConfigurationSection(cellName);
			ConfigurationSection prisonersSection = cell.getConfigurationSection("prisoner");
			if(prisonersSection==null){
				result = cell;
				break;
			}
			Set<String> prisoners = prisonersSection.getKeys(false);
			if(prisoners==null || prisoners.size()==0){
				result = cell;
				break;
			}
		}
		return result;
	}
}
