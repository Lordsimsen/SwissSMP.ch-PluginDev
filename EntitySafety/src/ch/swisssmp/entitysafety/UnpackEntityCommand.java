package ch.swisssmp.entitysafety;

import ch.swisssmp.utils.EntityUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UnpackEntityCommand implements CommandExecutor {

    private final EntitySafetyPlugin plugin;

    protected UnpackEntityCommand(EntitySafetyPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Location location;
        String valueString;
        if((sender instanceof Player)){
            Player player = (Player) sender;
            location = player.getLocation();
            valueString = args[0];
        }
        else if(args.length>4){
            String worldName = args[0];
            World world = Bukkit.getWorld(worldName);
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            location = new Location(world,x,y,z);
            valueString = args[4];
        }
        else{
            return false;
        }

        JsonObject json;
        try{
            json = JsonUtil.parse(valueString);
        }
        catch(Exception e){
            return false;
        }

        if(json==null){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Json scheint ungültig zu sein.");
            return true;
        }

        Entity entity = EntityUtil.deserialize(location, json);
        if(entity==null){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Entity konnte nicht entpackt werden.");
            return true;
        }
        sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Entity entpackt.");
        return true;
    }
}
