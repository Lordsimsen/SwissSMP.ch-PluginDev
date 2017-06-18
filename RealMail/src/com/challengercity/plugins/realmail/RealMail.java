/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.challengercity.plugins.realmail;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
/**
 * 
 *
 * @author Ben Sergent V
 */
public class RealMail extends JavaPlugin {

    // TODO Add letter delivery queue to deliver at a specific time option
    protected static RealMail plugin;
	protected static final String version = "0.3.3";
    protected static FileConfiguration mailboxesConfig = null;
    protected static File mailboxesFile = null;
    protected static FileConfiguration packagesConfig = null;
    protected static java.io.File packagesFile = null;
    protected static ItemMeta mailboxRecipeMeta = null;
    protected static BookMeta stationeryMeta = null;
    protected static String prefix = ChatColor.WHITE+"["+ChatColor.GOLD+"Post"+ChatColor.WHITE+"] ";
    
    /* Mailbox Textures */
    final String mailboxTextureBlue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZhNDllZmFhYWI1MzI1NTlmZmY5YWY3NWRhNmFjNGRkNzlkMTk5ZGNmMmZkNDk3Yzg1NDM4MDM4NTY0In19fQ==";
    final String mailboxIdBlue = "48614330-6c44-47be-85ec-33ed037cf48c";
    final String mailboxTextureWhite = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5ZTE5NzFjYmMzYzZmZWFhYjlkMWY4NWZjOWQ5YmYwODY3NjgzZjQxMjk1NWI5NjExMTdmZTY2ZTIifX19";
    final String mailboxIdWhite = "480bff09-ed89-4214-a2bd-dab19fa5177d";
    final String mailboxTextureRed = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZhODljZTg1OTMyYmVjMWExYzNmMzFjYjdjMDg1YTViZmIyYWM3ZTQwNDA5NDIwOGMzYWQxMjM4NzlkYTZkYSJ9fX0=";
    final String mailboxIdRed = "6a71ad04-2422-41f3-a501-6ea5707aaef3";
    final String mailboxTextureGreen = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiY2NiNTI0MDg4NWNhNjRlNDI0YTBjMTY4YTc4YzY3NmI4Yzg0N2QxODdmNmZiZjYwMjdhMWZlODZlZSJ9fX0=";
    final String mailboxIdGreen = "60621c0e-cb3e-471b-a237-4dec155f4889";
    
	@SuppressWarnings("deprecation")
	@Override
    public void onEnable() {
		plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        ItemStack blueMailboxCoupon = new ItemStack(Material.PAPER, 1);
        mailboxRecipeMeta = blueMailboxCoupon.getItemMeta();
        mailboxRecipeMeta.setDisplayName("Briefkasten-Coupon");
        mailboxRecipeMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Rechtsklick tauscht Coupon",ChatColor.DARK_PURPLE+"gegen einen Briefkasten"));
        blueMailboxCoupon.setItemMeta(mailboxRecipeMeta);
        ShapedRecipe blueMailboxRecipe = new ShapedRecipe(new NamespacedKey(this,"Coupon"), blueMailboxCoupon);
        blueMailboxRecipe.shape("  w", "iii", "ici");
        blueMailboxRecipe.setIngredient('w', org.bukkit.Material.WOOL, -1);
        blueMailboxRecipe.setIngredient('i', org.bukkit.Material.IRON_INGOT);
        blueMailboxRecipe.setIngredient('c', org.bukkit.Material.CHEST);
        this.getServer().addRecipe(blueMailboxRecipe);
        
        ItemStack stationery = new ItemStack(Material.BOOK_AND_QUILL, 1);
        stationeryMeta = (org.bukkit.inventory.meta.BookMeta) stationery.getItemMeta();
        stationeryMeta.setDisplayName("§rStationery");
        stationeryMeta.setLore(Arrays.asList("Empfänger: Name als Titel","Versenden: Rechtsklick auf einen Briefkasten"));
        stationeryMeta.addPage("");
        stationery.setItemMeta(stationeryMeta);
        ShapelessRecipe stationeryRecipe = new ShapelessRecipe(new NamespacedKey(this,"Briefpapier"), stationery);
        stationeryRecipe.addIngredient(Material.PAPER);
        stationeryRecipe.addIngredient(Material.FEATHER);
        this.getServer().addRecipe(stationeryRecipe);
        
        if (mailboxesFile == null) {
            mailboxesFile = new java.io.File(getDataFolder(), "mailboxes.yml");
        }
        mailboxesConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(mailboxesFile);
        
        if (packagesFile == null) {
            packagesFile = new java.io.File(getDataFolder(), "packages.yml");
        }
        packagesConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(packagesFile);
        
        getServer().getPluginManager().registerEvents(new MailListener(), this);
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        
        getLogger().log(Level.INFO, "RealMail v{0} enabled.", version);
    }
    
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "RealMail v{0} disabled.", version);
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (cmd.getName().equalsIgnoreCase("realmail")) { // TODO Make the commands more organized when dealing w/ console vs player
            //<editor-fold defaultstate="collapsed" desc="Intruction Commands">
            if (args.length == 0 || (args.length < 2 && args[0].equals("1"))) { // Show crafting
                sender.sendMessage(prefix+String.join("\n",new String[] {
                    ChatColor.GOLD+""+ChatColor.BOLD+" Crafting-Rezepte",
                    ChatColor.GOLD+"Briefkasten:",
                    ChatColor.DARK_GRAY+"  --"+ChatColor.WHITE+"w   w"+ChatColor.WHITE+" = Wolle (1x)",
                    ChatColor.GRAY+"  i i i   i"+ChatColor.WHITE+" = Eisenbarren (5x)",
                    ChatColor.GRAY+"  i "+ChatColor.DARK_RED+"c"+ChatColor.GRAY+"i   "+ChatColor.DARK_RED+"c"+ChatColor.WHITE+" = Kiste (1x)",
                    ChatColor.GOLD+"Briefpapier:",
                    ChatColor.WHITE+"  1x Papier und 1x Feder",
                    ChatColor.WHITE+"Verwende '/mail 2' für Verwendung"
                }));
            } else if (args.length < 2) {
                if (args[0].equals("2")) { // Show usage
                    sender.sendMessage(prefix+String.join("\n",new String[] {
                        ChatColor.GOLD+""+ChatColor.BOLD+" Verwendung",
                        ChatColor.GOLD+"Einen Brief verwenden:",
                        ChatColor.WHITE+"  1. Crafte etwas Briefpapier"+(getConfig().getBoolean("let_players_spawn_stationary", false)?" oder verwende /mail new":""),
                        ChatColor.WHITE+"  2. Schreibe deinen Brief",
                        ChatColor.WHITE+"    - Gib [Subject:mySubject] auf der ersten Zeile ein, um einen Betreff zu setzen",
                        ChatColor.WHITE+"  3. Hänge gegebenenfalls Items an (siehe '/mail 3')",
                        ChatColor.WHITE+"  4. Signiere das Buch/Paket mit dem Namen des Empfängers im Titel",
                        ChatColor.WHITE+"  5. Rechtsklicke auf einen Briefkasten mit dem Brief/Paket in der Hand",
                        ChatColor.WHITE+"Verwende '/mail 3' für Anhänge"
                    }));
                } else if (args[0].equals("3")) { // Show attachments
                    sender.sendMessage(prefix+String.join("\n",new String[] {
                        ChatColor.GOLD+""+ChatColor.BOLD+" Anhänge",
                        ChatColor.GOLD+"Einpacken:",
                        ChatColor.WHITE+"  1. Nimm das Item aus dem Inventar",
                        ChatColor.WHITE+"  2. Klicke auf den Brief (max. "+getConfig().getInt("max_attachments", 4)+" Stacks)",
                        ChatColor.GOLD+"Auspacken:",
                        ChatColor.WHITE+"  1. Nimm das Paket aus dem Inventar",
                        ChatColor.WHITE+"  2. Rechtsklicke auf leere Slots im Inventar",
                        ChatColor.WHITE+"    Beispiel: http://bit.ly/1Cijgbl",
                        sender.hasPermission("realmail.admin.seeAdminHelp")?ChatColor.WHITE+"Verwende '/mail 4' für Administration":ChatColor.WHITE+"Verwende '/mail 1' für Crafting"
                    }));
                } else if (args[0].equals("4")) { // Show adminministration
                    if (sender.hasPermission("realmail.admin.seeAdminHelp")) {
                        sender.sendMessage(prefix+String.join("\n",new String[] {
                            ChatColor.GOLD+""+ChatColor.BOLD+" Administration",
                            ChatColor.GOLD+"/mail send "+ChatColor.WHITE+" Sende den Brief in deiner Hand an den Empfänger",
                            ChatColor.GOLD+"/mail bulksend "+ChatColor.WHITE+" Sende den Brief in deiner Hand an alle Spieler mit einem Briefkasten",
                            ChatColor.GOLD+"/mail spawn <mailbox|stationery> "+ChatColor.WHITE+" Erschaffe einen Briefkasten oder Briefpapier",
                            ChatColor.GOLD+"/mail open [player] "+ChatColor.WHITE+" Öffne einen Briefkasten",
                            ChatColor.WHITE+"Verwende '/mail 1' für Crafting"
                        }));
                    } else {
                        sender.sendMessage(prefix+"Du hast keine Berechtigung zur Anzeige der Adminbefehle.");
                    }
                }
            } //</editor-fold>
            
            if (args.length >= 1 && args[0].equals("version")) {
                sender.sendMessage(prefix+new String[] {ChatColor.GOLD+"RealMail v"+version, "Kontaktiere detig_iii für Updates"});
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix+prefix+ChatColor.WHITE+"This command can only be run by a player.");
                } else {
                    //<editor-fold defaultstate="collapsed" desc="NonConsole Commands">
                    Player player = (Player) sender;
                    if (args.length >= 1) {
                        if (args[0].equals("send")) {
                            if (player.hasPermission("realmail.admin.sendmailAnywhere")) {
                                ItemStack itemHand = player.getInventory().getItemInMainHand();
                                if (itemHand.getType() == Material.WRITTEN_BOOK && itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName() && (itemHand.getItemMeta().getDisplayName().contains("Letter") || itemHand.getItemMeta().getDisplayName().contains("Package"))) {
                                    BookMeta bookMeta = (BookMeta) itemHand.getItemMeta();
									OfflinePlayer recipient = Bukkit.getOfflinePlayer(bookMeta.getTitle());
                                    sendMail(itemHand, player, recipient, true);
                                } else {
                                    sender.sendMessage(prefix+"Du kannst nur Briefe oder Pakete versenden.");
                                }
                            } else {
                                player.sendMessage(prefix+"Du hast keine Berechtigung für die Post.");
                            }
                        } else if (args[0].equals("bulksend")) {
                            if (player.hasPermission("realmail.admin.bulkmail")) {
                                ItemStack itemHand = player.getInventory().getItemInMainHand();
                                if (itemHand.getType() == Material.WRITTEN_BOOK && itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName() && (itemHand.getItemMeta().getDisplayName().contains("Letter") || itemHand.getItemMeta().getDisplayName().contains("Package"))) {
                                    List<String> players = (List<String>) mailboxesConfig.getList("players", new LinkedList<String>());
                                    for (String p : players) {
                                        sendMail(itemHand, player, UUID.fromString(p), false);
                                    }
                                    sender.sendMessage(prefix+"Brief an alle Spieler auf dem Server versendet.");
                                } else {
                                    sender.sendMessage(prefix+"Du kannst nur Briefe und Pakete versenden.");
                                }
                            } else {
                                player.sendMessage(prefix+"Du kannst keine Massenbriefe versenden.");
                            }
                        } else if (args[0].equals("spawn")) {
                            if (args.length != 2) {
                                sender.sendMessage(prefix+"Command syntax: /realmail spawn <mailbox|stationery>");
                            } else {
                                if (args[1].equals("mailbox")) {
                                    if (player.hasPermission("realmail.admin.spawn.mailbox")) {
                                        giveMailbox(player);
                                    } else {
                                        player.sendMessage(prefix+"Du hast dafür keine Rechte.");
                                    }
                                } else if (args[1].equals("stationary") || args[1].equals("stationery")) {
                                    if (player.hasPermission("realmail.admin.spawn.stationary")) {
                                        giveStationery(player);
                                    } else {
                                        player.sendMessage(prefix+"Du hast dafür keine Rechte.");
                                    }
                                } else {
                                     sender.sendMessage(prefix+"Command syntax: /realmail spawn <mailbox|stationery>");
                                }
                            }
                        } else if (args[0].equals("open")) {
                            if (args.length >= 2) {
                                if (player.hasPermission("realmail.admin.openMailboxAnywhere.others")) {
                                    if (Bukkit.getOfflinePlayer(args[1]) != null) {
                                        openMailbox(Bukkit.getOfflinePlayer(args[1]), player);
                                    } else {
                                        player.sendMessage(prefix+("{0} does not have a mailbox.").replaceAll("\\{0}", args[1]));
                                    }
                                } else {
                                    player.sendMessage(prefix+"Du kannst keine fremden Briefkästen öffnen.");
                                }
                            } else {
                                if (player.hasPermission("realmail.admin.openMailboxAnywhere")) {
                                    openMailbox(player, player);
                                } else {
                                    player.sendMessage(prefix+"Du kannst keine fremden Briefkästen einsehen.");
                                }
                            }
                        } else if (args[0].equals("new")) {
                            if (player.hasPermission("realmail.admin.spawn.stationary") || getConfig().getBoolean("let_players_spawn_stationary", false)) {
                                 giveStationery(player);
                            } else {
                                player.sendMessage(prefix+"Du hast dafür keine Rechte."); // TODO Start replaces with language compatible here
                            }
                        }
                    }
                //</editor-fold>
                }
            }
            
        }
        
        return true;
    }
    
    public void giveMailbox(Player ply) {
        getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:give "+ply.getName()+" minecraft:skull 1 3 {display:{Name:\"Mailbox\",Lore:[\"§7Blue\",\"§7Punch to change texture\"]},SkullOwner:{Id:\""+mailboxIdBlue+"\",Name:\"ha1fBit\",Properties:{textures:[{Value:\""+mailboxTextureBlue+"\"}]}}}");
    }
    
    public void giveStationery(Player ply) {
        ItemStack stationery = new ItemStack(Material.BOOK_AND_QUILL, 1);
        stationery.setItemMeta(stationeryMeta);
        ply.getInventory().addItem(stationery);
    }
    
    @SuppressWarnings("unchecked")
	public boolean openMailbox(OfflinePlayer owner, Player viewer) {
        String ownerName = owner.getName();
        if (ownerName == null) {
            ownerName = "Someone";
        }
        String title = ownerName+"'s Mailbox";
        if (title.length() > 32) {
            title = title.replace("'s Mailbox", "");
        }
        if (title.length() > 32) {
            title = "Mailbox";
            viewer.sendMessage(prefix+("{0}'s Briefkasten geöffnet.").replaceAll("\\{0}", ownerName));
        }
        Inventory mailInv = Bukkit.createInventory(viewer, getConfig().getInt("mailbox_rows", 4) * 9, title);
        List<org.bukkit.inventory.meta.BookMeta> letters = (List<org.bukkit.inventory.meta.BookMeta>) mailboxesConfig.getList(owner.getUniqueId()+".letters", new LinkedList<org.bukkit.inventory.meta.BookMeta>());
        for (org.bukkit.inventory.meta.BookMeta letterMeta : letters) {
            ItemStack newBook;
            if (letterMeta.getDisplayName().contains("Stationary") || letterMeta.getDisplayName().contains("Stationery")) {
                newBook = new ItemStack(Material.BOOK_AND_QUILL, 1);
            } else {
                newBook = new ItemStack(Material.WRITTEN_BOOK, 1);
            }
            newBook.setItemMeta(letterMeta);
            Map<Integer,ItemStack> leftover = mailInv.addItem(newBook);
            if (!leftover.isEmpty()) {
                viewer.sendMessage(prefix+"Es konnten nicht alle Briefe dargestellt werden. Bitte mach zuerst Platz.");
                break;
            }
        }
        viewer.openInventory(mailInv);
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean sendMail(ItemStack mailItem, Player fromPlayer, OfflinePlayer toPlayer, boolean sendMessages) {
        if (mailItem.getType() != Material.WRITTEN_BOOK) {
            return false;
        }
        BookMeta mailMeta = (BookMeta) mailItem.getItemMeta();
        if (mailboxesConfig.getList(toPlayer.getUniqueId()+".letters", new LinkedList<ItemStack>()).size() < (getConfig().getInt("mailbox_rows", 4)*9)) {
            java.util.Date dateRaw = java.util.Calendar.getInstance().getTime();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat();
            format.applyPattern(getConfig().getString("dateformat"));
            String dateString = format.format(dateRaw);

            List<String> oldLore = (List<String>) mailMeta.getLore();
            List<String> lore = (List<String>) new LinkedList(Arrays.asList("§r§7Date: "+dateString));
            for (String oldLoreLine : oldLore) {
                if (oldLoreLine.contains("ID")) {
                    lore.add(oldLoreLine);
                } else if (oldLoreLine.contains("To")) {
                    lore.add(oldLoreLine);
                }
            }
            mailMeta.setLore(lore);

            List<org.bukkit.inventory.meta.BookMeta> letters = (List<org.bukkit.inventory.meta.BookMeta>) mailboxesConfig.getList(toPlayer.getUniqueId()+".letters", new LinkedList<org.bukkit.inventory.meta.BookMeta>());
            letters.add(mailMeta);
            mailboxesConfig.set(toPlayer.getUniqueId()+".letters", letters);
            mailboxesConfig.set(toPlayer.getUniqueId()+".unread", true);
            try {
                mailboxesConfig.save(mailboxesFile);
                fromPlayer.getInventory().remove(mailItem);
                if (sendMessages) {
                    fromPlayer.sendMessage(prefix+("Brief versendet an {0}.").replaceAll("\\{0}", toPlayer.getName()));
                }
                udpateMailboxFlags(toPlayer.getPlayer());
                if (toPlayer.getPlayer() != null) {
                    toPlayer.getPlayer().sendMessage(prefix+"Du hast einen Brief! Prüfe deinen Briefkasten. Verwende '/mail', falls du nicht weisst wie das geht.");
                }
            } catch (Exception ex) {
                fromPlayer.sendMessage(prefix+"Der Brief konnte nicht versendet werden.");
                if (getConfig().getBoolean("verbose_errors", false)) {
                    ex.printStackTrace();
                }
            }
        } else {
            if (sendMessages) {
                fromPlayer.sendMessage(prefix+"Der Briefkasten des Empfängers ist voll. Bitte versuche es später nochmals.");
            }
            if (toPlayer.getPlayer() != null) {
                toPlayer.getPlayer().sendMessage(prefix+("{0} hat versucht, dir einen Brief zu senden, aber dein Briefkasten ist voll. Du solltest ihn bei Gelegenheit etwas leeren.").replaceAll("\\{0}", fromPlayer.getName()));
            }
        }
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean sendMail(ItemStack mailItem, Player fromPlayer, UUID toUUID, boolean sendMessages) {
        if (mailItem.getType() != Material.WRITTEN_BOOK) {
            return false;
        }
        BookMeta mailMeta = (BookMeta) mailItem.getItemMeta();
        if (mailboxesConfig.getList(toUUID+".letters", new LinkedList<ItemStack>()).size() < (getConfig().getInt("mailbox_rows", 4)*9)) {
            java.util.Date dateRaw = java.util.Calendar.getInstance().getTime();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat();
            format.applyPattern(getConfig().getString("dateformat"));
            String dateString = format.format(dateRaw);

            List<String> oldLore = (List<String>) mailMeta.getLore();
            List<String> lore = (List<String>) new LinkedList(Arrays.asList("§r§7To: "+mailMeta.getTitle(), "§r§7Date: "+dateString));
            for (String oldLoreLine : oldLore) {
                if (oldLoreLine.contains("ID")) {
                    lore.add(oldLoreLine);
                    break;
                }
            }
            mailMeta.setLore(lore);

            List<org.bukkit.inventory.meta.BookMeta> letters = (List<org.bukkit.inventory.meta.BookMeta>) mailboxesConfig.getList(toUUID+".letters", new LinkedList<org.bukkit.inventory.meta.BookMeta>());
            letters.add(mailMeta);
            mailboxesConfig.set(toUUID+".letters", letters);
            mailboxesConfig.set(toUUID+".unread", true);
            try {
                mailboxesConfig.save(mailboxesFile);
                fromPlayer.getInventory().remove(mailItem);
                if (sendMessages) {
                    fromPlayer.sendMessage(prefix+("Brief versendet an {0}.").replaceAll("\\{0}", Bukkit.getOfflinePlayer(toUUID).getName()));
                }
                udpateMailboxFlags(Bukkit.getOfflinePlayer(toUUID));
                if (Bukkit.getPlayer(toUUID) != null) {
                    Bukkit.getPlayer(toUUID).sendMessage(prefix+"Du hast einen Brief! Prüfe deinen Briefkasten. Verwende '/mail', falls du nicht weisst wie das geht.");
                }
            } catch (Exception ex) {
                fromPlayer.sendMessage(prefix+"Der Brief konnte nicht verwendet werden.");
                if (getConfig().getBoolean("verbose_errors", false)) {
                    ex.printStackTrace();
                }
            }
        } else {
            if (sendMessages) {
                fromPlayer.sendMessage(prefix+"Der Briefkasten des Empfängers ist voll. Bitte versuche es später nochmals.");
            }
            if (Bukkit.getPlayer(toUUID) != null) {
                Bukkit.getPlayer(toUUID).sendMessage(prefix+("{0} hat versucht, dir einen Brief zu senden, aber dein Briefkasten ist voll. Du solltest ihn bei Gelegenheit etwas leeren.").replaceAll("\\{0}", fromPlayer.getName()));
            }
        }
        return true;
    }
    
    public boolean udpateMailboxFlags(OfflinePlayer owner) {
        return true;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Old Methods">
    @SuppressWarnings("unused")
	private boolean sendBook(org.bukkit.inventory.ItemStack bookStack, Player fromPlayer, String toString, boolean bulk) {
        /*Player target  = org.bukkit.Bukkit.getServer().getPlayer(toString);
        try {
            java.util.Date dateRaw = java.util.Calendar.getInstance().getTime();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat();
            format.applyPattern(getConfig().getString("dateformat"));
            String dateString = format.format(dateRaw);
            
            org.bukkit.inventory.meta.BookMeta bookMeta = (org.bukkit.inventory.meta.BookMeta) bookStack.getItemMeta();
            java.util.List<String> oldPages = bookMeta.getPages();
            java.util.List<String> newPages = new java.util.LinkedList<String>();
            newPages.add("§0From: "+fromPlayer.getDisplayName()+"\n§0To: "+toString+"\n§0Subject: "+ bookMeta.getTitle() +"\n§0Date: "+dateString+"\n§0\n§0\n§0\n§0\n§0\n§0\n§0\n§0\n§l§1--Real Mail--§r");
            if (oldPages.size()>0) {
                for (int i = 0; i < oldPages.size(); i++) {
                    newPages.add(oldPages.get(i));
                }
            }
            bookMeta.setPages(newPages);
            
            java.util.List<String> lore = new java.util.LinkedList<String>();
            lore.add("§7to  "+toString);
            lore.add("§7on "+dateString);
            bookMeta.setLore(lore);
            
            bookStack.setItemMeta(bookMeta);

             Send Book 
            //target.getInventory().addItem((org.bukkit.inventory.ItemStack) bookCraftStack);
            //fromPlayer.setItemInHand(new org.bukkit.inventory.ItemStack(0));
            if (mailboxesConfig.contains(toString)) {
                org.bukkit.World world = Bukkit.getWorld((String) mailboxesConfig.get(toString+".world"));
                org.bukkit.block.Block block = world.getBlockAt(mailboxesConfig.getInt(toString+".x"), mailboxesConfig.getInt(toString+".y"), mailboxesConfig.getInt(toString+".z"));
                if (block.getTypeId() == 54) {
                    org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
                    org.bukkit.inventory.Inventory chestInv = chest.getBlockInventory();
                    chestInv.addItem(bookStack);
                    setSignStatus(true, chest.getBlock(), toString);
                    if (!bulk) {
                        fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"Mail Sent!");
                        fromPlayer.setItemInHand(new org.bukkit.inventory.ItemStack(0));
                    }
                    if (target != null) {
                        target.sendMessage(prefix+prefix+ChatColor.WHITE+"You've got mail!");
                    }
                    // If there's a sign, mark as unread
                } else {
                    if (target != null) {
                        target.sendMessage(prefix+prefix+ChatColor.WHITE+fromPlayer.getDisplayName()+" tried to send you a message, but your mailbox is missing!");
                        target.sendMessage(prefix+prefix+ChatColor.WHITE+"Use "+org.bukkit.ChatColor.ITALIC+"/rm setmailbox"+org.bukkit.ChatColor.RESET+" on a chest.");
                    }
                    if (!bulk) {
                        fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"Failed to send.");
                        fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"They don't have a mailbox!");
                    }
                }
            } else {
                if (target != null) {
                    target.sendMessage(prefix+prefix+ChatColor.WHITE+fromPlayer.getDisplayName()+" tried to send you a message, but you don't have a mailbox!");
                    target.sendMessage(prefix+prefix+ChatColor.WHITE+"Use "+org.bukkit.ChatColor.ITALIC+"/rm setmailbox"+org.bukkit.ChatColor.RESET+" on a chest.");
                }
                if (!bulk) {
                    fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"Failed to send.");
                    fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"They don't have a mailbox!");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (!bulk) {
                fromPlayer.sendMessage(prefix+prefix+ChatColor.WHITE+"Failed to mail the book.");
            }
        }*/
        return true;
    }
    
    public boolean setSignStatus(boolean unread, org.bukkit.block.Block chestBlock, String ownerName) {
        /*for (int x = chestBlock.getX()-getConfig().getInt("signradius"); x <= chestBlock.getX()+getConfig().getInt("signradius"); x++) {
            for (int y = chestBlock.getY()-getConfig().getInt("signradius"); y <= chestBlock.getY()+getConfig().getInt("signradius"); y++) {
                for (int z = chestBlock.getZ()-getConfig().getInt("signradius"); z <= chestBlock.getZ()+getConfig().getInt("signradius"); z++) {
                    org.bukkit.block.Block block = chestBlock.getWorld().getBlockAt(x, y, z);
                    //if (block.getType() == org.bukkit.Material.SIGN || block.getType() == org.bukkit.Material.SIGN_POST) {
                    if (block.getTypeId() == 63 || block.getTypeId() == 68) {
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
                        if (sign.getLine(0).equals("[Mailbox]")) {
                            sign.setLine(1, ownerName);
                            if (unread) {
                                sign.setLine(2, "  §a"+getConfig().getString("unreadmailsigntext"));
                                mailboxesConfig.set(ownerName+".unread", true);
                            } else {
                                sign.setLine(2, getConfig().getString("readmailsigntext"));
                                mailboxesConfig.set(ownerName+".unread", false);
                            }
                            sign.update();
                            try {
                                mailboxesConfig.save(mailboxesFile);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }*/
        return false;
    }
    //</editor-fold>
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Login notifications">
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Login runnable">
    //</editor-fold>
    
}
