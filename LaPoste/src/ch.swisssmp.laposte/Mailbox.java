package ch.swisssmp.laposte;

import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/*
This class has become utterly pointless.
 */
public class Mailbox {

//    private void spawnMailbox(){
//        mailboxCarrier = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
//        mailboxCarrier.setVisible(false);
//        mailboxCarrier.setGravity(false);
//        mailboxCarrier.setInvulnerable(true);
//        mailboxCarrier.setSmall(true);
//        ItemStack mailbox = CustomItems.getCustomItemBuilder("LA_POSTE_MAILBOX").build();
//        ItemUtil.setString(mailbox, "owner", owner.toString());
//        ItemUtil.setBoolean(mailbox, "placed_mailbox", true);
//        mailboxCarrier.getEquipment().setHelmet(mailbox);
//    }

    protected static File getFile(World world){
        File pluginDirectory = new File(world.getWorldFolder(), "plugindata/LaPoste");
        File dataFile = new File(pluginDirectory, "mailboxes.yml");
        if(!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
        if(!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e){
                return null;
            }
        }
        return dataFile;
    }

    protected static void saveMailbox(Location location, Player owner){
        File file = getFile(location.getWorld());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if(yamlConfiguration == null) yamlConfiguration = new YamlConfiguration();

        if(yamlConfiguration.get(owner.getUniqueId().toString()) == null){
            yamlConfiguration.set(owner.getUniqueId().toString(), new ArrayList<Location>());
        }
        ArrayList<Location> mailboxes = (ArrayList) yamlConfiguration.get(owner.getUniqueId().toString());
        if(!mailboxes.contains(location)) mailboxes.add(location);

        yamlConfiguration.set(owner.getUniqueId().toString(), mailboxes);
        yamlConfiguration.save(file);
    }

    public static UUID getMailboxOwner(Location location){
        File file = getFile(location.getWorld());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        for(String key : yamlConfiguration.getKeys(false)){
            List<Location> mailboxes = (List) yamlConfiguration.get(key);
            for(Location loc : mailboxes){
                if(loc.equals(location)) return UUID.fromString(key);
            }
        }
        return null;
    }

    protected static void removeMailbox(Location location) {
        File file = getFile(location.getWorld());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        for(String key : yamlConfiguration.getKeys(false)) {
            List<Location> mailboxes = (List) yamlConfiguration.get(key);
            if (mailboxes == null || mailboxes.isEmpty()) continue;
            for (Location loc : mailboxes) {
                if (loc.equals(location)) {
                    mailboxes.remove(loc);
                    break;
                }
            }
        }
        yamlConfiguration.save(file);
    }
}
