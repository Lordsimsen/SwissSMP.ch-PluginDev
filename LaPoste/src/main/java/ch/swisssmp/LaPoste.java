package ch.swisssmp;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.UUID;

public class LaPoste {

    public static void send(UUID sender, UUID recipient, ItemStack delivery, Location location){
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "send.php", new String[] {
                "sender=" + sender,
                "recipient=" + recipient,
                "mail=" + ItemUtil.serialize(delivery)
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            if(json == null || json.get("success").getAsBoolean() == false){
                SwissSMPler.get(sender).sendActionBar(ChatColor.RED + "Konnte Sendung nicht aufgeben!");
                location.getWorld().dropItem(location, delivery);
                return;
            }
            SwissSMPler.get(sender).sendActionBar(ChatColor.GREEN + "Sendung aufgegeben!");
        });
    }

    public static void receive(UUID recipient){
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "receive.php", new String[]{
                "recipient=" + recipient.toString()
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            if(json == null || json.get("success").getAsBoolean() == false){
                SwissSMPler.get(recipient).sendActionBar(ChatColor.RED + "Briefkasten leer");
                return;
            }
            ItemStack delivery = ItemUtil.deserialize(json.get("mail").getAsString()); // "mail" ? or what's the field name?
            Bukkit.getPlayer(recipient).getInventory().setItemInMainHand(delivery);
        });
    }

    public static void validRecipient(Player player, BookMeta oldMeta, BookMeta newMeta, Location location){
        //false if no/too many players
        //true if player found
        String recipient = newMeta.getTitle();
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "check_recipient.php", new String[]{
                "recipient=" + recipient
        });
        request.onFinish(() ->{
            JsonObject json = request.getJsonResponse();
            if(json == null || json.get("sucess").getAsBoolean() == false){
                player.sendMessage(LaPostePlugin.getPrefix() + " Ungültiger Empfänger. Bitte überprüfe die Schreibweise seines Namens. Bei wiederholtem Misslingen kontaktiere einen Techniker.");
                ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
                item.setItemMeta(oldMeta);
                location.getWorld().dropItem(location, item);
            } else{
                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                item.setItemMeta(newMeta);
                location.getWorld().dropItem(location, item);
            }
        });
    }

//    public static void updateDeliveries(){
//        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "la_poste/deliveries.php");
//        request.onFinish(() -> {
//            YamlConfiguration yamlConfiguration = request.getYamlResponse();
//            if (yamlConfiguration == null) {
//                return;
//            }
//            for(String key : yamlConfiguration.getKeys(false)){
//                ConfigurationSection deliverySection = yamlConfiguration.getConfigurationSection(key);
//                deliverySection.set("deliverable", true); //suboptimal, da so nicht alle Pakete/Briefe 24 h brauchen. Sondern alle am Voratg verschickten zum selben Zeitpunkt deliverable werden.
//            }
//        });
//    }
}
