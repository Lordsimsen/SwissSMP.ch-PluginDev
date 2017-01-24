package ch.swisssmp.holosign;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be used within the game.");
			return true;
		}
		if(args==null) return false;
		if(args.length<1) return false;
		switch(args[0]){
		case "spawn":
		case "place":
		case "create":{
			if(args.length<3) {
				sender.sendMessage("/sign create [color] [text]");
				break;
			}
			ChatColor color = ChatColor.valueOf(args[1]);
			String[] textParts = Arrays.copyOfRange(args, 2, args.length);
			String text = String.join(" ", Arrays.asList(textParts));
			if(color==null) color = ChatColor.WHITE;
			Player player = (Player) sender;
	        ArmorStand armorStand = (ArmorStand) player.getWorld().spawn(player.getLocation(), ArmorStand.class);
	        armorStand.setVisible(false);
	        armorStand.setCustomName(color+text);
	        armorStand.setCustomNameVisible(true);
	        armorStand.setGravity(false);
			break;
		}
		case "remove":
		case "destroy":
		case "delete":{
			String containsText = "";
			if(args.length>1) {
				containsText = args[1];
			}
			int range = 5;
			if(args.length>2){
				range = Integer.parseInt(args[2]);
			}
			Player player = (Player) sender;
			List<Entity> entities = player.getNearbyEntities(range, range, range);
			int count = 0;
			for(Entity entity: entities){
				if(entity==null) continue;
				if(entity.getType()!=EntityType.ARMOR_STAND) continue;
				if(entity.getCustomName()==null) continue;
				if(!entity.isCustomNameVisible() || entity.hasGravity()) continue;
				if(entity.getCustomName().toLowerCase().contains(containsText.toLowerCase())){
					entity.remove();
					count++;
				}
			}
			sender.sendMessage(count+" Objekte entfernt.");
			break;
		}
		}
		return true;
	}

}
