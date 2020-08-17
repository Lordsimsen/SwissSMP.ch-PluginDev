package ch.swisssmp.city;

import java.util.List;
import java.util.UUID;

import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.HayPile;
import ch.swisssmp.city.ceremony.promotion.PromotionCeremonyData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.BlockUtil;
import ch.swisssmp.utils.PlayerData;
import ch.swisssmp.utils.SwissSMPler;

class EventListener implements Listener {
    private static final Material INITIATOR_MATERIAL = Material.BLAZE_POWDER;

    @EventHandler
    private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event) {
        event.addComponent("citysystem");
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        ItemUtility.updateItems(event.getPlayer().getInventory());
    }

    @EventHandler
    private void onOpenInventory(InventoryOpenEvent event) {
        ItemUtility.updateItems(event.getInventory());
    }

    @EventHandler
    private void onResourcepackUpdated(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() != Status.SUCCESSFULLY_LOADED) return;
        Player player = event.getPlayer();
        player.getWorld().playSound(player.getLocation(), "founding_ceremony_drums", SoundCategory.RECORDS, 0.01f, 1);
        Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () -> {
            player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
        }, 20);
    }

    @EventHandler
    private void onTributeAnnounce(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof org.bukkit.block.Chest)) {
            // Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Not a chest");
            return;
        }

        CityPromotion promotion = CitySystem.getCityPromotion(itemStack).orElse(null);
        if (promotion == null) {
            // Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Not a promotion key");
            return;
        }

        City city = promotion.getCity();
        if (city == null) {
            // Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load city from promotion key: " + promotion.getCityId());
            return;
        }

        if (!city.isMayor(player)) {
            // SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Nur der Bürgermeister kann die Aufstiegszeremonie initiieren!");
            return;
        }

        CityLevel level = promotion.getLevel();
        if (level == null) {
            // Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load city level from promotion key: " + promotion.getTechtreeId()+"/"+promotion.getLevelId());
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        PromotionCeremonyData data = PromotionCeremonyData.create(level);

        /*
         * Checks whether the haypile below the Chest is of adequate size.
         */
        if (!HayPile.checkSize(block, CityPromotionCeremony.baseMaterial, data.getPromotionHaybaleCount())) {
            if (block.getRelative(BlockFace.DOWN).getType() == Material.HAY_BLOCK) {
                SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Baue einen grösseren Heuhaufen.");
            }
            // else{
            // 		Bukkit.getLogger().info(CitySystemPlugin.getPrefix()+" Not a tribute chest");
            // }
            return;
        }

        /*
         * Checks whether at least the required tribute is present in the tributechest.
         */
        Inventory inventory = ((Chest) block.getState()).getBlockInventory();
        for (ItemStack required : data.getTribute()) {
            int proposedAmount = 0;
            for (ItemStack proposed : inventory) {
                if (proposed == null || required == null || required.getType() == Material.AIR) continue;
                if (proposed.getType() != required.getType()) continue;
                proposedAmount += proposed.getAmount();
            }
            if (proposedAmount < required.getAmount()) {
                SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du wagst es mit ungenügenden Opfergaben zu versuchen?");
                player.getWorld().strikeLightningEffect(player.getLocation());
                return;
            }
        }

        long time = player.getWorld().getTime();
        if (time > 12000) {
            SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Heute kannst du keine Zeremonie mehr starten.");
            return;
        }
        CityPromotionCeremony existing = CityPromotionCeremony.get(city).orElse(null);
        if (existing == null) {
            SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Versammle deine Bürger vor Sonnenuntergang am Festplatz!");
            SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + " Die Zeremonie beginnt bei Sonnenuntergang!");

            Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () -> {
                CityPromotionCeremony.start(block, player, city, data);
            }, (100)); //Todo replace with (12000-time)
        } else {
            SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Die Zeremonie beginnt bei Sonnenuntergang!");
        }
    }


    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == INITIATOR_MATERIAL) {
            onDropInitiatorMaterial(event);
        }
    }

    private void onDropInitiatorMaterial(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("citysystem.found")) return;
        if (event.getPlayer().getWorld() != Bukkit.getWorlds().get(0)) return;
        event.getItemDrop().setMetadata("player", new FixedMetadataValue(CitySystemPlugin.getInstance(), event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onItemDestroy(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.DROPPED_ITEM) return;
        Player responsible = getThrower((Item) event.getEntity());
        if (responsible == null) return;
        Item item = (Item) event.getEntity();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getType() == INITIATOR_MATERIAL) {
            Block block = BlockUtil.getClosest(event.getEntity().getLocation(), 2, (current) -> current.getType() == Material.FIRE);
            CityFoundingCeremony.start(block, responsible);
            item.remove();
            return;
        }
        CitizenBill billInfo = CitizenBill.get(itemStack).orElse(null);
        if (billInfo != null && billInfo.getPlayerData() != null && billInfo.isSignedByCitizen() && billInfo.isSignedByParent()) {
            CityToolListener.handleCitizenBillDestruction(responsible, billInfo);
            item.remove();
        }
    }

    private Player getThrower(Item item) {
        if (!item.hasMetadata("player")) return null;
        List<MetadataValue> values = item.getMetadata("player");
        UUID player_uuid = null;
        for (MetadataValue value : values) {
            if (value.getOwningPlugin() != CitySystemPlugin.getInstance()) continue;
            try {
                player_uuid = UUID.fromString(value.asString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Bukkit.getPlayer(player_uuid);
    }
}
