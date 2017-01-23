package ch.swisssmp.craftmmo.mmocommand;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MmoTalkCommand implements CommandExecutor{
	Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(sender instanceof Player)
    		player = (Player) sender;
    	else{
    		Main.info("Can only be executed from within the game.");
    		return true;
    	}
    	if(args.length<2){
    		return true;
    	}
    	String key = args[0];
    	if(!key.equals(MmoResourceManager.pluginToken)){
    		return true;
    	}
    	if(args.length>2){
			if(args.length>5){
		    	MmoResourceManager.processYamlResponse(player.getUniqueId(), "progress/select.php", new String[]{
		    			"player="+player.getUniqueId().toString(),
		    			"lore="+args[1], 
		    			"source="+args[3],
		    			"chapter="+args[5]
		    					});
			}	
    	}
		return true;
	}
}
