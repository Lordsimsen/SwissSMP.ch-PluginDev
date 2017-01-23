package ch.swisssmp.bigdoors;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class EventManager implements Listener{
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		ItemStack itemStack = event.getItemInHand();
		ItemMeta meta = itemStack.getItemMeta();
		if(!meta.hasDisplayName()){
			return;
		}
		String name = meta.getDisplayName();
		if(!Main.doors.contains("doors."+name)){
			return;
		}
		Handler handler = Handler.get(itemStack.getType());
		String handlerName = Handler.getName(event.getBlock(), itemStack);
		if(handlerName.equals(""))
			return;
		Main.doors.set("handlers."+handlerName, name);
		Main.saveYamls();
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GREEN+handler.toString()+" mit '"+name+"' verknüpft!");
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Handler handler = Handler.get(block.getType());
		String handlerName = Handler.getName(block);
		if(handlerName.equals("") || handlerName==null)
			return;
		if(!Main.doors.contains("handlers."+handlerName))
			return;
		Main.doors.set("handlers."+handlerName, null);
		Main.saveYamls();
		event.getPlayer().sendMessage(ChatColor.GREEN+handler.toString()+" entfernt!");
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		switch(action){
		case RIGHT_CLICK_BLOCK:
		case PHYSICAL:
			if(isHandler(block)){
				Door.set(block, player);
			}
			break;
		default:
			return;
		}
	}
	public static boolean isHandler(Block block){
		return isHandler(block, block.getType());
	}
	public static boolean isHandler(Block block, Material material){
		List<Material> possibleMaterials = Arrays.asList(
				Material.LEVER, 
				Material.WOOD_BUTTON, 
				Material.STONE_BUTTON, 
				Material.WOOD_PLATE,
				Material.STONE_PLATE,
				Material.IRON_PLATE,
				Material.GOLD_PLATE
				);
		if(!possibleMaterials.contains(material))
			return false;
		String doorName = Door.getName(block);
		if(doorName.equals(""))
			return false;
		return Main.doors.contains("doors."+doorName);
	}
}
