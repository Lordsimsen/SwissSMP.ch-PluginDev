package ch.swisssmp.city;

import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.utils.BlockUtil;
import ch.swisssmp.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

class CityToolListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.useInteractedBlock() == Event.Result.ALLOW) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType().isInteractable()) return;
        }
        SigilRingInfo sigilRingInfo = SigilRingInfo.get(event.getItem()).orElse(null);
        if (sigilRingInfo != null) {
            this.onSigilRingInteract(event, sigilRingInfo);
            return;
        }

        CitizenBill citizenBill = CitizenBill.get(event.getItem()).orElse(null);
        if (citizenBill != null) {
            this.onCitizenBillInteract(event, citizenBill);
            return;
        }
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event){
        CitizenBill billInfo = CitizenBill.get(event.getItemDrop().getItemStack()).orElse(null);
        if(billInfo!=null && billInfo.getPlayerData()!=null){
            onDropCitizenBill(event, billInfo);
        }
    }

    protected static void handleCitizenBillDestruction(Player responsible, CitizenBill billInfo){
        City city = billInfo.getCity();
        if(city==null) return;
        PlayerData playerData = billInfo.getPlayerData();
        UUID citizenUid = playerData.getUniqueId();
        Citizenship citizenship = city.getCitizenship(citizenUid).orElse(null);
        if(citizenship==null){
            return;
        }
        if(city.isMayor(citizenUid)||city.isFounder(citizenUid)){
            if(!responsible.getUniqueId().equals(citizenUid) && !city.isMayor(responsible.getUniqueId()) && !responsible.hasPermission(CitySystemPermission.ADMIN)){
                SwissSMPler.get(responsible).sendActionBar(ChatColor.RED+"Du kannst "+playerData.getDisplayName()+" nicht aus der Stadt entfernen.");
                return;
            }
            else if(city.isMayor(citizenUid) && city.getCitizenships().size()>1){
                responsible.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+" Trete dein Amt als Bürgermeister ab, bevor du "+city.getName()+" verlässt.");
                return;
            }
        }

        city.removeCitizen(citizenship, (success)->{
            if(!success){
                responsible.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.RED + " Konnte "+playerData.getName()+" nicht entfernen. (Systemfehler)");
                return;
            }

            citizenship.announceCitizenshipRevoked(responsible);
            responsible.sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GRAY + " Du hast "+playerData.getName()+" aus der Bürgerliste von " + city.getName() + " entfernt.");
            ItemUtility.updateItems();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permission reload");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addon reload");
        });
    }

    private void onDropCitizenBill(PlayerDropItemEvent event, CitizenBill billInfo){
        City city = billInfo.getCity();
        if(!city.isCitizen(event.getPlayer().getUniqueId())) return;
        boolean isFounder = city.isFounder(event.getPlayer().getUniqueId());
        boolean isMayor = city.isMayor(event.getPlayer().getUniqueId());
        boolean isOwner = billInfo.getPlayerData().getUniqueId().equals(event.getPlayer().getUniqueId());
        if(!isFounder && !isMayor && !isOwner) return;
        event.getItemDrop().setMetadata("player", new FixedMetadataValue(CitySystemPlugin.getInstance(),event.getPlayer().getUniqueId()));
    }

    private void onSigilRingInteract(PlayerInteractEvent event, SigilRingInfo sigilRing) {
        if ((event.useInteractedBlock() == Event.Result.ALLOW && event.getClickedBlock().getType().isInteractable()) || event.useItemInHand() == Event.Result.DENY)
            return;
        CityView.open(event.getPlayer(), sigilRing.getCity());
    }

    private void onCitizenBillInteract(PlayerInteractEvent event, CitizenBill citizenBill) {
        if (event.useInteractedBlock() == Event.Result.ALLOW || event.useItemInHand() == Event.Result.DENY)
            return;
        if (citizenBill.getPlayerData() == null || citizenBill.getParentData() == null)
            return; // seems to be an invalid or unfinished bill
        if (citizenBill.isSignedByCitizen() && citizenBill.isSignedByParent()) return; // nothing to be done anymore

        // figure out who is trying to sign
        City city = citizenBill.getCity();
        Player player = event.getPlayer();
        Player otherPlayer;
        Player citizen;
        PlayerData otherPlayerData;
        PlayerData citizenData = citizenBill.getPlayerData();
        PlayerData parentData = citizenBill.getParentData();
        if (player.getUniqueId().equals(citizenData.getUniqueId())) {
            if (citizenBill.isSignedByCitizen()) {
                SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Bereits unterschrieben.");
                return;
            }
            citizen = player;
            otherPlayer = Bukkit.getPlayer(parentData.getUniqueId());
            otherPlayerData = parentData;
        } else if (player.getUniqueId().equals(parentData.getUniqueId()) || player.hasPermission(CitySystemPermission.ADMIN)) {
            if (citizenBill.isSignedByParent()) {
                SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Bereits unterschrieben.");
                return;
            }
            if (!citizenBill.isSignedByCitizen()) {
                SwissSMPler.get(player).sendActionBar(ChatColor.RED + citizenData.getName() + " muss zuerst unterschreiben.");
                return;
            }
            citizen = Bukkit.getPlayer(citizenData.getUniqueId());
            otherPlayer = citizen;
            otherPlayerData = citizenData;
        } else {
            // this player has nothing to do with it
            return;
        }

        if (otherPlayer == null) {
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + otherPlayerData.getName() + " muss anwesend sein.");
            return;
        }

        // check items

        PlayerInventory inv = event.getPlayer().getInventory();
        ItemStack itemInOtherHand = event.getHand() == EquipmentSlot.HAND ? inv.getItemInOffHand() : inv.getItemInMainHand();
        if (itemInOtherHand == null || itemInOtherHand.getType() != Material.FEATHER) {
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Halte eine Feder in der Zweithand.");
            return; // needs a feather on the other hand
        }

        int inkSlot = inv.first(Material.INK_SAC);
        if (inkSlot < 0) {
            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du brauchst Tinte im Inventar.");
            return; // needs ink somewhere in the inventory
        }

        ItemStack ink = inv.getItem(inkSlot);

        // sign!
        ItemStack itemStack = event.getItem();

        if (player == citizen) {
            citizenBill.setSignedByCitizen();
        } else {
            citizenBill.setSignedByParent();
        }

        if (citizenBill.isSignedByCitizen() && citizenBill.isSignedByParent()) {
            city.addCitizen(otherPlayer, player, citizenBill.getRole(), (success) -> {
                if (success) {
                    itemStack.setAmount(2);
                    ink.setAmount(ink.getAmount() - 1);
                    citizenBill.apply(itemStack);
                    SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Bürgerschein unterschrieben!");
                } else {
                    SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Konnte den Vorgang nicht abschliessen.");
                }
            });
        } else {
            ink.setAmount(ink.getAmount() - 1);
            citizenBill.apply(itemStack);
            SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Bürgerschein unterschrieben!");
        }
    }
}
