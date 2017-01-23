package ch.swisssmp.bigdoors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressurePlate;
import org.bukkit.util.Vector;

public class Door {
	public static boolean open(String door){
		return open(door, null);
	}
	public static boolean open(String door, Player player){
		return set(door, true, player);
	}
	public static boolean close(String door){
		return close(door, null);
	}
	public static boolean close(String door, Player player){
		return set(door, false, player);
	}
	public static boolean set(Block block){
		return set(block, null);
	}
	public static boolean set(Block block, Player player){
		MaterialData data = block.getState().getData();
		boolean open;
		boolean reset = false;
		if(data instanceof Lever){
			Lever lever = (Lever) data;
			open = lever.isPowered();
		}
		else if(data instanceof Button){
			Button button = (Button) data;
			open = !button.isPowered();
			reset = true;
		}
		else if(data instanceof PressurePlate){
			PressurePlate pressurePlate = (PressurePlate) data;
			open = !pressurePlate.isPressed();
			reset = true;
		}
		else return false;
		String doorName = Door.getName(block);
		if(doorName.equals(""))
			return false;
		boolean success = set(doorName, open, player);
		if(success && reset){
			Runnable task = new ResetDoorTask(doorName, !open);
			Main.server.getScheduler().runTaskLater(Main.plugin, task, 40L);
		}
		return success;
	}
	public static boolean set(String doorName, boolean open, Player player){
		if(doorName.equals("") || doorName==null)
			return false;
		String sectionName;
		if(open){
			sectionName = "doors."+doorName+".1";
		}
		else{
			sectionName = "doors."+doorName+".0";
		}
		if(!Main.doors.contains(sectionName)){
			return false;
		}
		ConfigurationSection doorSection = Main.doors.getConfigurationSection(sectionName);
		if(doorSection.contains("message") && player != null){
			player.sendMessage(doorSection.getString("message"));
		}
		String schematicName = doorSection.getString("schematic");
		if(schematicName.equals("") || schematicName==null)
			return false;
		Vector vector = doorSection.getVector("vector");
		World world = Main.server.getWorld(doorSection.getString("world"));
		Location location = new Location(world, vector.getX(),vector.getY(),vector.getZ());
		Schematic.paste(schematicName, location);
		return true;
	}
	public static String getName(Block block){
		String handlerName = Handler.getName(block);
		if(handlerName.equals(""))
			return "";
		if(!Main.doors.contains("handlers."+handlerName)){
			return "";
		}
		return Main.doors.getString("handlers."+handlerName);
	}
}
