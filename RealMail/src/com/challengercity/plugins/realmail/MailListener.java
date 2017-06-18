package com.challengercity.plugins.realmail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class MailListener implements org.bukkit.event.Listener {
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler(priority = EventPriority.NORMAL)
    public void onUseItemEvent(PlayerInteractEvent e) {
    	String prefix = RealMail.prefix;
    	Server server = RealMail.plugin.getServer();
        FileConfiguration mailboxesConfig = RealMail.mailboxesConfig;
        FileConfiguration config = RealMail.plugin.getConfig();
        if (e.getItem() != null) {
            ItemStack is = e.getItem();
            ItemStack toBeRemoved = is.clone();
            toBeRemoved.setAmount(1);
            /* Exchange Coupon */
            if (is.getType() == Material.PAPER && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore() && is.getItemMeta().getDisplayName().contains("Coupon")) {
                e.getPlayer().getInventory().removeItem(toBeRemoved);
                RealMail.plugin.giveMailbox(e.getPlayer());
                e.getPlayer().sendMessage(prefix+"Du hast deinen Coupon in einen Briefkasten umgewandelt.");
            }
            /* Cycle texture */
            else if (is.getType() == Material.SKULL_ITEM && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && is.getItemMeta().hasLore() && is.getItemMeta().getLore().get(1).contains("Schlage, um Farbe zu ändern")) {
                e.getPlayer().getInventory().removeItem(toBeRemoved);
                if (is.getItemMeta().getLore().get(0).contains("Blue")) {
                    server.dispatchCommand(server.getConsoleSender(), "minecraft:give "+e.getPlayer().getName()+" minecraft:skull 1 3 {display:{Name:\"§rMailbox\",Lore:[\"§r§7White\",\"§r§7Schlage, um Farbe zu ändern\"]},SkullOwner:{Id:\""+RealMail.plugin.mailboxIdWhite+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+RealMail.plugin.mailboxTextureWhite+"\"}]}}}");
                } else if (is.getItemMeta().getLore().get(0).contains("White")) {
                    server.dispatchCommand(server.getConsoleSender(), "minecraft:give "+e.getPlayer().getName()+" minecraft:skull 1 3 {display:{Name:\"§rMailbox\",Lore:[\"§r§7Red\",\"§r§7Schlage, um Farbe zu ändern\"]},SkullOwner:{Id:\""+RealMail.plugin.mailboxIdRed+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+RealMail.plugin.mailboxTextureRed+"\"}]}}}");
                } else if (is.getItemMeta().getLore().get(0).contains("Red")) {
                    server.dispatchCommand(server.getConsoleSender(), "minecraft:give "+e.getPlayer().getName()+" minecraft:skull 1 3 {display:{Name:\"§rMailbox\",Lore:[\"§r§7Green\",\"§r§7Schlage, um Farbe zu ändern\"]},SkullOwner:{Id:\""+RealMail.plugin.mailboxIdGreen+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+RealMail.plugin.mailboxTextureGreen+"\"}]}}}");
                } else if (is.getItemMeta().getLore().get(0).contains("Green")) {
                    server.dispatchCommand(server.getConsoleSender(), "minecraft:give "+e.getPlayer().getName()+" minecraft:skull 1 3 {display:{Name:\"§rMailbox\",Lore:[\"§r§7Blue\",\"§r§7Schlage, um Farbe zu ändern\"]},SkullOwner:{Id:\""+RealMail.plugin.mailboxIdBlue+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+RealMail.plugin.mailboxTextureBlue+"\"}]}}}");
                }
                e.getPlayer().sendMessage(prefix+"Du hast das Aussehen deines Briefkastens geändert.");
            }
            /* Stationery Stuff */
            else if (is.getType() == Material.WRITTEN_BOOK && is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().hasDisplayName() && (is.getItemMeta().getDisplayName().contains("§rLetter") || is.getItemMeta().getDisplayName().contains("§rPackage"))) {
                if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.SKULL)) {

                    List<String> players = (List<String>) mailboxesConfig.getList("players", new LinkedList<String>());
                    
                    playersLoop:
                    for (String p : players) {
                        List<Location> playersMailboxLocations = (List<Location>) mailboxesConfig.getList(p+".mailboxes", new LinkedList<Location>());
                        for (Location loc : playersMailboxLocations) {
                            if (e.getClickedBlock().getLocation().equals(loc)) {
                                org.bukkit.inventory.meta.BookMeta newLetter = (org.bukkit.inventory.meta.BookMeta) is.getItemMeta();
                                OfflinePlayer recipient = Bukkit.getOfflinePlayer(newLetter.getTitle());
                                if (e.getPlayer().hasPermission("realmail.user.sendmail")) {
                                    if (config.getBoolean("universal_mailboxes", false) || (!config.getBoolean("universal_mailboxes", false) && p.equals(e.getPlayer().getUniqueId()+"")) || e.getPlayer().hasPermission("realmail.admin.sendmailAnywhere")) {
                                        ItemStack newLetterItem = new ItemStack(Material.WRITTEN_BOOK);
                                        newLetterItem.setItemMeta(newLetter);
                                        RealMail.plugin.sendMail(newLetterItem, e.getPlayer(), recipient, true);
                                    } else {
                                        e.getPlayer().sendMessage(prefix+"Das ist nicht dein Briefkasten. Verwende '/mail', um herauszufinden, wie du deinen eigenen herstellst.");
                                    }
                                } else {
                                    e.getPlayer().sendMessage(prefix+"Du kannst das Post-System noch nicht verwenden.");
                                }
                                break playersLoop;
                            }
                        }
                    }
                    
                }
            }
        } // End empty hand detection
        
        /* Open Mailbox */
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.SKULL) && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.WRITTEN_BOOK) {
            List<String> players = (List<String>) mailboxesConfig.getList("players", new LinkedList<String>());
            OfflinePlayer mailboxOwner = null;
            for (String p : players) {
                List<Location> locations = (List<Location>) mailboxesConfig.getList(p+".mailboxes", new LinkedList<Location>());
                for (Location loc : locations) {
                    if (e.getClickedBlock().getLocation().equals(loc)) {
                        mailboxOwner = Bukkit.getOfflinePlayer(UUID.fromString(p));
                    }
                }
            }
            if (mailboxOwner != null) {
                if (config.getBoolean("universal_mailboxes", false)) {
                    RealMail.plugin.openMailbox(e.getPlayer(), e.getPlayer());
                } else {
                    if (config.getBoolean("lock_mailboxes", true)) {
                        if (mailboxOwner.getUniqueId().equals(e.getPlayer().getUniqueId()) || e.getPlayer().hasPermission("realmail.admin.openMailboxAnywhere.others")) {
                        	RealMail.plugin.openMailbox(mailboxOwner, e.getPlayer());
                        } else {
                            e.getPlayer().sendMessage(prefix+"Das ist nicht dein Briefkasten. Verwende '/mail', um herauszufinden, wie du deinen eigenen herstellst.");
                        }
                    } else {
                    	RealMail.plugin.openMailbox(mailboxOwner, e.getPlayer());
                    }
                }
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Signing Letters">
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onEditBook(PlayerEditBookEvent e) {
        if (e.getPreviousBookMeta() != null && e.getPreviousBookMeta().hasDisplayName() && (e.getPreviousBookMeta().getDisplayName().contains("Stationary") || e.getPreviousBookMeta().getDisplayName().contains("Stationery") || e.getPreviousBookMeta().getDisplayName().contains("Package"))) {
            
            BookMeta newBM = e.getNewBookMeta();
            if (e.getPreviousBookMeta().getDisplayName().contains("Package")) {
                newBM.setDisplayName("§rPackage");
                newBM.setLore(e.getPreviousBookMeta().getLore());
            } else {
                newBM.setDisplayName("§rStationery");
                newBM.setLore(Arrays.asList("Empfänger: Name als Titel","Versenden: Rechtsklick auf einen Briefkasten"));
            }
            e.setNewBookMeta(newBM);
            
            
            if (e.isSigning()) {
                if (newBM.getDisplayName().contains("Stationary") || newBM.getDisplayName().contains("Stationery")) {
                    newBM.setDisplayName("§rLetter");
                }

                List<String> bookLore = newBM.getLore();
                bookLore.add("§r§7To: "+newBM.getTitle());
                newBM.setLore(bookLore);

                if (newBM.getPageCount() >= 1) {
                    String firstPage = newBM.getPages().get(0); // [subject|subj|s:Test Subject;moon|moonrune|rune;burn|burnonread|selfdestruct|destruct]
                    firstPage = firstPage.split("\n")[0];
                    if (firstPage.matches("^(.*)\\[Subject:(.*)\\](.*)$")) { // [Subject:Test Subject]

                        firstPage = firstPage.replaceFirst("^(.*)\\[Subject:", "");
                        firstPage = firstPage.replaceFirst("\\](.*)", "");
                        newBM.setDisplayName(newBM.getDisplayName()+" - "+firstPage);
                    }
                }
                
                // Check if the recipient exists before signing
                if (RealMail.mailboxesConfig.getList("players", new LinkedList<String>()).contains(Bukkit.getOfflinePlayer(newBM.getTitle()).getUniqueId()+"")) {
                    e.setNewBookMeta(newBM);
                } else { // TODO Make sure to still save the book like when you hit "Done" even when the signing fails
                    e.getPlayer().sendMessage(RealMail.prefix+("Konnte den Brief nicht signieren. {0} ist nicht auf diesem Server.").replaceAll("\\{0}", newBM.getTitle()));
                    e.setSigning(false);
                }
            }
        }
    }
    //</editor-fold>
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
    	String prefix = RealMail.prefix;
        FileConfiguration config = RealMail.plugin.getConfig();
        //<editor-fold defaultstate="collapsed" desc="Only letters and attachments in mailboxes and no villagers">
        if (e.getInventory().getName().contains("Mailbox")) {
            ItemStack cursor = e.getCursor();
            ItemStack current = e.getCurrentItem();
            
            boolean allowCursor = false;
            boolean allowCurrent = false;
            
            if (cursor == null || cursor.getType() == Material.AIR) {
                allowCursor = true;
            } else {
                if (cursor.hasItemMeta()) {
                    if (cursor.getItemMeta().hasDisplayName()) {
                        if (cursor.getItemMeta().getDisplayName().contains("Stationary") || cursor.getItemMeta().getDisplayName().contains("Stationery") || cursor.getItemMeta().getDisplayName().contains("Letter") || cursor.getItemMeta().getDisplayName().contains("Package")) {
                            allowCursor = true;
                        }
                    }
                }
            }
            if (current == null || current.getType() == Material.AIR) {
                allowCurrent = true;
            } else {
                if (current.hasItemMeta()) {
                    if (current.getItemMeta().hasDisplayName()) {
                        if (current.getItemMeta().getDisplayName().contains("Stationary") || current.getItemMeta().getDisplayName().contains("Stationery") || current.getItemMeta().getDisplayName().contains("Letter") || current.getItemMeta().getDisplayName().contains("Package")) {
                            allowCurrent = true;
                        }
                    }
                }
            }
            if ((!allowCursor) || (!allowCurrent)) {
                e.setCancelled(true);
            }
        } else if (e.getInventory().getType() == InventoryType.MERCHANT) {
            ItemStack cursor = e.getCursor();
            ItemStack current = e.getCurrentItem();
            
            boolean disallowCursor = false;
            boolean disallowCurrent = false;
            
            if (cursor != null && cursor.hasItemMeta()) {
                if (cursor.getItemMeta().hasDisplayName()) {
                    if (cursor.getItemMeta().getDisplayName().contains("Stationary") || cursor.getItemMeta().getDisplayName().contains("Stationery") || cursor.getItemMeta().getDisplayName().contains("Letter") || cursor.getItemMeta().getDisplayName().contains("Package")) {
                        disallowCursor = true;
                    }
                }
            }
            if (current != null && current.hasItemMeta()) {
                if (current.getItemMeta().hasDisplayName()) {
                    if (current.getItemMeta().getDisplayName().contains("Stationary") || current.getItemMeta().getDisplayName().contains("Stationery") || current.getItemMeta().getDisplayName().contains("Letter") || current.getItemMeta().getDisplayName().contains("Package")) {
                        disallowCurrent = true;
                    }
                }
            }
            if (disallowCursor || disallowCurrent) {
                e.setCancelled(true);
            }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Attach items">
        if (e.isLeftClick()) { // TODO Fix all these creative bugs
            ItemStack current = e.getCurrentItem();
            ItemStack cursor = e.getCursor();
            
            if ((current == null || current.getType() == Material.BOOK_AND_QUILL) && cursor != null && cursor.getType() != Material.AIR) {
                if (current != null && current.hasItemMeta()) {
                    if (current.getItemMeta().hasDisplayName()) {
                        if (current.getItemMeta().getDisplayName().contains("Stationary") || current.getItemMeta().getDisplayName().contains("Stationery") || current.getItemMeta().getDisplayName().contains("Package")) {
                            if (cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().getDisplayName().contains("Package")) {
                                e.getWhoClicked().sendMessage(prefix+"Du kannst kein Paket in ein Paket legen. Das gäbe einen Fehler in der Matrix!");
                                e.setResult(Event.Result.DENY);
                            } else {
                                if (e.getWhoClicked().hasPermission("realmail.user.attach")) {
                                    if (e.getClick() != ClickType.CREATIVE) {
                                        List<ItemStack> attachments = new LinkedList<ItemStack>();
                                        String code = "";
                                        ItemMeta im = current.getItemMeta();
                                        if (im.hasLore()) {
                                            for (String loreLine : im.getLore()) {
                                                if (loreLine.contains("ID")) {
                                                    code = loreLine.replace("§r§7ID: ", "");
                                                    attachments = (List<ItemStack>) RealMail.packagesConfig.getList(code, new LinkedList<ItemStack>());
                                                    break;
                                                }
                                            }
                                        }
                                        if (attachments.size() < config.getInt("max_attachments", 4) || e.getWhoClicked().hasPermission("realmail.admin.bypassAttachmentLimits")) {
                                            if (code.equals("")) {
                                                code = UUID.randomUUID().toString();
                                                List<String> lore = im.getLore();
                                                
                                                boolean hasDetachInstr = false;
                                                for (String detachLoreLine : lore) {
                                                    if (detachLoreLine.contains("auszupacken")) {
                                                        hasDetachInstr = true;
                                                    }
                                                }
                                                
                                                if (!hasDetachInstr) {
                                                    lore.add("§r§7Rechtsklicke auf leeren Slot, um Items auszupacken.");
                                                }
                                                
                                                lore.add("§r§7ID: "+code);
                                                im.setLore(lore);
                                            }
                                            attachments.add(cursor.clone());
                                            RealMail.packagesConfig.set(code, attachments);
                                            try {
                                            	RealMail.packagesConfig.save(RealMail.packagesFile);
                                                e.getWhoClicked().sendMessage(prefix+ChatColor.WHITE+cursor.getType().name()+" x"+cursor.getAmount()+" angehängt.");
                                                im.setDisplayName("§rPackage");
                                                current.setItemMeta(im);
                                                e.setCursor(new ItemStack(Material.AIR));
                                                //cursor.setType(Material.AIR);
                                                //cursor.setAmount(0);
                                                e.setResult(Event.Result.DENY);
                                            } catch (Exception ex) {
                                                e.getWhoClicked().sendMessage(prefix+"Das Item konnte nicht angehängt werden.");
                                            }
                                        } else {
                                            e.getWhoClicked().sendMessage(prefix+("Maximale Anzahl Items im Anhang erreicht. ({0})").replaceAll("\\{0}", config.getInt("max_attachments", 4)+""));
                                            e.setResult(Event.Result.DENY);
                                        }
                                    } else {
                                        e.getWhoClicked().sendMessage(prefix+("Anhänge können nicht im Creative-Modus gemacht werden."));
                                    }
                                } else {
                                    e.getWhoClicked().sendMessage(prefix+"Du kannst keine Items anhängen.");
                                    e.setResult(Event.Result.DENY);
                                }
                            }
                        }
                    }
                }
            }
        }
        //</editor-fold>
        FileConfiguration packagesConfig = RealMail.packagesConfig;
        //<editor-fold defaultstate="collapsed" desc="Detach items">
        if ((e.isRightClick() || e.getClick() == ClickType.CREATIVE) && e.getCursor() != null && e.getCursor().getType() != Material.AIR && (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)) {
            if (e.getCursor().getType() == Material.WRITTEN_BOOK || e.getCursor().getType() == Material.BOOK_AND_QUILL) {
                if (e.getCursor().hasItemMeta() && e.getCursor().getItemMeta().hasDisplayName() && e.getCursor().getItemMeta().getDisplayName().contains("Package") && e.getCursor().getItemMeta().hasLore()) {
                    for (String loreLine : e.getCursor().getItemMeta().getLore()) {
                        if (loreLine.contains("ID")) {
                            String code = loreLine.replace("§r§7ID: ", "");
                            if (packagesConfig.contains(code)) {
                                
                                List<ItemStack> attachments = (List<ItemStack>) packagesConfig.getList(code, new LinkedList<ItemStack>());
                                
                                e.setCurrentItem(attachments.get(0));
                                attachments.remove(0);
                                
                                if (attachments.size() <= 0) {
                                    ItemMeta im = e.getCursor().getItemMeta();
                                    ArrayList<String> lore2 = (ArrayList<String>) im.getLore();
                                    for (String loreLine2 : (ArrayList<String>) lore2.clone()) {
                                        if (loreLine2.contains("ID")) {
                                            lore2.remove(loreLine2);
                                            break;
                                        }
                                    }
                                    im.setLore(lore2);
                                    e.getCursor().setItemMeta(im);
                                    attachments = null;
                                }
                                
                                packagesConfig.set(code, attachments);
                                
                                try {
                                    packagesConfig.save(RealMail.packagesFile);
                                    e.setResult(Event.Result.DENY);
                                } catch (Exception ex) {
                                    e.getWhoClicked().sendMessage(prefix+"Das Item konnte nicht ausgepackt werden.");
                                    if (config.getBoolean("verbose_errors", false)) {
                                        ex.printStackTrace();
                                    }
                                }
                                
                            } else {
                                e.getWhoClicked().sendMessage(prefix+"Das Item konnte nicht ausgepackt werden, unbekannter Fehler.");
                            }
                            break;
                        }
                    }
                }
            }
        }
        //</editor-fold>
    }
    
    //<editor-fold defaultstate="collapsed" desc="Save mailbox on close">
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent e) {
        FileConfiguration mailboxesConfig = RealMail.mailboxesConfig;
        FileConfiguration config = RealMail.plugin.getConfig();
        if (e.getInventory().getName().contains("Mailbox")) {
            List<BookMeta> letters = new LinkedList<BookMeta>();
            for (ItemStack is : e.getInventory().getContents()) { // TODO Should do anything about extra letters?
                if (is != null && is.hasItemMeta()) {
                    letters.add((org.bukkit.inventory.meta.BookMeta) is.getItemMeta());
                }
            }
            String ownerName = e.getInventory().getName();
            ownerName = ownerName.replace("'s Mailbox", "");
            mailboxesConfig.set(Bukkit.getOfflinePlayer(ownerName).getUniqueId()+".letters", letters);
            mailboxesConfig.set(Bukkit.getOfflinePlayer(ownerName).getUniqueId()+".unread", false);
            RealMail.plugin.udpateMailboxFlags(Bukkit.getOfflinePlayer(ownerName));
            try {
                mailboxesConfig.save(RealMail.mailboxesFile);
            } catch (Exception ex) {
                RealMail.plugin.getLogger().log(Level.INFO, "Failed to save {0}''s mailbox.", e.getPlayer().getName());
                if (config.getBoolean("verbose_errors", false)) {
                    ex.printStackTrace();
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Detect mailbox placing">
    @SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
    	String prefix = RealMail.prefix;
        FileConfiguration mailboxesConfig = RealMail.mailboxesConfig;
        FileConfiguration config = RealMail.plugin.getConfig();
        if (e.getItemInHand() != null) {
            ItemStack is = e.getItemInHand();
            
            if (is.getType() == Material.SKULL_ITEM && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() >= 2 && is.getItemMeta().getLore().get(1).contains("Schlage, um Farbe zu ändern")) {
                
                List<Location> locations = (List<Location>) mailboxesConfig.getList(e.getPlayer().getUniqueId()+".mailboxes", new LinkedList<Location>());
                locations.add(e.getBlock().getLocation());
                mailboxesConfig.set(e.getPlayer().getUniqueId()+".mailboxes", locations);
                
                List<String> players = (List<String>) mailboxesConfig.getList("players", new LinkedList<String>());
                if (!players.contains(e.getPlayer().getUniqueId().toString())) {
                    players.add(e.getPlayer().getUniqueId().toString());
                }
                mailboxesConfig.set("players", players);
                
                try {
                    mailboxesConfig.save(RealMail.mailboxesFile);
                    e.getPlayer().sendMessage(prefix+"Briefkasten platziert.");
                } catch (Exception ex) {
                    e.getPlayer().sendMessage(prefix+"Der Briefkasten konnte nicht platziert werden.");
                    if (config.getBoolean("verbose_errors", false)) {
                        ex.printStackTrace();
                    }
                }
            }
            
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Detect mailbox breaking">
    @SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
    	String prefix = RealMail.prefix;
    	Server server = RealMail.plugin.getServer();
        FileConfiguration mailboxesConfig = RealMail.mailboxesConfig;
        FileConfiguration config = RealMail.plugin.getConfig();
        List<String> players = (List<String>) mailboxesConfig.getList("players", new LinkedList<String>());
        for (@SuppressWarnings("unused") String p : players) {
            List<Location> locations = (List<Location>) mailboxesConfig.getList(e.getPlayer().getUniqueId()+".mailboxes", new LinkedList<Location>());
            for (Location loc : locations) {
                if (e.getBlock().getLocation().equals(loc)) {
                    
                    locations.remove(e.getBlock().getLocation());
                    mailboxesConfig.set(e.getPlayer().getUniqueId()+".mailboxes", locations);
                    
                    try {
                        mailboxesConfig.save(RealMail.mailboxesFile);
                        e.setCancelled(true);
                        e.getBlock().setType(Material.AIR);
                        server.dispatchCommand(server.getConsoleSender(), "minecraft:give "+e.getPlayer().getName()+" minecraft:skull 1 3 {display:{Name:\"§rMailbox\",Lore:[\"§r§7Blue\",\"§r§7Schlage, um Farbe zu ändern\"]},SkullOwner:{Id:\""+RealMail.plugin.mailboxIdBlue+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+RealMail.plugin.mailboxTextureBlue+"\"}]}}}");
                    } catch (Exception ex) {
                        e.getPlayer().sendMessage(prefix+"Der Briefkasten konnte nicht entfernt werden.");
                        if (config.getBoolean("verbose_errors", false)) {
                            ex.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Crafting">
    @EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
    public void onCraft(CraftItemEvent e) {
    	String prefix = RealMail.prefix;
    	if(e.getRecipe()==null) return;
        if (e.getRecipe().getResult().hasItemMeta() && e.getRecipe().getResult().getItemMeta().hasLore() && e.getRecipe().getResult().getItemMeta().getDisplayName().contains(RealMail.stationeryMeta.getDisplayName())) { // Stationery
            if (!e.getWhoClicked().hasPermission("realmail.user.craft.stationary")) {
                e.getWhoClicked().sendMessage(prefix+"Du kannst noch kein Briefpapier herstellen.");
                e.setResult(Event.Result.DENY);
            }
        } else if (e.getRecipe().getResult().hasItemMeta() && e.getRecipe().getResult().getItemMeta().hasLore() && e.getRecipe().getResult().getItemMeta().getDisplayName().contains(RealMail.mailboxRecipeMeta.getDisplayName())) { // Mailbox
            if (!e.getWhoClicked().hasPermission("realmail.user.craft.mailbox")) {
                e.getWhoClicked().sendMessage(prefix+"Du kannst noch keinen Briefkasten herstellen.");
                e.setResult(Event.Result.DENY);
            }
        }
    }
    //</editor-fold>
}
