package ch.swisssmp.entitysafety;

import ch.swisssmp.text.RawTextObject;
import ch.swisssmp.text.properties.ClickEventProperty;
import ch.swisssmp.text.properties.TextProperty;
import ch.swisssmp.utils.EntityUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PackEntityCommand implements CommandExecutor {

    private final EntitySafetyPlugin plugin;

    protected PackEntityCommand(EntitySafetyPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage(EntitySafetyPlugin.getPrefix()+" Kann nur ingame verwendet werden.");
            return true;
        }

        Player player = (Player) sender;
        Optional<Entity> entity = player.getWorld().getNearbyEntities(player.getLocation(),2,2,2)
                .stream().filter(e->e.getType()!= EntityType.PLAYER && e.getVehicle()==null).findAny();
        if(!entity.isPresent()){
            player.sendMessage(EntitySafetyPlugin.getPrefix()+" Keine Entity gefunden.");
            return true;
        }

        RawTextObject rawText = new RawTextObject(new TextProperty("Entity kopieren"));
        rawText.add(new ClickEventProperty(ClickEventProperty.Action.COPY_TO_CLIPBOARD, EntityUtil.serialize(entity.get()).toString()));
        player.spigot().sendMessage(rawText.toSpigot());
        return true;
    }
}
