package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class EventListener implements Listener {

    @EventHandler
    private void onPlayerResourePackUpdate(PlayerResourcePackUpdateEvent event){
        event.addComponent("laposte");
    }

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        if(player == null) return;
        UUID playerId = player.getUniqueId();
        HTTPRequest request = DataSource.getResponse(LaPostePlugin.getInstance(), "check_mailbox.php", new String[]{
                "recipient=" + playerId
        });
        request.onFinish(() -> {
            JsonObject json = request.getJsonResponse();
            if (json == null || !json.get("success").getAsBoolean() || json.get("count").getAsInt() == 0) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(LaPostePlugin.getInstance(), () -> {
                try {
                    SwissSMPler.get(playerId).sendMessage(LaPostePlugin.getPrefix() + " Du hast Post!");
                } catch (NullPointerException e){
                    return;
                }
            }, 300L);
        });
    }


    @EventHandler
    private void onMailboxPlace(BlockPlaceEvent event){
        if(!ItemUtil.getBoolean(event.getItemInHand(), "la_poste_mailbox")) return;
        BlockState blockState = event.getBlockPlaced().getState();
        if(!(blockState instanceof Skull)) return;
        Location location = event.getBlockPlaced().getLocation();
        Player player = event.getPlayer();
        Mailbox.saveMailbox(location, player);


        Skull mailbox = (Skull) blockState;
        mailbox.setOwningPlayer(player);
    }

    @EventHandler
    private void onMailboxRemove(BlockBreakEvent event){
        if(event.getBlock().getType() != Material.PLAYER_HEAD) return;
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Location location = event.getBlock().getLocation();
        if(Mailbox.getMailboxOwner(location) == null) return;

        ArrayList<ItemStack> drops = (ArrayList<ItemStack>) event.getBlock().getDrops();
        if(drops.size() > 1) return;
        ItemStack drop = drops.get(0);

        ItemMeta dropMeta = drop.getItemMeta();
        dropMeta.setDisplayName("LaPoste Briefkasten");
        drop.setItemMeta(dropMeta);
        ItemUtil.setBoolean(drop, "la_poste_mailbox", true);

        location.getWorld().dropItem(location, drop);

        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);

        Mailbox.removeMailbox(location);
    }

    @EventHandler
    private void onReceivedLetterOpening(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack letter = event.getItem();
        if(letter == null || letter.getType() == Material.AIR) return;
        if(!(ItemUtil.getBoolean(letter, "la_poste_letter") && ItemUtil.getBoolean(letter, "received"))) return;
        CustomItems.getCustomItemBuilder("LA_POSTE_LETTER_OPENED").update(letter);
    }

    @EventHandler
    private void onMailboxInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null) return;
        if(!(event.getClickedBlock().getState() instanceof Skull)) return;
        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        UUID ownerId = Mailbox.getMailboxOwner(location);
        if(ownerId == null){
            return;
        }
        Player owner = Bukkit.getPlayer(ownerId);
        if(event.getItem() == null || event.getItem().getType() == Material.AIR){
            if(player == owner){
                LaPoste.receive(player.getUniqueId(), player.getLocation());
                return;
            } else{
                SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Dieser Briefkasten gehört " + ChatColor.AQUA + owner.getName());
                return;
            }
        }
        ItemStack delivery = event.getItem();
        if(ItemUtil.getBoolean(delivery, "received")) return;
        if(delivery.getType() == Material.WRITTEN_BOOK && (ItemUtil.getBoolean(delivery, "la_poste_package") || ItemUtil.getBoolean(delivery, "la_poste_letter"))){
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            OfflinePlayer recipient = Bukkit.getOfflinePlayer(bookMeta.getTitle());
            UUID recipientId = recipient.getUniqueId();

            PlayerInventory playerInventory = player.getInventory();
            EquipmentSlot equipmentSlot = event.getHand();
            if(equipmentSlot == EquipmentSlot.HAND){
                playerInventory.setItemInMainHand(new ItemStack(Material.AIR));
            } else{
                playerInventory.setItemInOffHand(new ItemStack(Material.AIR));
            }
            LaPoste.send(player, recipient, event.getItem(), event.getPlayer().getLocation());
        }
    }

    @EventHandler
    private void onPackageModify(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;
        ItemStack cursor = event.getCursor();
        if(ItemUtil.getBoolean(cursor, "la_poste_package")){
            if((inventory.getItem(event.getSlot()) == null || inventory.getItem(event.getSlot()).getType() == Material.AIR) && event.getClick() == ClickType.RIGHT){
                BookMeta cursorMeta = (BookMeta) cursor.getItemMeta();
                if(ItemUtil.getBoolean(cursor, "received")
                        && !(cursorMeta.getTitle().contains(event.getView().getPlayer().getName()))
                        && !event.getView().getPlayer().hasPermission("laposte.admin")){
                    SwissSMPler.get(event.getView().getPlayer().getUniqueId()).sendActionBar(ChatColor.RED + "Du kannst keine fremden Pakete öffnen");
                    return;
                }
                try {
                    inventory.setItem(event.getSlot(), Package.removeLastItem(cursor));
                } catch (Exception e){
                    SwissSMPler.get(event.getView().getPlayer().getUniqueId()).sendActionBar(ChatColor.YELLOW + e.getMessage());
                }
                event.setCancelled(true);
                return;
            }
            return;
        }
        ItemStack clicked = inventory.getItem(event.getSlot());
        if(clicked == null) return;
        if(cursor == null || cursor.getType() == Material.AIR) {
            return;
        }
        if(clicked.getType() != Material.WRITABLE_BOOK) {
        }
        if(!ItemUtil.getBoolean(clicked, "la_poste_package")) {
            return;
        }
        if(event.getClick() != ClickType.LEFT) return;
        try {
            Package.addItem(clicked, cursor);
            event.setCursor(new ItemStack(Material.AIR));
        } catch (Exception e){
            SwissSMPler.get(event.getView().getPlayer().getUniqueId()).sendActionBar(ChatColor.YELLOW + e.getMessage());
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onBookSign(PlayerEditBookEvent event){
        if(!event.isSigning()) return;
        Player player = event.getPlayer();
        if((!ItemUtil.getBoolean(player.getInventory().getItemInMainHand(), "la_poste_package")
                && !ItemUtil.getBoolean(player.getInventory().getItemInMainHand(), "la_poste_letter"))
                && !ItemUtil.getBoolean(player.getInventory().getItemInOffHand(), "la_poste_package")
                && !ItemUtil.getBoolean(player.getInventory().getItemInOffHand(), "la_poste_letter")){
            Bukkit.getLogger().info("Not package and not letter in mainhand AND not package and not letter in offhand");
            return;
        }
        LaPoste.validateRecipient(event, player, event.getPreviousBookMeta(), event.getNewBookMeta(), player.getLocation(), event.getSlot());
    }

    /*
    Prevents copying a Package by combining the signed variant with a writable book and
    prevents players without permission (addon) from crafting letters/packages/mailboxes
     */
    @EventHandler
    private void onItemCraft(PrepareItemCraftEvent event){
        CraftingInventory inventory = event.getInventory();
        if(inventory.getResult() == null) return;
        ItemStack result = inventory.getResult();
        if(result.equals(CraftingRecipes.paketRezept.getResult())
                || result.equals(CraftingRecipes.briefRezept.getResult())
                || result.equals(CraftingRecipes.whiteBoxRecipe.getResult())
                || result.equals(CraftingRecipes.blueBoxRecipe.getResult())
                || result.equals(CraftingRecipes.greenBoxRecipe.getResult())
                || result.equals(CraftingRecipes.redBoxRecipe.getResult())){
            if(!event.getView().getPlayer().hasPermission("laposte.craft")) event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }
        for(ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (ItemUtil.getBoolean(itemStack, "la_poste_package")) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }
}
