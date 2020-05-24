package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.UUID;

public class LaPoste {

    public static void send(Player sender, OfflinePlayer recipient, ItemStack delivery, Location location){
        UUID senderId = sender.getUniqueId();
        UUID recipientId = recipient.getUniqueId();
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "send.php", new String[] {
                "sender=" + senderId,
                "recipient=" + recipientId,
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
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "laposte " + sender.getName() + " " + recipient.getName());
        });
    }

    public static void receive(UUID recipient, Location location){
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "receive.php", new String[]{
                "recipient=" + recipient.toString()
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            if(json == null || json.get("success").getAsBoolean() == false){
                SwissSMPler.get(recipient).sendActionBar(ChatColor.YELLOW + "Briefkasten leer");
                return;
            }
            ItemStack delivery = ItemUtil.deserialize(json.get("mail").getAsString());
            ItemUtil.setBoolean(delivery, "received", true);
            BookMeta deliveryMeta = (BookMeta) delivery.getItemMeta();
            SwissSMPler.get(recipient).sendActionBar(ChatColor.GREEN + "Du hast Post von " + ChatColor.YELLOW + deliveryMeta.getAuthor() + ChatColor.GREEN + "!");
            location.getWorld().dropItem(location, delivery);
        });
    }

    public static void validateRecipient(PlayerEditBookEvent event, Player player, BookMeta oldMeta, BookMeta newMeta, Location location, int slot){
        String recipient = newMeta.getTitle();
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "check_recipient.php", new String[]{
                "recipient=" + recipient
        }, RequestMethod.POST_SYNC);
        JsonObject json = request.getJsonResponse();
        if(json == null || !json.get("success").getAsBoolean()) {
            player.sendMessage(LaPostePlugin.getPrefix() + " Ungültiger Empfänger. Bitte überprüfe die Schreibweise seines Namens. Bei wiederholtem Misslingen kontaktiere einen Techniker.");
            event.setCancelled(true);
        } else{
            ItemStack decoy = new ItemStack(Material.WRITTEN_BOOK);
            decoy.setItemMeta(newMeta);
            recipient = json.get("player").getAsJsonObject().get("name").getAsString();
            newMeta.setTitle(recipient);
            BookMeta newerMeta = newMeta;
            String type = ItemUtil.getBoolean(decoy, "la_poste_package") ? "Paket" : "Brief";
            newerMeta.setDisplayName(ChatColor.YELLOW + type + " für " + ChatColor.AQUA + recipient);

            decoy.setItemMeta(newerMeta);
//            event.setNewBookMeta(newerMeta);

            updateCustomItem(decoy, ItemUtil.getBoolean(decoy, "la_poste_package"));
            event.setNewBookMeta((BookMeta) decoy.getItemMeta());
        }
    }

    private static void updateCustomItem(ItemStack delivery, boolean isPackage){
        if(isPackage){
            CustomItems.getCustomItemBuilder("LA_POSTE_PACKAGE_SIGNED").update(delivery);
        } else{
            CustomItems.getCustomItemBuilder("LA_POSTE_LETTER_SIGNED").update(delivery);
        }
    }
}
