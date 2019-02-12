package ch.swisssmp.npc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class NPCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		if(!(sender instanceof Player)){
			sender.sendMessage("[NPC] Kann nur ingame verwendet werden.");
			return true;
		}
		Player player = (Player) sender;
		
		switch(args[0].toLowerCase()){
		case "erstelle":
		case "create":{
			if(args.length<2) return false;
			String typeString = args[1];
			EntityType entityType;
			try{
				entityType = EntityType.valueOf(typeString);
			}
			catch(Exception e){
				sender.sendMessage("[NPC] "+ChatColor.RED+"Unbekannter Typ "+typeString);
				return true;
			}
			
			NPCInstance npc = NPCInstance.create(entityType, player.getLocation());
			if(npc==null){
				sender.sendMessage("[NPC] "+ChatColor.RED+"Der NPC konnte nicht erstellt werden.");
				return true;
			}
			SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"NPC erstellt!");
			return true;
		}
		default: return false;
		}
	}

}
