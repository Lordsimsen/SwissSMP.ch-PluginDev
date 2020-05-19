package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


/*
This class has become utterly pointless.
 */
public class Mailbox {

    private final UUID owner;

    private ArmorStand mailboxCarrier;
    private Location location;

    public Mailbox(Player owner, Location location){
        this.owner = owner.getUniqueId();
        this.location = location;
        spawnMailbox();
        saveMailbox();
    }

    private void spawnMailbox(){
        mailboxCarrier = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        mailboxCarrier.setVisible(false);
        mailboxCarrier.setGravity(false);
        mailboxCarrier.setInvulnerable(true);
        mailboxCarrier.setSmall(true);
        ItemStack mailbox = CustomItems.getCustomItemBuilder("LA_POSTE_MAILBOX").build();
        ItemUtil.setString(mailbox, "owner", owner.toString());
        ItemUtil.setBoolean(mailbox, "placed_mailbox", true);
        mailboxCarrier.getEquipment().setHelmet(mailbox);
    }

    private void saveMailbox(){
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "la_poste/mailboxes.php");
        request.onFinish(() -> {
                    YamlConfiguration yamlConfiguration = request.getYamlResponse();
                    if (yamlConfiguration == null) {
                        return;
                    }
                    for(String key : yamlConfiguration.getKeys(false)){
                        if(owner == UUID.fromString(key)) {
                            Bukkit.getLogger().info("User with id: " + owner.toString() + " already has a mailbox");
                            return;
                        }
                    }
                    ConfigurationSection playerConfiguration = yamlConfiguration.createSection(owner.toString());
                    playerConfiguration.set("player", Bukkit.getPlayer(owner));
                    playerConfiguration.set("world", location.getWorld());
                    playerConfiguration.set("location", location);
                    playerConfiguration.set("mailboxCarrier", mailboxCarrier);
                });
    }

    public static UUID getMailboxOwner(ArmorStand mailboxCarrier){
        ItemStack mailbox = mailboxCarrier.getEquipment().getHelmet();
        if(!ItemUtil.getBoolean(mailbox, "placed_mailbox")) return null;
        UUID owner = null;
        try {
            owner = UUID.fromString(ItemUtil.getString(mailbox, "owner"));
        } catch (Exception e){
            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Couldn't retrieve UUID");
            return null;
        }
        return owner;
    }

    public static void removeMailbox(YamlConfiguration yamlConfiguration, UUID owner) throws Exception{
        for(String key : yamlConfiguration.getKeys(false)){
            if(UUID.fromString(key) == owner){
                ConfigurationSection mailboxSection = yamlConfiguration.getConfigurationSection(key);
                ArmorStand mailboxCarrier = (ArmorStand) mailboxSection.get("mailboxCarrier");
                if(mailboxCarrier != null) mailboxCarrier.remove();
                yamlConfiguration.remove(key);
                return;
            }
            throw new Exception("Du hast keine Mailbox");
        }
    }
}
