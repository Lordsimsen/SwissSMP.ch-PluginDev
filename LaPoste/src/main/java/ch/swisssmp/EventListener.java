package ch.swisssmp;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

public class EventListener implements Listener {

    @EventHandler
    private void onMailboxPlace(BlockPlaceEvent event){
        if(!ItemUtil.getBoolean(event.getItemInHand(), "la_poste_mailbox")) return; //Might not work cuz the item alrdy disappeared?
        BlockState blockState = event.getBlockPlaced().getState();
        if(!(blockState instanceof Skull)) return;
        Skull mailbox = (Skull) blockState;
        mailbox.setOwningPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())); //might just overwrite the texture to the player's
    }

    @EventHandler
    private void onMailboxInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null) return;
        if(!(event.getClickedBlock().getState() instanceof Skull)) return;
        Skull skull = (Skull) event.getClickedBlock().getState();
        OfflinePlayer player = skull.getOwningPlayer();
        Player owner = player.getPlayer();
        if(!owner.hasPermission("laposte.use")) return;
        if(event.getItem() == null || event.getItem().getType() == Material.AIR){
            if(event.getPlayer() == owner){
                LaPoste.receive(event.getPlayer().getUniqueId());
                return;
            }
        }
        if(event.getItem().getType() == Material.WRITTEN_BOOK){
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            LaPoste.send(event.getPlayer().getUniqueId(), Bukkit.getPlayer(bookMeta.getTitle()).getUniqueId(), event.getItem(), event.getPlayer().getLocation());
        }
    }

    @EventHandler
    private void onPackageFill(InventoryClickEvent event){
        if(!(event.getClickedInventory() instanceof CraftingInventory)) return;
        CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
        ItemStack cursor = event.getCursor();
        if(ItemUtil.getBoolean(cursor, "la_poste_package")){
            if(inventory.getItem(event.getSlot()) == null || inventory.getItem(event.getSlot()).getType() == Material.AIR){
                inventory.setItem(event.getSlot(), Package.removeLastItem(cursor));
                return;
            }
            return;
        }
        ItemStack clicked = inventory.getItem(event.getSlot());
        if(clicked == null) return;
        if(clicked.getType() == Material.WRITTEN_BOOK) return;
        if(!ItemUtil.getBoolean(clicked, "la_poste_package")) return;
        if(event.getClick() != ClickType.LEFT) return;
        try {
            Package.addItem(clicked, cursor);
        } catch (Exception e){
            SwissSMPler.get(event.getView().getPlayer().getUniqueId()).sendActionBar(ChatColor.YELLOW + "Paket bereits voll!");
        }
    }

//    @EventHandler
//    private void oldOnMailboxPlace(PlayerInteractEvent event){
//        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
//        if(event.getClickedBlock() == null) return;
//        if(event.getItem().getType() != Material.PLAYER_HEAD) return;
//        if(!ItemUtil.getBoolean(event.getItem(), "la_poste_mailbox")) return;
//        Player player = event.getPlayer();
//        if(!player.hasPermission("laposte.use")) {
//            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du hast dafür keine Berechtigung");
//            return;
//        }
//        BlockState blockState = event.getClickedBlock().getState();
//        if(blockState instanceof Skull){
//            Skull skull = (Skull) blockState;
//            skull.
//        }
//        UUID playerId = player.getUniqueId();
//        ItemUtil.setString(event.getItem(), "owner", playerId.toString());
//    }


    /*
    Utterly obsolete as player heads are no entities
     */
//    @EventHandler
//    private void onMailboxInteract(PlayerInteractEntityEvent event){
//        if(event.getRightClicked() == null) return;
//        if(!(event.getRightClicked() instanceof ArmorStand)) return;
//        ArmorStand mailboxCarrier = (ArmorStand) event.getRightClicked();
//        ItemStack mailbox = mailboxCarrier.getEquipment().getHelmet();
//        if(mailbox == null || mailbox.getType() != Material.GOLDEN_PICKAXE) return; // not too necessary. needs to be validated by detig customitem creation
//        if(!ItemUtil.getBoolean(mailbox, "placed_mailbox")) return;
//        UUID owner = UUID.fromString(ItemUtil.getString(mailbox, "owner"));
//        Player player = event.getPlayer();
//        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
//        if(ItemUtil.getBoolean(mainHand, "la_poste_letter") || ItemUtil.getBoolean(mainHand, "la_poste_package")){
//            //TODO verschicken, bzw. in deliveries.php schreiben.
////            LaPoste.send(yamlConfiguration, mainHand);
//        }
//        if(player.getUniqueId() != owner){
//            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Kein Zugriff");
//            return;
//        }
//        if(mainHand == null || mainHand.getType() == Material.AIR){ //Webserver nach Lieferung abfragen und gegenenfalls ausliefern
//            HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "receive.php", new String[] {
//                    "recipient=" + player.getUniqueId()
//            });
//            request.onFinish(() -> {
//                JsonObject json = request.getJsonResponse();
//                if(json == null || json.get("success").getAsBoolean() == false){
//                    SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Briefkasten leer");
//                    return;
//                }
//                LaPoste.deliver(json, player);
//            });
//        }
//
//    }

    @EventHandler
    private void onBookSign(PlayerEditBookEvent event){
        if(!event.isSigning()) return;
        LaPoste.validRecipient(event.getPlayer(), event.getPreviousBookMeta(), event.getNewBookMeta(), event.getPlayer().getLocation());
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if(inventory.getItemInOffHand().getType() == Material.WRITABLE_BOOK && (inventory.getItemInMainHand() == null || inventory.getItemInMainHand().getType() == Material.AIR)){
            inventory.setItemInOffHand(null);
        } else{
            inventory.setItemInMainHand(null);
        }
    }

//    @EventHandler
//    private void onMailboxCraft(PrepareItemCraftEvent event){
//        //if it's the mailbox event, set mailbox (playerhead) as result.
//        //mind the colors
//    }
}
