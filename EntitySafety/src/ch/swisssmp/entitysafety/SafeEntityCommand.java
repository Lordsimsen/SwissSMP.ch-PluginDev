package ch.swisssmp.entitysafety;

import ch.swisssmp.utils.EntityUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class SafeEntityCommand implements CommandExecutor {

    private final EntitySafetyPlugin plugin;

    protected SafeEntityCommand(EntitySafetyPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0) return false;

        if(!(sender instanceof Player)){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
            return true;
        }

        Player player = (Player) sender;

        UUID uuid;
        try{
            uuid = UUID.fromString(args[0]);
        }
        catch(Exception e){
            return false;
        }

        EntityDeathLog log = plugin.getLog();
        Optional<EntityDeathLogEntry> entry = log.get(uuid);
        if(!entry.isPresent()){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Eintrag nicht gefunden.");
            return true;
        }

        JsonObject json = entry.get().getJsonData();
        if(json.has("damageable") && json.get("damageable").getAsJsonObject().get("h").getAsFloat()<=0){
            json.get("damageable").getAsJsonObject().addProperty("h",1);
        }
        Location location = player.getLocation();
        Entity result = EntityUtil.deserialize(location, json);
        if(result==null){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Entity konnte nicht wiederhergestellt werden.");
            return true;
        }
        if(result instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) result;
            if(livingEntity.getHealth()<=0) livingEntity.setHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }

        log.remove(uuid);
        log.save();
        sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Entity wiederhergestellt.");
        return true;
    }
}
